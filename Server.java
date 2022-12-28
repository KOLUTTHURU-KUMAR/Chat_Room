import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    public static void main(String[] args) throws Exception {
        ArrayList<Socket> clients = new ArrayList<>();
        HashMap<String, Socket> clientNameList = new HashMap<String, Socket>();
        HashMap<Socket, String> socketsList = new HashMap<Socket, String>();

        ServerSocket ss = new ServerSocket(5000);
        System.out.println("Server is started...");
        while (true) {
            Socket socket = ss.accept();
            clients.add(socket);
            new ThreadServer(socket, clients, clientNameList, socketsList).start();
        }
    }
}

class ThreadServer extends Thread {

    private Socket socket;
    private ArrayList<Socket> clients;
    private HashMap<String, Socket> clientNameList;
    private HashMap<Socket, String> socketsList;
    
    public ThreadServer(Socket socket, ArrayList<Socket> clients, HashMap<String, Socket> clientNameList, HashMap<Socket, String> socketsList) {
        this.socket = socket;
        this.clients = clients;
        this.clientNameList = clientNameList;
        this.socketsList = socketsList;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            while (true) {
                String outputString = input.readLine();
                if (outputString.equals("logout")) {
                    throw new SocketException();
                }

                if (!clientNameList.containsKey(socket)) {
                    String[] messageString = outputString.split(":", 2);
                    clientNameList.put(messageString[0], socket);
                    socketsList.put(socket, messageString[0]);
                    System.out.println(outputString);
                    try {
                        String[] pc = outputString.split(";", 2);
                        if (!pc[1].equals("")) {
                            privchat(pc[0], pc[1]);
                        } else {
                            showMessageToAllClients(socket, outputString);
                        }
                    } catch (Exception e) {
                        showMessageToAllClients(socket, outputString);
                    }
                    
                } else {
                    String[] pc = outputString.split(";", 2);
                    if (!pc[1].equals("")) {
                        privchat(pc[0], pc[1]);
                    } else {
                        System.out.println(outputString);
                        showMessageToAllClients(socket, outputString);
                    }
                    
                }
            }
        } catch (SocketException e) {
            String printMessage = socketsList.get(socket).toString() + "has left the chat room";
            System.out.println(printMessage);
            showMessageToAllClients(socket, printMessage);
            clients.remove(socket);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void showMessageToAllClients(Socket sender, String outputString) {
        Socket socket;
        PrintWriter printWriter;
        int i = 0;
        while (i < clients.size()) {
            socket = clients.get(i);
            i++;
            try {
                if (socket != sender) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println(outputString);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    private void privchat(String outputString, String receiver) throws Exception {
        Socket sock;
        PrintWriter printWriter; 
        sock = clientNameList.get(receiver);
        printWriter = new PrintWriter(sock.getOutputStream(), true);
        printWriter.println(outputString + "(\\whispers)");
    }
}
