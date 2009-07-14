#!/bin/sh
###########################################################
# Create and deploy the current maven site (snapshot) #####
###########################################################
#
# As it doesn't seem sensible to deploy the maven site to
# GoogleCode via
#  
# <extension>
#	<groupId>org.apache.maven.wagon</groupId>
#	<artifactId>wagon-webdav</artifactId>
#	<version>1.0-beta-2</version>
# </extension>
# 
# as this causes thousands of commits, we'll try another way.
#

base=https://piccolo2d.googlecode.com/svn/site/doc/piccolo2d.java
version=1.3-SNAPSHOT

cwd=`pwd`
tmp=target/site-stage
msg="--message \"$0\""

svn="svn"
#svn="echo svn"
cut="cut --characters=9-"
xargs="xargs --no-run-if-empty"
if [[ "$MACHTYPE" == "i386-apple-darwin9.0" ]] ; then
	echo "Huh, I'm running on a Mac."
	cut="cut -c 9-"
	xargs="xargs"
fi

# create a fresh site
#mvn clean install site

$svn mkdir $msg $base/release-$version 2> /dev/null
ls -Al target
# checkout only if not there yet.
if [ -d $tmp ]; then
	echo "Revert and update $tmp"
	$svn revert --recursive $tmp
	$svn update $tmp
else
	echo "Checkout $base/release-$version"
	echo "... go, get some tea..."
	$svn checkout $base/release-$version $tmp > /dev/null
fi

echo "Copy generated site to $tmp"
cp -r target/site/* $tmp/piccolo2d
cp -r core/target/site/* $tmp/piccolo2d-core
cp -r extras/target/site/* $tmp/piccolo2d-extras
cp -r examples/target/site/* $tmp/piccolo2d-examples

echo "Issue local svn delete/add commands"
cd $tmp
find . -type f | $xargs svn status --verbose 2> /dev/null | egrep -e "^\!" | $cut | $xargs $svn delete
find . -mindepth 1 -type d | $xargs svn status --verbose 2> /dev/null | egrep -e "^\?" | $cut | $xargs $svn add
find . -type f | $xargs svn status --verbose 2> /dev/null | egrep -e "^\?" | $cut | $xargs $svn add --non-recursive

echo "Set mime-type"
find . -name "*.html" | xargs $svn propset svn:mime-type text/html
find . -name "*.xml" | xargs $svn propset svn:mime-type text/xml
find . -name "*.css" | xargs $svn propset svn:mime-type text/css
find . -name "*.gif" | xargs $svn propset svn:mime-type image/gif
find . -name "*.png" | xargs $svn propset svn:mime-type image/png

# go back where we came from
cd $cwd

echo "###########################################################"
echo " the site is generated and ready to upload."
echo " "
echo " Review the changes:"
echo " $ svn status $tmp"
echo " "
echo " Do the upload:"
echo " $ svn commit $tmp"
echo " "
DROP=~/Dropbox/Public/piccolo2d.java
echo " Or rsync it (without .svn directories) to $DROP:"
echo " $ rsync --exclude=.svn --exclude=.DS_Store -a --delete --delete-excluded --compress-level=0 --progress target/site-stage $DROP"
