
MODULE = LabBook

FORKS= waba superwaba
RUN_FORK=

MW_CLASS= Tester

EXE_CLASS_HEAP_SIZE = 10000000
EXE_OBJ_HEAP_SIZE = 5000000
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes

include ../ReleaseSystem/waba.inc

CLASSPATH := ${CLASSPATH}:${WGRAPH_DIR}

all: dependencies main warp jar exgen
