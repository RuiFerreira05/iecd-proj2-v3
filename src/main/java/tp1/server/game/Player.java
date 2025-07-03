package tp1.server.game;

import java.net.Socket;
import java.util.ArrayList;

public class Player {

    private String username;
    private String password;
    private String nacionality;
    private int age;
    private String color;
    private String photo;
    private Socket socket;
    private int victories;
    private int defeats;
    private ArrayList<String> gameTimes; //Guarda o tempo que o player gastou em cada jogo

    public Player(String username, String password, String nacionality, int age, String color,String photo) {
        this.username = username;
        this.password = password;
        this.nacionality = nacionality;
        this.age = age;
        this.color= color;
        this.photo = photo;
        this.socket = null;
        this.victories= 0;
        this.defeats= 0;
        this.gameTimes= new ArrayList<> ();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNacionality() {
        return nacionality;
    }

    public void setNacionality(String nacionality) {
        this.nacionality = nacionality;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getColor(){
        return this.color;
    }

    public void setColor(String color){
        this.color= color;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Socket getSocket() {
        return socket;
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getVictories(){
        return this.victories;
    }

    public int getDefeats(){
        return this.defeats;
    }

    public void setVictories(int victories){
        this.victories= victories;
    }

    public void setDefeats(int defeats){
        this.defeats= defeats;
    }

    public ArrayList<String> getGameTimes(){
        return this.gameTimes;
    }

}