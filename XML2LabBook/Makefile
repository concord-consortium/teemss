
MODULE = XML2LabBook

DEP_NAMES = wabasdk wextra wgraph LabBook

MW_CLASS= XML2LabBook
PLATFORMS= other
RUN_FORK= other
RUN_PLATFORM = other

PROBELIB_DIR:= $(CURDIR)/../ProbeLib/classes/_CUR_PLATFORM_
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes/_CUR_PLATFORM_
LABBOOK_DIR:=$(CURDIR)/../LabBook/classes/_CUR_PLATFORM_
CCPROBE_DIR:=$(CURDIR)/../CCProbe/classes/_CUR_PLATFORM_
JAR_LIST:= -C $(CURDIR)/classes/other . \
-C $(subst _CUR_PLATFORM_,other,$(CCPROBE_DIR)) org/concord/CCProbe \
-C $(subst _CUR_PLATFORM_,other,$(WGRAPH_DIR)) graph \
-C $(subst _CUR_PLATFORM_,other,$(PROBELIB_DIR)) org/concord/ProbeLib \
-C $(subst _CUR_PLATFORM_,other,$(LABBOOK_DIR)) org/concord/LabBook 

SRC_DIR= src
SKIP_FILES=src/QTManager.java

include ../ReleaseSystem/waba.inc

JAR_BUILD := other

CLASSPATH := $(CLASSPATH):${WGRAPH_DIR}:${LABBOOK_DIR}:${CCPROBE_DIR}:${PROBELIB_DIR}:jars/QTManager.jar:jars/crimson.jar:jars/xalan.jar

all: dependencies main
test: dependencies main

LabBook:
	java -cp $(call RUN_CP,other):$(call PLATFORM_CP,other):classes/other xml2labbook.XML2LabBook merlotTest.xml

