import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InvertedIndex {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> indexMap;
    private int NUNMBER_THREADS;
    private int N_end;
    private int N_start;
    private static String rootDataPath;
    private String stopWordsPath;
    private LinkedList<String> inDataPathsList;
    private LinkedList<String> stopWordsList;


    InvertedIndex(int NUNMBER_THREADS, int N_start, int N_end, String rootDataPath, String stopWordsPath) {
        this.NUNMBER_THREADS = NUNMBER_THREADS;
        this.N_end = N_end;
        this.N_start = N_start;
        this.rootDataPath = rootDataPath;
        this.stopWordsPath = stopWordsPath;

    }


    public void buildIndexParallel() throws FileNotFoundException, InterruptedException {
        int N_end = this.N_end;
        int N_start = this.N_start;

        this.stopWordsList = readStopWords(this.stopWordsPath);
        this.inDataPathsList = createInDataPathsList(rootDataPath);
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> indexMap = new ConcurrentHashMap<>();

        IndexThread threadArray[] = new IndexThread[this.NUNMBER_THREADS];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < this.NUNMBER_THREADS; i++) { //розбиття на потоки
            threadArray[i] = new IndexThread(i, NUNMBER_THREADS, N_start, N_end, rootDataPath, inDataPathsList, stopWordsList, indexMap);
            threadArray[i].start();
        }
        for (int i = 0; i < this.NUNMBER_THREADS; i++) { //очікування завершення усіх потоків
            threadArray[i].join();
        }
        long stopTime = System.currentTimeMillis();
        this.indexMap = indexMap;
        System.out.println(NUNMBER_THREADS + " threads parrallel algorythm build index in " + (stopTime - startTime) + " ms");
    }



    private static LinkedList<String> readStopWords(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner input = new Scanner(file);
        LinkedList<String> stopWords = new LinkedList<>();
        while (input.hasNext()) {
            String word = input.next();
            stopWords.add(word);
        }

        return stopWords;
    }

    private static LinkedList<String> createInDataPathsList(String rootDataPath) {
        LinkedList<String> inDataPathsList = new LinkedList<>();
        rootDataPath = rootDataPath.concat("test/neg/");
        String tempInDataPath = rootDataPath;
        for (int j = 0; j < 2; j++) {
            for (int n = 0; n < 2; n++) {
                inDataPathsList.add(tempInDataPath);
                tempInDataPath = tempInDataPath.replace("/neg/", "/pos/");
            }
            tempInDataPath = tempInDataPath.replace("/test/", "/train/");
            tempInDataPath = tempInDataPath.replace("/pos/", "/neg/");
        }
        tempInDataPath = tempInDataPath.replace("/neg/", "/unsup/");
        inDataPathsList.add(tempInDataPath);

        return inDataPathsList;
    }

    public String getFilesByPhrase(String userPhrase) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndexMap = this.indexMap;
        LinkedList<String> userKeysList = new LinkedList<>();
        ConcurrentLinkedQueue<String> resultPathsList = new ConcurrentLinkedQueue<>();

        userPhrase = stylize(userPhrase);
        String[] userKeysArr = userPhrase.split(" ");
        for (String userKey : userKeysArr) {
            if (userKey.length() == 0 || this.stopWordsList.contains(userKey)) {
                continue;
            }
            userKeysList.add(userKey);
        }
        if (userKeysList.size() == 0) {
            return "Your phrase is incorrect. It contains only stop words and/or symbols";
        } else if (userKeysList.size() == 1) {
            String tempKey = userKeysList.get(0);
            if (invertedIndexMap.containsKey(tempKey)) {
                resultPathsList = invertedIndexMap.get(tempKey);
                return listToClientResponce(resultPathsList, tempKey);
            } else {
            }
        } else {
            resultPathsList = invertedIndexMap.get(userKeysList.get(0));
            if (resultPathsList == null) {
                return "Key: " + userPhrase + " not found!";
            }
            for (int i = 1; i < userKeysList.size(); i++) {
                ConcurrentLinkedQueue<String> currentPathsList = invertedIndexMap.get(userKeysList.get(i));
                if (currentPathsList == null) {
                    return "Key: " + userPhrase + " not found!";
                }
                resultPathsList.retainAll(currentPathsList);
            }
            return listToClientResponce(resultPathsList, userPhrase);
        }
        return "Key: " + userPhrase + " not found!";
    }

    public static String stylize(String word) {
        word = word.replaceAll("<br", "");
        //[^A-Za-zА-Яа-я0-9] = only letters and digits
        word = word.replaceAll("[^A-Za-zА-Яа-я0-9]", " ");
        word = word.replaceAll("\\s+", " ");
        word = word.toLowerCase();

        return word;
    }

    private static String listToClientResponce(ConcurrentLinkedQueue<String> pathsList, String clientMsg) {
        String result = "Key: " + clientMsg + " is at files:\n";
        int i = 0;
        for (String tempPath : pathsList) {
            result += ((i + 1) + ") " + rootDataPath + tempPath + "\n");
            i++;
        }

        return result;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> returnIndexMap() {
        return this.indexMap;
    }

}
