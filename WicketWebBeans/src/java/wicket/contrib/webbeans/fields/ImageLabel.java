package wicket.contrib.webbeans.fields;

import wicket.Component;
import wicket.Resource;
import wicket.ResourceReference;
import wicket.markup.ComponentTag;
import wicket.markup.html.PackageResource;
import wicket.markup.html.image.Image;
import wicket.markup.html.panel.Panel;


/**
 * A Panel that contains an image. Exists so that we can substitute an image in place of a Label.
 *
 * @author Dan Syrstad
 */
public class ImageLabel extends Panel
{
    /**
     * Construct a new ImageLabel.
     *
     * @param id the Wicket id.
     * @param component the component whose package will be used a base from which to load the image.
     * @param imageName the image name/path, relative to the component.
     * @param altText the "alt" text for the img tag. May be null.
     */
    public ImageLabel(String id, Class<? extends Component> component, String imageName, final String altText)
    {
        super(id);
        setRenderBodyOnly(true);

        add( new ImageWithAltText("i", new ResourceReference(component, imageName), altText) );
    }

    private static final class ImageWithAltText extends Image
    {
        private String altText;
        
        ImageWithAltText(String id, ResourceReference resourceReference, String altText)
        {
            super(id, resourceReference);
            this.altText = altText;
        }

        @Override
        protected void onComponentTag(ComponentTag tag)
        {
            super.onComponentTag(tag);
            if (altText != null) {
                tag.put("alt", altText);
                tag.put("title", altText);
            }
        }
    }
}
