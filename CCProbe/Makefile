
MODULE = CCProbe

DEP_NAMES = wabajump wabasdk wextra wgraph LabBook

MW_CLASS= CCProbe
PLATFORMS= palm other ce
RUN_FORK= other
RUN_PLATFORM = other
WARP_PLATFORM = ce
JUMP_PLATFORM =palm
VERSION_TEMPLATE = source/Version.java.tpl

EXE_CLASS_HEAP_SIZE = 5000000
EXE_OBJ_HEAP_SIZE = 2000000
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes/_CUR_PLATFORM_
LABBOOK_DIR:=$(CURDIR)/../LabBook/classes/_CUR_PLATFORM_
JAR_LIST:= -C $(CURDIR)/classes/other org/concord/CCProbe \
-C $(subst _CUR_PLATFORM_,other,$(WGRAPH_DIR)) graph \
-C $(subst _CUR_PLATFORM_,other,$(LABBOOK_DIR)) org/concord/LabBook 

include ../ReleaseSystem/waba.inc

JAR_BUILD := other
CLASSPATH := $(CLASSPATH):${WGRAPH_DIR}:${LABBOOK_DIR}

all: dependencies main warp jar exegen jump
test: dependencies main
