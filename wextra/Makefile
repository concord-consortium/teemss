SRC_DIRS = cc_extra/event cc_extra/icons cc_extra/ui cc_extra/ui/$(@) cc_extra/util cc_extra/probware cc_extra/probware/probs extra/ui extra/io extra/util \
	wababin
SKIP_FILES = 

PLATFORMS = ce desktop palm

main: $(PLATFORMS)

$(PLATFORMS): $(PLATFORMS:%=classes/%)
	javac -g -classpath /home/scytacki/cvstest/wabasdk/classes -d classes/$@ $(filter-out $(SKIP_FILES), $(wildcard $(SRC_DIRS:%=%/*.java)))

$(PLATFORMS:%=classes/%) classes:
	mkdir $@
