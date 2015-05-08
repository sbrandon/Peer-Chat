/*
 * This thread handles UDP Communications from other servers in the network
 */
package com.peerchat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerHandler extends Thread{
	
	private Server server;
	private DatagramSocket serverSocket;
	
	//Constructor
	public ServerHandler(Server server, DatagramSocket serverSocket){
		this.server = server;
		this.serverSocket = serverSocket;
	}
	
	public void run(){
		try{
			byte[] receiveData = new byte[1024];
			while(true){
				DatagramPacket receive = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receive);
				String received = new String(receive.getData());
				System.out.println("Received via UDP: " + received);
				//Add Server to list of servers
				InetAddress ipAddress = receive.getAddress();
				server.addServer(ipAddress);
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	//Getters & Setters
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public DatagramSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(DatagramSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
}
