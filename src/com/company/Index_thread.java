package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Index_thread extends Thread {
    public ConcurrentHashMap<String, LinkedList<String>> my_map;
    int NUNMBER_THREADS;
    int N_end;
    int N_start;
    int thread_id;
    String dir_path;
    LinkedList<String> dir_source;
    LinkedList<String> stop_words;


    Index_thread(int thread_id ,int NUNMBER_THREADS,int N_start, int N_end,String dir_path, LinkedList<String> dir_source, LinkedList<String> stop_words,ConcurrentHashMap<String, LinkedList<String>> my_map) {
        this.thread_id = thread_id;
        this.NUNMBER_THREADS = NUNMBER_THREADS;
        this.N_end = N_end;
        this.N_start = N_start;
        this.dir_path = dir_path;
        this.stop_words = stop_words;
        this.my_map = my_map;
        this.dir_source = dir_source;
    }


    public void run() {
        int N_end = this.N_end;
        int N_start = this.N_start;
        String dir_path = this.dir_path;
        for (int j = 0; j < this.dir_source.size(); j++) {
            String temp_dir_path = this.dir_source.get(j);
            if (j == (this.dir_source.size() - 1)) {
                N_start = N_start * 4;
                N_end = N_end * 4;
            }
            for (int i = N_start+this.thread_id; i < N_end; i+=this.NUNMBER_THREADS) {
                //                System.out.print(i + ") ");

//                String temp_path = file_with_mark(i, temp_dir_path);
                String temp_path = Inverted_index.file_with_mark(i, temp_dir_path);
                File file = new File(temp_path);
                Scanner input = null;
                try {
                    input = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (input.hasNext()) {
                    String word = input.next();
//                    word = stylize(word);
                    word = Inverted_index.stylize(word);
                    if (word.length() == 0 || this.stop_words.contains(word)) {
                        continue;
                    }
                    if (my_map.containsKey(word)) {
                        LinkedList<String> current_list = my_map.get(word);
                        if (!current_list.contains(temp_path)) {
                            temp_path = temp_path.replace(dir_path, "");
                            current_list.add(temp_path);
                        } else {
                            continue;
                        }
                    } else {
                        LinkedList<String> current_list = new LinkedList<String>();
                        current_list.add(temp_path);
                        my_map.put(word, current_list);
                    }
                    //                    System.out.print(word + " ");
                }
                //                System.out.println();
            }
        }
    }


}
