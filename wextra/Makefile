SRC_DIRS = cc_extra/event cc_extra/icons cc_extra/ui cc_extra/ui/$(@) extra/ui extra/io extra/util
SKIP_FILES = 

PLATFORMS = ce desktop palm

main: $(PLATFORMS)

$(PLATFORMS): $(PLATFORMS:%=classes/%)
	javac -classpath /home/scytacki/cvstest/wabasdk/classes -d classes/$@ $(filter-out $(SKIP_FILES), $(wildcard $(SRC_DIRS:%=%/*.java)))

$(PLATFORMS:%=classes/%) classes:
	mkdir $@
