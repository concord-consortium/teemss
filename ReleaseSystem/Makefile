# You must have your CVSROOT variable set
# You must pass this the TAG variable

ALL_MODULE_NAME= $(MODULE)-all
BUILD_DOC= $(ALL_MODULE_NAME)/$(MODULE)/bin/BUILD.txt
TAG_NAME := `read; echo $$REPLY`

all:
# Check out the release to our directory
# This will ask for a password
	cvs checkout -r $(TAG) $(ALL_MODULE_NAME)
# Build it:  This means it must automatically build all 
#   dependencies the return code is checked
	cd $(ALL_MODULE_NAME)/$(MODULE) && $(MAKE)
# Save tag name
	@echo "The tag of this build is:" > $(BUILD_DOC) 
	@echo "$(TAG)" >> $(BUILD_DOC) 
# ftp the sources to the correct directory
	#ncftpput -u scytacki -m $(REMOTE_HOST) \
$(REMOTE_ROOT)/$(MODULE)/$(TAG) \
#$(ALL_MODULE_NAME)/$(MODULE)/bin/*
