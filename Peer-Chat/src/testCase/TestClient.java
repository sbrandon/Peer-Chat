/*
 * This is an old test case for client server type chat network. 
 * Not currently in use.
 * Stephen Brandon May '14
 */
package testCase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

public class TestClient {

	private DataOutputStream sendMessage;
	private BufferedReader reader;
	private int portNumber;
	private Socket socket;
	private boolean connected;
	
	//Constructor
	public TestClient(int portNumber){
		this.portNumber = portNumber;
		connected = false;
		try{
			this.socket = new Socket("localhost", portNumber);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void start(){
		try
		{
			//new Listener().start();
			reader = new BufferedReader(new InputStreamReader(System.in));
			sendMessage = new DataOutputStream(socket.getOutputStream());
			connected = true;
			join();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Connect With Server");
			connected = false;
		}	
		while(connected){
		    try {
				String input = reader.readLine();
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("type","CHAT");
				map.put("text", input);
				String jsonText = JSONValue.toJSONString(map);
				sendMessage.writeBytes(jsonText + "\n");
			} catch (IOException e) {
				e.printStackTrace();
				connected = false;
			}
		}
	}
	
	public void join(){
		System.out.println("Please join the network...");
		try {
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("type","JOINING_NETWORK");
			System.out.println("Enter Node ID:");
			String nodeId = reader.readLine();
			map.put("node_id", nodeId);
			System.out.println("Enter IP Address of Gateway Node:");
			String ipAddress = reader.readLine();
			map.put("ip_address", ipAddress);
			String jsonText = JSONValue.toJSONString(map);
			sendMessage.writeBytes(jsonText + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Main
	public static void main(String args[]) throws Exception {
		int portNumber = 8888;
		TestClient client = new TestClient(portNumber);
		client.start();
	}
	
	public class Listener extends Thread{
		
		public void run(){
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while(true){
					if(reader.ready()){
						System.out.println("FROM SERVER: " + reader.readLine());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}