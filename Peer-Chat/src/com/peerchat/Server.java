/*
 * This Class is the main server class it is responsible for starting the two threads that look after
 * client/server communication. 
 */
package com.peerchat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

	private ArrayList<ClientWorker> clients = new ArrayList<ClientWorker>();
	private ArrayList<InetAddress> servers = new ArrayList<InetAddress>();

	//Constructor
	public Server(){
		
	}
	
	//Add a new client to clients list
	public void addClient(ClientWorker client){
		if(!clients.contains(client)){
			clients.add(client);
		}
	}
	
	//Add a new IP address to list of server
	public void addServer(InetAddress ipAddress){
		if(!servers.contains(ipAddress)){
			servers.add(ipAddress);
		}
	}
	
	//Forward Message to all known servers
	public void forwardtoServer(String message){
		
	}
	
	//Forward Message to all of the clients registered with this server
	public void forwardToClient(String message){
		for(ClientWorker client : clients){
			PrintWriter writer = client.getWriter();
			writer.println(message);
		}
	}
	
	//Main method
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		ClientHandler clientHandler = new ClientHandler(server, new ServerSocket(8888));
		ServerHandler serverHandler = new ServerHandler(server, new DatagramSocket(9999));
		clientHandler.start();
		serverHandler.start();
	}
	
}
