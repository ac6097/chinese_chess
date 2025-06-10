package cd;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SocketUtil {
    private static SocketUtil instance = new SocketUtil();
    private Socket socket;

    private Message req = new Message();
    private Message resp;

    private SocketUtil(){

    }

    public static SocketUtil getInstance() {
        return instance;
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Message getReq() {
        return req;
    }

    public void setReq(Message req) {
        this.req = req;
    }

    public Message getResp() {
        return resp;
    }

    public void setResp(Message resp) {
        this.resp = resp;
    }

    public void sendLoginReq(Object content){
        req.setType(Message.Type.LOGIN);
        req.setContent(content);
        send(socket,req);
    }

    public void sendClientList(){
        req.setType(Message.Type.LIST);
        send(socket,req);
    }

    public void sendFightReq(String to){
        req.setType(Message.Type.FIGHT);
        req.setTo(to);
        send(socket,req);
    }


    //创建Socket对象
    public static Socket create(String ip, int port){
        try{
            return new Socket(InetAddress.getByName(ip),port);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    //连接本地就不需要ip地址
    public static Socket createLocalHost(int port){
        try{
            System.out.println("正在连接服务器...........");
            return new Socket(InetAddress.getLocalHost(),port);
//            System.out.println("成功连接到服务器");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void send(Socket s,Message msg) {
        if(s == null || s.isClosed()){
            throw new IllegalArgumentException("Socket is null or closed");
        }
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = s.getOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static Object receive(Socket s) {
            InputStream is = null;
            ObjectInputStream ois = null;
            try {
                is = s.getInputStream();
                ois = new ObjectInputStream(is);
                return ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }


    public void close(InputStream is, OutputStream os){
        if (is != null){
            try{
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (os != null){
            try{
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRegReq(User bean) {
        try {
            Message req = new Message();
            req.setType(Message.Type.REG);
            req.setData(bean);
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            oos.writeObject(req);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendForgetPasswordReq(String account) {
        try {
            Message req = new Message();
            req.setType(Message.Type.FORGET);
            req.setData(account);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
