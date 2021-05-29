import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Inverted_index {
    private ConcurrentHashMap<String, LinkedList<String>> my_map;
    private int NUNMBER_THREADS;
    private int N_end;
    private int N_start;
    private static String dir_path;
    private String stop_words_path;
    private LinkedList<String> dir_sources;
    private LinkedList<String> stop_words;


    Inverted_index(int NUNMBER_THREADS, int N_start, int N_end, String dir_path, String stop_words_path) throws FileNotFoundException, InterruptedException {
        this.NUNMBER_THREADS = NUNMBER_THREADS;
        this.N_end = N_end;
        this.N_start = N_start;
        this.dir_path = dir_path;
        this.stop_words_path = stop_words_path;

    }




    public void build_index_parallel() throws FileNotFoundException, InterruptedException {
        int N_end = this.N_end;
        int N_start = this.N_start;
        String dir_path = this.dir_path;

        this.stop_words = read_stop_words(this.stop_words_path);
        this.dir_sources = create_dir_source_list(dir_path);
        ConcurrentHashMap<String, LinkedList<String>> my_map = new ConcurrentHashMap<String, LinkedList<String>>();


        Index_thread thread_array[] = new Index_thread[this.NUNMBER_THREADS];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < this.NUNMBER_THREADS; i++) { //розбиття на потоки
            thread_array[i] = new Index_thread(i, NUNMBER_THREADS, N_start, N_end, dir_path, this.dir_sources, stop_words, my_map);
            thread_array[i].start();
        }
        for (int i = 0; i < this.NUNMBER_THREADS; i++) { //очікування завершення усіх потоків
            thread_array[i].join();
        }
        long stopTime = System.currentTimeMillis();
        this.my_map = my_map;
        System.out.println(NUNMBER_THREADS + " threads parrallel algorythm build index in " + (stopTime - startTime) + " ms");
    }

    public void build_index() throws FileNotFoundException {
        int N_end = this.N_end;
        int N_start = this.N_start;
        String dir_path = this.dir_path;

        this.stop_words = read_stop_words(this.stop_words_path);
        this.dir_sources = create_dir_source_list(dir_path);
        ConcurrentHashMap<String, LinkedList<String>> my_map = new ConcurrentHashMap<String, LinkedList<String>>();

        long time = System.currentTimeMillis();

        for (int j = 0; j < this.dir_sources.size(); j++) {
            String temp_source_path = this.dir_sources.get(j);
            if (j == (this.dir_sources.size() - 1)) {
                N_start = N_start * 4;
                N_end = N_end * 4;
            }
            for (int i = N_start ; i < N_end; i ++) {
                String temp_path = Inverted_index.file_with_mark(i, temp_source_path);
                File file = new File(temp_path);
                Scanner input = null;
                try {
                    input = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (input.hasNext()) {
                    String word = input.next();
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
                }
            }
        }

        System.out.println("Inverted index bild 1 thread for " + (System.currentTimeMillis() - time) + " ms");
        this.my_map = my_map;
    }

    public static String file_with_mark(int num, String dir) {
        int min_mark = 0;
        int max_mark = 10;
        if (dir.contains("/neg/")) {
            max_mark = 4;
        } else if (dir.contains("/pos/")) {
            min_mark = 5;
        }
        String path = "";
        for (int i = min_mark; i <= max_mark; i++) {
//            System.out.println(i);
            File f1 = null;
            path = dir + String.valueOf(num) + "_" + String.valueOf(i) + ".txt";
//            System.out.println(path);
            f1 = new File(path);
            if (f1.exists()) {
//                System.out.print(path + " ");
                return path;
            }
        }
        return "Err last path:" + path;
    }

    public static String stylize(String word) {

        word = word.replaceAll("<br", "");
        //[^A-Za-zА-Яа-я0-9] = only letters and digits
        word = word.replaceAll("[^A-Za-zА-Яа-я0-9]", " ");
        word = word.replaceAll("\\s+", " ");
        word = word.toLowerCase();

        return word;
    }


    private static LinkedList<String> read_stop_words(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner input = new Scanner(file);
        LinkedList<String> stop_words = new LinkedList<>();
        while (input.hasNext()) {
            String word = input.next();
            stop_words.add(word);
        }
        System.out.println();

        return stop_words;
    }

    private static LinkedList<String> create_dir_source_list(String dir_path) {
        LinkedList<String> dir_source = new LinkedList<String>();
        dir_path = dir_path.concat("test/neg/");
        String temp_dir_path = dir_path;
        for (int j = 0; j < 2; j++) {
            for (int n = 0; n < 2; n++) {
                dir_source.add(temp_dir_path);

                temp_dir_path = temp_dir_path.replace("/neg/", "/pos/");
            }
            temp_dir_path = temp_dir_path.replace("/test/", "/train/");
            temp_dir_path = temp_dir_path.replace("/pos/", "/neg/");
        }


        temp_dir_path = temp_dir_path.replace("/neg/", "/unsup/");
        dir_source.add(temp_dir_path);
        return dir_source;
    }

    public String get_files_by_phrase(String key) {
        ConcurrentHashMap<String, LinkedList<String>> my_map = this.my_map;
        LinkedList<String> key_list = new LinkedList<String>();
        LinkedList<String> result_paths_list = new LinkedList<String>();

        key = stylize(key);
        String[] words = key.split(" ");
        for (String word : words) {
            if (word.length() == 0 || this.stop_words.contains(word)) {
                continue;
            }
            key_list.add(word);
        }
        if (key_list.size() == 0){
            return "Your phrase is incorrect. It contains only stop words and/or symbols";
        }else if (key_list.size() == 1) {
            String temp_key = key_list.get(0);
            if (my_map.containsKey(temp_key)) {
                result_paths_list = my_map.get(temp_key);
                return list_to_client_responce(result_paths_list,temp_key);
            } else {
            }
        } else {
            result_paths_list = my_map.get(key_list.get(0));
            if (result_paths_list == null) {
                return "Key: " + key + " not found!";
            }
            for (int i = 1; i < key_list.size(); i++) {
                LinkedList<String> current_list = my_map.get(key_list.get(i));
                if (current_list == null) {
                    return "Key: " + key + " not found!";
                }
                result_paths_list.retainAll(current_list);
            }
            return list_to_client_responce(result_paths_list,key);
        }
        return "Key: " + key + " not found!";
    }

    private void print_list(LinkedList<String> list, String name) {
        String dir_path = this.dir_path;
        System.out.println(name + ":");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ") " + dir_path + list.get(i));
        }
    }

    public static String list_to_client_responce(LinkedList<String> list, String client_msg) {
        String result = "Key: " + client_msg + " is at files:\n";
        for (int i = 0; i < list.size(); i++) {
            result += (i + ") " + dir_path + list.get(i) + "\n");
        }
        return result;
    }

    public ConcurrentHashMap<String, LinkedList<String>> return_map(){
        return this.my_map;
    }

}
