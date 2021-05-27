package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int V = 25;
        int coef = 10;
        int N_end = ((12500 / 50 * V))/coef;
        int N_start = (12500 / 50 * (V-1))/coef;
        String dir_path = "F:/Repository/parallel_computing/datasets/aclImdb/test/neg/";

        LinkedList<String> stop_words = read_stop_words("stop_words.txt");

        String temp_dir_path = dir_path;
        for (int j = 0; j < 2; j++) {
            for (int n = 0; n < 2; n++) {
                System.out.println("\n------------------------------------------" + temp_dir_path + "------------------------------------------\n" );

                check_all_files(temp_dir_path,N_start,N_end,stop_words);

                temp_dir_path = temp_dir_path.replace("/neg/","/pos/");
            }
            temp_dir_path = temp_dir_path.replace("/test/","/train/");
            temp_dir_path = temp_dir_path.replace("/pos/","/neg/");
        }


        temp_dir_path = temp_dir_path.replace("/neg/","/unsup/");
        System.out.println("\n------------------------------------------" + temp_dir_path + "------------------------------------------\n" );
        check_all_files(temp_dir_path,N_start*4,N_end*4,stop_words);


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

    public static void check_all_files(String temp_dir_path,int N_start,int N_end,LinkedList<String> stop_words) throws FileNotFoundException {
        for (int i = N_start; i < N_end; i++) {
            System.out.print(i + ") ");
            String temp_path = file_with_mark(i,temp_dir_path);
            File file = new File(temp_path);
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                String word  = input.next();
                word = stylize(word);
                if(word.length() == 0 || stop_words.contains(word)){
                    continue;}
                System.out.print("["+word + "] ");
            }
            System.out.println();
        }
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
        while (input.hasNext()) {
            String word  = input.next();
            System.out.print(word+" ");
            stop_words.add(word);

        }

        return stop_words;
    }
}
