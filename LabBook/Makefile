
MODULE = LabBook

FORKS= waba superwaba

MW_CLASS= CCProbe
PLATFORMS= palm other ce
RUN_FORK= palm
RUN_PLATFORM = palm
WARP_PLATFORM = ce
JUMP_PLATFORM =palm

EXE_CLASS_HEAP_SIZE = 10000000
EXE_OBJ_HEAP_SIZE = 2000000
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes/_CUR_PLATFORM_
JAR_LIST:= -C $(CURDIR)/classes/other org/concord/LabBook \
-C $(subst _CUR_PLATFORM_,other,$(WGRAPH_DIR)) graph/

include ../ReleaseSystem/waba.inc

JAR_BUILD := other
CLASSPATH := $(CLASSPATH):${WGRAPH_DIR}

all: dependencies main warp jar exegen
