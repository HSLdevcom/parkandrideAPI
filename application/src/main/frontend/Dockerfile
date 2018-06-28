FROM node:10

# some bower components expect that bower is installed
RUN npm install -g bower

# create work directory
RUN mkdir -p /opt/app && \
    chown node /opt/app
WORKDIR /opt/app
USER node

# fetch dependencies
ADD .yarn-cache.tgz /
ADD package.json yarn.lock /tmp/
RUN cd /tmp && yarn

# do the build
ADD . /opt/app/
RUN ln -s /tmp/node_modules /opt/app/node_modules && \
    ln -s /tmp/node_modules/@bower_components /opt/app/vendor
RUN npm run build
