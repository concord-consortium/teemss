# to include this you need to define:
# MODULE
# DEP_NAMES
#

#should check for already built dependencies
#need a list somehow
#export EXP_DEP_NAME = $(EXP_DEP_NAME) $(foreach DEP,$(DEP_NAMES), $(if $(findstring $DEP, $EXP_DEP_NAME), , $(DEP))) 

_DIR_NAMES = $(DEP_NAMES) ReleaseSystem

define MAKE_DEP
$(foreach el,$(_DIR_NAMES),$(MAKE) -C ../$(el) TAG=`cat tag` $(1)
)
endef

ifeq ($(TAG),)
TAG = $(shell cat tag)
TAG_FILE = tag
endif

.PHONY: cvs-diff cvs-tag build-tag set-version

dependencies: $(DEP_NAMES:%=../%)
	
release: tag $(DEP_NAMES) ReleaseSystem
	# supposably this means we have taged everything
	# so now we can call the realease makefile which will checkout
	# all these files using our tag and try to build it and publish it
	cd ../ReleaseSystem/ && $(MAKE) release-sub TAG=$(shell cat tag) MODULE=$(MODULE)

build-tag:
	rm $(basename $(VERSION_TEMPLATE))
	cvs update $(basename $(VERSION_TEMPLATE))
	$(MAKE) cvs-diff
	$(call MAKE_DEP, cvs-diff)
	$(update_build)
	$(call MAKE_DEP, )
	$(MAKE) 
	mv new_build build
	echo $(MODULE)_$(subst .,_,$(VERSION_EXP))_`cat build` > tag
	cvs commit -m "build checkin" $(basename $(VERSION_TEMPLATE)) build version tag
	$(call MAKE_DEP, cvs-tag)
	$(MAKE) cvs-tag
	
tag:
	@echo "You must make a version or branch first for " $(MOD_NAME)
	@exit 1

cvs-diff:
	cvs -Q diff > /dev/null || (echo The  directory **$(notdir $(CURDIR))** is different than the repository; test)
	
#Tag the dependencies with our tag so we can get them back
cvs-tag: $(TAG_FILE)
	cvs tag $(TAG)

VERSION_EXP = $(subst _,.,$(shell cat version))
BUILD_EXP = $(shell cat build)
MAJOR_VER =  $(basename $(basename $(VERSION_EXP)))
MINOR1_VER = $(subst .,,$(suffix $(basename $(VERSION_EXP))))
MINOR2_VER =  $(subst .,,$(suffix $(VERSION_EXP)))

_VERSION_TEMP_ = $(shell cat $(VERSION_TEMPLATE))

define update_build
@expr $(BUILD_EXP) + 1 > new_build
sed -e s/_BUILD_VER_/$(shell cat new_build)/ -e s/_MAJOR_VER_/$(MAJOR_VER)/ \
-e s/_MINOR1_VER_/$(MINOR1_VER)/ -e s/_MINOR2_VER_/$(MINOR2_VER)/ \
< $(VERSION_TEMPLATE) > $(basename $(VERSION_TEMPLATE))
endef

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

