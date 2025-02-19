## MapleGlory

## Setup

Basic configuration is available via environment variables - the names and default values of the configurable options
are defined in [ServerConstants.java](src/main/java/mapleglory/server/ServerConstants.java) and [ServerConfig.java](src/main/java/mapleglory/server/ServerConfig.java).

#### Java setup

Building the project requires Java 21 and maven.

```bash
# Build jar
$ mvn clean package
```

#### Database setup

It is possible to use either CassandraDB or ScyllaDB, no setup is required other than starting the database.

```bash
# Start CassandraDB
$ docker run -d -p 9042:9042 cassandra:5.0.0

# Alternatively, start ScyllaDB
$ docker run -d -p 9042:9042 scylladb/scylla --smp 1
```