java -cp ..\..\jars\bsf.jar;..\..\jars\js.jar;..\..\jars\xalan.jar;..\..\jars\xerces.jar org.apache.xalan.xslt.Process -in ..\unit.xml -xsl labbook.xsl -out labbook.xml
sed -f aircart-insert labbook.xml > temp
del labbook.xml
rename temp labbook.xml