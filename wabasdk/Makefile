SRC_DIRS = waba/applet waba/io  waba/ui waba/fx waba/sys waba/util cc_util 

#
# Add these directories to SRC_DIRS if you want to compile the serial implementation 
# code:
# waba/io/impl waba/io/impl/Macintosh waba/io/impl/NonMacintosh
#
#  Note: to compile the waba/io/impl code  currently requires: 
#   MRJClasses.zip  -- Macintosh MRJ support
#   comm.jar        -- java serial port api 
#   RXTX.jar        -- serial port implementation for linux and MacOSX, and soon we'll use
#                      on windows. 
#  To include these you should define this variable, or put the jars  
#    in your default classpath
#IMPL_CLASSPATH=../Projects/Macintosh/CodeWarrior/jars/MRJClasses.zip:../Projects/Macintosh/CodeWarrior/jars/RXTXcomm.jar:../Projects/Macintosh/CodeWarrior/jars/comm.jar 
IMPL_CLASSPATH=

SKIP_FILES = 

main: classes

classes: $(wildcard $(SRC_DIRS:%=src_desktop/%/*.java))
	[ -d classes ] || mkdir classes
	javac -classpath classes:$(CLASSPATH):jars/serialImpl.jar:$(IMPL_CLASSPATH) \
-d classes $(filter-out $(SKIP_FILES), $?)
	touch classes

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc
