#! /bin/bash


cd ../pdf

for FO_FILE in `ls *.fo`
 do
echo rtfing $FO_FILE
java -cp ../fop/jfor/jfor-0.5.1.jar:/home/scytacki/Concord/lib/xerces.jar \
org.jfor.jfor.main.CmdLineConverter $FO_FILE \
`echo $FO_FILE | sed -e 's/\.fo/.rtf/g'`
done
