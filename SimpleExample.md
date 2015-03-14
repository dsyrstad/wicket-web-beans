Back: [Requirements](Requirements.md) Next: [Actions](Actions.md)

Let's take a look at a simple bean from the examples: [TestBean.java](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/simple/TestBean.java)

This is a Java Bean compliant POJO. As most people know, getters start with "get" or "is" and setters start with "set". Java Beans also require a public no-argument constructor, which we have. All beans must be Serializable by Wicket convention.

This bean also implements PropertyChangeListeners and Events. This is an optional part of the Java Beans spec. However, if your bean implements add/removePropertyChangeListener(), BeanForm will automatically register itself as a listener to your bean. In this example, we're going to use PropertyChangeEvents to notify BeanForm when dependent properties change.

If you don't implement PropertyChangeListeners, BeanForm already knows if a single property changes from a change on the form. For example, setFirstName() automatically changes the input value to upper case. BeanForm knows to refresh this field on the form because the user changed it. If you type "xyzzy" in the First Name field and tab or click away from the field, the field is sent to the bean and the field is dynamically refreshed to it's new value "XYZZY".

Before we get too deep, let's take a look at the Wicket page [SimpleBeanPage.java](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/simple/SimpleBeanPage.java) and HTML:

```
	<html xmlns:wicket>
	<head>
	<wicket:head>
	    <wicket:link><link href="bean.css" type="text/css" rel="stylesheet" ></link></wicket:link>
		<title>Simple Bean Page</title>
	</wicket:head>
	</head>
	<body >
		<span wicket:id="beanForm"></span>
	</body>
	</html>
```


You can see this is pretty simple. There is only a single component added to the page - "beanForm" - which is an instance of [BeanForm](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/containers/BeanForm.java). You can see in the constructor that we create an instance of TestBean which is passed to BeanForm. We also create an instance of [BeanMetaData](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/model/BeanMetaData.java). BeanMetaData reflects on the TestBean class to derive the fields and actions for the form.

Note that you can also pass a Wicket IModel that contains your bean, rather than the bean itself, to BeanForm. If the bean is a List, or the IModel contains a List, the results will be displayed as a data table.

If you bring up this page in a browser (see RunningSamples), you will see something like:

![http://wicket-web-beans.googlecode.com/svn/wiki/images/SimpleBeanScreenShot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/SimpleBeanScreenShot.png)

Note that "Result" is not editable because there is no setter method on the bean. Also, if you type numbers into Operand 1 and Operand 2, you'll see that the Result field automatically is calculated and updated. The calculation is done by TestBean and the PropertyChangeEvents are notifying WWB to update the result field.

As we mentioned, BeanMetaData represents the metadata for a bean properties and actions. By default, the metadata originates by convention:

  * Label names for properties are derived from the property name. E.g., "customerName" becomes "Customer Name"; "address2" becomes "Address 2" (or from the JavaBean BeanInfo "displayName").
  * Field components for the Java primitive/wrapper types, enum types, java.util.Date and java.util.Calendar thier sub-classes, and java.util.Lists are pre-configured.
  * All JavaBean properties are displayed. Non-JavaBean methods are not displayed.
  * All fields are editable if viewOnly is false (see the BeanMetaData constructor). Otherwise they are all view-only.
  * If a property is not writable, it is displayed view-only.
  * If your beans use JPA or JDO, other property aspects can be controlled by annotations available in these APIs.
  * Actions are derived from action methods defined on the component (e.g., the Page). See Actions below.
  * All fields are displayed in alphabetical order by property name.
  * All fields are displayed in a single page. (Known as the "default tab", but the tab is hidden).

Field types are deduced from the property's Java type. The mappings from the property's Java class to the [Field](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/Field.java) type is done by [ComponentRegistry](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/model/ComponentRegistry.java). ComponentRegistry has mappings for most common types and more types can be added if necessary. Also, Field types may be overridden for a specific property in the "beanprops" file, which we'll discuss later.

Back: [Requirements](Requirements.md) Next: [Actions](Actions.md)