SRC_DIRS = source/ui source/ui/_plat_ source/util source/io source/event icons source/wababin 
SKIP_FILES = 

PLATFORMS = ce desktop palm
CLASSPATH += :$(shell for jar in $(wildcard ../jars/*); do echo -n $$jar:; done)

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
	cp icons/*.bmp $@

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc

