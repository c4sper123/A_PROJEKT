package android.testnavigation.Requests;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.socket.client.Socket;

public class MySocket {
    private static Socket socket;

    public static synchronized void connect(Socket socket){
        MySocket.socket = socket;
    }

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return ( netInfo != null && netInfo.isConnected() );
    }
}