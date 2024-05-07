import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class Server {
    private static final int PORT = 6060;
    private ServerSocket serverSocket;
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> answersGiven = new ConcurrentHashMap<>();
    private final String[] questions = {"What is 1+1?", "What is the capital of France?", "What is 2*2?"};
    private final String[] answers = {"2", "Paris", "4"};

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
    }

    public void acceptClients() {
        System.out.println("Accepting clients...");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, this)).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    public synchronized void addClient(String username, ClientHandler clientHandler) {
        clients.put(username, clientHandler);
        scores.put(username, 0);
        answersGiven.put(username, 0);
        clientHandler.sendQuestion(questions[0]);  // Send the first question to the client immediately upon joining.
    }

    public synchronized void removeClient(String username) {
        if (clients.containsKey(username)) {
            clients.remove(username);
            scores.remove(username);
            answersGiven.remove(username);
            System.out.println(username + " has left the quiz.");
            checkIfAllDone(); // Check if all have done when a user leaves to handle sudden disconnects.
        }
    }

    public synchronized void receiveAnswer(String answer, String username, ClientHandler handler) {
        int questionIndex = answersGiven.get(username);
        if (answers[questionIndex].equalsIgnoreCase(answer.trim())) {
            int newScore = scores.get(username) + 1;
            scores.put(username, newScore);
            handler.sendMessage("Correct answer. Your score: " + newScore);
        } else {
            handler.sendMessage("Wrong answer.");
        }

        if (questionIndex < questions.length - 1) {
            answersGiven.put(username, questionIndex + 1);
            handler.sendQuestion(questions[questionIndex + 1]);
        } else {
            handler.sendMessage("You have completed the quiz.");
            answersGiven.put(username, questions.length); // Mark that this client is done
            checkIfAllDone(); // Check after marking the client as done
        }
    }

    private void checkIfAllDone() {
        boolean allDone = answersGiven.values().stream().allMatch(count -> count >= questions.length);
        if (allDone) {
            announceWinner();
        }
    }

    private void announceWinner() {
        Optional<Map.Entry<String, Integer>> maxEntry = scores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        String winnerMessage = maxEntry.map(entry -> "Winner is: " + entry.getKey() + " with a score of: " + entry.getValue())
                .orElse("No winner.");

        broadcastMessage(winnerMessage);
    }

    private void broadcastMessage(String message) {
        clients.values().forEach(client -> client.sendMessage(message));
    }

    private void closeServer() {
        try {
            serverSocket.close();
            System.out.println("Server closed after announcing the winner.");
        } catch (IOException e) {
            System.out.println("Error closing the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.acceptClients();
    }
}
