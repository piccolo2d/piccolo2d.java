#!/bin/sh
#
# Run ragel on SvgParsePath to create
# - java source (state machine)
# - state machine svg 
# - state machine png 
#

cwd=`pwd`
src=src/main/ragel
target=target/ragel
target_java=src/main/java/org/piccolo2d/svg

cd `dirname $0`/../../..
mkdir -p $target
mkdir -p $target_java

file=PointParser
opts="-e"
ragel $opts -p -V -o $target/$file.dot $src/$file.rl
#dot -o $target/$file.png -Tpng $target/$file.dot
dot -o $target/$file.svg -Tsvg $target/$file.dot
ragel $opts -s -J -o $target_java/$file.java $src/$file.rl

file=TrafoParser
opts="-e"
ragel $opts -p -V -o $target/$file.dot $src/$file.rl
#dot -o $target/$file.png -Tpng $target/$file.dot
dot -o $target/$file.svg -Tsvg $target/$file.dot
ragel $opts -s -J -o $target_java/$file.java $src/$file.rl

file=PathParser
opts="-e"
ragel $opts -p -V -o $target/$file.dot $src/$file.rl
#dot -o $target/$file.png -Tpng $target/$file.dot
#dot -o $target/$file.svg -Tsvg $target/$file.dot
ragel $opts -s -J -o $target_java/$file.java $src/$file.rl

file=CssParser0
opts="-e"
ragel $opts -p -V -o $target/$file.dot $src/$file.rl
#dot -o $target/$file.png -Tpng $target/$file.dot
dot -o $target/$file.svg -Tsvg $target/$file.dot
ragel $opts -s -J -o $target_java/css0/$file.java $src/$file.rl

file=CssParser
opts="-e"
ragel $opts -p -V -o $target/$file.dot $src/$file.rl
#dot -o $target/$file.png -Tpng $target/$file.dot
dot -o $target/$file.svg -Tsvg $target/$file.dot
ragel $opts -s -J -o $target_java/css/$file.java $src/$file.rl

cd $cwd
