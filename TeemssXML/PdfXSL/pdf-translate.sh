#! /bin/bash


cd ../fop/Fop-0.20.1

for FO_FILE in `cd ../../pdf; ls *.fo`
 do
echo foping $FO_FILE
./fop.sh -fo ../../pdf/$FO_FILE -pdf \
../../pdf/`echo $FO_FILE | sed -e 's/\.fo/.pdf/g'`
done
