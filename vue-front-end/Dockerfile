# 1) Build Vue
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 2) Runtime: Caddy
FROM caddy:2-alpine
COPY --from=build /app/dist /srv