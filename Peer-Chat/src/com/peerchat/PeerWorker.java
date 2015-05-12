/*
 * This class represents the functions that other nodes can call.
 * Stephen Brandon May '14
 */
package com.peerchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PeerWorker implements Runnable, PeerChat{

	private Socket socket;
	private Peer peer;
	
	//Constructor
	public PeerWorker(Socket socket, Peer peer){
		this.socket = socket;
		this.peer = peer;
	}
	
	//The following function implements hashing of an arbitrary string.
	public int hashCode(String str){
		int hash = 0;
		for(int i = 0; i < str.length(); i++){
			hash = hash * 31 + str.charAt(i);
		}
		return Math.abs(hash);
	}

	@Override
	public void init(Socket socket, int uid) {
		// TODO Auto-generated method stub
	}

	@Override
	public long joinNetwork(InetSocketAddress bootstrap_node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean leaveNetwork(long network_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void chat(String text, String[] tags) {
		// TODO Auto-generated method stub
	}

	@Override
	public ChatResult[] getChat(String[] words) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String command = "";
				while(reader.ready()){
					command = reader.readLine();
					//System.out.println("NODE-RECEIVED: " + command);
				}
				if(!command.isEmpty()){
					JSONObject json = (JSONObject) new JSONParser().parse(command);
					String type = json.get("type").toString();
					switch(type){
						case "JOINING_NETWORK":
							//Add new node to routing table and reply with copy of routing table.
							String nodeId = json.get("node_id").toString();
							String ipAddress = json.get("ip_address").toString();
							peer.addToRouting(nodeId, ipAddress);
							peer.routingInfo(nodeId, ipAddress);
							break;
						case "JOINING_NETWORK_RELAY":
							//Update routing table with new node.
							String newId = json.get("node_id").toString();
							String newIp = json.get("ip_address").toString();
							peer.addToRouting(newId, newIp);
							break;
						case "ROUTING_INFO":
							//Populate routing table with values sent from gateway node.
							JSONArray routes = (JSONArray) json.get("route_table");
							//Add each route from the routing info message to our routing table.
							for(int i = 0; i <routes.size(); i++){
								JSONObject route = (JSONObject) new JSONParser().parse(routes.get(i).toString());
								String id = route.get("node_id").toString();
								String ip = route.get("ip_address").toString();
								peer.addToRouting(id, ip);
							}
							break;
						case "LEAVING_NETWORK":
							//Remove node from routing table.
							String leavingId = json.get("node_id").toString();
							peer.removeFromRouting(leavingId);
							break;
						case "CHAT":
							//Print received chat messages to console.
							System.out.println("Message From: " + json.get("sender_id"));
							System.out.println(json.get("text"));
							break;
						case "ACK_CHAT":
							break;
						case "CHAT_RETRIEVE":
							break;
						case "CHAT_RESPONSE":
							break;
						case "PING":
							break;
						case "ACK":
							break;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
