package cd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import game.Chess;

public class GameFrame extends JFrame implements ActionListener {

    private GamePanel gp = null;
    private int player;

    private Socket socket;

    private String account;

    private String to;   //对手的名称

    public void setAccount(String account) {
        this.account = account;
        gp.setAccount(account);
    }

    private boolean isLocked = false;

    private Message req;


    public void setTo(String to) {
        this.to = to;
        gp.setTo(to);
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
        gp.setLocked(locked);
    }

    public void setPlayer(int player) {
        this.player = player;
        gp.setCurPlayer(player);
    }

    public GameFrame(){

    }
    public GameFrame(Socket socket, String account,String to) {
        this.socket = socket;
        this.account = account;
        this.to = to;

        setTitle("------------------" + account);
        //设置窗口的大小
        setSize(560, 500);


        //设置窗口居中
        setLocationRelativeTo(null);
        //设置点击关闭按钮同时结束虚拟机，每一个Java运行的程序都一个虚拟机
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置布局管理员
        setLayout(new BorderLayout());
        //将游戏面板添加到窗口中
        gp = new GamePanel();
        gp.setAccount(account);
        gp.setSocket(socket);
        gp.setTo(to);


        gp.setGameFrame(this);

        gp.startReceive();
        add(gp, BorderLayout.CENTER);
        //添加按钮面板
        JPanel btnPanel = new JPanel(new GridLayout(4, 1));
        add(btnPanel, BorderLayout.EAST);
        JLabel hintLabel = new JLabel("红色方回合");
        btnPanel.add(hintLabel);
        gp.setHintLabel(hintLabel);

        JButton btnHuiQi = new JButton("悔棋");
        btnHuiQi.setActionCommand("huiqi");
        btnHuiQi.addActionListener(this);
        btnPanel.add(btnHuiQi);

//        JButton btnSave = new JButton("保存棋谱");
//        btnSave.setActionCommand("baocun");
//        btnSave.addActionListener(this);
//        btnPanel.add(btnSave);
//
//        JButton btnImport = new JButton("导入棋谱");
//        btnImport.setActionCommand("daoru");
//        btnImport.addActionListener(this);
//        btnPanel.add(btnImport);

        JButton btnQiuHe = new JButton("求和");
        btnQiuHe.setActionCommand("qiuhe");
        btnQiuHe.addActionListener(this);
        btnPanel.add(btnQiuHe);

        JButton btnRenShu = new JButton("认输");
        btnRenShu.setActionCommand("renshu");
        btnRenShu.addActionListener(this);
        btnPanel.add(btnRenShu);

        setVisible(true);
    }

    public static void main(String[] args) {
        GameFrame gf = new GameFrame(SocketUtil.createLocalHost(9999), "testAccount", "testTo");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("按钮被点击了");
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "huiqi":
                System.out.println("huiqi");
                Message req = new Message();
                req.setType(Message.Type.HUIQI);
                req.setFrom(account);
                req.setTo(to);
                req.setContent(new Record());
                gp.huiqi(req);
                break;
//            case "baocun":
//                System.out.println("baocun");
//                save();
//
//                break;
//            case "daoru":
//                System.out.println("daoru");
//                daoru();
//                break;
            case "qiuhe":
                System.out.println("qiuhe");
                qiuhe();
                break;
            case "renshu":
                System.out.println("renshu");
                renshu();
                break;
            default:
                break;
        }
    }


    private void renshu() {
        int result = JOptionPane.showConfirmDialog(null, "确认认输？", "认输", JOptionPane.YES_NO_OPTION);
        System.out.println(result);
        if (JOptionPane.YES_OPTION == result) {//点击Yes
            //发送求和请求消息
            Message req = new Message();
            req.setType(Message.Type.DEFEAT);
            req.setFrom(account);
            req.setTo(to);
            req.setContent(new Record());
            SocketUtil.send(socket,req);
            gp.getCt().setShutdown(true);
            this.dispose();
            new Main(socket,account);
        }
    }


    private void qiuhe() {
        int result = JOptionPane.showConfirmDialog(null, "确认向对手求和？", "求和", JOptionPane.YES_NO_OPTION);
        System.out.println(result);
        if (JOptionPane.YES_OPTION == result) {//点击Yes
            //发送求和请求消息
            Message req = new Message();
            req.setType(Message.Type.PEACE);
            req.setFrom(account);
            req.setTo(to);
            req.setContent(new Record());
            //锁定棋盘
            gp.setLocked(true);
            SocketUtil.send(socket,req);
            //求和按钮不可再点击
        }
    }



    private void daoru() {
        JFileChooser chooser = new JFileChooser();
//        chooser.setFileSelectionMode(JFileChooser.);
        int result=chooser.showOpenDialog(null);
        File parent = chooser.getSelectedFile();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(parent);
            ois = new ObjectInputStream(fis);
            Chess[] chesses = (Chess[]) ois.readObject();
            gp.setChesses(chesses);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void save() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result=chooser.showOpenDialog(null);
        File parent = chooser.getSelectedFile();
        System.out.println("parent--->"+parent);
        String path = parent.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(gp.getChesses());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
