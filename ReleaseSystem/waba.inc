# MW_CLASS
# EXT_CLASSES
# EXT_FILES
# JAR_EXTRAS
# JAR_NAME
# FORKS = superwaba
# RUN_FORK = superwaba
# PLATFORMS = palm wince desk other

WARP_FORK= superwaba
FORK_DIR= /home/scytacki/waba/myforks
WABA_DIR= /home/scytacki/waba/myforks/superwaba/classes_java
WEXTRAS_DIR= /home/scytacki/waba/myforks/superwaba/wextras/classes
CLASSES_DIR= classes
BIN_DIR= bin
JAR_NAME= $(MW_CLASS).jar
WJUMP_DIR= wjump

export PILAINC=/home/scytacki/pila

CLASSPATH= $(WEXTRAS_DIR)
COMPILE_CP= $(subst _CUR_FORK_,$(1),$(CLASSPATH):$(FORK_DIR)/_CUR_FORK_/classes_java)
ARCHIVE_CP= $(subst _CUR_FORK_,$(1),$(CLASSPATH))

CLIENT_IPS= $(foreach num, $(shell cat clients), 4.19.234.$(shell echo $$(($(num) + 130)))) 
FTP_PRE=ncftpput -m -E $(IP_ADDRESS)
FTP_FILES=\
['/program files/waba' $(BIN_DIR)/$(MW_CLASS).wrp $(BIN_DIR)/$(MW_CLASS).lnk] \
['/windows/start menu' $(BIN_DIR)/$(MW_CLASS).lnk]
FTP_COMMAND= echo $(IP_ADDRESS); $(patsubst %],% ; , $(FTP_FILES:[%=$(FTP_PRE) %))

first: all

main: $(CLASSES_DIR) $(PLATFORMS)
ifeq ($(PLATFORMS),)
	javac -classpath $(call COMPILE_CP,waba) -d $(CLASSES_DIR) -O $(wildcard source/*.java)
endif

$(PLATFORMS): $(PLATFORMS:%=$(CLASSES_DIR)/%)
	javac -classpath $(call COMPILE_CP,waba) -d $(CLASSES_DIR)/$@ -O $(wildcard source/*.java) $(wildcard source/$@/*.java)

$(CLASSES_DIR) $(BIN_DIR) $(PLATFORMS:%=$(CLASSES_DIR)/%) $(WJUMP_DIR):
	-mkdir $@

warp: $(BIN_DIR) main
	cd $(CLASSES_DIR)/$(WARP_FORK); java -classpath .:$(call ARCHIVE_CP,$(WARP_FORK)):$(FORK_DIR)/$(WARP_FORK)/classes wababin.Warp c ../../$(BIN_DIR)/$(MW_CLASS) $(MW_CLASS).class

exegen: 
	cd $(CLASSES_DIR); /home/scytacki/waba/sdk/exegen /l $(EXE_CLASS_HEAP_SIZE) /m $(EXE_OBJ_HEAP_SIZE) /s 5000 /t 50 $(MW_CLASS) $(MW_CLASS) $(MW_CLASS); mv $(MW_CLASS).lnk ../bin

jump: classes/waba
	javac -classpath $(CLASSES_DIR)/waba:.:$(call ARCHIVE_CP,waba):$(FORK_DIR)/wabajump/classes:$(CLASSES_DIR)/waba  -d $(CLASSES_DIR)/waba -O $(wildcard source/*.java) $(wildcard source/waba/*.java)
	cd $(WJUMP_DIR); pilrc -R $(MW_CLASS).res $(MW_CLASS).rcp
	cd $(WJUMP_DIR);java -classpath /home/scytacki/jump/jar/jump.jar:.:$(call ARCHIVE_CP,waba):$(FORK_DIR)/wabajump/classes:../$(CLASSES_DIR)/waba Jump -mw $(MW_CLASS) 
	cd $(WJUMP_DIR);pila $(MW_CLASS).asm
	mv $(WJUMP_DIR)/$(MW_CLASS).prc $(BIN_DIR)
	cd $(WJUMP_DIR); rm *.res *.asm *.bin

test: 
	echo $$PILAINC

# this might need to have it's classpath twiddled with
jar: $(BIN_DIR) main
	cd $(CLASSES_DIR)/$(RUN_FORK); jar c0vf ../../$(BIN_DIR)/$(MW_CLASS).jar $(EXT_FILES) *.class -C $(FORK_DIR)/$(RUN_FORK)/classes_java waba/applet/*.class waba/fx/*.class waba/ui/*.class waba/io/*.class waba/sys/*.class waba/util/*.class $(JAR_EXTRAS)

ftp: get-clients
	@$(foreach IP_ADDRESS, $(CLIENT_IPS), $(FTP_COMMAND))

get-clients:
	@echo "Enter the ipaq numbers:"
	@read; echo $$REPLY > clients

RUN-FORMATS = palm wince desk
run-palm = 
run-wince = /w 240 /h 300 /color
run-desk =  /w 640 /h 480 /color

$(RUN-FORMATS:%=run-%) run:
	cd $(CLASSES_DIR)/$(RUN_FORK); java -cp .:$(call COMPILE_CP,$(RUN_FORK)) waba.applet.Applet \
$($@) $(MW_CLASS) 

clean:
	rm -rf $(CLASSES_DIR)/*
	rm -rf $(BIN_DIR)/*

include ../ReleaseSystem/Makefile.inc
