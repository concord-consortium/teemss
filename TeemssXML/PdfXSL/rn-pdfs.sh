#! /bin/bash

for FO_FILE in `ls *.pdf`
 do
echo copying $FO_FILE
mv $FO_FILE `echo $FO_FILE | 
             sed -e 's/^Energy/te/g' | 
	     sed -e 's/^Motions_and_Forces/mf/g' |
	     sed -e 's/Heating_With_Electricity/Heating/g' |
	     sed -e 's/Multiple_Transformations/Multiple_Trans/g' |
	     sed -e 's/Single_Transformations/Single_Trans/g' |
	     sed -e 's/Potential_Kinetic_Energy/Potential_Kinetic/g'`
done
