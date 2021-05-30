import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            DataInputStream inFromServer = new DataInputStream(socket.getInputStream());
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String clientMessage = "", serverMessage = "";
            while (true) {
                System.out.println("Enter phrase to search or '"+ServerClientThread.exitPhrase+"':");
                clientMessage = bufferedReader.readLine();
                if (clientMessage.equals(ServerClientThread.exitPhrase)) {
                    break;
                }
                outToServer.writeUTF(clientMessage);
                outToServer.flush();
                serverMessage = inFromServer.readUTF();
                System.out.println(serverMessage);
            }
            outToServer.close();
            outToServer.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}