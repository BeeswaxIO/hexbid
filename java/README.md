# Hexbid Java

This project is an open-source java implementation of Beeswax bidder server
based on [Netty](http://netty.io/) server framework. It is a starting point for
the development of production quality custom bidder.

### How to build
You require the following to build hexbid:

* [OpenJDK 7 or higher](http://openjdk.java.net/install/)
* Latest stable [Apache Maven](https://maven.apache.org/download.cgi)

Checkout hexbid repository from github:
```
git clone https://github.com/BeeswaxIO/hexbid.git
```

Go to `java/` folder and run:
```
mvn package
```

Note: Unit tests will initialize logger(default log path is /var/log/beeswax/hexbid/)
so please make sure you have write permission to that directory. You can change
the path in log4j2.xml.

### How to start the server

Go to `java/` folder and run:
```
java -jar target/hexbid-1.0.0.jar
```

### Bidder
[Bidder](src/main/java/com/beeswax/hexbid/bidder/Bidder.java) defines the
sample bidding logic that you want to customize. Now reference implementation
randomly selects eligible creative and provides examples of custom strategies as
the following:

Flat Price Strategy
  - set a flat price from custom strategy parameters to bid agent request.

Random Price Strategy
  - sets a random bid price not exceeding max bid micros from custom strategy parameters.

Retargeting Strategy
  - sets bid price based on user score and base price.

Note: CPCStrategy and CPMStrategy are not used for custom bidder.