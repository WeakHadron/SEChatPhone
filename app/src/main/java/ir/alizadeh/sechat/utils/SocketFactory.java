package ir.alizadeh.sechat.utils;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {
    private static volatile Socket socket;
    public static volatile boolean interrupted = false;

    public static Socket getSocket(String addr,int port) throws IOException {
        if (socket == null) {
            socket = new Socket(addr, port);
        }
        return socket;
    }
    public static void destroySocket() throws IOException {
        if(socket != null){
            socket.close();
            socket = null;
            interrupted = true;

        }
    }
}
