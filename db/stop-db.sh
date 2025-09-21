#!/usr/bin/env bash

CONTAINER_NAME=order-db

docker ps -a -q -f name=${CONTAINER_NAME} | grep -q . && docker stop ${CONTAINER_NAME}