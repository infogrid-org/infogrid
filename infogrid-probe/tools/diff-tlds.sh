#!/bin/sh

for f in `find ./web/v -name '*.tld' -print`; do
    export g=`echo $f | sed 's#^\./web/v/##g'`;
    echo '---- now looking at:' $g
    diff ../../modules/*/src/$g ./web/v/$g
done;
