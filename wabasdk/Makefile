
#
# Add these directories to SRC_DIRS if you want to compile the serial implementation 
# code:
#IMPL_DIRS= waba/io/impl waba/io/impl/Macintosh waba/io/impl/NonMacintosh
IMPL_DIRS=

#
#  Note: to compile the waba/io/impl code  currently requires: 
#   MRJClasses.zip  -- Macintosh MRJ support
#   comm.jar        -- java serial port api 
#   RXTX.jar        -- serial port implementation for linux and MacOSX, and soon we'll use
#                      on windows. 
#  Put these in your ../jar directory
PROJECT_JARS=$(shell for jar in $(wildcard ../jars/*) ; do echo -n $$jar: ; done)

SRC_DIRS = waba/applet waba/io  waba/ui waba/fx waba/sys waba/util cc_util $(IMPL_DIRS)

SKIP_FILES = 

main: classes

classes: $(wildcard $(SRC_DIRS:%=src_desktop/%/*.java))
	[ -d classes ] || mkdir classes
	javac -classpath classes:$(CLASSPATH):jars/serialImpl.jar:$(PROJECT_JARS) \
-d classes $(filter-out $(SKIP_FILES), $?)
	touch classes

serialImpl: classes
	mkdir _rxtx_jar_
	cd _rxtx_jar_; jar xvf ../../jars/RXTXcomm.jar
	jar cvf jars/serialImpl.jar -C classes waba/io/impl waba/io/impl/Macintosh waba/io/impl/NonMacintosh -C _rxtx_jar_ gnu
	rm -r _rxtx_jar_

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc
