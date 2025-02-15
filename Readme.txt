This is the Piccolo2D.Java README file

INTRODUCTION
	
Welcome to Piccolo2D! Piccolo2D is a revolutionary way (in the Jazz ZUI 
tradition) to create robust, full-featured graphical applications in 
Java, with striking features such as zooming and multiple representation. 
Piccolo2d.Java is an extensive toolkit based on the Java2D API.

REQUIREMENTS

To run Piccolo2D.Java applications you need to have a Java Runtime
Environment (JRE) or Java Development Kit (JDK) version 1.6 or newer.

To build Piccolo2D.Java you need to have a Java Runtime Environment
(JRE) or Java Development Kit (JDK) version 1.6 or newer, and Apache
Maven version 3.0.5 or newer.

Java Runtime Environment (JRE)
http://java.sun.com/javase/downloads/index.jsp#jre

Java Development Kit (JDK)
http://java.sun.com/javase/downloads/index.jsp#jdk

Apache Maven
http://maven.apache.org/download.html


USING PICCOLO2D.JAVA

To include the Piccolo2D core classes in your project, use a
dependency of

<dependency>
  <groupId>org.piccolo2d</groupId>
  <artifactId>piccolo2d-core</artifactId>
  <version>3.0.1</version>
</dependency>

in your pom.xml.  To include the Piccolo2D core classes and the
Piccolo2D extras classes in your project, use a dependency of

<dependency>
  <groupId>org.piccolo2d</groupId>
  <artifactId>piccolo2d-extras</artifactId>
  <version>3.0.1</version>
</dependency>

in your pom.xml.  If your project does not use maven, simply include
the relevant Piccolo2D jars in your project's classpath.


BUILDING PICCOLO2D.JAVA

To build all the Piccolo2D modules

$ mvn install

To run the Piccolo2D examples runnable jar

$ java -jar examples/target/piccolo2d-examples-{version}-jar-with-dependencies.jar
