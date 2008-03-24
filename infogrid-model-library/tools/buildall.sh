#!/bin/sh

echo '**** Now building ModuleAdvertisementSerializer ****'
ant -f modules/org.infogrid.module.moduleadvertisementserializer/build.xml jar || exit 1;

echo '**** Now building ALLTESTS ****'
ant -f tests/org.infogrid.ALLTESTS/build.xml jar || exit 1;

echo '**** Now building MeshWorld ****'
ant -f apps/org.infogrid.meshworld/build.xml dist || exit 1;

echo '**** Now building NetMeshWorld ****'
ant -f apps/org.infogrid.meshworld.net/build.xml dist || exit 1;

echo '**** Now running ALLTESTS ****'
ant -f tests/org.infogrid.ALLTESTS/build.xml run || exit 1;
