package com.company;
import java.net.*;
import java.io.*;
public class Client {

    public static void main(String[] args) throws Exception {
        try{
            Socket socket=new Socket("127.0.0.1",8888);
            DataInputStream in_from_server = new DataInputStream(socket.getInputStream());
            DataOutputStream out_to_server = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String client_message="",server_message="";
            while(!(client_message=="bye")){
                System.out.println("Enter phrase to search or bye:");
                client_message=br.readLine();
                out_to_server.writeUTF(client_message);
                out_to_server.flush();
                server_message=in_from_server.readUTF();
                System.out.println(server_message);
            }
            out_to_server.close();
            out_to_server.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
}