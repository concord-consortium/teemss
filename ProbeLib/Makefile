SRC_DIRS = source source/probes
SKIP_FILES = 

PLATFORMS = ce desktop palm

main:
	$(foreach PLAT, $(PLATFORMS), $(MAKE) PLATFORM=$(PLAT) classes/$(PLAT);)

ifneq ($(PLATFORM),)
classes/$(PLATFORM): $(wildcard $(subst _plat_,$(PLATFORM),$(SRC_DIRS:%=%/*.java)))
	[ -d $@ ] || mkdir -p $@
	javac -classpath $@:../wextra/classes/$(PLATFORM):$(CLASSPATH):../wabasdk/classes -d $@ $?
	touch $@
endif

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc
