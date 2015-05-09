/*
 * This Class is the main node class it is responsible for starting the two threads that look after
 * node communication and routing communication in the p2p network. 
 * Stephen Brandon May '14
 */
package com.peerchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class Peer {
	
	private HashMap<String, String> gatewayPeers = new HashMap<String, String>();
	private HashMap<String, String> routingTable = new HashMap<String, String>();
	private String ipAddress;
	private String nodeId;

	//Constructor
	public Peer(){
		
	}
	
	//When a peer connects to this node it is added to the gatewayPeers.
	public void nodeJoined(PeerWorker node){
		//gatewayPeers.put(node.getNodeId(), node);
	}
	
	//When a node joins the network it must be added to our routing table.
	public void addToRouting(PeerWorker node){
		//routingTable.put(node.getNodeId(), node);
	}
	
	//Returns our routing table in json string format.
	public void routingInfo(String joinNodeId){
		Map<String, String> routingInfo = new LinkedHashMap<String, String>();
		routingInfo.put("type", "ROUTING_INFO");
		routingInfo.put("gateway_id", nodeId);
		routingInfo.put("node_id", joinNodeId);
		routingInfo.put("ip_address", ipAddress);
		//Iterate through the hash map to build routing table
		JSONArray routeTable = new JSONArray();
		Iterator<Entry<String, String>> iterator = routingTable.entrySet().iterator();
		while(iterator.hasNext()){
			Map<String, String> route = new LinkedHashMap<String, String>();
			route.put("node_id", iterator.next().getKey());
			route.put("ip_address", iterator.next().getValue());
			routeTable.add(route);
		}
		routingInfo.put("route_table", routeTable.toJSONString());
		System.out.println(JSONValue.toJSONString(routingInfo));
	}
	
	//Main method
	public static void main(String[] args) throws IOException {
		Peer server = new Peer();
		GatewayHandler gateWayHandler = new GatewayHandler(server, new ServerSocket(8767));
		PeerUI peerUI = new PeerUI(server);
		peerUI.start();
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
