#! /bin/bash


for FO_FILE in `cd ../pdf; ls *.fo`
 do
echo foping $FO_FILE
cd ../fop/Fop-0.20.1/; ./fop.sh -fo ../../pdf/$FO_FILE -pdf \
../../pdf/`echo $FO_FILE | sed -e 's/.fo/.pdf/g'`
done
