#!/bin/sh

for f in `find ./web/v -name '*.tld' -print`; do
    export g=`echo $f | sed 's#^\./web/v/##g'`;
    cp ../../modules/*/src/$g ./web/v/$g
done;
