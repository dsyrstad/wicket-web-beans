# Running the Samples #

  1. Build WWB from the source distribution by typing: mvn clean install
  1. cd wicketwebbeans-examples
  1. mvn jetty:run  (runs examples using Jetty)
  1. Point your browser at: http://localhost:8080/wicketwebbeans-examples/WebBeans

You can also deploy the wicketwebbeans-examples.war file to your favorite app server. If you're using Tomcat, we recommend 5.5.23 or later.

This release also contains Databinder support. The examples can be run by following step 0 above and then doing:

  1. cd wicketwebbeans-databinder-examples
  1. mvn jetty:run  (runs examples using Jetty)
  1. Point your browser at: http://localhost:8080/wicketwebbeans-databinder-examples/app

Back to WicketWebBeans