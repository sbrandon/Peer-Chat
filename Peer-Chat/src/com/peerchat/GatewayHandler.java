/*
 * This thread handles TCP communications for nodes that use this node as a gateway.
 * Stephen Brandon May '14
 */
package com.peerchat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GatewayHandler extends Thread{
	
	private ServerSocket serverSocket;
	private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	private Peer server;
	
	//Constructor
	public GatewayHandler(Peer server, ServerSocket serverSocket){
		this.server = server;
		this.serverSocket = serverSocket;
	}
	
	public void run(){
		try{
			while(true){
				if(executorService.getActiveCount() < executorService.getMaximumPoolSize()){
					Socket socket = serverSocket.accept();
					PeerWorker client = new PeerWorker(socket, server);
					GatewayHandler.executorService.execute(client);
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	//Getters & Setters
	public Peer getServer() {
		return server;
	}

	public void setServer(Peer server) {
		this.server = server;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
}
