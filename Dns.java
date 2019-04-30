
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * This class implements java Socket server
 *
 * @author pankaj
 *
 */
public class Dns {

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 5000;
    private static ArrayList<Node> nodeList = new ArrayList<Node>();

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        //create the socket server object
        System.out.println("DNS Server IP : " + NetworkUtil.getCurrentEnvironmentNetworkIp());
        System.out.println("Starting DNS Server on port : " + port);
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            System.out.println("Menunggu request...");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            String msg[] = message.split(" ");

            Node node = new Node();
            node.setProcName(msg[0]);
            node.setIpAddress(msg[1]);
            node.setPort(Integer.valueOf(msg[2]));
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            if (!nodeList.contains(node)) {
                System.out.println(node.getProcName() + " belum terdaftar, mendaftarkan...");
                nodeList.add(node);
                System.out.println("Registered : " + node.getProcName() + " " + node.getIpAddress() + " " + node.getPort());

                //write object to Socket
                oos.writeObject("Client berhasil terdaftar, record id " + nodeList.indexOf(node));
            }

            //close resources
            ois.close();
            oos.close();
            // oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if (message.equalsIgnoreCase("exit")) {
                break;
            }
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();

    }

}

class Node {

    private String procName;
    private String ipAddress;
    private int port;

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}

