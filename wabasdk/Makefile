SRC_DIRS = waba/applet waba/io waba/io/impl waba/io/impl/Macintosh waba/io/impl/NonMacintosh waba/ui waba/fx waba/sys waba/util cc_util
SKIP_FILES = 

main: classes
	javac -classpath $(CLASSPATH):../Projects/Macintosh/CodeWarrior/jars/MRJClasses.zip -d classes \
$(filter-out $(SKIP_FILES), $(wildcard $(SRC_DIRS:%=src_desktop/%/*.java)))

classes:
	mkdir classes
