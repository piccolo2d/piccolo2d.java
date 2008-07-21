#!/bin/sh
svnrepo=https://piccolo2d.googlecode.com/svn
#svnrepo=svn://localhost/m/piccolo
svnbase=$svnrepo/piccolo2d.java/branches/mavenize

message="-m mavenize"
message=""
#svn delete $message  $svnbase
#svn cp $message  $svnrepo/piccolo.java/trunk $svnbase

core=piccolo2d
examples=examples
extras=piccolo2dx

##################################################################
## prepare (empty) directories
##################################################################

svn delete $message ./lib
svn delete $message ./usercontrib
svn delete $message ./piccologl
svn delete $message ./build.sh
svn delete $message ./build.bat
svn delete $message ./build.xml
svn delete $message ./license-ant.txt
svn delete $message ./license-junit.html


svn mkdir $message ./parent
# core
svn mkdir $message ./$core
svn mkdir $message ./$core/src
svn mkdir $message ./$core/src/main
svn mkdir $message ./$core/src/test
svn mkdir $message ./$core/src/main/resources
svn mkdir $message ./$core/src/test/resources

# extras
svn mkdir $message ./$extras
svn mkdir $message ./$extras/src
svn mkdir $message ./$extras/src/main
svn mkdir $message ./$extras/src/main/java
svn mkdir $message ./$extras/src/main/resources
svn mkdir $message ./$extras/src/test
svn mkdir $message ./$extras/src/test/java
svn mkdir $message ./$extras/src/test/resources
svn mkdir $message ./$extras/src/test/java/edu
svn mkdir $message ./$extras/src/test/java/edu/umd
svn mkdir $message ./$extras/src/test/java/edu/umd/cs
svn mkdir $message ./$extras/src/test/java/edu/umd/cs/piccolox
svn mkdir $message ./$extras/src/test/java/edu/umd/cs/piccolox/pswing

# examples
svn mkdir $message ./$examples
svn mkdir $message ./$examples/src
svn mkdir $message ./$examples/src/main
svn mkdir $message ./$examples/src/main/java
svn mkdir $message ./$examples/src/main/resources
svn mkdir $message ./$examples/src/test
svn mkdir $message ./$examples/src/test/java
svn mkdir $message ./$examples/src/test/resources

##################################################################
## move sources
##################################################################

# piccolox
svn move $message ./extras/edu/umd/cs/piccolox/pswing/tests ./$extras/src/test/java/edu/umd/cs/piccolox/pswing
svn move $message ./extras/edu ./$extras/src/main/java/
svn move $message ./tests/PFrameTest.java ./$extras/src/test/java
svn move $message ./tests/NotificationCenterTest.java ./$extras/src/test/java
# extras cleanup
svn delete $message ./tests/RunAllUnitTests.java
svn delete $message ./extras

# core
svn move $message ./src ./$core/src/main/java
svn move $message ./tests ./$core/src/test/java

# examples
svn move $message ./examples/edu ./$examples/src/main/java/

##################################################################
## add poms
##################################################################

svn cat $svnbase/pom.xml > ./pom.xml
svn cat $svnbase/parent/pom.xml > ./parent/pom.xml
svn cat $svnbase/piccolo/pom.xml > ./$core/pom.xml
svn cat $svnbase/piccolox/pom.xml > ./$extras/pom.xml
svn cat $svnbase/examples/pom.xml > ./$examples/pom.xml

svn add $message ./pom.xml
svn add $message ./parent/pom.xml
svn add $message ./$core/pom.xml
svn add $message ./$extras/pom.xml
svn add $message ./$examples/pom.xml

