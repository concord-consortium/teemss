
MODULE = LabBook

FORKS= waba superwaba

MW_CLASS= Tester
PLATFORMS= palm other
RUN_FORK= other
RUN_PLATFORM = other
WARP_PLATFORM = other
JUMP_PLATFORM = palm

EXE_CLASS_HEAP_SIZE = 10000000
EXE_OBJ_HEAP_SIZE = 5000000
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes/_CUR_FORK_
JAR_EXTRAS= -C $(CURDIR)/classes/other org/concord/LabBook -C $(WGRAPH_DIR) graph

include ../ReleaseSystem/waba.inc

JAR_BUILD := other
CLASSPATH := ${WGRAPH_DIR}

all: dependencies main warp jar exgen
