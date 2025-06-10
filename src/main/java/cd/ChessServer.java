package cd;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

//本机ip:10.135.17.7
//10.135。1.43
public class ChessServer {
    private static Map<String, ServerThread> clients;//保存登录的所有客户端线程

    public static void main(String[] args) {
        new ChessServer().start();
    }

    public void start() {
        try {
            //此处以本机为服务端
            ServerSocket server = new ServerSocket(8080);
            clients = new HashMap<>();
            System.out.println("服务启动成功");
            while (true) {
                Socket accept = server.accept();
                ServerThread st = new ServerThread(accept);
                st.start();
                if (accept == null){
                    System.out.println("无法创建Socket,请检查端口是否被占用");
                }else{
                    System.out.println("服务器已经启动,正在监听端口8080....");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerThread extends Thread implements Runnable {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
//            SocketUtil.getInstance().setSocket(socket);
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object receive = SocketUtil.receive(socket);

//                    SocketUtil.send(socket,"登录成功");
                    if (receive instanceof Message) {
                        Message request = (Message) receive;
                        switch (request.getType()) {
                            case LOGIN:
                                login(request);
                                break;
                            case REG:
                                reg(request);
                                break;
                            case DEFEAT:
                                renshu(request);
                                break;
                            case FORGET:
                                forget(request);
                                break;
                            case PEACE:
                                qiuhe(request);
                                break;
                            case PEACE_FAILURE:
                                qiuheFailure(request);
                                break;
                            case PEACE_SUCCESS:
                                qiuheSuccess(request);
                                break;
                            case HUIQI:
                                huiqi(request);
                                break;
                            case EAT:
                                eat(request);
                                break;
                            case MOVE:
                                move(request);
                                break;
                            case FIGHT:
                                fight(request);
                                break;
                            case LIST:
                                list();
                                break;
                            case CHECK_REG:
                                checkReg(request);
                                break;
                            case WIN:
                                handleWin(request);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private Message c2c(Message.Type type, Message req) {
            String to = req.getTo();
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());
            resp.setContent(req.getContent());
            resp.setType(type);
            SocketUtil.getInstance().send(stTo.getSocket(), resp);
            return resp;
        }

        private void renshu(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.DEFEAT);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void qiuheSuccess(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.PEACE_SUCCESS);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void qiuheFailure(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.PEACE_FAILURE);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void qiuhe(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.PEACE);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void huiqi(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.HUIQI);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void eat(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.EAT);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void move(Message req) {
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());

            resp.setContent(req.getContent());

            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.MOVE);

            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void fight(Message req) {
//            System.out.println("fight");
            String from = req.getFrom();
            String to = req.getTo();
            ServerThread stFrom = clients.get(from);
            ServerThread stTo = clients.get(to);
            Message resp = new Message();
            //挑战发起者为红棋
            resp.setFrom(req.getFrom());
            resp.setTo(req.getTo());
            resp.setFromPlayer(0);
            resp.setToPlayer(1);

            resp.setType(Message.Type.FIGHT_SUCCESS);

            SocketUtil.send(stFrom.getSocket(), resp);
            SocketUtil.send(stTo.getSocket(), resp);
        }

        private void list() {
            Message resp = new Message();
            resp.setType(Message.Type.SUCCESS);
            resp.setContent(getAccountList());
            clients.forEach((k, v) -> {
                SocketUtil.getInstance().send(v.getSocket(), resp);
            });
        }

        private Vector getAccountList() {
            Vector<String> list = new Vector<>();
            Set<String> keySet = clients.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }

            return list.size() == 0 ? null : list;
        }

        private void login(Message request) {
            User user = (User) request.getContent();
            String account = user.getAccount();
            String password = user.getPassword();

            try {
                System.out.println("从数据库加载信息");
                // 从数据库加载用户信息
                User dbUser = DBUtil.loadUser(account);
                System.out.println(dbUser);
                if (dbUser == null) {
                    Message response = new Message();
                    response.setType(Message.Type.REG_NOT_FOUND);
                    response.setContent("用户不存在！");
                    SocketUtil.send(socket, response);
                    return;
                }

                if (!dbUser.getPassword().equals(password)) {
                    System.out.println(dbUser.getPassword());
                    Message response = new Message();
                    response.setType(Message.Type.LOGIN_FAILURE);
                    response.setContent("密码错误！");
                    SocketUtil.send(socket, response);
                    return;
                }

                // 登录成功，将用户加入在线列表
                clients.put(account, this);
                Message response = new Message();
                response.setType(Message.Type.SUCCESS);
                response.setContent("登录成功！");
                SocketUtil.send(socket, response);
            } catch (SQLException e) {
                e.printStackTrace();
                Message response = new Message();
                response.setType(Message.Type.LOGIN_FAILURE);
                response.setContent("登录失败，请稍后再试！");
                SocketUtil.send(socket, response);
            }
        }

        private void reg(Message request) {
            User user = (User) request.getContent();
            String account = user.getAccount();
            String password = user.getPassword();

            // 检查账号和密码是否为空
            if (account.isEmpty() || password.isEmpty()) {
                Message response = new Message();
                response.setType(Message.Type.REG_FAILURE);
                response.setContent("账号和密码不能为空！");
                SocketUtil.send(socket, response);
                return;
            }

            // 检查密码长度是否合法
            if (password.length() < 6) {
                Message response = new Message();
                response.setType(Message.Type.REG_FAILURE);
                response.setContent("密码长度不能少于6位！");
                SocketUtil.send(socket, response);
                return;
            }

            // 检查账号是否已存在
            if (clients.containsKey(account)) {
                Message response = new Message();
                response.setType(Message.Type.REG_FAILURE);
                response.setContent("账号已存在！");
                SocketUtil.send(socket, response);
                return;
            }

            // 保存用户信息到数据库
            try {
                DBUtil.saveUser(user); // 使用DBUtil保存用户信息到数据库
            } catch (SQLException e) {
                e.printStackTrace();
                Message response = new Message();
                response.setType(Message.Type.REG_FAILURE);
                response.setContent("注册失败，请稍后再试！");
                SocketUtil.send(socket, response);
                return;
            }

            // 返回注册成功消息
            Message response = new Message();
            response.setType(Message.Type.REG_SUCCESS);
            response.setContent("注册成功，请登录！");
            SocketUtil.send(socket, response);
        }


        private void forget(Message request) {
            String account = (String) request.getContent();

            // 检查账号是否为空
            if (account.isEmpty()) {
                Message response = new Message();
                response.setType(Message.Type.FORGET_FAILURE);
                response.setContent("请输入账号！");
                SocketUtil.send(socket, response);
                return;
            }

            // 检查账号是否存在
            if (!clients.containsKey(account)) {
                Message response = new Message();
                response.setType(Message.Type.FORGET_FAILURE);
                response.setContent("账号不存在！");
                SocketUtil.send(socket, response);
                return;
            }
            User user = (User) request.getContent();
            String password = user.getPassword();

            // 返回密码信息
            Message response = new Message();
            response.setType(Message.Type.FORGET_SUCCESS);
            response.setContent("您的密码是：" + password);
            SocketUtil.send(socket, response);
        }


        private void checkReg(Message msg) {
            String account = (String) msg.getContent();
            Message response = new Message();

            try {
                // 从数据库加载用户信息
                User dbUser = DBUtil.loadUser(account);
                
                if (dbUser != null) {
                    response.setType(Message.Type.REG_FOUND); // 用户已注册
                    System.out.println("[DEBUG] 用户 '" + account + "' 存在于数据库中，返回 REG_FOUND。");
                } else {
                    response.setType(Message.Type.REG_NOT_FOUND); // 用户未注册
                    System.out.println("[DEBUG] 用户 '" + account + "' 不存在于数据库中，返回 REG_NOT_FOUND。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setType(Message.Type.REG_NOT_FOUND);
                System.out.println("[ERROR] 检查用户注册状态时发生数据库错误: " + e.getMessage());
                e.printStackTrace();
            }

            // 发送响应给客户端
            SocketUtil.send(socket, response);
        }
    }
    private void handleWin(Message req){
        String winner = req.getFrom();
        String loser = req.getTo();
        String resultMsg = (String) req.getContent();

        // 给胜利方发确认
        Message winMsg = new Message();
        winMsg.setType(Message.Type.WIN);
        winMsg.setContent("你赢了！" + resultMsg);
        SocketUtil.send(clients.get(winner).getSocket(), winMsg);

        // 给失败方发通知
        Message loseMsg = new Message();
        loseMsg.setType(Message.Type.WIN);
        loseMsg.setContent("你输了！" + resultMsg);
        SocketUtil.send(clients.get(loser).getSocket(), loseMsg);

        // 清除对战状态
        clients.remove(winner);
        clients.remove(loser);
    }

}