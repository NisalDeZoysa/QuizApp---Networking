import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private String username; // Declare username at a higher scope

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            writer = new PrintWriter(output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            writer.println("Enter your Username to join the Quiz:");
            username = reader.readLine().trim(); // Initialize username here
            server.addClient(username, this);

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                server.receiveAnswer(inputLine, username, this);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            server.removeClient(username); // Properly handle the removal
        }
    }

    public void sendQuestion(String question) {
        writer.println(question);
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
