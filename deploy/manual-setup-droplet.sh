#!/bin/bash
# MANUAL DROPLET SETUP - Copy & paste into droplet console
#
# Option 1 (easiest): Run `bash` then paste this entire file, press Ctrl+D
# Option 2: Run `cat > /tmp/setup.sh` then paste, press Ctrl+D, then `bash /tmp/setup.sh`
#
set -e

REPO_URL="${REPO_URL:-https://github.com/Vector-Devs/CursorHackathon.git}"
APP_DIR="/opt/cursorhackathon"

echo "==> Installing Docker..."
apt-get update -qq && apt-get upgrade -y -qq
apt-get install -y -qq ca-certificates curl gnupg git
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -qq && apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "==> Enabling multi-arch..."
docker run --rm --privileged tonistiigi/binfmt --install all 2>/dev/null || true

echo "==> Starting Docker..."
systemctl enable docker && systemctl start docker

echo "==> Cloning repo..."
mkdir -p /opt
rm -rf "$APP_DIR"
git clone "$REPO_URL" "$APP_DIR"
cd "$APP_DIR"

echo "==> Creating .env..."
echo "ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY:-}" > .env

echo "==> Building from source and starting (takes 5-10 min)..."
docker compose -f docker-compose.droplet.yml build
docker compose -f docker-compose.droplet.yml up -d

echo "==> Done! App: http://$(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')"
docker compose -f docker-compose.droplet.yml ps
