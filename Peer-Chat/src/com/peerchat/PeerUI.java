/*
 * This is the user interface this class takes input from the user and can call 
 * methods on the peer class.
 * Stephen Brandon May '15
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
			//UI always returns to menu after completing function.
			menu();
		}
	}
	
	//The following function implements hashing of an arbitrary string.
	public int hashCode(String str){
		int hash = 0;
		for(int i = 0; i < str.length(); i++){
			hash = hash * 31 + str.charAt(i);
		}
		return Math.abs(hash);
	}
	
	//Allow this node to join the network.
	public void joinNetwork(){
		try {
			System.out.println("Please Join The Network...");
			/*Alternative unique ID join
			 *System.out.println("Enter Email:");
			 *int hash = hashCode(reader.readLine());
			 *String nodeId = Integer.toString(hash);
			 */ 
			System.out.println("Enter Node ID:");
			String nodeId = reader.readLine();
			System.out.println("Enter Gateway IP Address (Blank if you're the first node in the network)");
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
	
	//Send ping message to other nodes.
	public void ping(){
		try{
			System.out.println("Enter Target ID For Ping");
			String targetId = reader.readLine();
			//Should first check ID exists before going to all this trouble.
			System.out.println("Pinging...");
			peer.ping(targetId);
			Boolean ack = false;
			long startTime = System.currentTimeMillis();
			while((System.currentTimeMillis()-startTime)<5000){
				if(peer.getPingAck().equals(targetId)){
					ack = true;
					break;
				}
				System.out.print(".");
				Thread.sleep(500);
			}
			if(ack == true){
				System.out.println("He's Alive!");
			}
			else{
				System.out.println("He's dead Jim...");
			}
		} catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	//User menu
	public void menu(){
		try{
			System.out.println("");
			System.out.println("Menu: Enter number");
			System.out.println("1. Send Chat Message");
			System.out.println("2. Route Table");
			System.out.println("3. Ping Node");
			System.out.println("4. Leave Network");
			String choice = reader.readLine();
			if(choice.equals("1")){
				chat();
			}
			else if(choice.equals("2")){
				peer.printRoutes();
			}
			else if(choice.equals("3")){
				ping();
			}
			else if(choice.equals("4")){
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