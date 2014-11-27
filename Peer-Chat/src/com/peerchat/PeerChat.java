package com.peerchat;

import java.net.InetSocketAddress;
import java.net.Socket;

public interface PeerChat {

	public void init(Socket socket, int uid);
	public long joinNetwork(InetSocketAddress bootstrap_node);
	public boolean leaveNetwork(long network_id);
	public void chat(String text, String[] tags);
	public ChatResult[] getChat(String[] words);
	
}
