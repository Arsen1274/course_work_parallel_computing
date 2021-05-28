package com.company;

import java.io.FileNotFoundException;

public class Server {
    public static void main(String[] args) throws FileNotFoundException {
        int V = 25;
        int coef = 1;
        int N_end = ((12500 / 50 * V)) / coef;
        int N_start = (12500 / 50 * (V - 1)) / coef;
        String dir_path = "F:/Repository/parallel_computing/datasets/aclImdb/";
        String stop_words_path = "stop_words.txt";
        Inverted_index my_index = new Inverted_index(N_start, N_end, dir_path, stop_words_path);


        my_index.build_index();
        my_index.get_files_by_phrase("Excel home videos");

    }
}
