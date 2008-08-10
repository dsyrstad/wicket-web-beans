/*---
   Copyright 2006-2008 Visual Systems Corporation.
   http://www.vscorp.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
        http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
---*/
package net.sourceforge.wicketwebbeans.containers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.wicketwebbeans.actions.BeanActionButton;
import net.sourceforge.wicketwebbeans.fields.AbstractField;
import net.sourceforge.wicketwebbeans.fields.Field;
import net.sourceforge.wicketwebbeans.fields.InputField;
import net.sourceforge.wicketwebbeans.model.BeanMetaData;
import net.sourceforge.wicketwebbeans.model.BeanPropertyModel;
import net.sourceforge.wicketwebbeans.model.ElementMetaData;
import net.sourceforge.wicketwebbeans.model.TabMetaData;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;

/**
 * Generic component for presenting a bean form. Supports the following parameters: <p>
 * <ul>
 * <li>label - the form's label.</li>
 * <li>rows - if the bean is a List, this is the number of rows to be displayed. Defaults to 10.</li>
 * <li>container - a container to use in place of the default BeanGridPanel or BeanTablePanel. This container must must be a Panel and
 *   implement a constructor of the form: <p>
 *   <code>public Constructor(String id, final Object bean, BeanMetaData beanMetaData, TabMetaData tabMetaData)</code>
 *   <p>
 *   where id = Wicket component ID<br>
 *   bean = the bean, or IModel containing the bean<br>
 *   beanMetaData = the BeanMetaData for bean<br>
 *   tabMetaData = the tab metadata
 *   </li>
 * </ul>
 * 
 * You can override default error message for a required field.<br>
 * Property key of the message: <em>wicketwebbeans.BeanForm.fieldIsRequired</em><br>
 * Variable in the message which will be substituted for field label: <em>${fieldLabel}</em>
 *
 * @author Dan Syrstad
 */
public class BeanForm extends Panel
{
    private static final long serialVersionUID = -7287729257178283645L;

    public static final String PARAM_ROWS = "rows";

    private static Class<?>[] CONTAINER_CONSTRUCTOR_PARAMS = { String.class, Object.class, BeanMetaData.class,
                    TabMetaData.class };

    private Form form;
    private FormVisitor formVisitor;
    private FeedbackPanel feedback;
    // Wicket ID/HTML ID of field with focus.
    private String focusFieldId = null;
    private String submitFieldName;
    private String submitFieldValue;
    private BeanPropertyChangeListener listener = new BeanPropertyChangeListener();

    /** Maps components in this form to their properties. */
    private Set<ComponentPropertyMapping> componentPropertyMappings = new HashSet<ComponentPropertyMapping>(200);
    /** Components that should be refreshed on the new Ajax Component update. */
    private Set<ComponentPropertyMapping> refreshComponents = new HashSet<ComponentPropertyMapping>(200);
    /** Form submit recursion counter. Zero means we're not validating currently. */

    /**
     * Construct a new BeanForm.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     *  The bean may be a List or, if an IModel, a model that returns a List. If so, the bean is display is
     *  displayed using BeanTablePanel. Otherwise BeanGridPanel is used.
     * @param beanMetaData the meta data for the bean. If bean is a List or model of a List, then this must be
     *  the BeanMetaData for a single element (row) of the List. 
     */
    public BeanForm(String id, final Object bean, final BeanMetaData beanMetaData)
    {
        this(id, bean, beanMetaData, null);
    }

    /**
     * Construct a new BeanForm.
     *
     * @param id the Wicket id for the panel.
     * @param bean the bean to be displayed. This may be an IModel or regular bean object.
     *  The bean may be a List or, if an IModel, a model that returns a List. If so, the bean is display is
     *  displayed using BeanTablePanel. Otherwise BeanGridPanel is used.
     * @param beanMetaData the meta data for the bean. If bean is a List or model of a List, then this must be
     *  the BeanMetaData for a single element (row) of the List.
     * @param container an optional container to use in place of the default BeanGridPanel or BeanTablePanel. This container must must be a Panel and
     *   implement a constructor of the form: <p>
     *   <code>public Constructor(String id, final Object bean, BeanMetaData beanMetaData, TabMetaData tabMetaData)</code>
     *   <p>
     *   where id = Wicket component ID<br>
     *   bean = the bean, or IModel containing the bean<br>
     *   beanMetaData = the BeanMetaData for bean<br>
     *   tabMetaData = the tab metadata<p>
     *   May be null.
     */
    @SuppressWarnings("serial")
    public BeanForm(String id, final Object bean, final BeanMetaData beanMetaData,
                    final Class<? extends Panel> container)
    {
        super(id);

        Form hiddenForm = new Form("f");
        hiddenForm.setOutputMarkupId(true);
        hiddenForm.add(new AjaxFieldUpdater(hiddenForm, "onajax"));
        hiddenForm.add(new HiddenField("focusFieldId", new PropertyModel(this, "focusFieldId")));
        hiddenForm.add(new HiddenField("submitFieldName", new PropertyModel(this, "submitFieldName")));
        hiddenForm.add(new HiddenField("submitFieldValue", new PropertyModel(this, "submitFieldValue")));
        add(hiddenForm);

        // This form is never actually submitted. It exists to hold input fields only.
        form = new Form("nsf");
        form.add(new HiddenField("ajaxFormId", new Model(hiddenForm.getMarkupId())));
        add(form);

        String title = beanMetaData.getLabel();
        form.add(new Label("title", title));

        form.add(new Label("beanFormIndicatorErrorLabel", new ResourceModel("beanFormError.msg",
                        "An error occurred on the server. Your session may have timed out.")));

        beanMetaData.consumeParameter(PARAM_ROWS);

        formVisitor = new FormVisitor();

        // Single default tab - none explicitly specified. Don't add a tab panel.
        // TODO - Go away.
        form.add( createPanel("tabs", bean, beanMetaData, beanMetaData.getTabs().get(0), container) );

        // Use a FeedbackMessageFilter to handle messages for multiple BeanForms on a page. This is because messages are stored on the session.
        IFeedbackMessageFilter feedbackFilter = new IFeedbackMessageFilter() {
            public boolean accept(FeedbackMessage message)
            {
                // If the reporter is a field and this is refreshing because of a non-Ajax form submit, it's very likely that the field has been detached
                // from its parent because it is in a list view. As a result, findParent doesn't return the BeanForm.
                Component reporter = message.getReporter();
                AbstractField reporterField = (AbstractField)(reporter instanceof AbstractField ? reporter : reporter
                                .findParent(AbstractField.class));
                if (reporterField != null) {
                    return reporterField.getBeanForm().getId().equals(BeanForm.this.getId());
                }

                Component parent = (reporter instanceof BeanForm ? reporter : reporter.findParent(BeanForm.class));
                return reporter == BeanForm.this || parent == null || parent == BeanForm.this;
            }
        };

        feedback = new FeedbackPanel("feedback", feedbackFilter);
        feedback.setOutputMarkupId(true);
        form.add(feedback);

        // Add bean actions.
        List<ElementMetaData> globalActions = beanMetaData.getGlobalActions();
        form.add(new ListView("actions", globalActions) {
            protected void populateItem(ListItem item)
            {
                ElementMetaData element = (ElementMetaData)item.getModelObject();
                item.add(new BeanActionButton("action", element, form, bean));
            }
        });
    }

    /**
     * Creates the panel for the given tab.
     *
     * @param panelId the Wicket id for the panel component.
     * @param bean may be a bean or an IModel containing a bean.
     * @param beanMetaData the BeanMetaData.
     * @param tabMetaData the TabMetaData.
     * @param container the container class to use. May be null.
     * 
     * @return a Panel.
     */
    protected Panel createPanel(String panelId, Object bean, BeanMetaData beanMetaData, TabMetaData tabMetaData,
                    Class<? extends Panel> containerClass)
    {
        if (containerClass == null) {
            containerClass = beanMetaData.getContainerClass();
        }

        if (containerClass != null) {
            try {
                Constructor<? extends Panel> constructor = containerClass.getConstructor(CONTAINER_CONSTRUCTOR_PARAMS);
                return constructor.newInstance(panelId, bean, beanMetaData, tabMetaData);
            }
            catch (Exception e) {
                throw new RuntimeException("Error instantiating container", e);
            }
        }

        boolean isList = (bean instanceof List);
        if (bean instanceof IModel) {
            Object modelBean = ((IModel)bean).getObject();
            isList = (modelBean instanceof List);
        }

        if (isList) {
            // BeanTablePanel expects a model. Wrap bean if necessary.
            IModel model;
            if (bean instanceof IModel) {
                model = (IModel)bean;
            }
            else {
                model = new Model((Serializable)bean);
            }

            // Get Number of rows from parameters
            int rows = beanMetaData.getIntParameter(PARAM_ROWS, 10);
            return new BeanTablePanel(panelId, model, beanMetaData, rows);
        }

        return new BeanGridPanel(panelId, bean, beanMetaData, tabMetaData);
    }

    /**
     * Finds the BeanForm that is the parent of the given childComponent.
     *
     * @param childComponent the child, may be null.
     * 
     * @return the parent BeanForm, or null if childComponent is not part of a BeanForm.
     */
    // TODO Would really like to get rid of this - BeanForm shouldn't need to be known to children.
    public static BeanForm findBeanFormParent(Component childComponent)
    {
        if (childComponent == null) {
            return null;
        }

        return (BeanForm)childComponent.findParent(BeanForm.class);
    }

    /**
     * Determines if the BeanForm associated with childComponent is currently in a form
     * submit phase.
     * 
     * @param childComponent the child, may be null.
     * 
     * @return true if the BeanForm is validating, or false if not.
     */
    // TODO Go away.
    public static boolean isInSubmit(Component childComponent)
    {
        return false;
    }

    /**
     * Rather than using Wicket's required field validation, which doesn't play well with Ajax and forms,
     * allow validation of fields on actions. User must call this from the action method.
     * Adds errors to the page if empty required fields are found. 
     *
     * @return true if validation was successful, else false if errors were found.
     */
    public boolean validateRequired()
    {
        RequiredFieldValidator validator = new RequiredFieldValidator();

        visitChildren(AbstractField.class, validator);

        return !validator.errorsFound;
    }

    /**
     * Registers the given component with this form. This is usually called by Fields
     * (for example, see {@link AbstractField}) to add the form behavior to their
     * components.
     * 
     * @param component
     */
    public void registerComponent(Component component, BeanPropertyModel beanModel, ElementMetaData element)
    {
        ComponentPropertyMapping mapping = new ComponentPropertyMapping(beanModel, element);
        componentPropertyMappings.add(mapping);

        // Make sure we don't register ourself twice.
        if (beanModel != null && beanModel.getBeanForm() == null) {
            // Listen for PropertyChangeEvents on this bean, if necessary.
            // TODO When do we unregister?? Maybe a WeakRef to ourself in the listener? Then listener unregisters
            // TODO if we don't exist anymore.
            element.getBeanMetaData().addPropertyChangeListener(beanModel, listener);
            beanModel.setBeanForm(this);
        }

        if (component instanceof MarkupContainer) {
            ((MarkupContainer)component).visitChildren(formVisitor);
        }
        else {
            formVisitor.component(component);
        }
    }

    /**
     * Gets the listener.
     *
     * @return a BeanPropertyChangeListener.
     */
    public BeanPropertyChangeListener getListener()
    {
        return listener;
    }

    /**
     * Allows external app to set the field to receive focus.
     * 
     * @param component the component, may be null to unset the field.
     */
    public void setFocusComponent(Component component)
    {
        setFocusFieldId(component == null ? null : component.getId());
    }

    /**
     * Gets the focusFieldId.
     *
     * @return the focusFieldId.
     */
    public String getFocusFieldId()
    {
        return focusFieldId;
    }

    /**
     * Sets the focusFieldId.
     *
     * @param focusFieldId the focusFieldId to set.
     */
    public void setFocusFieldId(String focusField)
    {
        this.focusFieldId = focusField;
    }

    /**
     * Gets the submitFieldName.
     *
     * @return a String.
     */
    public String getSubmitFieldName()
    {
        return submitFieldName;
    }

    /**
     * Sets submitFieldName.
     *
     * @param submitFieldName a String.
     */
    public void setSubmitFieldName(String submitFormFieldName)
    {
        this.submitFieldName = submitFormFieldName;
    }

    /**
     * Gets the submitFieldValue.
     *
     * @return a String.
     */
    public String getSubmitFieldValue()
    {
        return submitFieldValue;
    }

    /**
     * Sets submitFieldValue.
     *
     * @param submitFieldValue a String.
     */
    public void setSubmitFieldValue(String submitFormFieldValue)
    {
        this.submitFieldValue = submitFormFieldValue;
    }

    /**
     * @return true if {@link #refreshComponents(AjaxRequestTarget, Component)} needs to be called.
     */
    public boolean isComponentRefreshNeeded()
    {
        return !refreshComponents.isEmpty();
    }

    /**
     * Clears the components that would be refreshed if {@link #refreshComponents(AjaxRequestTarget, Component)} were called.
     */
    public void clearRefreshComponents()
    {
        refreshComponents.clear();
    }

    /**
     * Refresh the targetComponent, in addition to any components that need to be updated
     * due to property change events.
     *
     * @param target
     * @param targetComponent the targetComponent, may be null.
     */
    public void refreshComponents(final AjaxRequestTarget target, Component targetComponent)
    {
        if (targetComponent != null) {
            refreshComponent(target, targetComponent);
        }

        if (!refreshComponents.isEmpty()) {
            // Refresh components fired from our PropertyChangeListener.

            // Visit all children and see if they match the fired events. 
            form.visitChildren(new IVisitor() {
                public Object component(Component component)
                {
                    Object model = component.getModel();
                    if (model instanceof BeanPropertyModel) {
                        BeanPropertyModel propModel = (BeanPropertyModel)model;
                        ElementMetaData componentMetaData = propModel.getElementMetaData();
                        for (ComponentPropertyMapping mapping : refreshComponents) {
                            if (mapping.elementMetaData == componentMetaData) {
                                refreshComponent(target, component);
                                break;
                            }
                        }
                    }

                    return IVisitor.CONTINUE_TRAVERSAL;
                }
            });

            refreshComponents.clear();
        }
    }

    private void refreshComponent(final AjaxRequestTarget target, Component targetComponent)
    {
        // Refresh this field. We have to find the parent Field to do this.
        MarkupContainer field;
        if (targetComponent instanceof Field) {
            field = (MarkupContainer)targetComponent;
        }
        else {
            field = targetComponent.findParent(AbstractField.class);
        }

        if (field != null) {
            if (!field.getRenderBodyOnly()) {
                target.addComponent(field);
            }
            else {
                // Field is RenderBodyOnly, have to add children individually
                field.visitChildren(new IVisitor() {
                    public Object component(Component component)
                    {
                        if (!component.getRenderBodyOnly()) {
                            target.addComponent(component);
                        }

                        return IVisitor.CONTINUE_TRAVERSAL;
                    }

                });
            }
        }
        else {
            target.addComponent(targetComponent);
        }
    }

    private final class FormVisitor implements IVisitor, Serializable
    {
        private static final long serialVersionUID = 8134029312041772069L;

        public Object component(Component component)
        {
            if (component instanceof FormComponent) {
                boolean addBehavior = true;
                for (IBehavior behavior : (List<IBehavior>)component.getBehaviors()) {
                    if (behavior instanceof SimpleAttributeModifier) {
                        if (((SimpleAttributeModifier)behavior).getAttribute().equals("onchange")) {
                            addBehavior = false;
                            break;
                        }
                    }
                }

                if (addBehavior) {
                    component.add(new SimpleAttributeModifier("onfocus", "wwbBeanForm.onFocus(this)"));
                    component.add(new SimpleAttributeModifier("onchange", "wwbBeanForm.onChange(this)"));
                }
            }

            return IVisitor.CONTINUE_TRAVERSAL;
        }
    }


    private final class AjaxFieldUpdater extends AjaxFormValidatingBehavior
    {
        private static final long serialVersionUID = -6152087577132232936L;

        private AjaxFieldUpdater(Form form, String event)
        {
            super(form, event);
        }

        @Override
        protected void onSubmit(final AjaxRequestTarget target)
        {
            /*
            // NOTE: The following code fails to clear off field errors that have been corrected.
             
            // Only refresh messages if we have one. Otherwise previous error messages go away on the
            // first field change.
            if (form.getPage().hasFeedbackMessage()) {
                super.onSubmit(target);
            }
            */
            super.onSubmit(target);
            System.out.println("Got " + submitFieldName + '=' + submitFieldValue + " focus=" + focusFieldId);
            // FormComponent.getInputName()
            TextField x; x.
            refreshComponents(target, getComponent());
        }

        @Override
        protected void onError(AjaxRequestTarget target)
        {
            super.onError(target);
            System.out.println("Error Got " + submitFieldName + '=' + submitFieldValue + " focus=" + focusFieldId);
            refreshComponents(target, getComponent());
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator()
        {
            return AjaxBusyDecorator.INSTANCE;
        }
    }


    public static final class AjaxBusyDecorator implements IAjaxCallDecorator
    {
        private static final long serialVersionUID = 1L;
        public static final AjaxBusyDecorator INSTANCE = new AjaxBusyDecorator();

        public CharSequence decorateOnFailureScript(CharSequence script)
        {
            return "wwbBeanForm.indicatorError();" + script;
        }

        public CharSequence decorateOnSuccessScript(CharSequence script)
        {
            return "wwbBeanForm.indicatorOff();" + script;
        }

        public CharSequence decorateScript(CharSequence script)
        {
            return "wwbBeanForm.indicatorOn(); " + script;
        }
    }


    /**
     * Simple data structure for mapping components and properties. <p>
     */
    private static final class ComponentPropertyMapping implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        /** IModel holding the bean. */
        private BeanPropertyModel beanModel;
        private ElementMetaData elementMetaData;

        ComponentPropertyMapping(BeanPropertyModel beanModel, ElementMetaData elementMetaData)
        {
            this.beanModel = beanModel;
            this.elementMetaData = elementMetaData;
        }

        /** 
         * {@inheritDoc}
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            int result = 31 + ((beanModel == null) ? 0 : beanModel.hashCode());
            result = 31 * result + ((elementMetaData == null) ? 0 : elementMetaData.hashCode());
            return result;
        }

        private Object getBean()
        {
            return beanModel.getBean();
        }

        /** 
         * {@inheritDoc}
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ComponentPropertyMapping)) {
                return false;
            }

            final ComponentPropertyMapping other = (ComponentPropertyMapping)obj;
            return beanModel == other.beanModel
                            && (elementMetaData == other.elementMetaData || (elementMetaData != null && elementMetaData
                                            .equals(other.elementMetaData)));
        }
    }


    /**
     * Listens to property change events on a bean and adds them to the queue of
     * components to be refreshed. <p>
     */
    public final class BeanPropertyChangeListener implements PropertyChangeListener, Serializable
    {
        private static final long serialVersionUID = 1L;

        public void propertyChange(PropertyChangeEvent evt)
        {
            // Find matching component
            Object bean = evt.getSource();
            String propName = evt.getPropertyName();
            for (ComponentPropertyMapping mapping : componentPropertyMappings) {
                if (bean == mapping.getBean() && propName.equals(mapping.elementMetaData.getPropertyName())) {
                    BeanForm.this.refreshComponents.add(mapping);
                }
            }
        }
    }


    /**
     * Validates required fields on the form and sets an error message on the component if necessary.
     */
    private final class RequiredFieldValidator implements IVisitor
    {
        private class FieldLabel implements Serializable
        {
            private static final long serialVersionUID = 1L;
            
            String fieldLabel;

            public FieldLabel(String fieldLabel)
            {
                this.fieldLabel = fieldLabel;
            }

            public String getFieldLabel()
            {
                return fieldLabel;
            }

            public void setFieldLabel(String fieldLabel)
            {
                this.fieldLabel = fieldLabel;
            }
        }

        boolean errorsFound = false;

        public Object component(Component component)
        {
            AbstractField field = (AbstractField)component;
            if (field.isRequiredField() && Strings.isEmpty(field.getModelObjectAsString())) {
                FieldLabel fieldName = new FieldLabel(field.getElementMetaData().getLabel());
                StringResourceModel labelModel = new StringResourceModel("wicketwebbeans.BeanForm.fieldIsRequired",
                                field.getElementMetaData().getBeanMetaData().getComponent(), new Model(fieldName),
                                "${fieldLabel} is required");
                BeanForm.this.error(labelModel.getObject().toString());
                errorsFound = true;
            }

            return CONTINUE_TRAVERSAL;
        }
    }
}
