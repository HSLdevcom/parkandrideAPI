FROM node:10 AS builder

# some bower components expect that bower is installed
RUN npm install -g bower

# working directory
RUN mkdir -p /opt/app && \
    chown node /opt/app
WORKDIR /opt/app
USER node

# cache node_modules
COPY --chown=node yarn-offline-mirror /opt/app/yarn-offline-mirror
COPY --chown=node .yarnrc package.json yarn.lock /opt/app/
RUN yarn install --frozen-lockfile --non-interactive --no-progress && \
    yarn cache clean && \
    bower cache clean

# do the build
COPY --chown=node *.* /opt/app/
COPY --chown=node karma /opt/app/karma/
COPY --chown=node src /opt/app/src/
RUN yarn run build

# ------------------------------------------------------------

FROM nginx:1.15-alpine

COPY --from=builder /opt/app/bin/static /usr/share/nginx/html
