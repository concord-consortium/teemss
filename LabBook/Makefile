
MODULE = LabBook

DEP_NAMES = wabajump wabasdk wextra

FORKS= waba superwaba

PLATFORMS= palm other ce
RUN_FORK= palm
RUN_PLATFORM = palm
WARP_PLATFORM = ce
JUMP_PLATFORM =palm

EXE_CLASS_HEAP_SIZE = 5000000
EXE_OBJ_HEAP_SIZE = 2000000
JAR_LIST:= -C $(CURDIR)/classes/other org/concord/LabBook

include ../ReleaseSystem/waba.inc

JAR_BUILD := other
CLASSPATH := $(CLASSPATH):

all: dependencies main
test: dependencies main
