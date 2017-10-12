#!/bin/bash
SITEDIR=/home/torysa/Site/Sites/lineal/
UPDIR=torys:~/www/toryanderson/lineal
lein clean
lein uberjar
cp target/cljsbuild/public/js/app.js $SITEDIR
echo "app.js moved"
cp resources/public/css/screen.css $SITEDIR
echo "style sheets moved"
rsync -av $SITEDIR $UPDIR
echo "update complete"
