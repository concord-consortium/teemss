#! /bin/bash

for XSL_FILE in analysis.xsl further.xsl hints.xsl intro.xsl investigation_toc.xsl materials.xsl project_toc.xsl safety.xsl think.xsl trial.xsl unit_toc.xsl
 do
echo translating $XSL_FILE
java org.apache.xalan.xslt.Process -in ../unit.xml -xsl $XSL_FILE
done
