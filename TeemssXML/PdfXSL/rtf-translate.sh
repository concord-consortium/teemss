#! /bin/bash


cd ../pdf

for FO_FILE in `ls *-rtf.fo`
 do
echo rtfing $FO_FILE
java -cp ../fop/jfor/jfor-0.5.1.jar:../jars/xerces.jar \
org.jfor.jfor.main.CmdLineConverter $FO_FILE \
`echo $FO_FILE | sed -e 's/-rtf\.fo/.rtf/g'`
done
