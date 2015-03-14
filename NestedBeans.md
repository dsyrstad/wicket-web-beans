Back: BeanContexts  Next: EnumeratedTypes

It is quite common for beans to reference other beans. WWB handles this for you. Let's look at [NestedBeanPage](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/nested/NestedBeanPage.java).

Note that we only create an instance of [Customer](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/nested/Customer.java). The bill-to and ship-to [Address](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/nested/Address.java) objects are automatically instantiated by WWB.

Here are the beanprops.

```
	# Nested Bean Example
	Customer {
		props: 
			firstName, lastName, 
			billToAddress{colspan: 3}, 
			shipToAddress{colspan: 3};
	}
	
	Address {
		props: 
			address1{colspan: 3}, 
			address2{colspan: 3},
			city, state, zip; 
	}
```


Here we have specifications for the two beans we use: Customer and Address. The "colspan" specification tells BeanGridPanel to span the field across the three grid columns. In other words, this causes the field to span the entire row.

The result looks like:

![http://wicket-web-beans.googlecode.com/svn/wiki/images/NestedBeanScreenshot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/NestedBeanScreenshot.png)

By default, nested objects are presented in a [BeanGridField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/BeanGridField.java) - which is a nested version of the BeanGridPanel used by BeanForm. There are other variations of nested bean fields, such as [BeanInCollapsibleField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/BeanInCollapsibleField.java), [BeanInlineField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/BeanInlineField.java), and [BeanWithParentLabelField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/BeanWithParentLabelField.java). If we wanted to use BeanInCollapsibleField rather than the default, we could say:

```
...	
	Customer {
		props: 
			firstName, lastName, 
			billToAddress{colspan: 3; fieldType: BeanInCollapsibleField }, 
			shipToAddress{colspan: 3; fieldType: BeanInCollapsibleField };
	}
```

Which results in collapsible fields for the Address beans:

![http://wicket-web-beans.googlecode.com/svn/wiki/images/NestedBean2Screenshot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/NestedBean2Screenshot.png)

These fields have a bar across the top that allows you to expand and contract the content.

Back: BeanContexts  Next: EnumeratedTypes