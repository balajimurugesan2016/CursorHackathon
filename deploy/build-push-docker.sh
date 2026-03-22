#!/bin/bash
# Build and push all Docker images to Docker Hub
# Prerequisites: docker login (docker login -u balajimurugesan2016)
# Usage: ./deploy/build-push-docker.sh
set -e

REGISTRY="${DOCKER_REGISTRY:-balajimurugesan2016}"
TAG="${DOCKER_TAG:-latest}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "==> Building and pushing to $REGISTRY (tag: $TAG)"
echo ""

build_push() {
  local context="$1"
  local image_name="$2"
  local full_image="$REGISTRY/$image_name:$TAG"
  echo "==> Building $full_image from $context (linux/amd64)"
  docker build --platform linux/amd64 -t "$full_image" "$context"
  echo "==> Pushing $full_image"
  docker push "$full_image"
  echo ""
}

# Build order: base dependencies first, then services that depend on them
build_push "./mockServices" "mock-services"
build_push "./enterpriseservice" "enterpriseservice"
build_push "./agents/location-service" "location-service"
build_push "./agents/ship-mobility-service" "ship-mobility-service"
build_push "./agents/news-agent" "news-agent"
build_push "./agents/probability-service" "probability-service"
build_push "./frontend" "frontend"

echo "==> Done! All images pushed to $REGISTRY"
