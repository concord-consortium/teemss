SRC_DIRS = source source/probes
SKIP_FILES = 

PLATFORMS = ce other palm

main:
	$(foreach PLAT, $(PLATFORMS), $(MAKE) PLATFORM=$(PLAT) classes/$(PLAT);)

ifneq ($(PLATFORM),)
classes/$(PLATFORM): $(wildcard $(subst _plat_,$(PLATFORM),$(SRC_DIRS:%=%/*.java)))
	[ -d $@ ] || mkdir -p $@
ifeq ($(PLATFORM),other)
	javac -classpath $@:../wextra/classes/desktop:$(CLASSPATH):../wabasdk/classes -d $@ $?
else
	javac -classpath $@:../wextra/classes/$(PLATFORM):$(CLASSPATH):../wabasdk/classes -d $@ $?
endif
	touch $@
endif

clean:
	rm -rf classes

include ../ReleaseSystem/Makefile.inc
