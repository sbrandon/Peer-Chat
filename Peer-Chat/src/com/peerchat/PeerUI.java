/*
 * This is the user interface this class takes input from the user and can call 
 * methods on the peer class.
 * Stephen Brandon May '14
 */
package com.peerchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class PeerUI extends Thread{
	
	private Peer peer;
	private BufferedReader reader;

	//Constructor
	public PeerUI(Peer peer){
		this.peer = peer;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run(){
		joinNetwork();
		while(true){
			menu();
		}
	}
	
	//Allow this node to join the network.
	public void joinNetwork(){
		try {
			System.out.println("Please Join The Network...");
			System.out.println("Enter Node ID:");
			String nodeId = reader.readLine();
			System.out.println("Enter Gateway IP Address (Blank you're the first node in the network)");
			String gatewayIp = reader.readLine();
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("type","JOINING_NETWORK");
			map.put("node_id", nodeId);
			map.put("ip_address", InetAddress.getLocalHost().getHostAddress().toString());
			String jsonText = JSONValue.toJSONString(map);
			//Initialise own node values
			peer.setIpAddress(InetAddress.getLocalHost().getHostAddress().toString());
			peer.setNodeId(nodeId);
			peer.communicate(gatewayIp, jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Send chat messages to other nodes.
	public void chat(){
		try{
			System.out.println("Enter Target ID (00 broadcast to all nodes)");
			String targetId = reader.readLine();
			System.out.println("Enter Your Message...");
			String message = reader.readLine();
			peer.chat(targetId, message);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//User menu
	public void menu(){
		try{
			System.out.println("Menu: Enter number");
			System.out.println("1. Send Chat Message");
			System.out.println("2. Route Table");
			System.out.println("3. Leave Network");
			String choice = reader.readLine();
			if(choice.equals("1")){
				chat();
			}
			else if(choice.equals("2")){
				peer.printRoutes();
			}
			else if(choice.equals("3")){
				leave();
			}
			else{
				System.out.println("Not a valid option. Please type a number and then press return.");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//Leave the network
	public void leave(){
		peer.leaveNetwork();
		System.exit(0);
	}
	
}
