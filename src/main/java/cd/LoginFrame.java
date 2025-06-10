package cd;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField tfAccount;
    private JPasswordField tfPassword;

    private Socket socket;

    public LoginFrame() {
        setTitle("jj象棋");
        //设置窗口的大小
        setSize(400, 300);
        //设置窗口居中
        setLocationRelativeTo(null);
        //设置点击关闭按钮同时结束虚拟机，每一个Java运行的程序都一个虚拟机
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //使用绝对布局
        setLayout(null);
        //账号文字
        JLabel lbAccount = new JLabel("账号");
        lbAccount.setBounds(50, 50, 50, 40);
        add(lbAccount);
        //账号输入框
        tfAccount = new JTextField();
        tfAccount.setBounds(110, 50, 200, 40);
        add(tfAccount);
        //账号文字
        JLabel lbPaasword = new JLabel("密码");
        lbPaasword.setBounds(50, 100, 50, 40);
        add(lbPaasword);
        //账号输入框
        tfPassword = new JPasswordField();
        tfPassword.setBounds(110, 100, 200, 40);
        add(tfPassword);
        //登录按钮
        JButton btnLogin = new JButton("登录");
        btnLogin.setBounds(50, 150, 260, 40);

        btnLogin.setActionCommand("login");
        btnLogin.addActionListener(this);
        add(btnLogin);
        //注册按钮
        JButton btnReg = new JButton("注册");
        btnReg.setBounds(50, 200, 120, 40);

        btnReg.setActionCommand("reg");
        btnReg.addActionListener(this);
        add(btnReg);
        //登录按钮
        JButton btnForget = new JButton("忘记密码");
        btnForget.setBounds(190, 200, 120, 40);

        btnForget.setActionCommand("forget");
        btnForget.addActionListener(this);
        add(btnForget);


        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "login":
                login();
                break;
            case "reg":
                reg();
                break;
            case "forget":
                forget();
                break;
            default:
                break;
        }
    }


    private void login() {
        String account = tfAccount.getText();
        char[] password = tfPassword.getPassword();
        String passwordStr = new String(password);
        User bean = new User(account, passwordStr);

        if (socket == null) {

            socket = SocketUtil.createLocalHost(8080);
//            SocketUtil.getInstance().setSocket(socket);








        }

        // 发送检查用户是否注册的请求
        Message checkRequest = new Message();
        checkRequest.setType(Message.Type.CHECK_REG);
        checkRequest.setContent(account);

        try {
            SocketUtil.send(socket, checkRequest);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接已关闭，请重新登录！");
            return;
        }

        // 接收服务器响应
        Object checkResponse = SocketUtil.receive(socket);
        if (checkResponse instanceof Message) {
            Message resp = (Message) checkResponse;
            if (resp.getType() == Message.Type.REG_NOT_FOUND) {
                // 用户未注册，提示用户注册
                JOptionPane.showMessageDialog(this, "用户未注册，请先注册！");
                return;
            } else if (resp.getType() == Message.Type.REG_FOUND) {
                System.out.println("[DEBUG] 收到 REG_FOUND，继续执行登录流程。");
                System.out.println("收到的信息类型为REG_FOUND");
                
                // 从数据库加载用户信息
                User user = null;
                try {
                    user = DBUtil.loadUser(account);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "加载用户信息时发生错误: " + e.getMessage());
                    return;
                }

                if (user != null) {
                    System.out.println("[DEBUG] 成功加载用户: " + user.getAccount());
                    System.out.println("[DEBUG] 加载的用户对象详情: " + user.toString());

                    // 创建并发送登录请求
                    Message request = new Message();
                    request.setType(Message.Type.LOGIN);
                    request.setContent(user);
                    request.setFrom(user.getAccount());

                    try {
                        SocketUtil.send(socket, request);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "连接已关闭，请重新登录！");
                        return;
                    }

                    // 接收服务器响应
                    Object response = SocketUtil.receive(socket);
                    if (response instanceof Message) {
                        Message loginResp = (Message) response;
                        if (loginResp.getType() == Message.Type.SUCCESS) {
                            // 登录成功，启动游戏主界面
                            JOptionPane.showMessageDialog(this, "登录成功！");
                            LoginFrame.this.dispose();
                            new Main(socket, user.getAccount());
                        } else {
                            JOptionPane.showMessageDialog(this, "登录失败，请检查账号或密码");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "无法加载用户信息，请重新登录。");
                    return;
                }
            }
        }



    }

    private void reg() {
        String account = tfAccount.getText();
        char[] password = tfPassword.getPassword();
        String passwordStr = new String(password);

        // 检查 socket 是否已初始化
        if (socket == null || socket.isClosed()) {
            socket = SocketUtil.createLocalHost(8080); // 重新初始化 socket
        }

        // 创建注册请求
        Message request = new Message();
        request.setType(Message.Type.REG);
        request.setContent(new User(account, passwordStr));

        // 发送请求到服务器
        try {
            SocketUtil.send(socket, request);
            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接已关闭，请重新登录！");
            return;
        }

        // 接收服务器响应
        Object response = SocketUtil.receive(socket);
        if (response instanceof Message) {
            Message resp = (Message) response;
            if (resp.getType() == Message.Type.REG_SUCCESS) {
                JOptionPane.showMessageDialog(this, "注册成功，请登录");
            } else {
                JOptionPane.showMessageDialog(this, resp.getContent().toString());
            }
        }
    }

    private void forget() {
        String account = tfAccount.getText();

        // 创建忘记密码请求
        Message request = new Message();
        request.setType(Message.Type.FORGET);
        request.setContent(account);

        // 发送请求到服务器
        SocketUtil.send(socket, request);

        // 接收服务器响应
        Object response = SocketUtil.receive(socket);
        if (response instanceof Message) {
            Message resp = (Message) response;
            if (resp.getType() == Message.Type.FORGET_SUCCESS) {
                JOptionPane.showMessageDialog(this, resp.getContent().toString());
            } else {
                JOptionPane.showMessageDialog(this, resp.getContent().toString());
            }
        }
    }
}
