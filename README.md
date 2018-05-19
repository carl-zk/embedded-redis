# embedded-redis
> Fork from https://github.com/kstyrc/embedded-redis

*Redis embedded server for Java integration testing*

## Usage
1. 
```java
RedisServer redisServer = new RedisServer();  //default port 6379
redisServer.start();
// do some work
redisServer.stop();
```
2.
```java
RedisServer redisServer = new RedisServer(7799);
```
above two will create a temp redis server, nothing will leave when server stop.
3.
```java
redisServer = new RedisServer(new File("/path/to/redis.conf"), 6380);
```
assign your own redis.conf, will keep your dump.rdb when server stop. 


## License
Licensed under the Apache License, Version 2.0


## Contributors
 * Krzysztof Styrc ([@kstyrc](http://github.com/kstyrc))
 * Piotr Turek ([@turu](http://github.com/turu))
 * anthonyu ([@anthonyu](http://github.com/anthonyu))
 * Artem Orobets ([@enisher](http://github.com/enisher))
 * Sean Simonsen ([@SeanSimonsen](http://github.com/SeanSimonsen))
 * Rob Winch ([@rwinch](http://github.com/rwinch))
 * Carl-zk ([@carl-zk](https://github.com/carl-zk/embedded-redis))

## Changelog
support jdk 1.8+
platform MAC x64; Linux/Unix x64 
