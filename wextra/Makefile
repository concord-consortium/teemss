SRC_DIRS = cc_extra/event cc_extra/icons cc_extra/ui cc_extra/ui/_plat_ cc_extra/util cc_extra/probware cc_extra/probware/probs extra/ui extra/io extra/util \
	wababin
SKIP_FILES = 

PLATFORMS = ce desktop palm

main: $(PLATFORMS:%=classes/%) icons

$(PLATFORMS:%=classes/%): classes/% : $(wildcard $(subst _plat_,%,$(SRC_DIRS:%=%/*.java)))
	[ -d $@ ] || mkdir -p $@
	javac -classpath $@:$(CLASSPATH):../wabasdk/classes -d $@ $(filter-out $(SKIP_FILES),$?)
	touch $@

icons: $(PLATFORMS:%=classes/%/icons)

$(PLATFORMS:%=classes/%/icons): 
	mkdir -p $@
	cp cc_extra/icons/*.bmp $@

include ../ReleaseSystem/Makefile.inc

