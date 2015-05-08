package com.peerchat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

	private ServerSocket serverSocket;
	private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	private ArrayList<ClientWorker> clients = new ArrayList<ClientWorker>();
	
	//Constructor
	public Server(ServerSocket serverSocket){
		this.serverSocket = serverSocket;
	}
	
	//Start the server
	public void start(){
		try{
			while(true){
				if(executorService.getActiveCount() < executorService.getMaximumPoolSize()){
					Socket socket = serverSocket.accept();
					ClientWorker client = new ClientWorker(socket, this);
					clients.add(client);
					Server.executorService.execute(client);
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
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
		Server server = new Server(new ServerSocket(8767));
		server.start();
	}
	
}
