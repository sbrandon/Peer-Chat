package com.peerchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChatWorker implements Runnable, PeerChat{

	private Socket socket;
	private String command;
	private Server server;
	
	//Constructor
	public ChatWorker(Socket socket, Server server){
		this.socket = socket;
		this.server = server;
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
				command = reader.readLine();
				JSONObject json = (JSONObject) new JSONParser().parse(command);
				String type = json.get("type").toString();
				switch(type){
					case "JOINING_NETWORK": 
						break;
					case "JOINING_NETWORK_RELAY":
						break;
					case "ROUTING_INFO":
						break;
					case "LEAVING_NETWORK":
						break;
					case "CHAT":
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
