package tp1.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;

public class Client {

    private static final int CONNECTED_STATE = 0;
    private static final int LOGGED_IN_STATE = 1;
    private static final int MATCHMAKING_STATE = 2;
    private static final int PLAYING_STATE = 3;

    private String serverIP;
    private int port;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private int state;
    private String username;
    
    private boolean yt;
    private String playerNum;
    private String board;
    private String opponentUsername;

    private boolean scannerAux = false;
    

    public Client(boolean debug) {
        try {
            scanner = new Scanner(System.in);
            if (debug) {
                getServerCredentials();
            } else {
                serverIP = "localhost"; // Default to localhost for non-debug mode
                port = 3004; // Default port
            }
            socket = new Socket(serverIP, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            yt = false;
            playerNum = null;
            System.out.println("Connection established with server.");
            state = CONNECTED_STATE;
        } catch (Exception e) {
            System.out.println("Connection with server failed.");
            System.exit(0);
        }
    }

    private void getServerCredentials() {
        System.out.println("Enter server IP address:");
        serverIP = scanner.nextLine();
        System.out.println("Enter server port:");
        port = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    }

    public void startClient() {
        while (true) {
            switch (state) {
                case CONNECTED_STATE:
                    handleConnectedState();
                    break;
                case LOGGED_IN_STATE:
                    handleLoggedInState();
                    break;
                case MATCHMAKING_STATE:
                    handleMatchmakingState();
                    break;
                case PLAYING_STATE:
                    handlePlayingState();
                    break;
            }
        }
    }

    private void handleConnectedState() {
        System.out.println("What would you like to do?");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Get player data");
        System.out.println("4. Exit");
        System.out.print("Client >> ");
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            return; // Skip the rest of the method
        }
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1:
                login();
                break;

            case 2:
                register();
                break;

            case 3:
                getPlayerData();
                break;

            case 4:
                exit();
                break;
        
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private void handleLoggedInState() {
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Play");
        System.out.println("2. Logout");
        System.out.println("3. Get my data");
        System.out.println("4. Get player data");
        System.out.println("5. Change player data");
        System.out.println("6. Exit");
        System.out.print(username + " >> ");
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            return; // Skip the rest of the method
        }
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1:
                play();
                break;

            case 2:
                logout();
                break;

            case 3:
                getMyData();
                break;

            case 4:
                getPlayerData();
                break;

            case 5:
                changePlayerData();
                break;

            case 6:
                exit();
                break;
        
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private void displayPlayerData(String response) {
        String[] parts = response.split(" ");
        if (parts[0].equals("found")) {
            String h = "========= " + parts[1] + " =========";
            String b = "=".repeat(h.length());
            System.out.println("\n" + h);
            System.out.println();
            System.out.println("Nationality: " + parts[2]);
            System.out.println("Age: " + parts[3]);
            System.out.println("Color: " + parts[4]);
            // System.out.println("Profile Picture: " + parts[5]);
            System.out.println("Victories: " + parts[6]);
            System.out.println("Defeats: " + parts[7]);
            System.out.println("Games played: " + parts[8]);
            System.out.println();
            System.out.println(b);
        } else {
            System.out.println("Player not found");
        }
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void getMyData() {
        out.println("getdata " + username);
        try {
            String response = in.readLine();
            displayPlayerData(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePlayerData() {
        System.out.println("What would you like to change in your profile?");
        System.out.println("1. Username");
        System.out.println("2. Password");
        System.out.println("3. Nacionality");
        System.out.println("4. Age");
        System.out.println("5. Photo");
        System.out.println("6. Favourite color");
        System.out.print(username + " >> ");
        String choice= scanner.next();
        while(!(choice.equals("1") | choice.equals("2") | choice.equals("3") | choice.equals("4") | choice.equals("5") | choice.equals("6"))){
            System.out.println("Invalid input");
            scanner.nextLine();
            System.out.print(username + " >> ");
            choice= scanner.next();
        }
        String value;
        switch(choice) {
			case "1":
				System.out.println("Enter new username: ");
				value = scanner.next();
				System.out.print(value);
		        out.println("change username " + value);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println(choice + " changed successfully to " + value);
		                state = LOGGED_IN_STATE;
		                this.username= value;
		            }  
		            else {
		            	System.out.println("Username change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
			case "2":
				System.out.println("Enter new password: ");
				value = scanner.next();
		        out.println("change password " + value);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println("Password changed successfully to " + value);
		                state = LOGGED_IN_STATE;
		            }    
		            else {
		                System.out.println("Password change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
			case "3":
				System.out.println("Enter new nacionality: ");
				value = scanner.next();
		        out.println("change nacionality " + value);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println("Nacionality changed successfully to " + value);
		                state = LOGGED_IN_STATE;
		            }
		            else {
		            	System.out.println("Nacionality change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
			case "4":	
				System.out.println("Enter new age: ");
				value = scanner.next();
		        out.println("change age " + value);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println("Age changed successfully to " + value);
		                state = LOGGED_IN_STATE;
		            }    
		            else {
		                System.out.println("Age change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
			case "5":
				System.out.println("Enter new photo path: ");
                String profilePath = scanner.next(); 
                String profilePicture= "default";
                byte[] photoData= null;
                if(profilePath != null){
                    File photo= new File(profilePath);
                    if(photo.exists()){ 
                        try {
                            photoData = Files.readAllBytes(photo.toPath());
                        } catch (IOException e) {
                            System.out.println("It wasn't possible to obtain photo from file. Try again.");
                        }
                        profilePicture= Base64.getEncoder().encodeToString(photoData);
                    }
                }
                System.out.println(profilePicture);
		        out.println("change photo " + profilePicture);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println("Photo changed successfully" );
		                state = LOGGED_IN_STATE;
		            }    
		            else {
		                System.out.println("Photo change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
            case "6":
                System.out.println("Enter new favourite color: ");
				value = scanner.next();
		        out.println("change color " + value);
		        try {
		            String response = in.readLine();
		            if (response.equals("valid")) {
		                System.out.println("Favourite changed successfully to " + value);
		                state = LOGGED_IN_STATE;
		            }
		            else {
		            	System.out.println("Favourite color change failed");
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				break;
        	}
    	}


    private void handleMatchmakingState() {
        System.out.println("You are in matchmaking. Waiting for a game...");
        try {
            String response = in.readLine();
            String[] parts = response.split(" ");
            if (parts[0].equals("gs")) { //Se o cliente receber mensagem de "game start" por parte do servidor
                System.out.println("\nGame started!");
                playerNum = parts[1];
                System.out.println("You are player " + playerNum);
                opponentUsername = parts[2];
                System.out.println("your opponent is: " + opponentUsername + "\n");
                if (parts.length == 4) {
                    yt = true;
                }
                board = in.readLine().split(" ")[1];
                state = PLAYING_STATE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] getMove() {
        System.out.println("Your turn! Enter your move (row column):");
        int row;
        int col;

        Long startTime = System.currentTimeMillis();
        Thread checkerThread = new Thread(() -> {
            scannerAux = false;
            try {
                if (scanner.hasNextLine()) { // Scanners block on hasNextCall if reading from console, don't ask me why
                    scannerAux = true;
                }
            } catch (Exception e) {
            }
        });
        checkerThread.start();
        while (true) {
            if (scannerAux) {
                String input = scanner.nextLine();
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        row = Integer.parseInt(parts[0]) - 1;
                        col = Integer.parseInt(parts[1]) - 1;
                        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
                            return new int[]{row, col};
                        } else {
                            System.out.println("Invalid move. Please enter numbers between 1 and 15.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter two numbers separated by a space.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter two numbers separated by a space.");
                }
            }
            if (System.currentTimeMillis() - startTime > 30000) { // 30 seconds timeout
                checkerThread.interrupt();
                scanner = new Scanner(System.in);
                System.out.println("Timeout! You took too long to make a move.");
                return new int[]{-1, -1}; // Indicate timeout
            }
            try {
                Thread.sleep(100); // Sleep to avoid busy waiting
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for input.");
                return new int[]{-1, -1}; // Indicate interruption
            }
        }
    }

    private void handlePlayingState() {
        displayBoard();
        if (yt) {
            int row;
            int col;
            int[] move = getMove();
            row = move[0];
            col = move[1];
            if (row == -1 && col == -1) {
                System.out.println("You timed out!");
                state = LOGGED_IN_STATE;
                return;
            }
            out.println("mv " + row + " " + col);
            try {
                String response = in.readLine();
                String[] parts = response.split(" ");
                if (parts[0].equals("valid")) {

                    System.out.println("Valid move!");
                    board = in.readLine().split(" ")[1];
                    yt = false;

                } else if (parts[0].equals("invalid")) {

                    System.out.println("Invalid move! Try again.");
                    board = in.readLine().split(" ")[1];

                } else if (parts[0].equals("ge")) { //game exit

                    if (parts[1].equals("pw")) { //player win
                        System.out.println("Game over! " + (parts[2].equals(playerNum)? "You win!" : "You lose!"));
                    } else if (parts[1].equals("tie")) {
                        System.out.println("Game over! It's a tie!");
                    }
                    state = LOGGED_IN_STATE;
                }
            } catch (Exception e) {

            }
        } else {
            System.out.println("Waiting for " + opponentUsername + "'s move...");
            try {
                String response = in.readLine();
                String[] parts = response.split(" ");
                if (parts[0].equals("yt")) {
                    System.out.println("Opponent made a move!");
                    board = in.readLine().split(" ")[1];
                    yt = true;
                } else if (parts[0].equals("ge")) {
                    if (parts[1].equals("pw")) {
                        System.out.println("Game over! " + (parts[2].equals(playerNum)? "You win!" : "You lose!"));
                    } else if (parts[1].equals("tie")) {
                        System.out.println("Game over! It's a tie!");
                    }
                    state = LOGGED_IN_STATE;
                }
            } catch (Exception e) {

            }
        }
    }

    private void displayBoard() {
        int aux = 0;
        System.out.print("     ");
        for (int i = 0; i < 15; i++) {
            String columnNumber = String.valueOf(i + 1);
            System.out.print(columnNumber + (columnNumber.length() == 1 ? "  " : " "));
        }
        System.out.println();
        System.out.print("    ");
        for (int i = 0; i < 15; i++) {
            System.out.print("---");
        }
        System.out.println();
        for (int i = 0; i < 15; i++) {
            String rowNumber = String.valueOf(i + 1);
            System.out.print(rowNumber + (rowNumber.length() == 1 ? "  | " : " | "));
            for (int j = 0; j < 15; j++) {
                if (board.charAt(aux) == '1') {
                    System.out.print("B  ");
                } else if (board.charAt(aux) == '2') {
                    System.out.print("W  ");
                } else {
                    System.out.print("+  ");
                }
                aux++;
            }
            System.out.println();
        }
        System.out.println();
    }

    private void login() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        out.println("login " + username + " " + password);
        try {
            String response = in.readLine();
            if (response.equals("valid")) {
                System.out.println("Login successful!");
                state = LOGGED_IN_STATE;
                this.username = username;
            } else {
                System.out.println("Login failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Enter nationality:");
        String nationality = scanner.nextLine();
        System.out.println("Enter age:");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter favourite color:");
        String color = scanner.nextLine();
        System.out.println("Enter foto path:");
        String profilePath = scanner.nextLine(); // Placeholder for profile picture
        String profilePicture= "default";
        byte[] photoData= null;
        if(profilePath != null){
            File photo= new File(profilePath);
            if(photo.exists()){ 
                try {
                    photoData = Files.readAllBytes(photo.toPath());
                } catch (IOException e) {
                    System.out.println("It wasn't possible to obtain photo from file. Try again.");
                }
                profilePicture= Base64.getEncoder().encodeToString(photoData);
            }
        }
        out.println("register " + username + " " + password + " " + nationality + " " + age + " " + color + " " + profilePicture );
        try {
            String response = in.readLine();
            if (response.equals("valid")) {
                System.out.println("Registration successful! You are now logged in.");
                state = LOGGED_IN_STATE;
                this.username = username;
            } else {
                System.out.println("Registration failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        out.println("logout");
        try {
            String response = in.readLine();
            if (response.equals("valid")) {
                System.out.println("Logout successful!");
                state = CONNECTED_STATE;
                this.username = null;
            } else {
                System.out.println("Logout failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPlayerData() {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        out.println("getdata " + username);
        try {
            String response = in.readLine();
            displayPlayerData(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        out.println("exit");
        try {
            in.close();
            out.close();
            socket.close();
            scanner.close();
            System.out.println("Connection closed. Exiting...");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play() {
        out.println("matchmake");
        try {
            String response = in.readLine();
            if (response.equals("valid")) {
                state = MATCHMAKING_STATE;
            } else {
                System.out.println("Matchmaking failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) {
        Client client = new Client(false);
        client.startClient();
    }
}