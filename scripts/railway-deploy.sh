#!/bin/bash
# Deploy Riscon stack to Railway via CLI
# Prerequisites: railway login, ANTHROPIC_API_KEY (for news-agent)
# Usage: ./scripts/railway-deploy.sh [project-name]
#        ANTHROPIC_API_KEY=xxx ./scripts/railway-deploy.sh   # optional, for full Predictions

set -e
# Skip interactive prompts (e.g. "Enter a variable") in Railway CLI
export CI=true

PROJECT_NAME="${1:-riscon}"
DOCKER_USER="balajimurugesan2016"

echo "=== Railway CLI deploy: $PROJECT_NAME ==="

# Check login
if ! railway whoami &>/dev/null; then
  echo "Run: railway login"
  exit 1
fi

# Create or link project
if railway status &>/dev/null 2>&1; then
  echo "Using linked project."
else
  echo "Creating project: $PROJECT_NAME"
  railway init -n "$PROJECT_NAME" || railway link
fi

# Add services from Docker images (skip if service already exists)
# Use || true so "already exists" does not stop the script
echo "Adding services (skipping if already exist)..."

railway add -i "${DOCKER_USER}/riscon-mockservices:latest" -s mockservices -v "INIT=1" || true
railway add -i "${DOCKER_USER}/riscon-enterpriseservice:latest" -s enterpriseservice -v "MOCK_SERVICES_BASE_URL=https://\${{mockservices.RAILWAY_PUBLIC_DOMAIN}}" || true
railway add -i "${DOCKER_USER}/riscon-location-service:latest" -s location-service -v "ENTERPRISE_SERVICE_URL=https://\${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}" -v "MOCK_SERVICES_URL=https://\${{mockservices.RAILWAY_PUBLIC_DOMAIN}}" || true
railway add -i "${DOCKER_USER}/riscon-ship-mobility-service:latest" -s ship-mobility-service -v "ENTERPRISE_SERVICE_URL=https://\${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}" -v "MOCK_SERVICES_URL=https://\${{mockservices.RAILWAY_PUBLIC_DOMAIN}}" || true
railway add -i "${DOCKER_USER}/riscon-news-agent:latest" -s news-agent -v "MOCK_SERVICES_URL=https://\${{mockservices.RAILWAY_PUBLIC_DOMAIN}}" -v "LOCATION_SERVICE_URL=https://\${{location-service.RAILWAY_PUBLIC_DOMAIN}}" || true
railway add -i "${DOCKER_USER}/riscon-probability-service:latest" -s probability-service -v "NEWS_AGENT_URL=https://\${{news-agent.RAILWAY_PUBLIC_DOMAIN}}" -v "SHIP_MOBILITY_URL=https://\${{ship-mobility-service.RAILWAY_PUBLIC_DOMAIN}}" || true
railway add -i "${DOCKER_USER}/riscon-frontend:latest" -s frontend -v "API_BACKEND_URL=https://\${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}" -v "PROBABILITY_SERVICE_URL=https://\${{probability-service.RAILWAY_PUBLIC_DOMAIN}}" || true

# Generate public domains for each service
echo "Adding public domains..."
for svc in mockservices enterpriseservice location-service ship-mobility-service news-agent probability-service frontend; do
  railway domain -s "$svc" || true
done

# Set/update variables (needed when services already exist; harmless for new services)
echo "Setting service variables..."
railway variable set -s enterpriseservice 'MOCK_SERVICES_BASE_URL=https://${{mockservices.RAILWAY_PUBLIC_DOMAIN}}'
railway variable set -s location-service 'ENTERPRISE_SERVICE_URL=https://${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}' 'MOCK_SERVICES_URL=https://${{mockservices.RAILWAY_PUBLIC_DOMAIN}}'
railway variable set -s ship-mobility-service 'ENTERPRISE_SERVICE_URL=https://${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}' 'MOCK_SERVICES_URL=https://${{mockservices.RAILWAY_PUBLIC_DOMAIN}}'
railway variable set -s news-agent 'MOCK_SERVICES_URL=https://${{mockservices.RAILWAY_PUBLIC_DOMAIN}}' 'LOCATION_SERVICE_URL=https://${{location-service.RAILWAY_PUBLIC_DOMAIN}}'
railway variable set -s probability-service 'NEWS_AGENT_URL=https://${{news-agent.RAILWAY_PUBLIC_DOMAIN}}' 'SHIP_MOBILITY_URL=https://${{ship-mobility-service.RAILWAY_PUBLIC_DOMAIN}}'
railway variable set -s frontend 'API_BACKEND_URL=https://${{enterpriseservice.RAILWAY_PUBLIC_DOMAIN}}' 'PROBABILITY_SERVICE_URL=https://${{probability-service.RAILWAY_PUBLIC_DOMAIN}}'

# ANTHROPIC_API_KEY (optional, for Predictions/AI)
if [ -n "$ANTHROPIC_API_KEY" ]; then
  echo "Setting ANTHROPIC_API_KEY for news-agent..."
  railway variable set -s news-agent "ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY"
else
  echo "Note: Set ANTHROPIC_API_KEY for news-agent: railway variable set -s news-agent ANTHROPIC_API_KEY=xxx"
fi

echo ""
echo "=== Deploy complete ==="
echo "Services will deploy automatically. Get frontend URL: railway domain -s frontend"
echo "View status: railway status"
