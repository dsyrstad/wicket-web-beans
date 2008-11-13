/*---
   Copyright 2008 Visual Systems Corporation.
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

package net.sourceforge.wicketwebbeans.model;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.wicketwebbeans.components.FormComponentWrapper;
import net.sourceforge.wicketwebbeans.test.Employee;
import net.sourceforge.wicketwebbeans.test.TestUtils;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.validation.validator.NumberValidator;

/**
 * Tests BeanFactory. <p>
 * 
 * @author Dan Syrstad
 */
public class BeanFactoryTest extends TestCase
{
    static {
        BeanFactoryTest.class.getClassLoader().setDefaultAssertionStatus(true);
    }

    public void testSuccessfulLoadAndGetBeanConfig() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("PintoBean { class: x; param: value } Another { class: x; param: value2 }");
        assertNotNull(factory.getBeanConfig("PintoBean"));
        assertNotNull(factory.getBeanConfig("Another"));
        assertNull(factory.getBeanConfig("X"));
        assertNotNull(factory.getPropertyResolver());
    }

    public void testGetBeanConfigWithParameters() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: y; }");
        Collection<ParameterAST> parameters = new ArrayList<ParameterAST>();

        // Test empty parameters
        BeanConfig config1 = factory.getBeanConfig("Bean1", parameters);
        assertNotNull(config1);
        assertEquals("Bean1", config1.getBeanName());
        assertSame(factory, config1.getBeanFactory());

        // Should return new instances for same bean name
        BeanConfig config2 = factory.getBeanConfig("Bean1");
        assertNotSame(config1, config2);

        // Test multiple parameters
        parameters.add(new ParameterAST("param1", Collections.singletonList(new ParameterValueAST("value1", true))));
        parameters.add(new ParameterAST("param2", Collections.singletonList(new ParameterValueAST("value2", true))));
        BeanConfig config3 = factory.getBeanConfig("Bean1", parameters);
        assertEquals("y", config3.getParameterValueAsString("class"));
        assertEquals("value1", config3.getParameterValueAsString("param1"));
        assertEquals("value2", config3.getParameterValueAsString("param2"));

        // Test not found
        assertNull(factory.getBeanConfig("notfound", parameters));
    }

    public void testLoadingBadUrl() throws Exception
    {
        // Null URL
        try {
            new BeanFactory().loadBeanConfig(null);
            fail();
        }
        catch (AssertionError e) {
            // Expected
        }

        // File Not Found
        try {
            new BeanFactory().loadBeanConfig(new URL("file:///xyzzy-not-found"));
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Error reading stream"));
            assertNotNull(e.getCause());
        }
    }

    public void testLoadClassBeanConfig() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("PintoBean { class: java.util.Date } Another { class: x; }");
        assertSame(Date.class, factory.loadClass(factory.getBeanConfig("PintoBean")));
        try {
            factory.loadClass(factory.getBeanConfig("Another"));
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        }
    }

    public void testLoadClassBeanConfigWithMissingClass() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("PintoBean { extends: x }");
        try {
            factory.loadClass(factory.getBeanConfig("PintoBean"));
        }
        catch (RuntimeException e) {
            // Expected
            assertEquals("Cannot find parameter 'class' on bean 'PintoBean'", e.getMessage());
        }
    }

    public void testNewInstanceBeanNotFound() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: y; }");
        try {
            factory.newInstance("Bean2");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("not defined in factory"));
        }
    }

    public void testClassAndExtendsParameter() throws Exception
    {
        try {
            TestUtils.createBeanFactory("PintoBean { param2: x }");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("must specify class or extends"));
        }

        // Class by itself is OK
        BeanFactory factory = TestUtils.createBeanFactory("PintoBean { class: x }");
        assertNotNull(factory.getBeanConfig("PintoBean").getParameterValue("class"));

        // Extends by itself is OK
        factory = TestUtils.createBeanFactory("PintoBean { extends: x }");
        assertNotNull(factory.getBeanConfig("PintoBean").getParameterValue("extends"));

        // But not both at same time
        try {
            TestUtils.createBeanFactory("PintoBean { class: y; extends: x }");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("cannot specify both class and extends"));
        }
    }

    public void testNewInstanceWithNoParameters() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: java.util.Date; }");
        assertTrue(factory.newInstance("Bean1") instanceof Date);
    }

    public void testNewInstanceUsingImports() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: Date; }");
        assertTrue(factory.newInstance("Bean1") instanceof Date);
    }

    public void testNewInstanceWithParameters() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { "
                        + "class: net.sourceforge.wicketwebbeans.model.TestBean;"
                        + "stringProp: \"stringValue\"; intProp: 5; doubleProp: 3.14; booleanProp: true; "
                        + " floatProp: 9.5; shortProp: 3; longProp: 123456789012345678;"
                        + " integerObjProp: 3; doubleObjProp: 9.99; floatObjProp: 10.3; shortObjProp: 62;"
                        + " booleanObjProp: false; longObjProp: 323; setterWithReturnValue:  \"swrv\";"
                        + " parameterValues: 1, \"2\", 3.1, true, symbol; model: \"modelString\"; "
                        + " parameterValue: \"single\";" + " modelOfSubBean: SubBean; }"
                        + " SubBean { class: java.util.Date; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertEquals(5, bean.getIntProp());
        assertEquals(3.14, bean.getDoubleProp());
        assertTrue(bean.isBooleanProp());
        assertEquals(9.5F, bean.getFloatProp());
        assertEquals((short)3, bean.getShortProp());
        assertEquals(123456789012345678L, bean.getLongProp());

        assertEquals(Integer.valueOf(3), bean.getIntegerObjProp());
        assertEquals(Double.valueOf(9.99), bean.getDoubleObjProp());
        assertEquals(Boolean.FALSE, bean.getBooleanObjProp());
        assertEquals(Float.valueOf(10.3F), bean.getFloatObjProp());
        assertEquals(Short.valueOf((short)62), bean.getShortObjProp());
        assertEquals(Long.valueOf(323L), bean.getLongObjProp());
        assertEquals("swrv", bean.getSetterWithReturnValue());

        List<ParameterValueAST> values = bean.getParameterValues();
        assertEquals(5, values.size());
        assertEquals(Integer.valueOf(1), values.get(0).getIntegerValue());
        assertEquals("2", values.get(1).getValue());
        assertEquals(Double.valueOf(3.1), values.get(2).getDoubleValue());
        assertEquals(Boolean.valueOf(true), values.get(3).getBooleanValue());
        assertEquals("symbol", values.get(4).getValue());

        assertEquals("single", bean.getParameterValue().getValue());
        assertEquals("modelString", bean.getModel().getObject());
        assertTrue(bean.getModelOfSubBean().getObject() instanceof Date);
        assertSame(factory, bean.getBeanFactory());
    }

    public void testNewInstanceWithPropertyParameter() throws Exception
    {
        TestBean origBean = new TestBean();
        origBean.setSomeOtherString("hello");
        BeanFactory factory = TestUtils.createBeanFactory(new Model(origBean), "Bean1 { "
                        + " class: net.sourceforge.wicketwebbeans.model.TestBean;" + //
                        " stringProp: $someOtherString; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertEquals("hello", bean.getStringProp());
    }

    public void testNewInstanceWithNonModelParameterOnModelProperty() throws Exception
    {
        TestBean origBean = new TestBean();
        origBean.setSomeOtherString("hello");
        BeanFactory factory = TestUtils.createBeanFactory(new Model(origBean), "Bean1 { "
                        + " class: net.sourceforge.wicketwebbeans.model.TestBean;" + //
                        " model: $someOtherString; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertEquals("hello", bean.getModel().getObject());
    }

    public void testNewInstanceClassNotFound() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: java.util.NotFound; }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot find class"));
            assertTrue(e.getMessage().contains("java.util.NotFound"));
            assertNotNull(e.getCause());
        }
    }

    public void testNewInstanceNoPublicDefaultConstructor() throws Exception
    {
        BeanFactory factory = TestUtils.createBeanFactory("Bean1 { class: java.lang.reflect.AccessibleObject; }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot create instance of bean"));
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    public void testNewInstancePropertyNotFound() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; notfound: x }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot find property"));
            assertTrue(e.getMessage().contains("notfound"));
        }
    }

    public void testNewInstancePropertySetterNotExposed() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; setterNotExposed: x }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().matches("Property setterNotExposed .* does not have an exposed setter.*"));
        }
    }

    public void testNewInstancePropertyTypeNotSupported() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; typeNotSupported: \"x\" }");

        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertNull(bean.getTypeNotSupported());
    }

    public void testNewInstancePropertyNotExposed() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; notExposed: \"x\" }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot find property"));
            assertTrue(e.getMessage().contains("notExposed"));
        }
    }

    public void testNewInstancePropertyThrows() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; throwsException: \"x\" }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage(), e.getMessage().contains("Unable to invoke setter"));
            assertTrue(e.getCause() instanceof InvocationTargetException);
        }
    }

    public void testNewInstanceWithNullParameters() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean;"
                                        + "integerObjProp: null; stringProp: \"null\" }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertNull(bean.getIntegerObjProp());
        assertEquals("null", bean.getStringProp());
    }

    public void testNewInstanceWithArgs() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1", "string", 32, 64);
        assertEquals("string", bean.getStringProp());
        assertEquals(32, bean.getIntProp());
        assertEquals(Integer.valueOf(64), bean.getIntegerObjProp());
    }

    public void testNewInstanceWithNullArgs() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1", null, 3, null);
        assertNull(bean.getStringProp());
        assertEquals(3, bean.getIntProp());
        assertNull(bean.getIntegerObjProp());
    }

    public void testNewInstanceWithBeanConfigConstructorArgs() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean { args: \"String\", 33, 66 } }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertEquals("String", bean.getStringProp());
        assertEquals(33, bean.getIntProp());
        assertEquals(Integer.valueOf(66), bean.getIntegerObjProp());
    }

    public void testNewInstanceWithBeanConfigFactoryMethodArg() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: org.apache.wicket.validation.validator.NumberValidator { factoryMethod: range ; args: -50, 50 } }");
        NumberValidator.RangeValidator validator = (NumberValidator.RangeValidator)factory.newInstance("Bean1");
        assertEquals(-50, validator.getMinimum());
        assertEquals(50, validator.getMaximum());
    }

    public void testNewInstanceWithMismatchedBeanConfigConstructorArgs() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean { args: \"String\", 33} }");
        try {
            factory.newInstance("Bean1");
            fail();
        }
        catch (RuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Cannot create instance of bean 'Bean1'"));
            assertEquals("Cannot find constructor matching args: {String,33}", e.getCause().getMessage());
        }
    }

    public void testNewInstanceWithUnmatchedArgs() throws Exception
    {
        BeanFactory factory = TestUtils
                        .createBeanFactory("Bean1 { class: net.sourceforge.wicketwebbeans.model.TestBean; }");
        try {
            factory.newInstance("Bean1", "1", 2);
            fail();
        }
        catch (RuntimeException e) {
            // Expected
            assertTrue(e.getMessage().contains("Cannot create instance of bean"));
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    public void testNewInstanceWithPropertyBindings() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        Employee bean = new Employee("15", null, BigDecimal.valueOf(25));
        BeanFactory factory = TestUtils.createBeanFactory(new Model(bean),
                        "Bean1 { class: net.sourceforge.wicketwebbeans.test.Employee;"
                                        + " name: $salary; salary: $name;  }");
        Employee newBean = (Employee)factory.newInstance("Bean1");
        assertEquals(bean.getName(), newBean.getSalary().toString());
        assertEquals(bean.getSalary().toString(), newBean.getName());
        PropertyChangeDispatcher dispatcher = PropertyChanger.getCurrent();
        List<PropertyBinder> propertyBinders = dispatcher.getPropertyBinders();
        assertEquals(2, propertyBinders.size());
        PropertyBinder binder1 = propertyBinders.get(0);
        assertFalse(binder1 instanceof AjaxPropertyBinder);
        assertSame(bean, binder1.getListenBean());
        assertSame(newBean, binder1.getUpdateBean());

        PropertyBinder binder2 = propertyBinders.get(1);
        assertFalse(binder2 instanceof AjaxPropertyBinder);
        assertSame(bean, binder2.getListenBean());
        assertSame(newBean, binder2.getUpdateBean());
    }

    public void testNewInstanceWithNonPropertyMethod() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        BeanFactory factory = TestUtils
                        .createBeanFactory(
                                        new Model(new Employee()),
                                        "Bean1 { class: org.apache.wicket.markup.html.form.TextField { args: \"id\" };"
                                                        + " add: Validator; }"
                                                        + "Validator { class: org.apache.wicket.validation.validator.NumberValidator { factoryMethod: range; args: -50, 50 } }");
        TextField field = (TextField)factory.newInstance("Bean1");
        assertEquals(1, field.getValidators().size());
    }

    public void testNewInstanceWithNonPropertyMethodAndTwoArgs() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        BeanFactory factory = TestUtils.createBeanFactory(new Model(new Employee()),
                        "Bean1 { class: TestBean; setStringAndIntProps: \"stringValue\", 123; }");
        TestBean bean = (TestBean)factory.newInstance("Bean1");
        assertEquals("stringValue", bean.getStringProp());
        assertEquals(123, bean.getIntProp());
    }

    public void testNewInstanceWithBeanAsBeanConfigConstructorArg()
    {
        fail("implement args: Bean1");
    }

    public void testResolvePropertyProxyModel() throws Exception
    {
        TestBean bean = new TestBean();
        bean.setIntProp(55);
        IModel model = new Model(bean);
        BeanFactory factory = new BeanFactory(model);
        PropertyProxyModel proxyModel = factory.resolvePropertyProxyModel("$intProp");
        assertEquals(Integer.valueOf(55), proxyModel.getObject());
        assertSame(model, proxyModel.getChainedModel());
    }

    public void testResolvePropertyProxyModelWithInvalidSpec() throws Exception
    {
        BeanFactory factory = new BeanFactory(new Model("test"));
        PropertyProxyModel model = factory.resolvePropertyProxyModel("$!!intProp");
        try {
            model.getObject();
            fail();
        }
        catch (Exception e) {
            // Excpected
        }
    }

    public void testResolveComponentWithPropertyBindings() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester();
        Employee bean = new Employee("false", null, null);
        BeanFactory factory = TestUtils.createBeanFactory(new Model(bean),
                        "Bean1 { class: org.apache.wicket.markup.html.basic.Label;" + " enabled: $name;  }");
        Label label = (Label)factory.resolveComponent("id", new ParameterValueAST("Bean1", false));
        assertFalse(label.isEnabled());
        PropertyChangeDispatcher dispatcher = PropertyChanger.getCurrent();
        List<PropertyBinder> propertyBinders = dispatcher.getPropertyBinders();
        assertEquals(1, propertyBinders.size());
        PropertyBinder binder1 = propertyBinders.get(0);
        assertTrue(binder1 instanceof AjaxPropertyBinder);
        assertSame(bean, binder1.getListenBean());
        assertSame(label, binder1.getUpdateBean());
    }

    public void testResolveComponentWithBean() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester(); // Need for application context
        BeanFactory factory = TestUtils
                        .createBeanFactory("Field { class: org.apache.wicket.markup.html.form.TextField; model: \"test\" }");

        ParameterValueAST valueAST = new ParameterValueAST("Field", false);
        Component component = factory.resolveComponent("id", valueAST);
        assertTrue(component.getClass().getName(), component instanceof FormComponentWrapper);
        Component wrappedComponent = ((FormComponentWrapper)component).getWrappedComponent();
        assertTrue(wrappedComponent.getClass().getName(), wrappedComponent instanceof TextField);
        assertEquals("id", component.getId());
        assertEquals("test", wrappedComponent.getModelObject());
    }

    public void testResolveComponentWithBeanWithImports() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester(); // Need for application context
        BeanFactory factory = TestUtils.createBeanFactory("Field { class: TextField; model: \"test\" }");
        factory.setPackageImports(new String[] { "org.apache.wicket.markup.html.form." });

        ParameterValueAST valueAST = new ParameterValueAST("Field", false);
        Component component = factory.resolveComponent("id", valueAST);
        assertTrue(component.getClass().getName(), component instanceof FormComponentWrapper);
        Component wrappedComponent = ((FormComponentWrapper)component).getWrappedComponent();
        assertTrue(wrappedComponent.getClass().getName(), wrappedComponent instanceof TextField);
        assertEquals("id", component.getId());
        assertEquals("test", wrappedComponent.getModelObject());
    }

    public void testResolveComponentWithInvalidBeanName() throws Exception
    {
        @SuppressWarnings("unused")
        WicketTester tester = new WicketTester(); // Need for application context
        BeanFactory factory = TestUtils
                        .createBeanFactory("Field { class: org.apache.wicket.markup.html.form.TextField; }");

        ParameterValueAST valueAST = new ParameterValueAST("UnknownBeanName", false);
        try {
            factory.resolveComponent("id", valueAST);
        }
        catch (RuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Cannot find bean named"));
        }
    }
}