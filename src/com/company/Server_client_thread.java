package com.company;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server_client_thread extends Thread {
    Socket serverClient;
    int clientNo;
    int squre;
    Inverted_index index_map;


    public Server_client_thread(Socket inSocket, int counter, Inverted_index my_index) {
        serverClient = inSocket;
        clientNo = counter;
        this.index_map = my_index;

    }

    public void run() {
        try {
            DataInputStream in_from_client = new DataInputStream(serverClient.getInputStream());
            DataOutputStream out_to_client = new DataOutputStream(serverClient.getOutputStream());
            String client_message = "", server_message = "";
            while (!client_message.equals("bye")) {
                client_message = in_from_client.readUTF();
                System.out.println("Phrase from Client-" + clientNo + ": " + client_message);
//                squre = Integer.parseInt(client_message) * Integer.parseInt(client_message);
                LinkedList<String> server_response_list = index_map.get_files_by_phrase(client_message);
//                server_message="From Server to Client-" + clientNo + " Square of " + client_message + " is " +squre;
                server_message = Inverted_index.list_to_client_responce(server_response_list, client_message);
                out_to_client.writeUTF(server_message);
                System.out.println("Response to Client-" + clientNo + " on phrase " + client_message + " have send");
                out_to_client.flush();
            }
            in_from_client.close();
            out_to_client.close();
            serverClient.close();
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            System.out.println("Client -" + clientNo + " exit!! ");
        }
    }
}