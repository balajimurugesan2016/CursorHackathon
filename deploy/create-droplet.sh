#!/bin/bash
# Creates a DigitalOcean droplet and deploys the app
# Prerequisites: doctl auth init, docker-compose.prod.yml committed and pushed
# Usage: ANTHROPIC_API_KEY=sk-ant-xxx ./create-droplet.sh

set -e

DROPLET_NAME="${DROPLET_NAME:-cursorhackathon-app}"
DROPLET_SIZE="${DROPLET_SIZE:-s-2vcpu-4gb}"
DROPLET_REGION="${DROPLET_REGION:-nyc1}"
DROPLET_IMAGE="${DROPLET_IMAGE:-ubuntu-24-04-x64}"
REPO_URL="https://github.com/Vector-Devs/CursorHackathon.git"
APP_DIR="/opt/cursorhackathon"

# Cloud-init user data
read -r -d '' USER_DATA << 'USERDATA' || true
#!/bin/bash
set -e
export DEBIAN_FRONTEND=noninteractive

# Wait for cloud-init
cloud-init status --wait 2>/dev/null || sleep 30

apt-get update -qq && apt-get upgrade -y -qq
apt-get install -y -qq ca-certificates curl gnupg git
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -qq && apt-get install -y -qq docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
docker run --rm --privileged tonistiigi/binfmt --install all 2>/dev/null || true
systemctl enable docker && systemctl start docker

mkdir -p /opt
git clone REPO_URL_PLACEHOLDER /opt/cursorhackathon
cd /opt/cursorhackathon

# Create .env
echo "ANTHROPIC_API_KEY=ANTHROPIC_KEY_PLACEHOLDER" > .env

docker compose -f docker-compose.droplet.yml build
docker compose -f docker-compose.droplet.yml up -d

echo "Deployment complete" >> /var/log/cursorhackathon-deploy.log
USERDATA

# Substitute placeholders
USER_DATA="${USER_DATA//REPO_URL_PLACEHOLDER/$REPO_URL}"
USER_DATA="${USER_DATA//ANTHROPIC_KEY_PLACEHOLDER/${ANTHROPIC_API_KEY:-}}"

USER_DATA_FILE=$(mktemp)
trap "rm -f $USER_DATA_FILE" EXIT
echo "$USER_DATA" > "$USER_DATA_FILE"

echo "Creating droplet $DROPLET_NAME ($DROPLET_SIZE in $DROPLET_REGION)..."
DROPLET_ID=$(doctl compute droplet create "$DROPLET_NAME" \
  --size "$DROPLET_SIZE" \
  --image "$DROPLET_IMAGE" \
  --region "$DROPLET_REGION" \
  --user-data-file "$USER_DATA_FILE" \
  --format ID \
  --no-header)

echo "Droplet ID: $DROPLET_ID"
echo "Waiting for droplet to be active..."
sleep 10
while [ "$(doctl compute droplet get "$DROPLET_ID" --format Status --no-header)" != "active" ]; do
  echo -n "."
  sleep 5
done
echo ""

IP=$(doctl compute droplet get "$DROPLET_ID" --format PublicIPv4 --no-header)
echo ""
echo "Droplet created. IP: $IP"
echo "Deployment is running in background (takes ~5-10 min to build). Check: ssh root@$IP 'docker compose -f /opt/cursorhackathon/docker-compose.droplet.yml ps'"
echo "App URL: http://$IP"
echo ""
echo "To set ANTHROPIC_API_KEY after deploy: ssh root@$IP \"echo 'ANTHROPIC_API_KEY=sk-ant-xxx' >> /opt/cursorhackathon/.env && cd /opt/cursorhackathon && docker compose -f docker-compose.droplet.yml up -d --force-recreate news-agent\""
