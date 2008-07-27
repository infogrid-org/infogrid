#!/bin/sh

for m in {apps,tests,modules}/*/ ; do
	echo Cleaning module $m
	ant -f "$m"build.xml -Dno.deps=1 clean > /dev/null
done;

echo '**** Now building ModuleAdvertisementSerializer ****'
ant -f modules/org.infogrid.module.moduleadvertisementserializer/build.xml jar || exit 1;

echo '**** Now building ALLTESTS ****'
ant -f tests/org.infogrid.ALLTESTS/build.xml jar || exit 1;

echo '**** Now building MeshWorld ****'
ant -f apps/org.infogrid.meshworld/build.xml dist || exit 1;

echo '**** Now building NetMeshWorld ****'
ant -f apps/org.infogrid.meshworld.net/build.xml dist || exit 1;

echo '*** Now building other modules not referenced so far ***'
ant -f modules/org.infogrid.lid.store/build.xml jar || exit 1;
ant -f modules/org.infogrid.lid.openid.store/build.xml jar || exit 1;
ant -f modules/org.infogrid.jee.lid/build.xml jar || exit 1;
ant -f modules/org.infogrid.store.jets3t/build.xml jar || exit 1;
ant -f tests/org.infogrid.comm.smtp.TEST/build.xml jar || exit 1;

echo '**** Now running ALLTESTS ****'
ant -f tests/org.infogrid.ALLTESTS/build.xml run || exit 1;
