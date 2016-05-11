package android.testnavigation.Requests;

import io.socket.client.Socket;

public class MySocket {
    private static Socket socket;

    public static synchronized void connect(Socket socket){
        MySocket.socket = socket;
    }

    public static synchronized Socket getSocket(){
        return socket;
    }
}