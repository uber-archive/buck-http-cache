FROM openjdk:8

MAINTAINER Dhaval Patel dhaval@uber.com;

RUN git clone https://github.com/uber/buck-http-cache.git

WORKDIR buck-http-cache

RUN ./run_buck_cache_client.sh standalone 
