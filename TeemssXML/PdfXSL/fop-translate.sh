#! /bin/bash

CLASSPATH=../jars/bsf.jar:../jars/js.jar:../jars/xalan.jar:../jars/xerces.jar

for XSL_FILE in investigations.xsl teachernotes.xsl
 do
echo translating $XSL_FILE
java -cp $CLASSPATH org.apache.xalan.xslt.Process -in ../unit.xml -xsl $XSL_FILE
done
