#!/usr/bin/env bash

#
# https://www.postgresql.org/docs/17/release-17-6.html
#
DOCKER_IMAGE_NAME=postgres:17.6
CONTAINER_NAME=order-db

##############################################################################################
#
# Run docker
#
# Setting --memory and --memory-swap with the same value
# disable SWAP completely (https://docs.docker.com/config/containers/resource_constraints/#prevent-a-container-from-using-swap)
#
##############################################################################################

docker ps -a -q -f name=${CONTAINER_NAME} | grep -q . && docker stop ${CONTAINER_NAME}

docker run --rm -d \
  --network host \
  -e POSTGRES_USER=order-user \
  -e POSTGRES_PASSWORD=order-password \
  -e POSTGRES_DB=order_db \
  --cpus=2 \
  --memory=2g --memory-swap=2g --memory-reservation=1g \
  --name ${CONTAINER_NAME} \
  ${DOCKER_IMAGE_NAME}
