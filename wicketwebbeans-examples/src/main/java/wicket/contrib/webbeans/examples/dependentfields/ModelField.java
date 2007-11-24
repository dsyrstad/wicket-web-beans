package wicket.contrib.webbeans.examples.dependentfields;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import wicket.contrib.webbeans.fields.EnumField;
import wicket.contrib.webbeans.model.ElementMetaData;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class ModelField extends EnumField
{
    private ElementMetaData makeProp;
    
    public ModelField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        // Init with an empty set of values. We can't build the list until later. 
        // We also cannot pass in an instance of ValuesModel at this time because it is
        // dependent on this instance.
        super(id, model, metaData, viewOnly, Collections.EMPTY_LIST);
        
        // Retrieve the parameter that defines which Make property we need.
        makeProp = getDependentProperty(metaData, "makeProp", Make.class);
        
        setValuesModel( new ValuesModel() ); 
    }
    
    private final class ValuesModel extends AbstractReadOnlyModel 
    {
        @Override
        public Object getObject()
        {
            // Retrieve the value of the dependent property.
            Make make = (Make)getDependentPropertyBean(makeProp);
            if (make != null) {
                // Build a list of models based on the make.
                List<Model> values = new ArrayList<Model>();
                for (Model modelChoice : Model.values()) {
                    if (modelChoice.getMake().equals(make)) {
                        values.add(modelChoice);
                    }
                }
                
                return values;
            }
            
            return Collections.EMPTY_LIST;
        }
    }
}
