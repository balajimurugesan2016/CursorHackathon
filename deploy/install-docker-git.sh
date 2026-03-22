#!/bin/bash
# Install Docker Compose and Git on an Ubuntu-based DigitalOcean droplet
# Run: curl -sSL https://raw.githubusercontent.com/.../install-docker-git.sh | bash
# Or: bash install-docker-git.sh (after copying to droplet)
set -e

echo "==> Installing Git..."
apt-get update -qq
apt-get install -y -qq git

echo "==> Installing Docker and Docker Compose..."
apt-get install -y -qq ca-certificates curl gnupg
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -qq
apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "==> Enabling Docker..."
systemctl enable docker && systemctl start docker

echo ""
echo "==> Done!"
echo "  Git:         $(git --version)"
echo "  Docker:      $(docker --version)"
echo "  Compose:     $(docker compose version)"
echo ""
echo "Run 'docker compose' to use Docker Compose."
