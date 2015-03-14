Back: NestedBeans  Next: [Tables](Tables.md)

Sometimes a property represents a selection from a set of values. If the property returns a Java enum, WWB makes it really easy to present a drop-down list of choices. Let's look at [EnumBeanPage](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/enums/EnumBeanPage.java):

The [Customer](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/enums/Customer.java) bean references a Java enum type called [CustomerType](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/enums/Customer.java):

And here are the beanprops:

```
	# Enum Bean Example
	Customer {
		cols: 1;
		props: 
			firstName, lastName, 
			customerType{defaultValue: Government}; 
	}
```

The result looks like:

![http://wicket-web-beans.googlecode.com/svn/wiki/images/EnumBeanScreenshot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/EnumBeanScreenshot.png)

We specify the parameter "default" for customerType so that the the drop-down defaults to "Government". If we hadn't specified the default, the drop-down would have defaulted to null (an empty selection).

Later you'll see how to implement custom/dynamic runtime enums where the values can be derived from a database or other source.

Back: NestedBeans  Next: [Tables](Tables.md)