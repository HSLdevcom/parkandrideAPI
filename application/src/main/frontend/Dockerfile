FROM node:10

# some bower components expect that bower is installed
RUN npm install -g bower

RUN mkdir -p /opt/app && \
    chown node /opt/app
WORKDIR /opt/app
USER node

ADD package.json yarn.lock /tmp/
RUN cd /tmp && yarn
RUN ln -s /tmp/node_modules /opt/app/node_modules && \
    ln -s /tmp/node_modules/@bower_components /opt/app/vendor
