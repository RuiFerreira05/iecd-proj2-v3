package tp1.server;

import java.io.*;
import java.net.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tp1.server.game.Game;
import tp1.server.game.Player;


/**
 * Classe que implementa uma tarefa que tem como função gerir um socket criado para
 * a comunicação entre o cliente e o servidor.
 */
public class Connection extends Thread{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;
    private Player player;

    private boolean playing;
    private Game game;
    private boolean matchmaking;
    private boolean isMyTurn;
    private char myChar;
    private Connection opponent;
    private boolean gsflag;
    private Long playtime;

    private String message;

    
    public Connection(Server server, Socket client) {
        this.server = server;
        this.client = client;
        this.player = null;

        this.playing = false;
        this.game = null;
        this.matchmaking = false;
        this.isMyTurn = false;
        this.myChar = 'd';
        this.opponent = null;
        this.gsflag = true;
        this.playtime = 0L;

        this.message = "";

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                if (in.ready()) {
                    message = in.readLine();
                }
                if (!playing) {
                    if (!message.isEmpty()) {
                        checkMessage(message);
                        message = "";
                    }
                } else {
                    handleGame();
                }
                sleep(10);
            }
        } catch (Exception e) {
            server.getRequestHandler().getConnections().remove(this);
            this.interrupt();
        }
    }

    public void resetPlayingAttributes() {
        this.playing = false;
        this.game = null;
        this.matchmaking = false;
        this.isMyTurn = false;
        this.myChar = 'd';
        this.opponent = null;
        this.gsflag = true;
    }

    private String getMove() {
        message = "";

        Thread readerThread = new Thread(() -> {
            try {
                message = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        readerThread.start();

        Long startTime = System.currentTimeMillis();
        while (true) {
            if (!message.isEmpty()) {
                this.playtime += System.currentTimeMillis() - startTime;
                return message;
            } else if (System.currentTimeMillis() - startTime >= 30000) { // 30 seconds timeout
                this.playtime += System.currentTimeMillis() - startTime;
                readerThread.interrupt();
                return "timeout";
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return "timeout";
                }
            }
        }
    }

    public void handleGame() {
        if (gsflag) {
            myChar = (game.getC1() == this ? game.getPlayer1Char() : game.getPlayer2Char());
            opponent = (game.getC1() == this ? game.getC2() : game.getC1());
            String otherPlayerUsername = isMyTurn ? game.getC2().getPlayer().getUsername() : game.getC1().getPlayer().getUsername();
            out.println("gs " + (isMyTurn ? "p1 " : "p2 ") + otherPlayerUsername + (isMyTurn ? " yt" : ""));
            out.println("board " + game.boardToString());
            gsflag = false;
        }
        
        if (isMyTurn) {
            try {
                message = getMove();
            } catch (Exception e) {
                handleGameDisconnect();
            }
            String[] parts = message.split(" ");
            if (parts[0].equals("mv")) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);

                if (!game.play((game.getC1() == this ? game.getPlayer1Char() : game.getPlayer2Char()), x, y)) {
                    out.println("invalid");
                    out.println("board " + game.boardToString());
                } else {
                    if (game.isGameWon() == myChar) {
 
                        String myTimeString = calcGameTime(this.playtime);
                        String theirTimeString = calcGameTime(opponent.playtime);

                        out.println("ge pw " + (game.getC1() == this ? "p1" : "p2"));
                        opponent.getOutput().println("ge pw " + (game.getC1() == this ? "p1" : "p2"));

                        this.player.setVictories(this.player.getVictories() + 1);
                        this.opponent.getPlayer().setDefeats(opponent.getPlayer().getDefeats() + 1);

                        this.player.getGameTimes().add(myTimeString);
                        this.opponent.getPlayer().getGameTimes().add(theirTimeString);

                        game.getC1().setPlaying(false);
                        game.getC2().setPlaying(false);
                    } else if (game.isTie()) {

                        String myTimeString = calcGameTime(this.playtime);
                        String theirTimeString = calcGameTime(opponent.playtime);

                        out.println("ge tie");
                        opponent.getOutput().println("ge tie");

                        this.player.getGameTimes().add(myTimeString);
                        this.opponent.getPlayer().getGameTimes().add(theirTimeString);

                        game.getC1().setPlaying(false);
                        game.getC2().setPlaying(false);
                    } else {
                        isMyTurn = false;
                        out.println("valid");
                        out.println("board " + game.boardToString());
                        opponent.setMyTurn(true);
                        opponent.getOutput().println("yt");
                        opponent.getOutput().println("board " + game.boardToString());
                    }
                }
            } else if (message.equals("surrender")) {
                handleSurrender();
            } else if (message.equals("timeout")) {
                handleTimeout();
            }
        }
    }

    private void handleTimeout() {
        if (game != null) {

            String myTimeString = calcGameTime(this.playtime);
            String theirTimeString = calcGameTime(opponent.playtime);

            opponent.getOutput().println("ge pw " + (game.getC1() == this ? "p2" : "p1"));

            player.setDefeats(player.getDefeats() + 1);
            player.getGameTimes().add(myTimeString);
            opponent.getPlayer().getGameTimes().add(theirTimeString);
            opponent.getPlayer().setVictories(opponent.getPlayer().getVictories() + 1);

            this.opponent.setPlaying(false);
            this.setPlaying(false);
            this.opponent.resetPlayingAttributes();
            this.resetPlayingAttributes();
        }
    }

    private void handleSurrender() {
        if (game != null) {

            String myTimeString = calcGameTime(this.playtime);
            String theirTimeString = calcGameTime(opponent.playtime);

            out.println("valid");

            player.setDefeats(player.getDefeats() + 1);
            player.getGameTimes().add(myTimeString);
            opponent.getPlayer().getGameTimes().add(theirTimeString);
            opponent.getPlayer().setVictories(opponent.getPlayer().getVictories() + 1);
            if (game.getC1() == this) {
                opponent.getOutput().println("ge pw p2");
                opponent.setPlaying(false);
            } else {
                opponent.getOutput().println("ge pw p1");
                opponent.setPlaying(false);
            }
            this.setPlaying(false);
        }
    }

    private void handleGameDisconnect() {
        if (game != null) {

            String myTimeString = calcGameTime(this.playtime);
            String theirTimeString = calcGameTime(opponent.playtime);

            if (game.getC1() == this) {
                game.getC2().getOutput().println("ge pw p2");
                game.getC2().setPlaying(false);
                game.getC2().getPlayer().setVictories(game.getC2().getPlayer().getVictories() + 1);
            } else {
                game.getC1().getOutput().println("ge pw p1");
                game.getC1().setPlaying(false);
                game.getC1().getPlayer().setVictories(game.getC1().getPlayer().getVictories() + 1);
            }
            this.player.setDefeats(this.player.getDefeats() + 1);
            this.player.getGameTimes().add(myTimeString);
            this.opponent.getPlayer().getGameTimes().add(theirTimeString);

            this.server.getRequestHandler().getConnections().remove(this);
            this.interrupt();
        }
    }

    private String calcGameTime(Long playtime) {
        int minutes = (int) (playtime / 60000);
        int seconds = (int) ((playtime % 60000) / 1000);
        String finalTime = minutes + " minutes and " + seconds + " seconds";
        this.playtime = 0L; // Reset playtime after calculating
        return finalTime;
    }

    /**
     * Método que processa a mensagem enviada pelo cliente de acordo com o protocolo
     * desenvolvido de modo a realizar o que é pedido pelo cliente.
     * Protocolo: ação username password nacionalidade idade foto
     * "ação" indica o tipo de ação desejada pelo cliente por exemplo obter informação (getdata).
     * "username" é um parâmetro que apenas é necessário para algumas ações e indica o nome de utilizador do cliente
     * "password" também é necessário apenas para algumas ações e indica a palavra passe do cliente.
     * "nacionalidade" também é necessário apenas para algumas ações e indica a nacionalidade do cliente.
     * "idade" também é necessário apenas para algumas ações e indica a idade do cliente.
     * "foto" também é necessário apenas para algumas ações e contem a foto de perfil do cliente.
     * @param Message
     */
    public void checkMessage(String Message) {
        String[] parts = Message.split(" ");
        switch (parts[0]) {

            //Ação para obter informação sobre o cliente 
            case "getdata":
                getdata(parts[1]);
                break;
            
            //Ação para guardar informação sobre o cliente 
            case "register":
                register(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5], parts[6]);
                break;
            
            //Ação que permite dar login ao cliente
            case "login":
                login(parts[1], parts[2]);
                break;
            
            //Ação que encontra outro jogador que também esteja conectado com o servidor e 
            //pronto para jogar
            case "matchmake":
                matchmake();
                break;
            
            //Ação que permite interromper a conexão do cliente com o servidor
            case "exit":
                exit();
                break;
            
            //Ação que permite ao cliente sair de sessão
            case "logout":
                logout();
                break;

            case "change":
                change(parts[1], parts[2]);
                break;

            case "getXML":
                getXML(parts[1]);
                break;    

            case "status":
                out.println("valid");

            default:
                break;
        }
    }

    public void getdata(String username) {
        Player search = server.searchPlayer(username);
        if (search != null) {
            String message= "found " + search.getUsername() + " " + search.getNacionality() + " " + search.getAge() + " " + search.getColor()+ " " + search.getPhoto() + " " + search.getVictories() + " " + search.getDefeats() + " " + search.getGameTimes().size();
            // for(int i=0; i < search.getGameTimes().size(); i++){
            //     message+= " " + search.getGameTimes().get(i);
            // }
            out.println(message);
        } else {
            out.println("invalid");
        }
    }

    public void register(String username, String password, String nacionality, int age, String color, String foto) {
        if (server.register(username, password, nacionality, age, color, foto, this)) {
            out.println("valid");
            player = server.searchPlayer(username);
        } else {
            out.println("invalid");
        }
    }

    public void login(String username, String password) {
        if (server.login(username, password, this)) {
            out.println("valid");
            player = server.searchPlayer(username);
        } else {
            out.println("invalid");
        }
    }

    public void matchmake() {
        if (player == null) {
            out.println("invalid");
            return;
        }
        resetPlayingAttributes();
        matchmaking = true;
        server.addToMatchmaker(player, this);
        out.println("valid");
    }

    public void exit() {
        try {
            out.println("valid");
            in.close();
            out.close();
            client.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void change(String type, String changeValue) {
        if (server.change(type, changeValue, this)) {
            out.println("valid");
        } else {
            out.println("invalid");
        }
    }

    public void getXML(String xml_name){
        String xml= "";
        if(xml_name.equals("nationalities")){
            NodeList nats = server.getNationalities().getElementsByTagName("nationality");
            for(int i=0; i < nats.getLength() ; i++){
                Element nat= (Element) nats.item(i);
                String nats_info= nat.getElementsByTagName("abbreviation").item(0).getTextContent() + " " ;
                xml += nats_info;
            }
            System.out.println(xml.length());
            out.println("foundXML " + xml + "end");
        }
        if(xml_name.equals("walloffame")){
            for(int i=0; i < this.server.getWall().size(); i++){
                String player_info= this.server.getWall().get(i).getPhoto() + " " + this.server.getWall().get(i).getUsername() + " " + this.server.getNationalityFlag(this.server.getWall().get(i).getNacionality()) + " ";
                xml += player_info;
            }
            out.println("foundWall " + xml + "end");
        }
        if(xml_name.equals("users")) {
            NodeList users = server.getUsers().getElementsByTagName("user");
            for(int i=0; i < users.getLength() ; i++) {
                Element user = (Element) users.item(i);
                String user_info = user.getElementsByTagName("username").item(0).getTextContent();
                xml += user_info + ";";
            }
            out.println("foundUsers " + xml + "end");
        }
    }

    public static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public void logout() {
        player = null;
        out.println("valid");
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BufferedReader getInput() {
        return in;
    }

    public PrintWriter getOutput() {
        return out;
    }

    public Boolean isPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isMatchmaking() {
        return matchmaking;
    }

    public void setMatchmaking(boolean matchmaking) {
        this.matchmaking = matchmaking;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }
    
    public Server getServer() {
    	return this.server;
    }
}