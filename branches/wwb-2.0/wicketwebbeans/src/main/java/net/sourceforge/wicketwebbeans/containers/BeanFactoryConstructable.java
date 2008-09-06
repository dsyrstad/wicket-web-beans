package net.sourceforge.wicketwebbeans.containers;

import net.sourceforge.wicketwebbeans.model.BeanFactory;

/**
 * Marker interface indicating that this is a Wicket Component that has the following constructor signature:<br/>
 * 
 * public SomeComponent(String id, final Object bean, BeanConfig beanConfig)
 * 
 * id - the Wicket id for the panel. <br/>
 * beanFactory - the {@link BeanFactory} to use to generate other components. <br/>
 *  <p>
 * 
 * @author Dan Syrstad
 */
public interface BeanFactoryConstructable
{
}
