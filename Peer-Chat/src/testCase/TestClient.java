package testCase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
	
	public TestClient(int portNumber){
		new BufferedReader(new InputStreamReader(System.in));
		this.portNumber = portNumber;
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
			System.out.println("Client is running. Port No. " + portNumber);
			System.out.println();
			sendMessage = new DataOutputStream(socket.getOutputStream());
			testChat();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Connect With Server");
		}	
	}
	
	public void testChat(){
		try
		{
			Map<String, String> map = new LinkedHashMap<String, String>();
			map.put("type","CHAT");
			String jsonText = JSONValue.toJSONString(map);
			sendMessage.writeBytes(jsonText + "\n");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println(reader.readLine());
		}
		catch(Exception e)
		{
			System.out.println("Cannot Connect With Server");
		}
	}
	
	//Main
	public static void main(String args[]) throws Exception {
		int portNumber = 8767;
		TestClient client = new TestClient(portNumber);
		client.start();
	}
}