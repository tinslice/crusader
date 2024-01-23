#!make

.DEFAULT_GOAL := help
.PHONY: help

mkfile_path := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))
root_path = "$(abspath $(mkfile_path)/../)"

mvn_cmd := "$(root_path)/mvnw"

PROJECT_VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
include $(mkfile_path)/.env

help:
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
	| sed -n 's/^\(.*\): \(.*\)##\(.*\)/\1\n \3/p'

get-version: ## get project version
	@echo $(PROJECT_VERSION)

package: ## build maven project
	$(mvn_cmd) --batch-mode -U -f $(root_path) clean package

install: ## build maven project and install in local maven repository
	$(mvn_cmd) --batch-mode -U -f $(root_path) clean install

set-version: ## set new project version. Run with 'make set-version PROJECT_VERSION=new_project_version'
	$(mvn_cmd) -f $(root_path) versions:set -DnewVersion=${PROJECT_VERSION} -DprocessAllModules=true -DgenerateBackupPoms=false versions:commit; \
	sed -i -e 's|<crusader.version>.*|<crusader.version>${PROJECT_VERSION}</crusader.version>|g' $(root_path)/examples/multitenant-spring-boot-example/pom.xml

deploy: ## deploy to maven repository
	$(mvn_cmd) --batch-mode -U -f $(root_path) clean deploy -P oss,release
