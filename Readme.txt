This is the Piccolo README file

INTRODUCTION
	
Welcome to Piccolo! Piccolo is a revolutionary way (in the Jazz ZUI 
tradition) to create robust, full-featured graphical applications in 
Java, with striking features such as zooming and multiple representation. 
Piccolo is an extensive toolkit based on the Java2D API.

REQUIRMENTS

To run Piccolo applications you need to install the Java 2 Runtime Environment (1.4), 
which can be downloaded from "http://www.javasoft.com/j2se/".

GETTING STARTED

Piccolo comes with four jar files that are located in the ./build directory. 
Note, if you downloaded the source release you will have to build these 
jars yourself, see the file build.xml for instruction. The jar files are;

  ./build/piccolo.jar  - This jar contains the Piccolo 2d graphics framework. 
  ./build/piccolox.jar - This jar contains nonessential, but mabye usefull Piccolo framework code.
  ./build/examples.jar - This jar contains simple examples of Piccolo programs.
  ./build/tests.jar    - This jar contains unit tests for classes in the Piccolo framework.

These jar files (excluding piccolo.jar which is a library) can all be run by 
double clicking with the mouse on the jar file or by running the command 

  	java -jar <jar file name>

MORE INFORMATION

More Piccolo documentation can be found in the ./doc directory of this release.

INSTALLATION NOTES

The Java 2 SDK is a development environment for building applications, 
applets, and components that can be deployed on implementations of the 
Java 2 Platform. 

* The standard 'unzip' utility on Solaris does not respect filename case,
  and leaves files in MS-DOS file format.  If you use unzip on Solaris
  (and possibly other Unix systems), you must specify the -U and -a options.
  i.e: unzip -U -a piccolo.zip
