package com.company;

import java.io.FileNotFoundException;

public class Server {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        int V = 25;
        int coef = 1;
        int N_end = ((12500 / 50 * V)) / coef;
        int N_start = (12500 / 50 * (V - 1)) / coef;
        int NUNMBER_THREADS = 4;
        String dir_path = "F:/Repository/parallel_computing/datasets/aclImdb/";
        String stop_words_path = "stop_words.txt";
        Inverted_index my_index = new Inverted_index(NUNMBER_THREADS,N_start, N_end, dir_path, stop_words_path);

        my_index.build_index_parallel();

        my_index.get_files_by_phrase("excel home videos");
        my_index.get_files_by_phrase("dog ");




    }
}
