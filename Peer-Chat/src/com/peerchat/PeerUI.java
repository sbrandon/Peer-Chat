package com.peerchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class PeerUI extends Thread{
	
	private Peer peer;
	private BufferedReader reader;
	private DataOutputStream sendMessage;
	private Socket socket;
	
	//Constructor
	public PeerUI(Peer peer){
		this.peer = peer;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run(){
		joinNetwork();
	}
	
	//Opens a new socket to given IP address.
	public void openSocket(String ipAddress){
		try{
			socket = new Socket(ipAddress, 8767);
			sendMessage = new DataOutputStream(socket.getOutputStream());
		}catch(Exception e){
			System.out.println("ERROR: Could Connect to Gateway");
		}
	}
	
	//Allow this node to join the network
	public void joinNetwork(){
		try {
			System.out.println("Please Join The Network...");
			System.out.println("Enter Node ID:");
			String nodeId = reader.readLine();
			System.out.println("Enter Gateway IP Address");
			String gatewayIp = reader.readLine();
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("type","JOINING_NETWORK");
			map.put("node_id", nodeId);
			map.put("ip_address", gatewayIp);
			String jsonText = JSONValue.toJSONString(map);
			//Initialise own node values
			peer.setIpAddress(InetAddress.getLocalHost().getHostAddress().toString());
			peer.setNodeId(nodeId);
			//Send to gateway
			openSocket(gatewayIp);
			sendMessage.writeBytes(jsonText + "\n");
			sendMessage.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Allow this node to chat
	public void chat(){
		
	}
	
	
}
