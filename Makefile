# See: https://www.gnu.org/software/make/manual/html_node/Choosing-the-Shell.html
SHELL := /bin/bash

.EXPORT_ALL_VARIABLES:
CWD := $(shell cd -P -- '$(shell dirname -- "$0")' && pwd -P)

PROJECT_NAME := "kafka_for_beginners_udemy"
COMPOSE_FILE := "docker-compose.yml"

# KAFKA
.PHONY: shell
shell:
	docker exec -it broker bash

# DOCKER

.PHONY: up
up:
	docker compose -f ${COMPOSE_FILE} -p ${PROJECT_NAME} up --detach $(opts)
	docker compose -p ${PROJECT_NAME} ps

.PHONY: down
down:
	docker compose -p ${PROJECT_NAME} down -v

.PHONY: restart
restart: down up

.PHONY: logs
logs:
	docker compose -f ${COMPOSE_FILE} -p ${PROJECT_NAME} logs --follow
