#!/bin/bash
# Run on a fresh Ubuntu droplet to deploy the app via Docker Compose
# Usage: curl -sSL <url> | bash   OR   bash setup-droplet.sh

set -e

REPO_URL="${REPO_URL:-https://github.com/Vector-Devs/CursorHackathon.git}"
APP_DIR="${APP_DIR:-/opt/cursorhackathon}"

echo "==> Updating system..."
apt-get update -qq && apt-get upgrade -y -qq

echo "==> Installing Docker..."
apt-get install -y -qq ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -qq && apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "==> Enabling multi-arch (for arm64 images on amd64)..."
docker run --rm --privileged tonistiigi/binfmt --install all 2>/dev/null || true

echo "==> Enabling and starting Docker..."
systemctl enable docker && systemctl start docker

echo "==> Cloning repo to $APP_DIR..."
mkdir -p "$(dirname "$APP_DIR")"
if [ -d "$APP_DIR/.git" ]; then
  cd "$APP_DIR" && git pull
else
  git clone "$REPO_URL" "$APP_DIR"
  cd "$APP_DIR"
fi

echo "==> Creating .env for ANTHROPIC_API_KEY..."
if [ ! -f .env ] && [ -n "$ANTHROPIC_API_KEY" ]; then
  echo "ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY" > .env
elif [ ! -f .env ]; then
  echo "# Set your Anthropic API key: echo 'ANTHROPIC_API_KEY=sk-ant-...' >> .env" > .env
  echo "ANTHROPIC_API_KEY=" >> .env
fi

echo "==> Pulling images and starting services..."
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d

echo "==> Done! App should be available on port 80."
docker compose -f docker-compose.prod.yml ps
