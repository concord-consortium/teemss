
MODULE = UltraFastTP
DEP_NAMES = wgraph ProbeLib

WGRAPH_DIR:= $(CURDIR)/../wgraph/classes
PROBELIB_DIR:= $(CURDIR)/../ProbeLib/classes
JAR_EXTRAS= -C $(WGRAPH_DIR) graph -C $(PROBELIB_DIR) .
PLATFORMS= palm other
RUN_FORK= other
RUN_PLATFORM = other
WARP_PLATFORM = other
JUMP_PLATFORM = palm

MW_CLASS= UltraFastTP

EXE_CLASS_HEAP_SIZE = 1000000
EXE_OBJ_HEAP_SIZE = 5000000

include ../ReleaseSystem/waba.inc

CLASSPATH := ${CLASSPATH}:${WGRAPH_DIR}:${PROBELIB_DIR}

all: dependencies main warp jar exegen
