# MW_CLASS
# EXT_CLASSES
# EXT_FILES
# JAR_EXTRAS
# JAR_NAME

WABA_DIR= /home/scytacki/waba/superwaba/vmsrc/classes_java
WEXTRAS_DIR= /home/scytacki/waba/superwaba/wextras/classes
CLASSES_DIR= classes
BIN_DIR= bin
JAR_NAME= $(MW_CLASS).jar

export CLASSPATH := ${WABA_DIR}:${WEXTRAS_DIR}:${CLASSES_DIR}

CLASSES= *.java

first: all

main: $(CLASSES_DIR)
	cp  $(EXT_CLASSES) $(CLASSES_DIR)
	javac -d $(CLASSES_DIR) -O $(CLASSES:%=source/%)
ifneq ($(EXT_FILES),)
	cp $(EXT_FILES) $(CLASSES_DIR)
endif

$(CLASSES_DIR) $(BIN_DIR):
	-mkdir $@

warp: $(BIN_DIR) main
	cd $(CLASSES_DIR); java wababin.Warp c ../$(BIN_DIR)/$(MW_CLASS) $(MW_CLASS).class

jar: $(BIN_DIR) main
	cd $(CLASSES_DIR); jar cvf ../$(BIN_DIR)/$(MW_CLASS).jar $(EXT_FILES) *.class -C $(WABA_DIR) waba $(JAR_EXTRAS)

run:
	cd $(CLASSES_DIR); java waba.applet.Applet $(MW_CLASS)

clean:
	rm -rf $(CLASSES_DIR)/*
	rm -rf $(BIN_DIR)/*

include ../ReleaseSystem/Makefile.inc
