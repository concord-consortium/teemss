
get-version:
	@echo "The old version was:"
	@echo $(subst _,.,$(shell cat tag))
	@echo "Enter the new version(tag) Don't use '_' in versions:"
	@read; echo $$REPLY > tag
	
version: get-version
	@echo $(subst .,_,$(shell cat tag)) > tag
	cvs tag `cat tag`

branch:
