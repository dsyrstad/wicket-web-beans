Back: [Tabs](Tabs.md) Next: [NestedBeans](NestedBeans.md)

WWB contexts allow you to develop different use cases for the same form and page code. For example, in a simple CRUD application, you might want to allow the user to enter all fields on the add of a record, simply view the record without changing anything, and only allow certain fields to be changed on update. Let's take a look at [ContextBeanPage](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/contexts/ContextBeanPage.java):

Note that we pass "view" to the BeanMetaData constructor. This is the context (or use case) that we want. Of course, you can dynamically come up with this context in the Page constructor depending on parameters passed, etc.

Here's the beanprops file:

```
	# Context Example
	TestBean {
		props: firstName, lastName, EMPTY,
			operand1, operand2, result, -number;
	}
	
	TestBean[view] {
		viewOnly: true;
	}
	
	TestBean[limitedEdit extends view] {
		props: firstName{ viewOnly: false };
	}
```

If you run this page from the examples, you will see that all fields are view-only. When we specify "TestBean`[`view`]`", it tells WWB to extend the default definition for TestBean. The "view" context inherits all of the parameters from the default specification and overrides the bean's "viewOnly" parameter to be true. This causes all of the fields to be view-only.

If you change the following line in ContextBeanPage, from "view" to "limitedEdit", WWB uses the third definition in the beanprops file:

```
        BeanMetaData meta = new BeanMetaData(bean.getClass(), "limitedEdit", this, null, false);
```

This cause just the firstName property to become editable.  The "`[`limitedEdit extends view`]`" tells WWB to extend the "view" context, which in turn extends the default context.

Back: [Tabs](Tabs.md) Next: [NestedBeans](NestedBeans.md)