/*
 * This Class is the main node class it is responsible for starting the two threads that look after
 * node communication and routing communication in the p2p network. This class is also responsible for
 * outgoing TCP communications.
 * Stephen Brandon May '15
 */
package com.peerchat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class Peer {
	
	//Routing table. Currently all node id's and ip's stored in a single HashMap in every node.
	private HashMap<String, String> routingTable = new HashMap<String, String>();
	private String ipAddress;
	private String nodeId;
	private PeerUI peerUI;
	private String pingAck = "";

	//Constructor
	public Peer(){
		peerUI = new PeerUI(this);
		peerUI.start();
	}
	
	//When a node joins the network it's added to our routing table.
	public void addToRouting(String nodeId, String ipAddress){
		routingTable.put(nodeId, ipAddress);
	}
	
	//When a node leaves the network it's removed from our routing table.
	public void removeFromRouting(String leavingId){
		routingTable.remove(leavingId);
	}
	
	//ROUTING_INFO. Returns our routing table in JSON string format. 
	@SuppressWarnings("unchecked")
	public void routingInfo(String joinNodeId, String joinIpAddress){
		Map<String, Serializable> routingInfo = new LinkedHashMap<String, Serializable>();
		routingInfo.put("type", "ROUTING_INFO");
		routingInfo.put("gateway_id", nodeId);
		routingInfo.put("node_id", joinNodeId);
		routingInfo.put("ip_address", ipAddress);
		//Iterate through the hash map to build routing table
		JSONArray routeTable = new JSONArray();
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			Map<String, String> route = new LinkedHashMap<String, String>();
			route.put("node_id", entry.getKey());
			route.put("ip_address", entry.getValue());
			routeTable.add(route);
		}
		routingInfo.put("route_table", routeTable);
		//Send routing table back to joining node.
		communicate(joinIpAddress, JSONValue.toJSONString(routingInfo));
		//Send JOINING_NETWORK_RELAY message to tell other nodes to update their tables
		joinRelay(joinNodeId, joinIpAddress);
	}
	
	//CHAT. takes input from PeerUI to get values from routing table to send message.
	public void chat(String targetId, String message){
		//Create the JSON message
		Map<String, String> chat = new LinkedHashMap<String, String>();
		chat.put("type", "CHAT");
		chat.put("target_id", targetId);
		chat.put("sender_id", nodeId);
		chat.put("text", message);
		if(targetId.equals("00")){
			//Send Chat message to all nodes.
			broadcast(JSONValue.toJSONString(chat));
		}
		else{
			//Send Chat message to target ID.
			String ip = routingTable.get(targetId);
			communicate(ip, JSONValue.toJSONString(chat));
		}
	}
	
	//JOINING_NETWORK_RELAY. Informs other nodes to update their routing tables with new arrival.
	public void joinRelay(String joinId, String joinIp){
		//Create JSON String
		Map<String, String> relay = new LinkedHashMap<String, String>();
		relay.put("type", "JOINING_NETWORK_RELAY");
		relay.put("node_id", joinId);
		relay.put("ip_address", joinIp);
		//Send to all nodes in routing table
		broadcast(JSONValue.toJSONString(relay));
	}
	
	//LEAVING_NETWORK. Informs other nodes that this node is leaving the network and to update their routing tables.
	public void leaveNetwork(){
		//Create JSON String
		Map<String, String> leave = new LinkedHashMap<String, String>();
		leave.put("type", "LEAVING_NETWORK");
		leave.put("node_id", nodeId);
		//Send to all nodes in routing table
		broadcast(JSONValue.toJSONString(leave));
	}
	
	//PING. Ping another node to see if it is alive.
	public void ping(String targetId){
		//Create JSON String
		Map<String, String> ping = new LinkedHashMap<String, String>();
		ping.put("type", "PING");
		ping.put("target_id", targetId);
		ping.put("sender_id", nodeId);
		ping.put("ip_address", ipAddress);
		String targetIp = routingTable.get(targetId);
		communicate(targetIp, JSONValue.toJSONString(ping));
	}
	
	//ACK. Sends an ACK message back to node that sent us ping.
	public void sendAck(String targetId){
		//Create JSON String
		Map<String, String> ack = new LinkedHashMap<String, String>();
		ack.put("type", "ACK");
		ack.put("node_id", nodeId);
		ack.put("ip_address", ipAddress);
		String targetIp = routingTable.get(targetId);
		communicate(targetIp, JSONValue.toJSONString(ack));
	}
	
	//Sends message to every node on routing table.
	public void broadcast(String message){
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		while(iterator.hasNext()){
			communicate(iterator.next().getValue(), message);
		}
	}
	
	//Allows this node to send JSON formatted messages to other nodes with given IP address.
	public void communicate(String ipAddress, String message){
		try{
			Socket socket = new Socket(ipAddress, 8767);
			DataOutputStream sendMessage = new DataOutputStream(socket.getOutputStream());
			sendMessage.writeBytes(message + "\n");
			sendMessage.close();
			socket.close();
			//System.out.println("NODE-SENT: " + message);
		}catch(Exception e){
			System.out.println("ERROR: Could Open Socket to: " + ipAddress);
		}
	}
	
	//Print out routing table for debugging purposes.
	public void printRoutes(){
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		System.out.println("| ID | IPADDRESS |");
		while(iterator.hasNext()){
			System.out.println("|----------------|");
			Entry<String, String> entry = iterator.next();
			System.out.println("| " + entry.getKey() + " | " + entry.getValue() + " |");
			System.out.println("");
		}
	}
	
	//Main method
	public static void main(String[] args) throws IOException {
		Peer server = new Peer();
		GatewayHandler gateWayHandler = new GatewayHandler(server, new ServerSocket(8767));
		gateWayHandler.start();
	}
	
	//Getters & Setters
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getPingAck() {
		return pingAck;
	}

	public void setPingAck(String pingAck) {
		this.pingAck = pingAck;
	}
	
}