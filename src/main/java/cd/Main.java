package cd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.util.Vector;

public class Main extends JFrame{
    private Socket socket;
    private String account;
    private JList list;
    private DefaultListModel model;

    private Vector<String> data;//所有登录的用户数据


    public void setAccount(String account) {
        this.account = account;
    }


    public Main(Socket socket,String account) {
        this.socket = socket;
        this.account = account;

        setTitle("jj象棋----------" + account);
        //设置窗口的大小
        setSize(400, 300);
        //设置窗口居中
        setLocationRelativeTo(null);
        //设置点击关闭按钮同时结束虚拟机，每一个Java运行的程序都一个虚拟机
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        String[] labels = {"客户端A", "客户端B", "客户端C"};
        model = new DefaultListModel();
//        for (int i = 0; i < labels.length; i++) {
//            model.addElement(labels[i]);
//        }

        list = new JList(model);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("双击" + list.getSelectedIndex());
                    String to = data.elementAt(list.getSelectedIndex());
                    Message req = new Message();
                    req.setType(Message.Type.FIGHT);
                    req.setFrom(account);
                    req.setTo(to);
                    SocketUtil.send(socket,req);
                }
            }
        });
        JScrollPane scrollPane1 = new JScrollPane(list);
        add(scrollPane1, BorderLayout.CENTER);
        setVisible(true);


        getClientList();
    }

    ClientThread ct;

    private void getClientList() {
        Message req = new Message();
        req.setType(Message.Type.LIST);
//        req.setFrom(account);
        SocketUtil.send(socket,req);

        new ClientThread(socket, new ClientThread.ResponseListener() {

            @Override
            public void success(Message resp) {
                if(resp.getType() == Message.Type.SUCCESS) {
                    model.clear();
                    data = (Vector<String>) resp.getContent();
                    data.forEach(item -> {
                        model.addElement(item);
                    });

                    list.validate();

                }else if (resp.getType() == Message.Type.FIGHT_SUCCESS){
//                    GameFrame gf = new GameFrame();
                    GameFrame gf = new GameFrame(socket,account,resp.getTo());
                    Main.this.dispose();   //隐藏窗口

                    if (account.equals(resp.getFrom())){
                        gf.setPlayer(resp.getFromPlayer());
                        gf.setLocked(false);        //红方开始不锁定棋盘
                        gf.setAccount(resp.getFrom());
                        gf.setTo(resp.getTo());
                    }else{
                        gf.setPlayer(resp.getToPlayer());
                        gf.setLocked(true);   //黑方开始锁定棋盘
                        gf.setAccount(resp.getTo());
                        gf.setTo(resp.getFrom());
                    }
                    System.out.println("前面出错了");
                    ct.setShutdown(true);
                    System.out.println("这里出错了");
                }
            }
        }).start();


        SocketUtil.getInstance().sendClientList();
        ClientReceiveThread cst = ClientReceiveThread.getInstance();
        cst.setListener(new ClientReceiveThread.ResponseListener() {
            @Override
            public void response(Message resp) {
                if (resp.getType() == Message.Type.SUCCESS) {
                    model.clear();
                    data = (Vector<String>) resp.getContent();
                    data.forEach(item -> {
                        model.addElement(item);
                    });

                    list.validate();
                } else if (resp.getType() == Message.Type.FIGHT_SUCCESS) {
                    GameFrame gf = new GameFrame(socket,account,resp.getTo());//打开游戏界面
                    if (SocketUtil.getInstance().getReq().getFrom().equals(resp.getFrom())) {
                        gf.setPlayer(0);
                        SocketUtil.getInstance().getReq().setTo(resp.getTo());
                        gf.setLocked(false);//红方一开始不锁定棋盘
                    } else {
                        gf.setPlayer(1);
                        SocketUtil.getInstance().getReq().setTo(resp.getFrom());
                        gf.setLocked(true);//红方一开始锁定棋盘
                    }
                    Main.this.dispose();//隐藏窗口
                }
            }
        });
    }

    public static void main(String[] args) {
//        new Main();
    }
}
