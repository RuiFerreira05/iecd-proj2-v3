package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
	private boolean end = true;
	private String favColor = null;
	
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private Socket socket = null;
	
	private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
	
	private String playerNum = null;
	private String opponentUsername = null;
	private boolean yt = false;
	private String board = null;
	public String xmlNat= "";
	public String xmlUser= "";
	public String xmlWof= "";
	private boolean boardChanged = false;
	
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
						//System.out.println(parts[0]);
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
							System.out.println("board updated:\n" + message);
							boardChanged = true;
							board = parts[1];
						} else if(parts[0].equals("foundXML") || !end) {
							end= false;
							xmlNat += message;
							if(message.contains("end")) {
								end= true;
								messageQueue.put(xmlNat);
							}
						} else if (parts[0].equals("foundUsers") || !end) {
							end = false;
							xmlUser = message;
							if (message.contains("end")) {
								end = true;
								messageQueue.put(xmlUser);
							}
						} else if (parts[0].equals("foundWall") || !end) {
							end = false;
							xmlWof = message;
							if (message.contains("end")) {
								end = true;
								messageQueue.put(xmlWof);
							}
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
	
	public String checkOppoMove() {
		return messageQueue.poll();
	}
	
	public boolean login(String user, String pass) {
		if (!isConnected) {
			return false;
		}
		writer.println("login " + user + " " + pass);
		try {
			System.out.println(messageQueue.size());
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
	
	public boolean register(String user, String pass, String nationality, String age, String favcolor, String profilePicture) {
		if (!isConnected) {
			System.out.println("uh oh");
	        return false;
	    }
		System.out.println("user: " + user);
		System.out.println("pass: " + pass);
		System.out.println("nat: " + nationality);
		System.out.println("age: " + age);
		System.out.println("color: " + favcolor);
		System.out.println("photo: " + profilePicture);
	    writer.println("register " + user + " " + pass + " " + nationality + " " + age + " " + favcolor + " " + profilePicture);

	    try {
	        String response = messageQueue.take();
	        System.out.println(response);
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
	
	public String move(int x, int y) {
		if (!isConnected || !isLoggedIn || !isPlaying || !yt) {
			return null;
		}
		yt = false;
		writer.println("mv " + x + " " + y);
		try {
			return messageQueue.take();
		} catch (Exception e) {
			System.out.println("Something went wrong with move: " + e.getMessage());
			return null;
		}
	}
	
	public boolean surrender() {
		if (!isConnected || !isLoggedIn || !isPlaying) {
			return false;
		}
		writer.println("surrender");
		try {
			String messageString = messageQueue.take();			
			if (messageString.equals("valid")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
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
			//System.out.println(response);
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
			String response = messageQueue.take();
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
	
	public void updateWof() {
		if (isConnected) {
			xmlWof= "";
			writer.println("getXML walloffame");
		}
	}
	
	public void updateNationalities() {
		if (isConnected) {
			xmlNat= "";
			end= true;
			writer.println("getXML nationalities");
		}
	}
	
	public void updateUsers() {
		if (isConnected) {
			xmlUser = "";
			writer.println("getXML users");
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
	
	public String getFavColor() {
		return favColor;
	}
	
	public void setFavColor(String favColor) {
		this.favColor = favColor;
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
	
	public void setYt(boolean yt) {
		this.yt = yt;
	}
	
	public String getBoard() {
		boardChanged = false;
		return board;
	}
	
	public void setBoard(String board) {
		this.board = board;
	}
	
	public boolean isBoardChanged() {
		return boardChanged;
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
	
	public void setOpponentUsername(String opponentUsername) {
		this.opponentUsername = opponentUsername;
	}
	
	public void setPlayerNum(String playerNum) {
		this.playerNum = playerNum;
	}

	public String getXmlNat() {
		try {
			if(xmlNat.isEmpty()) {
				updateNationalities();
				return messageQueue.take();
			} else {
				return xmlNat;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getXmlWof() {
		try {
			if(xmlWof.isEmpty()) {
				updateWof();
				return messageQueue.take();
			} else {
				return xmlWof;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getXmlUser() {
		try {
			if(xmlUser.isEmpty()) {
				updateUsers();
				return messageQueue.take();
			} else {
				return xmlUser;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public String[] getAllUsernames() {
	    String xmlUsers = getXmlUser();
	    if (xmlUsers == null) {
	    	return new String[0];
	    }
	    xmlUsers = xmlUsers.replace("foundUsers ", "").replace("end", "").trim();
	    String[] users = xmlUsers.split(";");
	    List<String> usernames = new ArrayList<>();
	    for (String user : users) {
	        String trimmed = user.trim();
	        if (!trimmed.isEmpty()) {
	            usernames.add(trimmed);
	        }
	    }
	    return usernames.toArray(new String[0]);
	}
	
	public String[] getUsersFame() {
	    String xmlWall = getXmlWof();
	    if (xmlWall == null) {
	    	return new String[0];
	    }
	    xmlWall = xmlWall.replace("foundWall ", "").replace("end", "").trim();
	    String[] users = xmlWall.split(" ");
	    return users;
	}
}
