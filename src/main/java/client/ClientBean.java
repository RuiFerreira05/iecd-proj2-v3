package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientBean {
	
	private final String uuid;
	
	private String username = null;
	private String pass= null;
	private boolean isConnected = false;
	private boolean isLoggedIn = false;
	private boolean isMatchmaking = false;
	private boolean isPlaying = false;
	
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private Socket socket = null;
	
	private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
	
	private String playerNum = null;
	private String opponentUsername = null;
	private boolean yt = false;
	private String board = null;
	
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
			new Thread(() -> {
				while (isConnected) {
					try {
						String message = reader.readLine();
						String parts[] = message.split(" ");
						if (parts[0].equals("gs")) {
							isPlaying = true;
							isMatchmaking = false;
							playerNum = parts[1];
							opponentUsername = parts[2];
							if (parts.length == 4) {
								yt = true;
							} else {
								yt = false;
							}
						} else if (parts[0].equals("board")) {
							board = parts[1];
						} else {
							messageQueue.put(message);
						}
					} catch (Exception e) {
						System.out.println("Error reading from server: " + e.getMessage());
						isConnected = false;
					}
				}
			}).start();
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
			messageQueue.take();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean login(String user, String pass) {
		if (!isConnected) {
			return false;
		}
		writer.println("login " + user + " " + pass);
		try {
			String response = messageQueue.take();
			if (response.equals("valid")) {
				this.username = user;
				this.pass= pass;
				this.isLoggedIn = true;
				return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println("Something went wrong with login: " + e.getMessage());
			return false;
		}
	}
	
	public boolean register(String user, String pass, String nationality, String age, String photo, String color) {
		if (!isConnected) {
	        return false;
	    }
	    writer.println("register " + user + " " + pass + " " + nationality + " " + age + " " + photo + " " + color);

	    try {
	        String response = messageQueue.take();
	        if (response.equals("valid")) {
	            this.username = user;
	            this.isLoggedIn = false;
	            return true;
	        } else {
	            return false;
	        }
	    } catch (Exception e) {
	        System.err.println("Something went wrong with register: " + e.getMessage());
	        return false;
	    }
	}
	
	public boolean logout() {
		if (!isConnected || !isLoggedIn) {
			return false;
		}
		writer.println("logout");
		try {
			String response = messageQueue.take();
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
			String response = messageQueue.take();
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
	
	public boolean changeData(String type, String changeValue) {
		if (!isConnected || !isLoggedIn) {
			return false;
		}
		writer.println("change "+ type + " " + changeValue);
		try {
			String response = reader.readLine();
			String[] parts = response.split(" ");
			if (parts[0].equals("valid")) {
				return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println("Something went wrong with changedata: " + e.getMessage());
			return false;
		}
	}
	
	public String[] getWof() {
		if (!isConnected) {
			return null;
		}
		String[] wofData = new String[15];
		writer.println("getXML walloffame");
		try {
			String response = messageQueue.take();
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
	
	public boolean matchmake() {
		if (!isConnected || !isLoggedIn) {
			return false;
		}
		if (isMatchmaking) {
			return false; // Already matchmaking
		}
		writer.println("matchmake");
		try {
			String answer = messageQueue.take();
			if (answer.equals("valid")) {
				isMatchmaking = true;
			}
			return isMatchmaking;
		} catch (Exception e) {
			System.out.println("Something went wrong with matchmake: " + e.getMessage());
			return false;
		}
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass= pass;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isConnected() {
		if (isConnected == false) {
			return false;
		} else {
			if (!checkStatus()) {
				username = null;
				isConnected = false;
				isLoggedIn = false;
				isMatchmaking = false;
				reader = null;
				writer = null;
				socket = null;
			}
			return isConnected;
		}
	}
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	public boolean isMatchmaking() {
		return isMatchmaking;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public String getPlayerNum() {
		return playerNum;
	}
	
	public String getOpponentUsername() {
		return opponentUsername;
	}
	
	public boolean isYt() {
		return yt;
	}
	
	public String getBoard() {
		return board;
	}
	
	public void setBoard(String board) {
		this.board = board;
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
