Back: [SimpleExample](SimpleExample.md) Next: [Customization](Customization.md)

It's simple to add actions to your form. Here's the code for [ActionBeanPage](http://code.google.com/p/wicket-web-beans/source/browse/trunk/wicketwebbeans-examples/src/main/java/com/googlecode/wicketwebbeans/examples/actions/ActionBeanPage.java).

Note the methods in ActionBeanPage.java: save, cancel, and clearLastName. These methods define the actions available from your Page (or whatever component the form is embedded in). By default, the action's label is derived from the method name. Every action method must have the following method signature:

```
    public void actionName(AjaxRequestTarget target, Form form, <BeanClassName> bean) 
    { 
        ...
    }
```

The target parameter normally null. The Wicket Form is always passed in "form" and the bean that the action applies to is in "bean". _BeanClassName_ is the class name of the bean to which the action will apply. Optionally this may be "Object" if the action should apply to all beans on your page. WWB only automatically matches actions whose "bean" parameter type matches the bean in question or Object.

This configuration yields the following page:
![http://wicket-web-beans.googlecode.com/svn/wiki/images/ActionBeanSimpleScreenshot.png](http://wicket-web-beans.googlecode.com/svn/wiki/images/ActionBeanSimpleScreenshot.png)


Back: [SimpleExample](SimpleExample.md) Next: [Customization](Customization.md)