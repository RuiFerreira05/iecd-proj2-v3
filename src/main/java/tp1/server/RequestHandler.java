package tp1.server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que implementa a tarefa "RequestHandler" que permite tratar de pedidos 
 * de conexão recebidos pelo servidor de modo a que o servidor não seja bloqueado 
 * enquanto está à espera de que um cliente peça para se conectar.  
 */
public class RequestHandler extends Thread {

    private int serverPort;
    private Server server;
    //Lista com todas as Tarefas que tratam de pedidos feitos por clientes já conectados. 
    //Estas tarefas vão ser responsáveis pelos sockets criados para cada cliente ao se conectarem com o servidor.
    private List<Connection> connections; 

    public RequestHandler(Server server, int serverPort) {
        this.serverPort = serverPort;
        this.server = server;
        this.connections = new ArrayList<>();
    }

    public void run() {
        //Tentar criar um socket que vai ser usado para receber pedidos de conexão ao servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                //Tarefa fica à escuta por pedidos de conexão, sendo bloqueada até receber um. 
                //Quando o receber, cria um novo socket para lidar com os pedidos do cliente que 
                //se quer conectar. Este socket, como já foi dito, vai ser gerido por outra tarefa.
                Connection c = new Connection(server, serverSocket.accept());
                connections.add(c);
                c.start();
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    public void stopListening() {
        try {
            for (Connection c : connections) {
                c.getClient().close();
            }
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}