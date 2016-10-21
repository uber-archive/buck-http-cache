# Buck HTTP Cache As a service. 

An Implementation of [Buck's HTTP Cache API](https://buckbuild.com/concept/http_cache_api.html) as a distributed cache as a service. 

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


## Using the HTTP Cache. 

The cache server by default runs on port `6457`. Under the hood we are using [Apache Ignite](http://https://ignite.apache.org/) as the Cache data grid. 
In order to use this http cache in your app to build, add the following entry to your `.buckconfig.local` file:

```
[cache]
  mode = http
  http_url = http://localhost:6457
  http_mode = readwrite
  http_max_concurrent_writes = 1
```

This is the most basic way of starting the cache locally. To test it out, you can build locally, delete the Buck local
cache and build again. For example:

```
rm -rf buck-out/ .buckd/ && <RUN_YOUR_BUCK_TARGET>
rm -rf buck-out/ .buckd/ && <RUN_YOUR_BUCK_TARGET>
```


The above mentioned way of configuring the cache URL works if you are in standlone mode. If you are running the cache in cluster mode, then you have two options. 

* Setup a loadbalancer in front of the nodes. 
* Randomly hit any one of the host. 

If you pick option two then you have to do something like this. Everytime you run the command you can pick one of the hosts at random. We suggest you setup a loadbalancer and use this as a fall back option. 

```
buck --config cache.http_url=http://${CACHE_SERVER}:${CACHE_PORT} build
```
