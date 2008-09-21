package net.sourceforge.wicketwebbeans.examples.experimental;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

public class AjaxExperimentalPage extends WebPage
{
    public AjaxExperimentalPage()
    {
        TextField field = new TextField("field");
        field.setModel(new Model());
        //field.setType(Date.class);
        field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                FormComponent formComponent = (FormComponent)getComponent();
                System.out.println(formComponent.getInputName() + "=" + getComponent().getModelObjectAsString());
            }
        });
        add(field);
    }
}
