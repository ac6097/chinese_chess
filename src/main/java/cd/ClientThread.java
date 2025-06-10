package cd;

import java.net.Socket;
import java.util.Vector;

public class ClientThread extends Thread{
    private Socket socket;
    private ResponseListener l;
    private boolean shutdown;

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public ClientThread(Socket socket, ResponseListener l) {
        this.socket = socket;
        this.l = l;
    }

    public interface ResponseListener {
        void success(Message resp);
    }


    @Override
    public void run() {
        while (!shutdown) {
            Object receive = SocketUtil.receive(socket);
//            if (receive == null) {
//                continue;
//            }
            System.out.println(receive);
            if (receive instanceof Message){
                Message resp = (Message) receive;
                if (l != null) {
                    l.success(resp);
                }

            }
        }
    }
}

