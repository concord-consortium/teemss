
MODULE = CCProbe

DEP_NAMES = wabajump wabasdk wextra ProbeLib wgraph LabBook palmtools

MW_CLASS= CCProbe
PLATFORMS= palm other ce
RUN_FORK= other
RUN_PLATFORM = other
WARP_PLATFORM = ce
JUMP_PLATFORM =palm
VERSION_TEMPLATES = source/Version.java.tpl jump-resources/CCProbe.rcp.tpl

EXE_CLASS_HEAP_SIZE = 2000000
EXE_OBJ_HEAP_SIZE = 8000000
WGRAPH_DIR:= $(CURDIR)/../wgraph/classes/_CUR_PLATFORM_
LABBOOK_DIR:=$(CURDIR)/../LabBook/classes/_CUR_PLATFORM_
PROBELIB_DIR:=$(CURDIR)/../ProbeLib/classes/_CUR_PLATFORM_
JAR_LIST:= -C $(CURDIR)/classes/other org/concord/CCProbe \
-C $(subst _CUR_PLATFORM_,other,$(WGRAPH_DIR)) org/concord/waba/graph \
-C $(subst _CUR_PLATFORM_,other,$(LABBOOK_DIR)) org/concord/LabBook \
-C $(subst _CUR_PLATFORM_,other,$(PROBELIB_DIR)) org/concord/ProbeLib 

define POST_BUILD_TAG_CMDS
cd $(BIN_DIR); cp * /home/ftp/pub/ccprobeware/bin
endef

include ../ReleaseSystem/waba.inc

JAR_BUILD := other
CLASSPATH := $(CLASSPATH):${WGRAPH_DIR}:${LABBOOK_DIR}:${PROBELIB_DIR}#:$(CURDIR)/../WFTPClient/classes/_CUR_PLATFORM_

all: dependencies main warp jar exegen jump
test: dependencies main













