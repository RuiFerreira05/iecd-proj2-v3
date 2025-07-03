package tp1.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import tp1.server.game.Game;
import tp1.server.game.Player;


public class Server {

    private final List<Player> players;
    private List<Connection> matchmakingPlayers;
    private List<Player> wallofFame;
    private RequestHandler rh;
    private Scanner scanner;
    private int defaultServerPort;
    private Document doc; 
    private Document nationalities;
    private Document wallFame;

    public Server() {
        players = new ArrayList<>();
        wallofFame= new ArrayList<>();
        loadPlayers();
        loadWallFame();
        parseNationalitiesFile("src/main/resources/nationalities.xml");
        matchmakingPlayers = new ArrayList<>(2);
        scanner = new Scanner(System.in);
        this.defaultServerPort = 3004; // Default port    
    }

    public void loadPlayers() {
        parseXmlFile("src/main/resources/users.xml");
        validateXml(this.doc, "src/main/resources/users.xsd");

        NodeList users= this.doc.getElementsByTagName("user");
        for(int i=0; i < users.getLength(); i++){
        	Element user= (Element) users.item(i);
        	String username= user.getElementsByTagName("username").item(0).getTextContent();
        	String password= user.getElementsByTagName("password").item(0).getTextContent();
        	int age= Integer.parseInt(user.getElementsByTagName("age").item(0).getTextContent());
            String color= user.getElementsByTagName("color").item(0).getTextContent();
        	String nacionality= user.getElementsByTagName("nacionality").item(0).getTextContent();
        	String foto= user.getElementsByTagName("foto").item(0).getTextContent();
        	Player player= new Player(username, password, nacionality, age, color, foto);
            this.players.add(player);
            player.setVictories(Integer.parseInt(user.getElementsByTagName("victories").item(0).getTextContent()));
            player.setDefeats(Integer.parseInt(user.getElementsByTagName("defeats").item(0).getTextContent()));
            NodeList games= user.getElementsByTagName("games").item(0).getChildNodes();
            if(games.getLength() != 0) {
            	 for(int j=0; j < games.getLength(); j++) {
            		 if (games.item(j).getNodeType() == Node.ELEMENT_NODE) {
            			 Element game= (Element) games.item(j);
            	         player.getGameTimes().add(game.getElementsByTagName("time").item(0).getTextContent()); 
            		 }	 
                 }
            }
        }
    }

    public void loadWallFame() {
        parseWallFameFile("src/main/resources/wall_of_fame.xml");
        validateXml(this.wallFame, "src/main/resources/wall_of_fame.xsd");

        NodeList users= this.wallFame.getElementsByTagName("user");
        for(int i= 0; i < users.getLength(); i++){
            Element user= (Element) users.item(i);
            String username= user.getElementsByTagName("username").item(0).getTextContent();
            for(int j=0; j < this.players.size(); j++){
                if(username.equals(this.players.get(j).getUsername())){
                    this.wallofFame.add(this.players.get(j));
                }
            }
        }
    }

    public void parseXmlFile (String filePath){
        File xmlFile= new File(filePath);

        DocumentBuilderFactory dbfactory= DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= null;
        try {
            builder= dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            this.doc= builder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void parseNationalitiesFile (String filePath){
        File xmlFile= new File(filePath);

        DocumentBuilderFactory dbfactory= DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= null;
        try {
            builder= dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            this.nationalities= builder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void parseWallFameFile (String filePath){
        File xmlFile= new File(filePath);

        DocumentBuilderFactory dbfactory= DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= null;
        try {
            builder= dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            this.wallFame= builder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void validateXml (Document doc, String xsdPath){
        SchemaFactory schemaFactory= SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile= new StreamSource(new File(xsdPath));
        Schema schema= null;
        try {
            schema= schemaFactory.newSchema(schemaFile);
            schema.newValidator().validate(new DOMSource(doc));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUpdatedXml(String path, Document document){
    	try {
			OutputStream outstream= new FileOutputStream(path);
			// DOMSource domSource= new DOMSource(this.doc);
            DOMSource domSource= new DOMSource(document);
			StreamResult result= new StreamResult(outstream);
			TransformerFactory transfact= TransformerFactory.newInstance();
			Transformer trans= transfact.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.transform(domSource, result);
			outstream.close();
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
    }

    public void savePlayers() {
        NodeList users= this.doc.getElementsByTagName("user");
        int aux= users.getLength();
    	for(int j=aux -1; j >= 0; j--) {
    		users.item(j).getParentNode().removeChild(users.item(j));
    	}
    	NodeList nodes= this.doc.getElementsByTagName("users").item(0).getChildNodes();
    	int aux2=nodes.getLength();
    	for(int k=aux2 -1; k >= 0; k--) {
    		if(!nodes.item(k).hasChildNodes()) {
    			nodes.item(k).getParentNode().removeChild(nodes.item(k));
    		}
    	}
        for(int i=0; i < this.players.size(); i++){
            if(this.players.get(i) != null){
                Element user= this.doc.createElement("user");
                Element nome= this.doc.createElement("username");
                nome.appendChild(this.doc.createTextNode(this.players.get(i).getUsername()));
                Element pass= this.doc.createElement("password");
                pass.appendChild(this.doc.createTextNode(this.players.get(i).getPassword()));
                Element age= this.doc.createElement("age");
                age.appendChild(this.doc.createTextNode(Integer.toString(this.players.get(i).getAge())));
                Element nacionality= this.doc.createElement("nacionality");
                nacionality.appendChild(this.doc.createTextNode(this.players.get(i).getNacionality()));
                Element color= this.doc.createElement("color");
                color.appendChild(this.doc.createTextNode(this.players.get(i).getColor()));
                Element foto= this.doc.createElement("foto");
                foto.appendChild(this.doc.createTextNode(this.players.get(i).getPhoto()));
                Element victories= this.doc.createElement("victories");
                victories.appendChild(this.doc.createTextNode(Integer.toString(this.players.get(i).getVictories())));
                Element defeats= this.doc.createElement("defeats");
                defeats.appendChild(this.doc.createTextNode(Integer.toString(this.players.get(i).getDefeats())));
                Element games= this.doc.createElement("games");
                ArrayList<String> times= this.players.get(i).getGameTimes();
                if(!times.isEmpty()){
                    for(int j=0; j < times.size(); j++){
                        Element game= this.doc.createElement("game");
                        Element time= this.doc.createElement("time");
                        time.appendChild(this.doc.createTextNode(times.get(j)));
                        game.appendChild(time);
                        games.appendChild(game);
                    }
                }
                user.appendChild(nome);
                user.appendChild(pass);
                user.appendChild(age);
                user.appendChild(nacionality);
                user.appendChild(color);
                user.appendChild(foto);
                user.appendChild(victories);
                user.appendChild(defeats);
                user.appendChild(games);

                this.doc.getElementsByTagName("users").item(0).appendChild(user);
            }
        }
        
        writeUpdatedXml("src/main/resources/users.xml", this.doc);
    }

    public void saveWallFame(){
        NodeList users= this.wallFame.getElementsByTagName("user");
        int aux= users.getLength();
    	for(int j=aux -1; j >= 0; j--) {
    		users.item(j).getParentNode().removeChild(users.item(j));
    	}
    	NodeList nodes= this.wallFame.getElementsByTagName("wallfame").item(0).getChildNodes();
    	int aux2=nodes.getLength();
    	for(int k=aux2 -1; k >= 0; k--) {
    		if(!nodes.item(k).hasChildNodes()) {
    			nodes.item(k).getParentNode().removeChild(nodes.item(k));
    		}
    	}

        for(int i= 0; i < this.wallofFame.size(); i++){
            Element user= this.wallFame.createElement("user");
            Element place= this.wallFame.createElement("place");
            place.appendChild(this.wallFame.createTextNode(Integer.toString(i+1) + "º"));
            Element photo= this.wallFame.createElement("photo");
            photo.appendChild(this.wallFame.createTextNode(this.wallofFame.get(i).getPhoto()));
            Element username= this.wallFame.createElement("username");
            username.appendChild(this.wallFame.createTextNode(this.wallofFame.get(i).getUsername()));
            Element flag= this.wallFame.createElement("flag");
            flag.appendChild(this.wallFame.createTextNode(getNationalityFlag(this.wallofFame.get(i).getNacionality())));

            user.appendChild(place);
            user.appendChild(photo);
            user.appendChild(username);
            user.appendChild(flag);
            this.wallFame.getElementsByTagName("wallfame").item(0).appendChild(user);
        }
        writeUpdatedXml("src/main/resources/wall_of_fame.xml", this.wallFame);
    }

    public String getNationalityFlag(String nationality){
        NodeList nationalitiesList= this.nationalities.getElementsByTagName("nationality");
        String flag= "";
        for(int i=0; i < nationalitiesList.getLength(); i++){
            Element nat= (Element) nationalitiesList.item(i);
            if(nat.getElementsByTagName("abbreviation").item(0).getTextContent().equals(nationality)){
                flag= nat.getElementsByTagName("flag").item(0).getTextContent();
            }
        }
        return flag;
    }

    public String getUserFavouriteColor(String username) {
        NodeList usersList = this.doc.getElementsByTagName("user");
        for (int i = 0; i < usersList.getLength(); i++) {
            Element userElement = (Element) usersList.item(i);
            String uName = userElement.getElementsByTagName("username").item(0).getTextContent();
            if (uName.equals(username)) {
                return userElement.getElementsByTagName("color").item(0).getTextContent();
            }
        }
        return "#000000"; // Default color if not found
    }

    public void updateWallFame(){
        List<Player> players_copy= this.players;
        players_copy.sort((a, b) -> { return compareVictories(a, b);});

        this.wallofFame.clear();
        int top= (this.players.size() < 5 )? this.players.size() : 5;
        for(int i= 0; i < top; i++){
            this.wallofFame.add(players_copy.get(i));
        }
    }

    public int compareVictories( Player a, Player b){
        if(a.getVictories() == b.getVictories()){
            if(timesAverage(b) < timesAverage(a)){
                return 1;
            }
            else{
                return -1;
            }
        }
        return b.getVictories() - a.getVictories();
    }

    public int timesAverage(Player player){
        int sum= 0;
        for(int i= 0; i < player.getGameTimes().size(); i++){
            String[] parts = player.getGameTimes().get(i).split(" ");
            int tempo_sec= Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[3]);
            sum += tempo_sec;
        }
        return sum/player.getGameTimes().size();
    }


    public void startServer(int port) {
        rh = new RequestHandler(this, port); 
        rh.start(); //Começar a tarefa que vai tratar de pedidos de conexão 
        System.out.println("Server started on port " + port);
    }

    // Starts the server on the default port (3004)
    public void startServer() {
        startServer(this.defaultServerPort);
    }

    public void stopServer() {
        rh.stopListening();
        savePlayers();
        saveWallFame();
    }

    public Player searchPlayer(String username) {
        synchronized (players) {
            for (Player player : players) {
                if (player.getUsername().equals(username)) {
                    return player;
                }
            }
        }
        return null;
    }

    public boolean login(String username, String password, Connection connection) {
        synchronized (players) {
            Player player = searchPlayer(username);
            if (player != null && player.getPassword().equals(password)) {
                player.setSocket(connection.getClient());
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean register(String username, String password, String nacionality, int age, String color, String foto, Connection connection) {
        synchronized (players) {
            for (Player player : players) {
                if (player.getUsername().equals(username)) {
                    return false;
                }
            }
            if(!nacionality.isEmpty()){
                boolean exists= false;
                NodeList nationalities= this.nationalities.getElementsByTagName("abbreviation");
                for (int i = 0; i < nationalities.getLength(); i++) {
                    if(nationalities.item(i).getTextContent().equals(nacionality)){
                        exists= true;
                    }
                }
                if(!exists){
                    return false;
                }
            }
            if(!(color.charAt(0) == '#' && color.length() == 7)){
                return false;
            }
            Player newPlayer = new Player(username, password, nacionality, age, color, foto);
            newPlayer.setSocket(connection.getClient());
            players.add(newPlayer);
            return true;
        }
    }

    public void addToMatchmaker(Player player, Connection connection) {
        synchronized (matchmakingPlayers) {
            matchmakingPlayers.add(connection);
            if (matchmakingPlayers.size() == 2) {
                Connection c1 = matchmakingPlayers.get(0);
                Connection c2 = matchmakingPlayers.get(1);

                Game g = new Game(c1, c2);
                c1.setPlaying(true);
                c1.setMatchmaking(false);
                c1.setGame(g);

                c2.setPlaying(true);
                c2.setMatchmaking(false);
                c2.setGame(g);

                c1.setMyTurn(true);

                matchmakingPlayers.clear();
                updateWallFame();
            }
        }
    }

    public boolean change(String type, String changeValue, Connection connection){
        synchronized (players) {
            switch (type) {
                case "username":
                    if(changeValue instanceof String && !changeValue.isEmpty()){
                        if(searchPlayer(changeValue) == null){
                            connection.getPlayer().setUsername(changeValue);
                            return true;
                        }
                    }
                    break;
                case "password":
                    if(changeValue instanceof String){
                        connection.getPlayer().setPassword(changeValue);
                        return true;
                    }
                    break;   
                case "nacionality":
                    if(changeValue instanceof String && !changeValue.isEmpty()){
                        NodeList nationalities= this.nationalities.getElementsByTagName("abbreviation");
                        for (int i = 0; i < nationalities.getLength(); i++) {
                            if(nationalities.item(i).getTextContent().equals(changeValue)){
                                connection.getPlayer().setNacionality(changeValue);
                                return true;
                            }
                        }
                    }    
                    break;   
                case "age":
                    if(changeValue.matches(".*\\d.*")){ //Verifica se a string é constituída por números
                        connection.getPlayer().setAge(Integer.parseInt(changeValue));
                        return true;
                    }    
                    break;
                case "color":
                    if(changeValue.charAt(0) == '#' && changeValue.length() == 7){
                        connection.getPlayer().setColor(changeValue);
                        return true;
                    }
                    break;
                case "photo":
                    if(changeValue instanceof String && !changeValue.isEmpty()){
                        connection.getPlayer().setPhoto(changeValue);
                        return true;
                    }    
                    break;         
                default:
                    System.out.println("Command type not recognized");
                    return false;
            }
            return false;
        }
    }


    public void consoleCommands(String command) {
        command = command.toLowerCase();
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "exit":
                System.out.println("Server shutting down...");
                scanner.close();
                this.stopServer();
                System.exit(0);
                break;

            case "connections":
                if (!rh.isAlive()) {
                    System.out.println("Server is not running.");
                    break;
                }
                System.out.println("Connections: " + rh.getConnections().size());
                break;

            case "start-server":
                if (parts.length == 2) {
                    try {
                        int port = Integer.parseInt(parts[1]);
                        System.out.println("Server starting on port " + port + "...");
                        this.startServer(port);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number.");
                    }
                } else if (parts.length == 1) {
                    System.out.println("Server starting on default port ("+this.defaultServerPort+")...");
                    this.startServer();
                }
                break;

            case "stop-server":
                if (!rh.isAlive()) {
                    System.out.println("Server is not running.");
                    break;
                }
                System.out.println("Server stopping...");
                this.stopServer();
                System.out.println("Server stopped.");
                break;

            case "reset-server":
                if (!rh.isAlive()) {
                    System.out.println("Server is not running.");
                    break;
                }
                System.out.println("Server resetting...");
                int port = rh.getServerPort();
                this.stopServer();
                this.startServer(port);
                break;

            case "matchmaking":
                if (!rh.isAlive()) {
                    System.out.println("Server is not running.");
                    break;
                }
                if (matchmakingPlayers.isEmpty()) {
                    System.out.println("No players in matchmaking queue.");
                } else {
                    System.out.println("Matchmaking players: " + matchmakingPlayers.size());
                    for (Connection conn : matchmakingPlayers) {
                        System.out.println("Player: " + conn.getPlayer().getUsername());
                    }
                }
                break;
        
            default:
                System.out.println("Command not recognized...");
                break;
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        while (true) {
            System.out.print("server >> ");
            String command = server.scanner.nextLine();
            server.consoleCommands(command);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getWall() {
        return wallofFame;
    }

    public List<Connection> getMatchmakingPlayers() {
        return matchmakingPlayers;
    }

    public RequestHandler getRequestHandler() {
        return rh;
    }

    public Document getNationalities(){
        return this.nationalities;
    }

    public Document getWallofFame(){
        return this.wallFame;
    }

    public Document getUsers(){
        return this.doc;
    }
}