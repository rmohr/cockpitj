cockpitj
========

A java websocket client for the great [cockpit-project](http://cockpit-project.org/).

This library is currently just a proof of concept. It allows you to connect to cockpit and exchange plain string 
messages. Further a cockpit debugging tool is included which works almost the same way like a [local interactive cockpit-bridge](http://stef.thewalter.net/protocol-for-web-access-to-system-apis.html) session.

Usage
-----

Build the project and install it into your local maven repository:

```
git@github.com:rmohr/cockpitj.git
cd cockpitj
mvn clean install -DskipTests=True
```

Then include the maven dependency

```xml
<dependency>
    <groupId>com.github.rmohr.cockpit</groupId>
    <artifactId>client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

in your project.

The following example shows a minimal possible lient which reads from and writes to a terminal:

```java
import com.github.rmohr.cockpit.client.Client;

final Client client = new Client(url, user, password, new ConsoleMessageHandler());
client.connect();

final Scanner scanner = new Scanner(System.in);
scanner.useDelimiter(Pattern.compile("[\\r\\n;]+"));
String message = "";
while (true) {
    String line = scanner.nextLine();
    if (line.equals("---")) {
        client.sendMessage(message);
        message = "";
    } else {
        message = message + line + "\n";
    }
}
```

The lines

```java
final Client client = new Client(url, user, password, new ConsoleMessageHandler());
client.connect()
```

are establishing the connection to cockpit. The line 

```java
client.sendMessage(message);
```

sends a message to cockpit.

Debugging
---------

A simple client for protocol debugging is included too. It uses cockpitj to connect to cockpit.
To run it, install the project, then move to the `debugger` subfolder and execute the exec-maven-plugin:

```bash
mvn clean install
cd debugger
mvn clean compile exec:java
```
By default the debugger uses the location and the credentials of the vagrant developer machine provided by cockpit 
and does not verify the TLS certificates to allow easy debugging.

To override the default connection values you can use system properties:

```bash
mvn clean compile exec:java -Durl=wss://localhost:9090/cockpit -Duser=root -Dpassword=foobar -DcheckCertificates=false
```

When you are successfully connected you will see cockpits welcome message:

```json
{"command":"init","version":1,"channel-seed":"1:","host":"localhost", [...]}
```

Now let's talk to cockpit. Since the newline is an essential character of cockpits websocket 
protocol we are delimiting message frames with an extra line which only contains __---__ followed by a newline. This 
delimiter is not sent to cockpit. It is only used to detect when you finished entering a complete message in the 
terminal.

This snippet will ask cockpit for user details:
```

{ "command": "init", "version": 1 }
---

{"bus":"internal","payload":"dbus-json3","name":null,"command":"open","channel":"mychannel","host":"localhost"}
---
mychannel
{"call":["/user","org.freedesktop.DBus.Properties","GetAll",["cockpit.User"]],"id":"1","type":"s"}
---
```

__The empty newlines are important!__ Empty newlines indicate that the message is a control command.

The response should look like this:

```
{"command":"ready","channel":"mychannel"}

mychannel
{"reply":[[{"Name":{"t":"s","v":"root"},"Full":{"t":"s","v":"root"},"Id":{"t":"x","v":0},"Shell":{"t":"s","v":"/bin/bash"},"Home":{"t":"s","v":"/root"},"Groups":{"t":"as","v":["root"]}}]],"type":"a{sv}","id":"1"}
```

To find out more about the cockpit websocket protocol visit the [documentation on github]
(https://github.com/cockpit-project/cockpit/blob/master/doc/protocol.md).

Run cockpit with vagrant
------------------------
The [HACKING.md](https://github.com/cockpit-project/cockpit/blob/master/HACKING.md) in the cockpit repository describes pretty well on how to quickly setup a development instance of cockpit.

On Fedora 22/23 you can run

```bash
sudo dnf install vagrant vagrant-libvirt
git clone git@github.com:cockpit-project/cockpit.git
cd cockpit
sudo vagrant up
```
After the bootstrap is done the service is accessible at [https://localhost:9090/cockpit](https://localhost:9090/cockpit) and the websocket is accessible at [wss://localhost:9090/cockpit/socket](https://localhost:9090/cockpit/socket). For both users _admin_ and _root_ the password _foobar_ is set by default.

Run tests
---------

The tests require a cockpit instance accessible at [https://localhost:9090/cockpit](https://localhost:9090/cockpit). 
