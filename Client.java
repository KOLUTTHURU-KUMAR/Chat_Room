import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws Exception {
        String name = "";
        String reply = "";

        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter your name (Please enter your name to join the chat): ");
        reply = sc.nextLine();
        name = reply;

        Socket s = new Socket("localhost", 5000);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        ThreadClient threadClient = new ThreadClient(s);
        new Thread(threadClient).start(); 

        out.println(reply + ": has joined chat-room.");
        do {
            String message = (name + " : ");
            reply = sc.nextLine();
            if (reply.equals("logout")) {
                out.println("logout");
                break;
            }
            out.println(message + reply);
        } while (!reply.equals("logout"));
        sc.close();
    }
}

class ThreadClient implements Runnable {

    private Socket socket;
    private BufferedReader in;

    public ThreadClient(Socket socket) throws Exception {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = in.readLine();
                System.out.println(message);
            }
        } catch (SocketException e) {
            System.out.println("You left the chat-room");
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            try {
                in.close();
                socket.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }
}