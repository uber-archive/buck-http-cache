# Buck HTTP Cache As a service. 

An Implementation of [Buck's HTTP Cache API](https://buckbuild.com/concept/http_cache_api.html) as a distributed cache service. 

## Quick start

### Standalone mode

Standalone mode starts this in a single node mode. For most usecases, this should work. Lets say you only have 1 project, with tens of developers and a few CI tasks, then this setup should be more than enough. 

#### From Source

```
# make sure that JAVA_HOME is set. (please use jdk8)
git clone https://github.com/uber/buck-http-cache.git
cd buck-http-cache
./run_buck_cache_client.sh standalone 
```

#### From Distribution

```
# make sure that JAVA_HOME is set. (please use jdk8)
wget https://github.com/uber/buck-http-cache/raw/dist/releases/cache-1.0.0.zip 
unzip cache-1.0.0.zip
./bin/cache server config/standalone.yml
```

### Cluster mode
If you have many users, projects that will use the cache, then chances are that standalone mode won't work for you. In this case you will need to setup a distributed cache. Lets say you want to deploy the cache to three machines (IP1, IP2, IP3). Update config file standalone.yml and add the following. 

```
multicastPort: 6734
hostIPs:
  - <IP1>
  - <IP2>
  - <IP3>
```

Now you are ready to start the cache in all three hosts. The data will be equally distributed across all three nodes, and you can query any node to fetch the data. Each node acts as a broker and as a in-memory data base. 
We will enable zookeeper based node discovery soon. At which point we won't have to manually add IPs to the condig. Just pointing to a common zk cluster should do the trick. 

## Usage

The cache server by default runs on port `6457`. Under the hood it uses [Apache Ignite](http://https://ignite.apache.org/) as the cache data grid.  In order to use this http cache in your project, add the following to your `buckconfig`:

```
[cache]
  mode = http
  http_url = http://buck-cache-android.uber.internal:6457
  http_mode = http://localhost:6457
```

This is the most basic way of starting the cache locally. For additional options, see [buckconfig](https://buckbuild.com/concept/buckconfig.html#cache). This works in standlone mode. If you are running the cache in cluster mode, then you may want to setup a loadbalancer in front of the nodes.

As an alternative, if you use [Okbuck](https://github.com/uber/okbuck) to build your gradle project with buck, you can modify `buckw` to randomly choose a server on every invocation

```
CACHE_SERVER=$[ (( $RANDOM % 3 )) + 1 ]
CACHE_URL="buck-cache-server${CACHE_SERVER}:6457"
EXTRA_BUCK_CONFIG="--config cache.http_url=http://${CACHE_URL}"
exec "$BUCK_BINARY" "$@" $EXTRA_BUCK_CONFIG
```
