
MODULE = UltraFastTP
DEP_NAMES = wgraph ProbeSystem BlockModel

include ../ReleaseSystem/waba.inc

WGRAPH_DIR:= $(CURDIR)/../wgraph/classes
PROBESYS_DIR:= $(CURDIR)/../ProbeSystem/classes
BLOCKMODEL_DIR:= $(CURDIR)/../BlockModel/classes
JAR_EXTRAS= -C $(WGRAPH_DIR) graph -C $(WEXTRAS_DIR) extra/util/Maths.class

CLASSPATH += ${CLASSPATH}:${WGRAPH_DIR}:.

PM_CLASSES= JavaProbeManager.class ProbeManager.class ProbeInfo.class
BM_CLASSES= TCPDataChannel.class

EXT_CLASSES= $(PM_CLASSES:%=$(PROBESYS_DIR)/%) $(BM_CLASSES:%=$(BLOCKMODEL_DIR)/%)
MW_CLASS= UltraFastTP

EXE_CLASS_HEAP_SIZE = 10000000
EXE_OBJ_HEAP_SIZE = 5000000

all: dependencies main warp jar exegen


