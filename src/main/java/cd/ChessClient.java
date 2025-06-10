package cd;

import cd.ServerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChessClient {
    private String account;
    private ServerThread st;

    public ChessClient(String testAccount) {

    }

    public ChessClient(String account, ServerThread st) {
        this.account = account;
        this.st = st;
    }

    public String getAccount() {
        return account;
    }

    public ChessClient setAccount(String account) {
        this.account = account;
        return this;
    }

    public ServerThread getSt() {
        return st;
    }

    public ChessClient setSt(ServerThread st) {
        this.st = st;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ChessClient{");
        sb.append("account='").append(account).append('\'');
        sb.append(", st=").append(st);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        try{
            Socket socket = new Socket(InetAddress.getLocalHost(),8080);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // 这里可以添加发送数据到服务器的逻辑，例如发送账号信息
            ChessClient client = new ChessClient("testAccount");
            oos.writeObject(client);
            oos.flush();

            // 接收服务器的响应数据
            Object response = null;
            try {
                response = ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("收到服务器响应: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
