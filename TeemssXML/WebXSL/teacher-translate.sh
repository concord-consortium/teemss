#! /bin/bash

for XSL_FILE in skills.xsl teacher_intro.xsl teacher_discuss.xsl teacher_background.xsl teacher_timeline.xsl teacher_toc.xsl
 do
echo translating $XSL_FILE
java org.apache.xalan.xslt.Process -in ../unit.xml -xsl $XSL_FILE
done
