/*
 * This Class is the main node class it is responsible for starting the two threads that look after
 * node communication and routing communication in the p2p network. This class is also responsible for
 * outgoing TCP communications.
 * Stephen Brandon May '14
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
	
	private HashMap<String, String> routingTable = new HashMap<String, String>();
	private String ipAddress;
	private String nodeId;
	private PeerUI peerUI;

	//Constructor
	public Peer(){
		peerUI = new PeerUI(this);
		peerUI.start();
	}
	
	//When a node joins the network it must be added to our routing table.
	public void addToRouting(String nodeId, String ipAddress){
		routingTable.put(nodeId, ipAddress);
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
		communicate(joinIpAddress, JSONValue.toJSONString(routingInfo));
	}
	
	//CHAT. takes input from PeerUI to get values from routing table to send message.
	public void chat(String targetId, String message){
		//First create the JSON message
		Map<String, String> chat = new LinkedHashMap<String, String>();
		chat.put("type", "CHAT");
		chat.put("target_id", targetId);
		chat.put("sender_id", nodeId);
		chat.put("text", message);
		if(targetId.equals("00")){
			//Send Chat message to all nodes.
			Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
			while(iterator.hasNext()){
				communicate(iterator.next().getValue(), JSONValue.toJSONString(chat));
			}
		}
		else{
			String ip = routingTable.get(targetId);
			communicate(ip, JSONValue.toJSONString(chat));
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
			System.out.println("ERROR: Could Connect to Gateway");
		}
	}
	
	public void printRoutes(){
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		System.out.println("| ID | IPADDRESS |");
		while(iterator.hasNext()){
			System.out.println("|----------------|");
			Entry<String, String> entry = iterator.next();
			System.out.println("| " + entry.getKey() + " | " + entry.getValue() + " |");
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
	
}
