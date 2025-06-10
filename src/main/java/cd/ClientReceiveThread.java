package cd;

import java.net.Socket;

public class ClientReceiveThread extends Thread{
    private static ClientReceiveThread instance = new ClientReceiveThread();
    private Socket socket;
    private ResponseListener listener;
    private boolean isOver = true;

    public interface ResponseListener {
        void response(Message resp);
    }

    public void setListener(ResponseListener listener) {
        this.listener = listener;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private ClientReceiveThread() {
    }

    public static ClientReceiveThread getInstance() {
        return instance;
    }


    @Override
    public void run() {
        try {
            while (isOver) {
                Object receive = SocketUtil.getInstance().receive(socket);
                System.out.println(receive);
                if (receive instanceof Message) {
                    Message resp = (Message) receive;
                    if (listener != null) {
                        listener.response(resp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
