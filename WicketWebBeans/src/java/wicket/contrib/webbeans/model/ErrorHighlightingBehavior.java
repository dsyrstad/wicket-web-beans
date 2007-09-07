
package wicket.contrib.webbeans.model;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.feedback.FeedbackMessages;
import wicket.model.AbstractModel;
import wicket.model.IModel;
import wicket.util.string.Strings;

/**
 * Behavior that modifies the CSS class attribute of the component's tag to be 
 * "beanFormError" if the component has an error.<p>
 * 
 * @author Dan Syrstad 
 */
public class ErrorHighlightingBehavior extends AttributeModifier
{
    public static final String BEAN_FORM_ERROR_CLASS = "beanFormError";
    /**
     * Construct a ErrorHighlightingBehavior. 
     *
     * @param attribute
     * @param addAttributeIfNotPresent
     * @param replaceModel
     */
    public ErrorHighlightingBehavior()
    {
        super("class", true, ErrorSwitchModel.INSTANCE);
    }

    @Override
    protected String newValue(String currentValue, String replacementValue)
    {
        if (Strings.isEmpty(replacementValue)) {
            if (currentValue == null) {
                return "";
            }
            
            // Remove just our class, leaving other classes.
            return currentValue.replace(BEAN_FORM_ERROR_CLASS, "").trim();
        }
        
        if (currentValue == null) {
            return replacementValue;
        }
        
        if (currentValue.contains(replacementValue + ' ')) {
            return currentValue;
        }
        
        return replacementValue + ' ' + currentValue;
    }

    private static final class ErrorSwitchModel extends AbstractModel 
    {
        static final ErrorSwitchModel INSTANCE = new ErrorSwitchModel();
        
        public Object getObject(Component component)
        {
            return component.hasErrorMessage() ? 
                            BEAN_FORM_ERROR_CLASS : 
                            "";
        }

        public void setObject(Component component, Object object)
        {
            throw new UnsupportedOperationException();
        }
    }
}
