import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        int V = 25;
        int coef = 1;
        int N_end = ((12500 / 50 * V)) / coef;
        int N_start = (12500 / 50 * (V - 1)) / coef;
        int NUNMBER_THREADS = 4;
        String rootDataPath = "F:/Repository/parallel_computing/datasets/aclImdb/";
        String stopWordsPath = "F:/Repository/parallel_computing/stop_words.txt";

        InvertedIndex invertedIndex = new InvertedIndex(NUNMBER_THREADS, N_start, N_end, rootDataPath, stopWordsPath);
        System.out.println("Building inverted index...");
        invertedIndex.buildIndexParallel();

        try {
            ServerSocket server = new ServerSocket(8888);
            int clientCounter = 0;
            System.out.println("Server Started ....");
            while (true) {
                clientCounter++;
                Socket serverClient = server.accept();  //server accept the client connection request
                System.out.println(" >> " + "Client Num:" + clientCounter + " started!");
                ServerClientThread sct = new ServerClientThread(serverClient, clientCounter, invertedIndex); //send  the request to a separate thread
                sct.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}



