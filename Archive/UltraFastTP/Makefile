
MODULE = UltraFastTP
DEP_NAMES = wgraph ProbeLib

WGRAPH_DIR:= $(CURDIR)/../wgraph/classes
PROBELIB_DIR:= $(CURDIR)/../ProbeLib/classes
JAR_EXTRAS= -C $(WGRAPH_DIR)/$(RUN_FORK) graph -C $(PROBELIB_DIR)/$(RUN_FORK) .
FORKS= superwaba waba
RUN_FORK= superwaba

MW_CLASS= UltraFastTP

EXE_CLASS_HEAP_SIZE = 1000000
EXE_OBJ_HEAP_SIZE = 5000000

include ../ReleaseSystem/waba.inc

CLASSPATH := ${CLASSPATH}:${WGRAPH_DIR}/_CUR_FORK_:${PROBELIB_DIR}/_CUR_FORK_

all: dependencies main warp jar exegen
