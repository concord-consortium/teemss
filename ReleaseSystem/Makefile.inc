# to include this you need to define:
# MODULE
# DEP_NAMES
#

#should check for already built dependencies
#need a list somehow
#export EXP_DEP_NAME = $(EXP_DEP_NAME) $(foreach DEP,$(DEP_NAMES), $(if $(findstring $DEP, $EXP_DEP_NAME), , $(DEP))) 

_DIR_NAMES = $(DEP_NAMES) ReleaseSystem

define MAKE_DEP
$(foreach el,$(_DIR_NAMES),$(MAKE) --no-print-directory -C ../$(el) TAG=`cat tag` $(1)
)
endef

ifeq ($(TAG),)
TAG = $(shell cat tag)
TAG_FILE = tag
endif

.PHONY: cvs-diff cvs-tag build-tag build-local set-version

dependencies: 
	$(call MAKE_DEP, )

release: tag $(DEP_NAMES) ReleaseSystem
	# supposably this means we have taged everything
	# so now we can call the realease makefile which will checkout
	# all these files using our tag and try to build it and publish it
	cd ../ReleaseSystem/ && $(MAKE) release-sub TAG=$(shell cat tag) MODULE=$(MODULE)

build-clean:
	$(call MAKE_DEP, clean)
	$(MAKE) clean

build-local:
	$(call MAKE_DEP, )
	$(MAKE) 
ifneq ($(POST_BUILD_CMDS),)
	$(POST_BUILD_CMDS)
endif

build-tag:
	rm $(basename $(VERSION_TEMPLATES))
	cvs update $(basename $(VERSION_TEMPLATES))
	@$(MAKE) --no-print-directory cvs-diff
	@$(call MAKE_DEP, cvs-diff)
	@$(MAKE) --no-print-directory cvs-update
	@$(call MAKE_DEP, cvs-update)
	$(MAKE) update-build
	$(call MAKE_DEP, )
	$(MAKE) 
	mv new_build build
	echo $(MODULE)_$(subst .,_,$(VERSION_EXP))_`cat build` > tag
	cvs commit -m "build checkin" $(basename $(VERSION_TEMPLATES)) build version tag
	$(call MAKE_DEP, cvs-tag)
	$(MAKE) cvs-tag
ifneq ($(POST_BUILD_CMDS),)
	$(POST_BUILD_CMDS)
endif
ifneq ($(POST_BUILD_TAG_CMDS),)
	$(POST_BUILD_TAG_CMDS)
endif

tag:
	@echo "You must make a version or branch first for " $(MOD_NAME)
	@exit 1

cvs-update:
	@echo UPDATING $(CURDIR) WITH REPOSITORY
	cvs update -d

cvs-diff:
	@echo CHECKING IF $(CURDIR) IS SYNCED WITH REPOSITORY
	@cvs -Q diff > /dev/null || (echo The  directory **$(notdir $(CURDIR))** is different than the repository; test)

#Tag the dependencies with our tag so we can get them back
cvs-tag: $(TAG_FILE)
	cvs tag $(TAG)

VERSION_EXP = $(subst _,.,$(shell cat version))
BUILD_EXP = $(shell cat build)
MAJOR_VER =  $(basename $(basename $(VERSION_EXP)))
MINOR1_VER = $(subst .,,$(suffix $(basename $(VERSION_EXP))))
MINOR2_VER =  $(subst .,,$(suffix $(VERSION_EXP)))

update-build:
	@expr $(BUILD_EXP) + 1 > new_build
	for CUR_TEMPLATE in $(VERSION_TEMPLATES); do \
		sed -e s/_BUILD_VER_/`cat new_build`/ -e s/_MAJOR_VER_/$(MAJOR_VER)/ \
-e s/_MINOR1_VER_/$(MINOR1_VER)/ -e s/_MINOR2_VER_/$(MINOR2_VER)/ \
< $$CUR_TEMPLATE > `echo $$CUR_TEMPLATE | sed -e 's/\.[^\.]*$$//g'`; \
    done

set-version:
	@echo "The old version was (build" $(BUILD_EXP)")"
	@echo $(VERSION_EXP)
	@echo "Enter the new version(tag) Don't use '_' in versions:"
	@read && echo $$REPLY > new_version
	@grep "[0-9]\+\.[0-9]\+\.[0-9]\+" new_version > /dev/null && mv new_version version || \
echo Malformed version number should be XX.XX.XX

branch:

test-version: get-version
	$(update_build)

