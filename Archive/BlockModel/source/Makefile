WABA_DIR= /home/scytacki/waba/superwaba/vmsrc/classes_java
WEXTRAS_DIR= /home/scytacki/waba/superwaba/wextras/classes
WGRAPH_DIR= ../../wgraph/classes
PROBESYS_DIR= ../../ProbeSystem/classes
TARGET_DIR= ../classes


CLASSPATH:= ${CLASSPATH}:${WABA_DIR}:${WEXTRAS_DIR}:${WGRAPH_DIR}:${TARGET_DIR}


CLASSES= BlockModel.java CanvasObject.java BlockObject.java Canvas.java \
	Layer.java TabManager.java VProbeObject.java ThermalPatch.java \
	GraphView.java ThermalPlane.java ThermalArea.java ModelObject.java\
	TCPDataChannel.java PSConnection.java GarbageObject.java \
	TabContainer.java PropWindow.java PropObject.java WApplet.java \
	OpenFileWindow.java ListBox.java ModelPage.java DataPage.java

PM_CLASSES= JavaProbeManager.class ProbeManager.class ProbeInfo.class

all: main warp jar

main:
	cp $(PM_CLASSES:%=$(PROBESYS_DIR)/%) $(TARGET_DIR)
	javac -d ../classes -O $(CLASSES)
	cp trash.bmp ../classes

warp: main
	cd ../classes; java wababin.Warp c BlockModel BlockModel.class

jar: main
	cd ../classes; jar cvf BlockModel.jar trash.bmp *.class 
	jar uvf ../classes/BlockModel.jar -C $(WGRAPH_DIR) graph
	jar uvf ../classes/BlockModel.jar -C $(WEXTRAS_DIR) extra/util/Maths.class
	jar uvf ../classes/BlockModel.jar -C $(WABA_DIR) waba

run:
	cd ../classes; java waba.applet.Applet BlockModel

clean:
	rm -rf ../classes/*
