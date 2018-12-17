# How to

## Build the project

```
mvn package
```

## Execute the Broker

```
mvn exec:java@broker -Dexec.args="broker-ipaddress"
```

## Execute one client

```
mvn exec:java@client -Dexec.args="broker-ipaddress"
```
