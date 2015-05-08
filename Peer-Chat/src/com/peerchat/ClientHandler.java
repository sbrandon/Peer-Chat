/*
 * This thread handles TCP communications form clients that are registered/subscribed to this server.
 */
package com.peerchat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientHandler extends Thread{
	
	private ServerSocket serverSocket;
	private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	private Server server;
	
	//Constructor
	public ClientHandler(Server server, ServerSocket serverSocket){
		this.server = server;
		this.serverSocket = serverSocket;
	}
	
	public void run(){
		try{
			while(true){
				if(executorService.getActiveCount() < executorService.getMaximumPoolSize()){
					Socket socket = serverSocket.accept();
					ClientWorker client = new ClientWorker(socket, server);
					server.addClient(client);
					ClientHandler.executorService.execute(client);
				}
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

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
}
