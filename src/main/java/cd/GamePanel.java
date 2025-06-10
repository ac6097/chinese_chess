package cd;

import game.Chess;
import game.ChessFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import javax.sound.sampled.*;

public class GamePanel extends JPanel {
    //定义一个保存所有棋子的成员变量，类型是数组
    private Chess[] chesses = new Chess[32];//保存所有棋子


    public Chess[] getChesses() {
        return chesses;
    }

    public void setChesses(Chess[] chesses) {
        this.chesses = chesses;
        repaint();
    }

    private String account;
    private Socket socket;
    private String to;       //对手名称

    private boolean isLocked = false;

    public boolean isLocked() {
        return isLocked;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    //自定义实现链表的数据结构
    //Java集合的使用
    private LinkedList<Record> huiqiList = new LinkedList();

    //当前选中的棋子
    private Chess selectedChess;
    //记住当前的阵营
    private int curPlayer = 0;

    public void setCurPlayer(int curPlayer) {
        this.curPlayer = curPlayer;
    }

    //提示的label
    private JLabel hintLabel;

    public void setHintLabel(JLabel hintLabel) {
        this.hintLabel = hintLabel;
    }

    private GameFrame gameFrame;

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    private Clip eatSoundClip;

    void huiqi(Message req) {
        Record record = huiqiList.pollLast();
        //将操作的棋子的坐标修改回去
        record.getChess().setP(record.getStart());
        chesses[record.getChess().getIndex()] = record.getChess();
        if (record.getEatenChess() != null) {
            chesses[record.getEatenChess().getIndex()] = record.getEatenChess();
        }
        curPlayer = 1 - record.getChess().getPlayer();


        if (req != null) {
            overMyTurn(record, req);
        }
        //刷新棋盘
        repaint();
    }

    private Message req;
    private ClientThread ct;

    public ClientThread getCt() {
        return ct;
    }

    public void startReceive() {
        new ClientThread(socket, new ClientThread.ResponseListener() {
            @Override
            public void success(Message resp){
                Object content = resp.getContent();
                if (content instanceof Record){
                    Record r = (Record) content;
                    switch (resp.getType()){
                        case MOVE:
                            chesses[r.getChess().getIndex()] = r.getChess();
                            huiqiList.add(r);

                            break;
                        case EAT:
                            //删除吃子
                            chesses[r.getEatenChess().getIndex()] = null;
                            chesses[r.getChess().getIndex()] = r.getChess();
                            checkWinCondition();
                            huiqiList.add(r);

                            break;
                        case HUIQI:
                            huiqi(null);
                            break;

                        case PEACE:
                            int result = JOptionPane.showConfirmDialog(null,"对方发起求和,是否同意求和?","求和",JOptionPane.YES_NO_OPTION);
                            Message req = new Message();
                            req.setFrom(account);
                            req.setTo(to);
                            req.setContent(new Record());
                            if (JOptionPane.YES_OPTION == result){
                                //同意
                                req.setType(Message.Type.PEACE_SUCCESS);
                                JOptionPane.showMessageDialog(null,"双方平局");
                                ct.setShutdown(true);
                                gameFrame.dispose();
                                new Main(socket,account);
                            }else{
                                //不同意
                                req.setType(Message.Type.PEACE_FAILURE);
                                JOptionPane.showMessageDialog(null,"您已拒绝求和,点击确定以继续对局");
                            }
                            SocketUtil.send(socket,req);
                            break;
                        case DEFEAT:
                            System.out.println("对方已认输");
                            isGameOver();
                            ct.setShutdown(true);
                            gameFrame.dispose();
                            new Main(socket,account);
                            break;
                        case PEACE_SUCCESS:
                            System.out.println("求和成功");
                            isGameOver();
                            ct.setShutdown(true);
                            gameFrame.dispose();
                            new Main(socket,account);
                            break;
                        case PEACE_FAILURE:
                            if("红色方回合".equals(hintLabel.getText()) && 0 == curPlayer || ("黑色方回合".equals(hintLabel.getText()) && 1 == curPlayer)){
                                isLocked = false;//解锁棋盘
                            }

                            break;
                        case WIN:
                            String resultMsg = (String) resp.getContent(); // 获取服务器传来的结果描述
                            String currentUser = account;
                            boolean isWinner = currentUser.equals(resp.getFrom());

                            // 显示明确的结果提示
                            String message = isWinner ? "胜利！" + resultMsg : "失败！" + resultMsg;
                            JOptionPane.showMessageDialog(null, message, "游戏结束", JOptionPane.INFORMATION_MESSAGE);

                            // 关闭资源
                            ct.setShutdown(true);
                            gameFrame.dispose();
                            new Main(socket, account); // 返回大厅
                            break;

                        default:
                            break;


                    }
                    //解锁棋盘
                    isLocked = false;
                    //修改提示文字
                    hintLabel.setText("黑色方回合".equals(hintLabel.getText()) ? "红色方回合" : "黑色方回合");
                    //重新绘制棋盘
                    repaint();
                }
            }
        }).start();                         //--------------------------------这里有问题

    }
    private void handleWin(Message resp) {
        String winner = resp.getFrom();
        String currentUser = account;

        String message = currentUser.equals(winner) ? "恭喜你获胜！" : "你输了，再接再厉！";
        JOptionPane.showMessageDialog(null, message);

        // 关闭游戏窗口，返回主界面
        ct.setShutdown(true);
        gameFrame.dispose();
        new Main(socket, account);
    }



    public GamePanel() {
        req = SocketUtil.getInstance().getReq();
        loadEatSound();
//        System.out.println("GamePanel");
        createChesses();
        startReceive();

        //添加点击事件
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isLocked) {//如果棋盘被锁，则点击无用
                    return;
                }
//                System.out.println("点击棋盘的坐标为：x=" + e.getX() + ",y=" + e.getY());
                Point p = Chess.getPointFromXY(e.getX(), e.getY());
//                System.out.println("点击棋盘的网格坐标对象为：p===" + p);
                if (selectedChess == null) {
                    //第一次选择
                    selectedChess = getChessByP(p);
                    if (selectedChess != null && selectedChess.getPlayer() != curPlayer) {
                        selectedChess = null;
                        hintLabel.setText("<html> 不能移动对方的棋子<br/>" + (curPlayer == 0 ? "红方走" : "黑方走") + "</html>");
                    }
                } else {
                    //重新选择、移动、吃子
                    Chess c = getChessByP(p);
                    if (c != null) {
                        //第n次点击的时候有棋子
                        //重新选择、吃子
                        if (c.getPlayer() == selectedChess.getPlayer()) {
                            //重新选择
//                            System.out.println("重新选择");
                            selectedChess = c;
                        } else {
                            //吃
//                            System.out.println("吃子");
                            if (selectedChess.isAbleMove(p, GamePanel.this)) {
                                handleEat(selectedChess,c,p);
                            }
                        }
                    } else {
                        //第n次点击的时候没有棋子，点的是空白地方
                        //移动
//                        System.out.println("移动");
                        if (selectedChess.isAbleMove(p, GamePanel.this)) {
                            Record record = new Record();
                            record.setChess(selectedChess);
                            record.setStart(selectedChess.getP());
                            record.setEnd(p);
                            record.setEatenChess(c);
                            huiqiList.add(record);
                            selectedChess.setP(p);
                            req.setType(Message.Type.MOVE);
                            req.setContent(record);

                            Message req = new Message();
                            req.setType(Message.Type.MOVE);
                            req.setFrom(account);
                            req.setTo(to);
                            req.setContent(record);

                            overMyTurn(record, req);

                            checkWinCondition();
                        }
                    }
                }
//                System.out.println("点击的棋子对象为：selectedChess===" + selectedChess);
//                System.out.println("棋子记录集合数据：" + huiqiList);
                //刷新棋盘，重新执行paint方法
                repaint();
            }
        });
    }

    private void overMyTurn(Record record, Message req) {

        if (req != null) {
            SocketUtil.send(socket,req);
        }
        //锁定自己的棋盘
        isLocked = true;
        //修改提示信息
        selectedChess = null;
        hintLabel.setText("红色方回合".equals(hintLabel.getText()) ? "黑色方回合" : "红色方回合");
    }


    public Chess getChessByP(Point p) {
        for (Chess item : chesses) {
            if (item != null && item.getP().equals(p)) {
                return item;
            }
        }

        return null;
    }

    private void createChesses() {
        String[] names = {"che", "ma", "xiang", "shi", "boss", "shi",
                "xiang", "ma", "che", "pao", "pao", "bing", "bing", "bing"
                , "bing", "bing"};
        int[] xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 8, 1, 3, 5, 7, 9};
        for (int i = 0; i < names.length; i++) {
            Chess c = ChessFactory.create(names[i], 0, xs[i]);
            c.setIndex(i);
            chesses[i] = c;//将棋子保存到数组中
        }
        for (int i = 0; i < names.length; i++) {
//            Chess c = new Chess(names[i], 1, ps[i]);//创建棋子对象
//            c.setName(names[i]);//指定棋子名称
//            c.setP(ps[i]);//指定棋子的网络坐标
//            c.setPlayer(1);
            game.Chess c = ChessFactory.create(names[i], 1, xs[i]);
            c.reserve();
            c.setIndex(i + 16);
            chesses[c.getIndex()] = c;//将棋子保存到数组中
        }
    }

    private void drawChesses(Graphics g) {
        for (Chess item : chesses) {//for-each循环
            if (item != null) {
                item.draw(g, this);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        System.out.println("paint");
        String bgPath = "pic" + File.separator + "qipan.jpg";
        Image bgImg = Toolkit.getDefaultToolkit().getImage(bgPath);
        g.drawImage(bgImg, 0, 0, this);

        drawChesses(g);

        if (selectedChess != null) {
            selectedChess.drawRect(g);
        }
    }

    public static void main(String[] args) {
        new GamePanel();
    }

    private void loadEatSound(){
        try {
            File soundFile = new File("resources/eat_sound.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            eatSoundClip = AudioSystem.getClip();
            eatSoundClip.open(ais);
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            e.printStackTrace();
        }
    }

    private void playEatSound(){
        if (eatSoundClip != null){
            eatSoundClip.setFramePosition(0);
            eatSoundClip.start();
        }
    }

    private void handleEat(Chess selectedChess, Chess c, Point p){
//          从数组中删除被吃掉的棋子
//          修改要移动棋子的坐标
        Record record = new Record();
        record.setChess(selectedChess);
        record.setStart(selectedChess.getP());
        record.setEnd(p);
        record.setEatenChess(c);
        huiqiList.add(record);
        chesses[c.getIndex()] = null;//从数组中删除被吃掉的棋子
        selectedChess.setP(p);

        Message req = new Message();
        req.setType(Message.Type.EAT);
        req.setFrom(account);
        req.setTo(to);
        req.setContent(record);

        playEatSound(); //播放吃子音效

        overMyTurn(record, req);

        checkWinCondition();
    }

    // GamePanel.java 修改判断逻辑
    private void checkWinCondition() {
        boolean redGeneralExist = false;
        boolean blackGeneralExist = false;

        // 遍历所有棋子检查将帅存在状态
        for (Chess chess : chesses) {
            if (chess != null && "boss".equals(chess.getName())) {
                if (chess.getPlayer() == 0) {
                    redGeneralExist = true;
                } else {
                    blackGeneralExist = true;
                }
            }
        }
        String winner = "";
        if(!redGeneralExist){
            winner += "黑色方";
        }
        if(!blackGeneralExist){
            winner += "红色方";
        }
        if(!winner.isEmpty()) {
            JOptionPane.showMessageDialog(null, winner + "获胜！");
        }
    }
    public boolean isGameOver() {
        return !isLocked;
    }


}
