# Build stage
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci

COPY . .
RUN npm run build

# Serve stage
FROM nginx:alpine
RUN apk add --no-cache gettext

# Replace default config with SPA routing
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copy built assets from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port (overridden by PORT at runtime)
EXPOSE 8080

CMD ["/bin/sh", "-c", "export PORT=${PORT:-8080} && envsubst '${PORT}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
