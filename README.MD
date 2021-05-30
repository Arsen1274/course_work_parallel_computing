# DESCRIPTION

This project can build an inverted index for input data. The index is building with a parallel algorithm with a variable number of threads.

An inverted index is an index data structure storing a mapping from content, such as words or numbers, to its locations in a document or a set of documents. In simple words, it is a hashmap like data structure that directs you from a word to a document or a web page.

# Installation

1. Clone repository

        git clone https://github.com/Arsen1274/course_work_parallel_computing.git

2. Change Configuration
 - Number of threads
 - Input data 
 - Stop words
 
3. Compile project

# Launching

In root folder (course_work_parallel_computing):

1) Lounch Server: 
java Server

2) Lounch Client: 
java Client 

# Configuring
 
 - Server.java
 
 
         int PORT = 8888;
         int NUNMBER_THREADS = 4;
         String rootDataPath = "F:/Repository/parallel_computing/datasets/aclImdb/";
         String stopWordsPath = "F:/Repository/parallel_computing/stop_words.txt";
 
         
         
- Client.Java

         Socket socket = new Socket("127.0.0.1",8888 );