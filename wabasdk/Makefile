SRC_DIRS = waba/applet waba/io waba/io/impl waba/io/impl/Macintosh waba/io/impl/NonMacintosh waba/ui waba/fx waba/sys waba/util cc_util
SKIP_FILES = 

main: classes

classes: $(wildcard $(SRC_DIRS:%=src_desktop/%/*.java))
	[ -d classes ] || mkdir classes
	javac -classpath classes:$(CLASSPATH):../Projects/Macintosh/CodeWarrior/jars/MRJClasses.zip:../Projects/Macintosh/CodeWarrior/jars/RXTXcomm.jar \
-d classes $(filter-out $(SKIP_FILES), $?)
	touch classes

include ../ReleaseSystem/Makefile.inc
