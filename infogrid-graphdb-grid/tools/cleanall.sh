#!/bin/sh

for m in {apps,tests,modules}/*/ ; do
	echo Cleaning module $m
	ant -f "$m"build.xml -Dno.deps=1 clean > /dev/null
done;
