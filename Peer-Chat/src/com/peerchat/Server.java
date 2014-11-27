package com.peerchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

	private ServerSocket serverSocket;
	private static final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	
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
					Server.executorService.execute(new ChatWorker(socket, this));
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	//Main method
	public static void main(String[] args) throws IOException {
		Server server = new Server(new ServerSocket(8767));
		server.start();
	}
	
}
