#! /bin/bash


for FO_FILE in `ls ../pdf/Energy*.pdf`
 do
echo copying $FO_FILE
cp $FO_FILE `echo $FO_FILE | sed -e 's/Energy/te/g'`
done
