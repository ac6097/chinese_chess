package cd;

import java.net.Socket;

public class ServerThread extends Thread implements Runnable {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
//            SocketUtil.getInstance().setSocket(socket);
    }

    public Socket getSocket() {
        return socket;
    }
}