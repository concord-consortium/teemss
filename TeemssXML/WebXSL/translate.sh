#! /bin/bash

appdir=`dirname $0`
cd $appdir

for XSL_FILE in analysis.xsl further.xsl hints.xsl intro.xsl investigation_toc.xsl materials.xsl project_toc.xsl safety.xsl think.xsl trial.xsl unit_toc.xsl
 do
echo translating $XSL_FILE
java -cp ../jars/bsf.jar:../jars/js.jar:../jars/xalan.jar:../jars/xerces.jar org.apache.xalan.xslt.Process -in ../unit.xml -xsl $XSL_FILE
done

chmod -R a+w ../html
