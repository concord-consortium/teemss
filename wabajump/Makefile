SRC_DIRS = fx io sys ui util

all: classes

classes: $(wildcard $(SRC_DIRS:%=source/waba/%/*.java))
	[ -d classes ] || mkdir classes
	javac -classpath classes:$(CLASSPATH):../wj_tools/jump.jar -d classes $?
	touch classes

include ../ReleaseSystem/Makefile.inc
