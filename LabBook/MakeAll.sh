#! /bin/sh

echo "BUILDING WABAJUMP"
cd ../wabajump
rm -f -R classes
make
echo "-------------------------"
echo "BUILDING WABASDK"
cd ../wabasdk
rm -f -R classes
make
echo "-------------------------"
echo "BUILDING WEXTRA"
cd ../wextra
rm -f -R classes
make
echo "-------------------------"
echo "BUILDING WGRAPH" 
cd ../wgraph
rm -f -R classes
make     
echo "-------------------------"
cd ../LabBook
rm -f -R classes
rm -f bin/CCProbe.*
rm -f wjump/CCProbe.asm
rm -f wjump/*.bin
echo "BUILDING PALM"
make jump
echo "BUILDING CE"
make warp
make exegen
echo "BUILDING DESKTOP"
make other
make jar
echo "UPLOADING FILES"
#ncftpput -u dima -p logal99 web.concord.org /home/ftp/pub/ccprobeware/bin bin/CCProbe.* > /dev/null






