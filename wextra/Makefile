SRC_DIRS = cc_extra/event cc_extra/icons cc_extra/ui cc_extra/ui/$(@) cc_extra/util cc_extra/probware cc_extra/probware/probs extra/ui extra/io extra/util \
	wababin
SKIP_FILES = 

PLATFORMS = ce desktop palm

main: $(PLATFORMS)

$(PLATFORMS): classes $(PLATFORMS:%=classes/%)
	javac -classpath ../wabasdk/classes -d classes/$@ $(filter-out $(SKIP_FILES), $(wildcard $(SRC_DIRS:%=%/*.java)))

classes $(PLATFORMS:%=classes/%):
	mkdir $@

icons: $(PLATFORMS:%=classes/%/icons)

$(PLATFORMS:%=classes/%/icons): 
	mkdir -p $@
	cp cc_extra/icons/*.bmp $@
