SRC_DIRS = fx io sys ui util

all: classes
	javac -deprecation -classpath $(CLASSPATH):../wj_tools/jump.jar:.. -d classes  $(wildcard $(SRC_DIRS:%=source/waba/%/*.java))

classes: 
	mkdir classes

include ../ReleaseSystem/Makefile.inc
