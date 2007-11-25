/*---
   Copyright 2006-2007 Visual Systems Corporation.
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
package net.sourceforge.wicketwebbeans.fields;

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
