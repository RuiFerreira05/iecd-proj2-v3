package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientBean {
	
	private final String uuid;
	
	private String username = null;
	private boolean isConnected = false;
	private boolean isLoggedIn = false;
	
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private Socket socket = null;
	
	private String serverIP = "localhost";
	private int serverPort = 3004;
	
	public ClientBean(String uuid) {
		this.uuid = uuid;
		connect();
	}
	
	public boolean connect() {
		try {
			socket = new Socket(serverIP, serverPort);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("Connected to server at " + serverIP + ":" + serverPort);
			isConnected = true;
			return true;
		} catch (Exception e) {
			System.err.println("Error connecting to server: '" + e.getMessage() + "'");
			isConnected = false;
			return false;
		}
	}
	
	public boolean checkStatus() {
		writer.println("status");
		try {
			reader.readLine();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean login(String user, String pass) {
		if (!isConnected) {
			return false;
		}
		writer.println("login " + user + " " + pass);
		try {
			String response = reader.readLine();
			if (response.equals("valid")) {
				this.username = user;
				this.isLoggedIn = true;
				return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println("Something went wrong with login: " + e.getMessage());
			return false;
		}
	}
	
	public boolean logout() {
		if (!isConnected || !isLoggedIn) {
			return false;
		}
		writer.println("logout");
		try {
			String response = reader.readLine();
			if (response.equals("valid")) {
				this.isLoggedIn = false;
				this.username = null;
				return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println("Something went wrong with logout: " + e.getMessage());
			return false;
		}
	}
	
	public String[] getdata(String username) {
		if (!isConnected) {
			return null;
		}
		String[] data = new String[8];
		writer.println("getdata " + username);
		try {
			String response = reader.readLine();
			String[] parts = response.split(" ");
			if (parts[0].equals("found")) {
				for (int i = 1; i < parts.length; i++) {
					data[i - 1] = parts[i];
				}
				return data;
			}
			return null;
		} catch (Exception e) {
			System.out.println("Something went wrong with getdata: " + e.getMessage());
			return null;
		}
	}
	
	public String[] getWof() {
		if (!isConnected) {
			return null;
		}
		String[] wofData = new String[15];
		writer.println("getXML walloffame");
		try {
			String response = reader.readLine();
			String[] parts = response.split(" ");
			if (parts[0].equals("found")) {
				for (int i = 1; i < parts.length; i++) {
					wofData[i - 1] = parts[i];
				}
				return wofData;
			}
			return null;
		} catch (Exception e) {
			System.out.println("Something went wrong with getWof: " + e.getMessage());
			return null;
		}
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isConnected() {
		if (isConnected == false) {
			return false;
		} else {
			isConnected = checkStatus();
			return isConnected;
		}
	}
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	public BufferedReader getReader() {
		return reader;
	}
	
	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}
	
	public PrintWriter getWriter() {
		return writer;
	}
	
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

}
