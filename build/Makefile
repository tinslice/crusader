#!make

mkfile_path := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))
root_path = "$(abspath $(mkfile_path)/../)"

include $(mkfile_path)/.env

test:
	echo ${PROJECT_VERSION}

build:
	mvn -f $(root_path) clean package

install:
	mvn -f $(root_path) clean install

set-version:
	mvn -f $(root_path) versions:set -DnewVersion=${PROJECT_VERSION} -DprocessAllModules=true -DgenerateBackupPoms=false; \
	sed -i -e 's|<crusader.version>.*|<crusader.version>${PROJECT_VERSION}</crusader.version>|g' $(root_path)/examples/multitenant-spring-boot-example/pom.xml

deploy:
	mvn -f $(root_path) deploy
