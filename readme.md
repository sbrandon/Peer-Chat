# CS7050 Individual Project - Peer Chat
Stephen Brandon
###Introduction
- Each node is both a client and a server.
- Any node can join the network by knowing the address of any other node.
- Nodes listen for TCP communications on port 8767.
- All messages are sent in JSON format.
- A socket is opened for outgoing messages and closed once they have been sent.

### Running a Node
1. Start the node:
```sh
C:\>java -jar node.jar
```
2. Pick a unique node ID. Currently two digit ID that user can choose. If a duplicate ID is entered the new node overwrites the old one in the nodes routing table. (An alternative implementation where the users email is hashed to provide a unique key is provided but I decided to stick to a 2 digit number as it is easiser to work with.)
3. Gateway IP address. Enter the IP address of any node in the network you want to join. That node will send back a copy of its routing table and send a relay message to all nodes to update their routing tables. If this is the first node in the network this can be left blank.

###Menu
```sh
Menu: Enter Number
1. Send Chat Message
2. Route Table
3. Ping Node
4. Leave Network
```
To select a menu item enter the corresponding number.

1. To send a message you will first be asked for the node ID that you want to send the message to. "00" will broadcast the message to everyone in the network. After entering a node ID you will be asked to enter the message text you wish to send.
2. The route table option allows you to see all other nodes in the network. The table contains node id's and ip addresses.
3. To ping another node.
4. The leave network option will tell other nodes you are leaving before closing the application. 

###Considerations
- The network would be much more scaleable if prefix routing was used rather than the need to tell every node in the network when somone comes or goes.
- I tested this with a bunch of AWS EC2 instances and it worked fine however if you wish to use nodes in different netowrks you need to tell the application the public IP address and this will not work where NAT is being used.
- I plan to implement a way to make sure only unique node IDs will be used. (See PeerUI.java where this implementation is provided)