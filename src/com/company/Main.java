package com.company;
import sun.plugin.javascript.navig.Link;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int V = 25;
        int coef = 1;
        int N_end = ((12500 / 50 * V))/coef;
        int N_start = (12500 / 50 * (V-1))/coef;
        String dir_path = "F:/Repository/parallel_computing/datasets/aclImdb/";

        LinkedList<String> stop_words = read_stop_words("stop_words.txt");
        LinkedList<String> dir_source = create_dir_source_list(dir_path);
        ConcurrentHashMap<String, LinkedList<String>>  my_map = new ConcurrentHashMap<String,  LinkedList<String>>();

        long time = System.currentTimeMillis();

        for (int j = 0; j <dir_source.size() ; j++) {
            String temp_dir_path = dir_source.get(j);
//            System.out.println("\n------------------------------------------" + temp_dir_path + "------------------------------------------\n" );
            if(j ==(dir_source.size() - 1)){
                N_start = N_start * 4;
                N_end = N_end * 4;
            }
            for (int i = N_start; i < N_end; i++) {
//                System.out.print(i + ") ");
                String temp_path = file_with_mark(i,temp_dir_path);
                File file = new File(temp_path);
                Scanner input = new Scanner(file);
                while (input.hasNext()) {
                    String word  = input.next();
                    word = stylize(word);
                    if(word.length() == 0 || stop_words.contains(word)){
                        continue;}
                    if(my_map.containsKey(word)){
                        LinkedList<String> current_list = my_map.get(word);
                        if(!current_list.contains(temp_path)){
                            temp_path = temp_path.replace(dir_path,"");
                            current_list.add(temp_path);
                        }
                        else{continue;}
                    }
                    else{
                        LinkedList<String> current_list = new LinkedList<String>();
                        current_list.add(temp_path);
                        my_map.put(word,current_list);
                    }
//                    System.out.print(word + " ");
                }
//                System.out.println();
            }
        }
        System.out.println("Inverted index bild for "+ (System.currentTimeMillis() - time) + " ms");
        get_files_by_phrase("school",my_map,dir_path);
        get_files_by_phrase("just gone ",my_map,dir_path);
//        LinkedList<String> result = get_files_by_phrase("just gone",my_map);
//        print_list(result,"result");
    }

    public static String file_with_mark(int num, String dir){
        int min_mark = 0;
        int max_mark = 10;
        if (dir.contains("/neg/")){
            max_mark = 4;
        }
        else if (dir.contains("/pos/")){
            min_mark = 5;
        }
        String path = "";
        for(int i = min_mark;i <= max_mark;i++){
//            System.out.println(i);
            File f1 = null;
            path = dir+String.valueOf(num)+"_"+String.valueOf(i)+".txt";
//            System.out.println(path);
            f1 = new File(path);
            if(f1.exists()){
//                System.out.print(path + " ");
                return path;
            }
        }
        return "Err last path:"+path;
    }



    public static String stylize(String word){//,LinkedList<String> punctual_symbol){

        word = word.replaceAll("<br","");
        //[^A-Za-zА-Яа-я0-9] = only letters and digits
        word = word.replaceAll("[^A-Za-zА-Яа-я0-9]", ""); // удалится все кроме букв и цифр
        word = word.toLowerCase();

        return word;
    }

    public static LinkedList<String> read_stop_words(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner input = new Scanner(file);
        LinkedList<String> stop_words = new LinkedList<>();
        System.out.println("Stop words:");
        while (input.hasNext()) {
            String word  = input.next();
            System.out.print(word+" ");
            stop_words.add(word);

        }
        System.out.println();

        return stop_words;
    }

    public static LinkedList<String> create_dir_source_list(String dir_path){
        LinkedList<String> dir_source = new LinkedList<String>();
        dir_path = dir_path.concat("test/neg/");
        String temp_dir_path = dir_path;
        for (int j = 0; j < 2; j++) {
            for (int n = 0; n < 2; n++) {
//                check_all_files(temp_dir_path,N_start,N_end,stop_words);
                dir_source.add(temp_dir_path);

                temp_dir_path = temp_dir_path.replace("/neg/","/pos/");
            }
            temp_dir_path = temp_dir_path.replace("/test/","/train/");
            temp_dir_path = temp_dir_path.replace("/pos/","/neg/");
        }


        temp_dir_path = temp_dir_path.replace("/neg/","/unsup/");
        dir_source.add(temp_dir_path);
//        check_all_files(temp_dir_path,N_start*4,N_end*4,stop_words);
        return dir_source;
    }

    public static LinkedList<String> get_files_by_phrase(String key, ConcurrentHashMap<String, LinkedList<String>> my_map,String dir_path){
        Scanner input = new Scanner(key);
        LinkedList<String> key_list  = new LinkedList<String>();
        LinkedList<String> result_paths_list = new LinkedList<String>();
        while (input.hasNext()){
            key_list.add(input.next());
        }
//        print_list(key_list,"key_list");
        if(key_list.size() == 1){
            if(my_map.containsKey(key)){
                result_paths_list = my_map.get(key);
                System.out.println("\nWord: " + key + " exist at files:");
                print_list(result_paths_list,key,dir_path);
                return result_paths_list;
            }
            else{
                System.out.println("\nKey: " + key + " not found:");
            }
        }
        else if (key_list.size() == 2){

            result_paths_list = my_map.get(key_list.get(0));
            for (int i = 1; i < key_list.size(); i++) {
                LinkedList<String> current_list = my_map.get(key_list.get(i));
                result_paths_list.retainAll(current_list);
            }
            System.out.println("\nPhrase: " + key + " exist at files:");
            print_list(result_paths_list,key,dir_path);

            return result_paths_list;
        }
        return result_paths_list;
    }

    public static void print_list(LinkedList<String> list,String name,String dir_path){
        System.out.println(name + ":" );
        for (int i = 0; i < list.size() ; i++) {
            System.out.println(i + ") " +dir_path+ list.get(i));
        }
    }


}
