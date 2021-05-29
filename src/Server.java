import java.net.ServerSocket;
import java.net.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        int V = 17;
        int coef = 1;
        int N_end = ((12500 / 50 * V)) / coef;
        int N_start = (12500 / 50 * (V - 1)) / coef;
        int NUNMBER_THREADS = 4;
        String dir_path = "F:/Repository/parallel_computing/datasets/aclImdb/";
        String stop_words_path = "F:/Repository/parallel_computing/stop_words.txt";


        Inverted_index my_index = new Inverted_index(NUNMBER_THREADS, N_start, N_end, dir_path, stop_words_path);
        System.out.println("Building inverted index...");
        my_index.build_index_parallel();
//        ConcurrentHashMap<String, LinkedList<String>> map_n_thread = my_index.return_map();


//        map_1_thread.forEach((k, v) -> {
//            if(!(v.size()==map_n_thread.get(k).size())){
//                System.out.print("Not Equal");
////                v.retainAll(map_n_thread.get(k));
//                System.out.println(" 1 thread:"+v.size()+"\t\t N thread:"+map_n_thread.get(k).size());
//            }
//
//        });

        my_index.get_files_by_phrase("(Trust ");

        try {
            ServerSocket server = new ServerSocket(8888);
            int counter = 0;
            System.out.println("Server Started ....");
            while (true) {
                counter++;
                Socket serverClient = server.accept();  //server accept the client connection request
                System.out.println(" >> " + "Client No:" + counter + " started!");
                Server_client_thread sct = new Server_client_thread(serverClient, counter, my_index); //send  the request to a separate thread
                sct.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}



