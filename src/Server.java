import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /*
    * // Add quiz questions and scoring system
    private Map<String, Integer> scores = new HashMap<>();
    private String[] questions = {"Question 1: 3 + 2 = ?", "Question 2: Capital of France?", "Question 3: 2 * 2 = ?"};
    private String[] answers = {"5", "Paris", "4"};
    private int currentQuestion = -1;
    *
    * */

    public void startServer(){

        try{

            while (!serverSocket.isClosed()){

                // if accept is true it is return socket socket object returen that can use to communicate with client
                Socket socket =  serverSocket.accept();  // block the whole programme until client connects

                ClientHandler clientHandler = new ClientHandler(socket);
                // ClientHandler clientHandler = new ClientHandler(socket, this);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch (IOException e) {

        }
    }


    /*
    * // Modify broadcastMessage to send quiz questions
    synchronized void broadcastQuestion() {
        if (currentQuestion + 1 < questions.length) {
            currentQuestion++;
            for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
                clientHandler.sendQuestion(questions[currentQuestion]);
            }
        } else {
            announceWinner();
        }
    }
    *
    *
    synchronized void receiveAnswer(String answer, String username) {
        if (answers[currentQuestion].equalsIgnoreCase(answer.trim()) && currentQuestion != -1) {
            int newScore = scores.getOrDefault(username, 0) + 1;
            scores.put(username, newScore);
        }
        broadcastQuestion();
    }

    void announceWinner() {
        String winner = scores.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No winner!");
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clientHandler.sendWinner(winner);
        }
    }
    *
    *
    * */

    public void closeServerSocket() {
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6060);

        Server server = new Server(serverSocket);
        server.startServer();


    }


}
