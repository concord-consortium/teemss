SRC_DIRS = cc_extra/event cc_extra/icons cc_extra/ui cc_extra/ui/_plat_ cc_extra/util cc_extra/probware cc_extra/probware/probs extra/ui extra/io extra/util \
	wababin
SKIP_FILES = 

PLATFORMS = ce desktop palm

main:
	$(foreach PLAT, $(PLATFORMS), $(MAKE) PLATFORM=$(PLAT) classes/$(PLAT);)
	$(MAKE) icons

ifneq ($(PLATFORM),)
classes/$(PLATFORM): $(wildcard $(subst _plat_,$(PLATFORM),$(SRC_DIRS:%=%/*.java)))
	[ -d $@ ] || mkdir -p $@
	javac -classpath $@:$(CLASSPATH):../wabasdk/classes -d $@ $(filter-out $(SKIP_FILES),$?)
	touch $@
endif

icons: $(PLATFORMS:%=classes/%/icons)

$(PLATFORMS:%=classes/%/icons): 
	mkdir -p $@
	cp cc_extra/icons/*.bmp $@

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc

