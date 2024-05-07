import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // helps to broadcast msg to everyone

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler (Socket socket) {
        try {
            this.socket = socket;

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);

            broadcastMessage("QuizMaster: " + clientUserName + " Has Entered the chat!");

        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    @Override
    public void run() {

        String messageFromClient;

        while(socket.isConnected()) {

            try{
                messageFromClient = bufferedReader.readLine(); //This is blocking the code. thats why we are using separate thread here
                broadcastMessage(messageFromClient);
            } catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    /*
    @Override
    public void run() {
        String fromClient;
        try {
            while (socket.isConnected()) {
                fromClient = bufferedReader.readLine();
                if (fromClient != null) {
                    server.receiveAnswer(fromClient, clientUserName); // Process answers
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    * */

    public void broadcastMessage(String messageToSend){

        for (ClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("QuizMaster: "+clientUserName+" has left the chat");
    }


    public void closeEverything(Socket socket , BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();

        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }

            if (bufferedWriter != null){
                bufferedWriter.close();
            }

            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    /*
    public void sendQuestion(String question) {
        try {
            bufferedWriter.write(question);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendWinner(String winner) {
        try {
            bufferedWriter.write("Winner is: " + winner);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
     */

}
