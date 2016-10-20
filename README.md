# Buck HTTP REST API Cache

An implementation of [Buck's HTTP Cache API](https://buckbuild.com/concept/http_cache_api.html).

## Installation / Running

To run this code, clone the repository, package the server with Maven, and run the output.

```
git clone https://github.com/uber/buck-http-cache.git
cd buck-http-cache
./run_buck_cache_client.sh
```

This starts a server, using port `8080`, with an embedded Hazelcast server, which is ideal for testing. To use this 
server locally with Buck, add the following entry to your `.buckconfig.local` file:

```
[cache]
  mode = http
  http_url = http://localhost:8080
  http_mode = readwrite
  http_max_concurrent_writes = 1
```

This is the most basic way of starting the cache locally. To test it out, you can build locally, delete the Buck local
cache and build again. For example:

```
rm -rf buck-out/ .buckd/ && buck test tasklib
rm -rf buck-out/ .buckd/ && buck test tasklib
```

## Deployment

This cache can run in three modes: `CLIENT`, `SERVER`, and `DATABASE_ONLY`. The modes operate as follows:
  - `CLIENT`: Starts up a Hazelcast cache in client mode and exposes it with the Buck REST API. This means that build 
    artifacts are not stored locally and this instance is not a part of the distributed cache. 
  - `SERVER`: Starts up a Hazelcast cache in server mode and also exposes it with the Buck REST API. This means that
    build artifacts are stored locally and this instance forms a part of the distributed cache. Depending on replication
    strategies, shutting this instance down may lose part of the cache.
  - `DATABASE_ONLY`: Starts up a Hazelcast cache in server mode, but does not expose it via the Buck REST API. To 
    connect to this instance, use a separate instance in `CLIENT` mode.
    
Since Hazelcast uses multicast to auto-discover other nodes, the recommended deployment strategy is to have multiple
instances running in `DATABASE_ONLY` mode on caching servers and start instances locally in `CLIENT` mode on building
servers. The `CLIENT` instances will auto-connect to the `DATABASE_ONLY` instances. Buck can be configured to talk to 
the local `CLIENT` instance, which will take care of auto-discovering the other instances.

## Configuration

There are three configuration files:
  - `configuration.yml`: This controls the server configuration. It can be used to [configure 
    Dropwizard](http://www.dropwizard.io/0.7.1/docs/manual/configuration.html) or to configure the mode the cache 
    operates in.
  - `hazelcast-client.xml`: When running in `CLIENT` mode, this configuration is used to connect the Hazelcast client.
    The configuration is [documented by Hazelcast](http://docs.hazelcast.org/docs/3.5/manual/html/javaclientconfiguration.html).
    This can be used to connect to explicit servers, if auto-discovery does not work (e.g., in the case of NAT translation).
  - `hazelcast-server.xml`: When running in `SERVER` or `DATABASE_ONLY` mode, the configuration used for the Hazelcast
    server. This connfiguration is [documented by Hazelcast](http://docs.hazelcast.org/docs/3.5/manual/html/configuringhazelcast.html).
