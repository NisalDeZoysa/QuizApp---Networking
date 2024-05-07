import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client() {
        try {
            socket = new Socket("localhost", 6060);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            new Thread(this::readMessages).start();

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = userInput.readLine();
                writer.println(input);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void readMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}

