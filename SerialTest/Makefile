
MODULE = SerialTest

DEP_NAMES = wabajump wabasdk wextra

MW_CLASS= SerialTest
PLATFORMS= palm other ce
RUN_FORK= other
RUN_PLATFORM = other
WARP_PLATFORM = ce
JUMP_PLATFORM =palm

EXE_CLASS_HEAP_SIZE = 10000000
EXE_OBJ_HEAP_SIZE = 5000000

include ../ReleaseSystem/waba.inc

JAR_BUILD := other

all: dependencies main warp jar exegen jump
test: dependencies main

