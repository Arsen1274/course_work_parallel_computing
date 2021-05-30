import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        int PORT = 8888;
        int NUNMBER_THREADS = 4;
        String rootDataPath = "F:/Repository/parallel_computing/datasets/aclImdb/";
        String stopWordsPath = "F:/Repository/parallel_computing/stop_words.txt";
        int V = 25;
        int coef = 1;
        int N_end = ((12500 / 50 * V)) / coef;
        int N_start = (12500 / 50 * (V - 1)) / coef;

        //Створюємо об'єкт класу InvertedIndex
        InvertedIndex invertedIndex = new InvertedIndex(NUNMBER_THREADS, N_start, N_end, rootDataPath, stopWordsPath);
        System.out.println("Building inverted index...");
        //будуємо інвертований індекс
        invertedIndex.buildIndexParallel();

        try {
            ServerSocket server = new ServerSocket(PORT);
            int clientCounter = 0;
            System.out.println("Server Started ....");
            while (true) {
                clientCounter++;
                //чекаємо надхлдження клієнта
                Socket serverClient = server.accept();
                System.out.println(" >> " + "Client Num:" + clientCounter + " started!");
                //створюємо новий потік для клієнта
                ServerClientThread sct = new ServerClientThread(serverClient, clientCounter, invertedIndex);
                sct.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}



