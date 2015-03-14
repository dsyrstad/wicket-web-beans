Back: [Tables](Tables.md) Next: MoreInformation

There are times when you may want to implement your own Fields. [AbstractField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/AbstractField.java) is the base class for the [Field](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/Field.java) interface. It is recommended that you use the AbstractField base class, or an extension of it, if your field will participate in a BeanForm.

By convention, all Fields must define a constructor with the signature:

```
	public SomeField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
```

In this example, we're going to enhance the com.googlecode.wicketwebbeans.examples.customfields.Address bean to add a Country property. The Country property will be a non-Java enumeration. While Java enums are statically defined at compile-time, there are cases where you need to derive the values at runtime - let's say from a database. To make things simpler for you, WWB defines an interface called [NonJavaEnum](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/model/NonJavaEnum.java). It also has a base class for this interface called [BaseNonJavaEnum](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/model/BaseNonJavaEnum.java). So let's look at our [Country](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/customfields/Country.java) enumeration.

You would normally query the database for countries in the static values() method. However, we don't have a database, so we just hard coded them. The idea is that you would retrieve these values at runtime. The static values() and valueOf() methods are similar to those found on a Java language enum.

Next we need to implement the Field. WWB has a base Field class [EnumField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans/src/main/java/com/googlecode/wicketwebbeans/fields/EnumField.java) that can be used for Java enums and NonJavaEnums. Here's our [CountryField](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/customfields/CountryField.java) that extends EnumField.

Next, we need to register the CountryField type so that whenever WWB sees a Country bean, it will know how to handle it. In this example, this is done in [CustomFieldPage](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/customfields/CustomFieldPage.java).

For simplicity, we add the mapping to ComponentRegistry directly in our Page code. However, you would normally create your instance or sub-class of ComponentRegistry outside of the Page code so that it can be used from multiple pages.

The end result looks like this:

![http://wicket-web-beans.googlecode.com/svn/wiki/images/CustomFieldScreenshot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/CustomFieldScreenshot.png)

Back: [Tables](Tables.md) Next: MoreInformation