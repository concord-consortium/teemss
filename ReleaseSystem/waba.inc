# MW_CLASS
# EXT_CLASSES
# EXT_FILES
# JAR_EXTRAS
# JAR_NAME
# FORKS = superwaba
# RUN_FORK = superwaba
# PLATFORMS = palm wince desk other
# RUN_PLATFORM  (the directory the main class is run from)
# WARP_PLATFOrM  (probably nothing, palm, or other)

JUMP_JAR = $(CURDIR)/../wj_tools/jump.jar
export PILAINC= $(CURDIR)/../wj_tools

WABA_DIR= $(CURDIR)/../wabasdk/classes
WEXTRAS_DIR= $(CURDIR)/../wextra/classes/desktop
WABAJUMP_DIR= $(CURDIR)/../wabajump/classes
CLASSES_DIR= classes
BIN_DIR= bin
JAR_NAME= $(MW_CLASS).jar
WJUMP_DIR= wjump
JAR_BUILD = other

WABA_CLASSES- = $(WABA_DIR)
WABA_CLASSES-other= $(WABA_DIR)
WABA_CLASSES-palm= $(WABAJUMP_DIR)
WABA_CLASSES-ce= $(WABA_DIR)

WEXTRA_CLASSES- = $(CURDIR)/../wextra/classes/desktop
WEXTRA_CLASSES-other= $(CURDIR)/../wextra/classes/desktop
WEXTRA_CLASSES-palm= $(CURDIR)/../wextra/classes/palm
WEXTRA_CLASSES-ce= $(CURDIR)/../wextra/classes/ce


PLATFORM_CP= $(subst _CUR_PLATFORM_,$(1),$(CLASSPATH))
COMPILE_CP= $(WEXTRA_CLASSES-$(1)):$(WABA_CLASSES-$(1))
ARCHIVE_CP= $(WEXTRA_CLASSES-$(1))
RUN_CP= $(WEXTRA_CLASSES-$(1)):$(WABA_CLASSES-)

CLIENT_IPS= $(foreach num, $(shell cat clients), 4.19.234.$(shell echo $$(($(num) + 130)))) 
FTP_PRE=ncftpput -m -E $(IP_ADDRESS)
FTP_FILES=\
['/program files/waba' $(BIN_DIR)/$(MW_CLASS).wrp $(BIN_DIR)/$(MW_CLASS).lnk] \
['/windows/start menu' $(BIN_DIR)/$(MW_CLASS).lnk]
FTP_COMMAND= echo $(IP_ADDRESS); $(patsubst %],% ; , $(FTP_FILES:[%=$(FTP_PRE) %))

first: all

main: $(CLASSES_DIR) $(PLATFORMS)
ifeq ($(PLATFORMS),)
	javac -classpath $(CLASSPATH):$(call COMPILE_CP,other) -d $(CLASSES_DIR) -g $(wildcard source/*.java)
endif

$(PLATFORMS): $(PLATFORMS:%=$(CLASSES_DIR)/%)
	javac -classpath $(call PLATFORM_CP,$(@)):$(call COMPILE_CP,$(@)) -d $(CLASSES_DIR)/$@ -O $(wildcard source/*.java) $(wildcard source/$@/*.java)

$(CLASSES_DIR) $(BIN_DIR) $(WJUMP_DIR):
	-mkdir $@

$(PLATFORMS:%=$(CLASSES_DIR)/%): $(CLASSES_DIR)
	[ -d $@ ] || -mkdir $@

warp: $(BIN_DIR) $(WARP_PLATFORM)
	cd $(CLASSES_DIR)/$(WARP_PLATFORM); java \
-classpath .:$(call PLATFORM_CP,$(WARP_PLATFORM)):$(WEXTRA_CLASSES-ce):$(WEXTRA_CLASSES-other) \
wababin.Warp c ../../$(BIN_DIR)/$(MW_CLASS) $(MW_CLASS).class

exegen: $(BIN_DIR) 
	cd $(CLASSES_DIR); java -classpath $(CLASSPATH):$(WEXTRAS_DIR) wababin.Exegen /l $(EXE_CLASS_HEAP_SIZE) /m $(EXE_OBJ_HEAP_SIZE) /s 5000 /t 50 $(MW_CLASS) $(MW_CLASS) $(MW_CLASS); mv $(MW_CLASS).lnk ../bin

jump: $(BIN_DIR) $(CLASSES_DIR)/$(JUMP_PLATFORM)
	cd $(WJUMP_DIR);pilrc -R $(MW_CLASS).res $(MW_CLASS).rcp
ifeq ($(JUMP_PLATFORM),)
	javac -classpath $(CLASSPATH):$(call ARCHIVE_CP,palm):$(WABAJUMP_DIR)  \
-d $(CLASSES_DIR)/$(JUMP_PLATFORM) \
-O $(wildcard source/*.java)
	cd $(WJUMP_DIR);java -classpath $(JUMP_JAR):$(CLASSPATH):$(call ARCHIVE_CP,palm):$(WABAJUMP_DIR):../$(CLASSES_DIR) \
 	   Jump -mw $(MW_CLASS) 
else
	javac -classpath $(call PLATFORM_CP,$(JUMP_PLATFORM)):$(call ARCHIVE_CP,palm):$(WABAJUMP_DIR)  \
-d $(CLASSES_DIR)/$(JUMP_PLATFORM) \
-O $(wildcard source/*.java) $(wildcard source/$(JUMP_PLATFORM)/*.java)
	cd $(WJUMP_DIR);java -classpath $(call PLATFORM_CP,$(JUMP_PLATFORM)):$(JUMP_JAR):$(call ARCHIVE_CP,palm):$(WABAJUMP_DIR):../$(CLASSES_DIR)/$(JUMP_PLATFORM) Jump -mw $(MW_CLASS)
endif
	cd $(WJUMP_DIR); pila $(MW_CLASS).asm
	mv $(WJUMP_DIR)/$(MW_CLASS).prc $(BIN_DIR)
	cd $(WJUMP_DIR);rm *.res *.asm *.bin

empty=
space= $(empty) $(empty)
fill= ^
array = testing one; testing two; testing 3
replace_space = $(subst $(space),$(fill),$(strip $(1)))
foreach_array = $(foreach element, $(subst ;,$(space),$(call replace_space, $(1))), \
$(call $(2), $(subst $(fill),$(space),$(element))))

command = echo start $(subst $(space), inside ,$(strip $(1))) ending;
test: 
	echo $$PILAINC
	$(call foreach_array, $(array), command)

jar_command=cd $(subst $(space),;jar c0vf $(CURDIR)/$(BIN_DIR)/$(MW_CLASS).jar ,$(strip $(1)));


# JAR_LIST:= $(WABA_DIR) .; $(WEXTRA_CLASSES-) .; $(JAR_LIST)
# this might need to have it's classpath twiddled with
jar: $(BIN_DIR) $(JAR_BUILD)
	cd $(CLASSES_DIR)/$(JAR_BUILD); jar c0vf ../../$(BIN_DIR)/$(MW_CLASS).jar $(EXT_FILES) *.class -C $(WABA_DIR) waba/ -C $(WEXTRA_CLASSES-) org/ -C $(WEXTRA_CLASSES-) wababin/ -C $(WEXTRA_CLASSES-) extra/ \
$(JAR_LIST)
#	$(call foreach_array, $(JAR_LIST), jar_command)

ftp: get-clients
	@$(foreach IP_ADDRESS, $(CLIENT_IPS), $(FTP_COMMAND))

get-clients:
	@echo "Enter the ipaq numbers:"
	@read; echo $$REPLY > clients

RUN-FORMATS = palm wince desk
run-palm = 
run-wince = /w 240 /h 300 /color
run-desk =  /w 640 /h 480 /color
run-palm-fork=palm
run-wince-fork=ce
run-desk-fork=other
run-fork=other

$(RUN-FORMATS:%=run-%) run:
	cd $(CLASSES_DIR)/$($(@)-fork); java -classpath .:$(call PLATFORM_CP,$(RUN_PLATFORM)):$(call RUN_CP,$($(@)-fork)) waba.applet.Applet \
$($@) $(MW_CLASS) 

DEBUG_OPTS = -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=localhost:2112,server=n,suspend=y

debug:
	cd $(CLASSES_DIR)/$(RUN_PLATFORM); java -classpath .:$(call RUN_CP,$(RUN_FORK)) $(DEBUG_OPTS) waba.applet.Applet $(run-desk) $(MW_CLASS)

clean:
	rm -rf $(CLASSES_DIR)/*
	rm -rf $(BIN_DIR)/*

include ../ReleaseSystem/Makefile.inc
