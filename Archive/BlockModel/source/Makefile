WABA_DIR= /home/scytacki/waba/superwaba/vmsrc/classes_java
WEXTRAS_DIR= /home/scytacki/waba/superwaba/wextras/classes
WGRAPH_DIR= /home/scytacki/cvstest/wgraph/classes
PROBESYS_DIR= /home/scytacki/projects/BlockModel/classes


CLASSPATH:= ${CLASSPATH}:${WABA_DIR}:${WEXTRAS_DIR}:${WGRAPH_DIR}:${PROBESYS_DIR}


CLASSES= BlockModel.java CanvasObject.java BlockObject.java Canvas.java \
	Layer.java TabManager.java VProbeObject.java ThermalPatch.java \
	GraphView.java ThermalPlane.java ThermalArea.java ModelObject.java\
	TCPDataChannel.java PSConnection.java GarbageObject.java \
	TabContainer.java PropWindow.java PropObject.java WApplet.java \
	OpenFileWindow.java ListBox.java ModelPage.java DataPage.java

PM_CLASSES= JavaProbeManager.class ProbeManager.class ProbeInfo.class

all: main warp jar

main:
	cd ~/src/ProbeSystem; cp $(PM_CLASSES) ~/projects/BlockModel/classes
	javac -d ../classes -O $(CLASSES)
	cp trash.bmp ../classes

warp: main
	cd ../classes; java wababin.Warp c BlockModel BlockModel.class

jar: main
	jar cvf ../classes/BlockModel.jar trash.bmp *.class 
	jar uvf ../classes/BlockModel.jar -C $(WGRAPH_DIR) graph
	jar uvf ../classes/BlockModel.jar -C $(WEXTRAS_DIR) extra/util/Maths.class
	jar uvf ../classes/BlockModel.jar -C $(WABA_DIR) waba

run:
	cd ../classes; java waba.applet.Applet BlockModel

clean:
	rm -rf ../classes/*
