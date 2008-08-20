/*---
   Copyright 2007 Visual Systems Corporation.
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

package net.sourceforge.wicketwebbeans.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.sourceforge.wicketwebbeans.containers.BeanForm;
import net.sourceforge.wicketwebbeans.containers.BeanGridPanel;
import net.sourceforge.wicketwebbeans.model.ComponentConfig;

import org.apache.wicket.markup.html.panel.Panel;


/**
 * Top or second-level annotation describing a bean and, optionally, its context. <p>
 * 
 * @author Dan Syrstad
 */
@Documented
@Target({TYPE}) @Retention(RUNTIME)
public @interface Bean {
    /** The class (type) of the bean. Only required if this annotation is not directly on the bean class. */
    Class<?> type() default Object.class;
    /** The WWB context for this bean. */ 
    String context() default "";
    /** The context being extended - default is the default context "". */
    String extendsContext() default "";
    
    /** The label to display for the bean. */
    String label() default "";
    /** Number of columns in the grid. Though not strictly a "standard" parameter, this is a property parameter supported by BeanGridPanel, 
     * which is the default layout used by {@link BeanForm}. See {@link BeanGridPanel} parameters for more details. The default is 3.
     */
    int columns() default 3;
    /** If the bean or bean's IModel passed to !BeanForm is a List, this is the number of rows to be displayed in a single page. The default is 10. */
    int rows() default 10;
    
    /** Specify detailed parameters for each action. */
    Action[] actions() default {};
    /** A short-cut in place of specifying individual Actions. Simply specify the action method names. This may include "-actionName" to remove an action. */
    String[] actionNames() default {};
    
    /** Defines the order of the properties in the view. Any properties not specified here will be displayed at the end. */
    Property[] properties() default {};
    /** A short-cut to in place of specifying individual Property annotations. Simply specify the property names. 
     * This may include "-propertyName" to remove a property. You may specify "action.actionMethodName" to 
     * include an action. Also, Property.EMPTY can be used to specify an empty cell. */
    String[] propertyNames() default {}; 
    
    /** A container to use in place of the default BeanGridPanel or BeanTablePanel. This container must must be a Panel and
     *   implement a constructor of the form: <p>
     *   <code>public Constructor(String id, final Object bean, ComponentConfig beanMetaData)</code>
     *   <p>
     *   where id = Wicket component ID<br>
     *   bean = the bean, or IModel containing the bean<br>
     *   beanMetaData = the ComponentConfig for bean<br>
     */
    Class<? extends Panel> container() default Panel.class;
    
    /** CSS class to be used for the bean. */
    String css() default "";
    
    /** Dynamic CSS class method name to be used for the bean. This method must have the signature: public String methodName(<BeanClass> bean, ComponentConfig beanMetaData). */
    String dynamicCss() default "";
    
    /** Arbitrary non-standard parameters. These are interpreted by the component. */
    Parameter[] params() default {};
    /** Short-cut to specify a single parameter. This is the parameter's name. */
    String paramName() default "";
    /** Short-cut to specify a single parameter. This is the parameter's value. */
    String paramValue() default "";
}
