#!/bin/bash
SITEDIR=/home/torysa/Site/Sites/lineal/
SITEDIR_FILES=$SITEDIRindex_files/
UPDIR=torys:www/toryanderson/lineal
lein clean
lein uberjar
cp target/cljsbuild/public/js/app.js $SITEDIR_FILES
echo "app.js moved"
cp resources/public/css/style.css $SITEDIR_FILES
echo "style sheets moved"
rsync -av $SITEDIR $UPDIR
echo "update complete"
