# Release Process #

**This needs updating for googlecode**


  * Make sure you followed: [Build & Publising Notes|Maven Build & Publising](Maven.md)
  * Change version #s
  * Deploy to wicketwebbeans repository
  * Tag with Maven version #
  * Update web site (see Maven notes)
  * Create release notes
  * Wait for Maven central repository replication

  * mvn clean ; zip full source tree: zip -r /tmp/wicketwebbeans-fullsrc-1.0-rc2.zip .

  * Upload bin/src/javadoc to SF.net
  * Change pom version #'s to next version + SNAPSHOT, build and deploy new snapshot as described in [Build & Publising Notes|Maven Build & Publising](Maven.md)
  * Announce to mailing lists, <[wicket-web-beans@googlecode.com](mailto:wicket-web-beans@googlecode.com)>, <[users@wicket.apache.org](mailto:users@wicket.apache.org)>.
  * Announce to SF news, Freshmeat
  * Take a break