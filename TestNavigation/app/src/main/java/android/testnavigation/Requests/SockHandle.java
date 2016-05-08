package android.testnavigation.Requests;


import io.socket.client.Socket;


public class SockHandle {
    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SockHandle.socket = socket;
    }
}