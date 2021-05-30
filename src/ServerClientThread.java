import java.io.*;
import java.net.Socket;

public class ServerClientThread extends Thread {
    Socket serverClient;
    int clientNum;
    static String exitPhrase = "Want to exit";
    InvertedIndex invIndex;


    public ServerClientThread(Socket inSocket, int clientCounter, InvertedIndex invIndex) {
        serverClient = inSocket;
        clientNum = clientCounter;
        this.invIndex = invIndex;
    }

    public void run() {
        try {
            DataInputStream inFromClient = new DataInputStream(serverClient.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(serverClient.getOutputStream());
            String messageFromClient = "", messageToClient = "";
            while (true) {

                messageFromClient = inFromClient.readUTF();
                if (messageFromClient.equals(exitPhrase)) {
                    break;
                }
                System.out.println("Phrase from Client-" + clientNum + ": " + messageFromClient);
                messageToClient = invIndex.getFilesByPhrase(messageFromClient);
                outToClient.writeUTF(messageToClient);
                System.out.println("Response to Client-" + clientNum + " on phrase " + messageFromClient + " have send");
                outToClient.flush();
            }
            inFromClient.close();
            outToClient.close();
            serverClient.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
        } finally {
            System.out.println("Client-" + clientNum + " exit!! ");
        }
    }
}