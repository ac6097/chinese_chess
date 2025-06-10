package game;

import cd.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;

/**
 * @Author: duan
 * @Date: Create in 14:32 2021/3/22
 */
public abstract class Chess implements Serializable {


    //定义一个常量，只能在定义时或代码块中修改值，其它不允许修改
    //棋子大
    private static final int SIZE = 30;
    //棋盘外边距
    private static final int MARGIN = 20;
    //棋子间距
    private static final int SPACE = 40;
    //棋子名称
    private String name;
    //set方法
    public void setName(String name) {
        this.name = name;
    }

    //棋子图片后缀
    private String suffix = ".png";
    //棋子阵营，0：红，1：黑
    protected int player;

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getPlayer() {
        return player;
    }

    //棋子绘制时的坐标位置
    private int x, y;
    //棋子的网格坐标
    protected Point p;
    //棋子的网格坐标，初始位置，不可改变
    private Point initP;
    //保存每个棋子的索引位置
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setP(Point p) {
        this.p = (Point) p.clone();
        if (initP == null) {
            initP = this.p;
        }
        calXY();
    }

    public Point getP() {
        return p;
    }

    public Chess(String name, int player, Point p) {
        this.name = name;
        this.player = player;
        setP(p);
    }

    public Chess(String name, Point p, int player) {
        this.name = name;
        this.player = player;
        setP(p);
    }

    public String getName() {
        return name;
    }

    public int isUpOrDown() {
        //上面和下面
        if (initP.y < 6) {
            //上面
            return 1;
        } else if (initP.y > 5) {
            //下面
            return 2;
        }

        return 0;
    }

    public boolean isHome(Point tp) {
        if (tp.x < 4 || tp.x > 6) {
            return false;
        }
        int upOrDown = isUpOrDown();
        if (upOrDown == 1) {
            //上
            if (tp.y > 3 || tp.y < 1) {
                return false;
            }
        } else if (upOrDown == 2){
            //下
            if (tp.y > 10 || tp.y < 8) {
                return false;
            }
        }

        return true;
    }


    public int line(Point tp) {
        if (p.y == tp.y) {
            //x
            return 3;
        } else if (p.x == tp.x) {
            //y
            return 2;
        } else if (Math.abs(p.x - tp.x) == Math.abs(p.y - tp.y)) {
            //正斜线
            return 1;
        } else {
            //日字
            if (Math.abs(p.x - tp.x) == 2 && Math.abs(p.y - tp.y) == 1) {
                //x
                return 0;
            } else if (Math.abs(p.x - tp.x) == 1 && Math.abs(p.y - tp.y) == 2) {
                //y
                return -1;
            }
        }

        return -2;
    }


    public int getStep(Point tp) {
        int line = line(tp);
        if (line == 3) {
            //x
            return Math.abs(p.x - tp.x);
        } else if (line == 2 || line == 1) {
            //y或正斜线
            return Math.abs(p.y - tp.y);
        }

        return 0;
    }

    public boolean isOverRiver(Point tp) {
        int upOrDown = isUpOrDown();
        if (upOrDown == 1) {
            //上
            if (tp.y < 6) {
                return false;
            }
        } else if (upOrDown == 2) {
            //下
            if (tp.y > 5) {
                return false;
            }
        }

        return true;
    }


    public boolean isBieJiao(Point tp, GamePanel gamePanel) {
        Point center = new Point();//中心点
        if ("xiang".equals(name)) {
            center.x = (p.x + tp.x) / 2;
            center.y = (p.y + tp.y) / 2;
            return gamePanel.getChessByP(center) != null;
        } else if ("ma".equals(name)) {
            int line = line(tp);
            if (line == 0) {
                //x
                center.x = (p.x + tp.x) / 2;
                center.y = p.y;
            } else if (line == -1) {
                //y
                center.y = (p.y + tp.y) / 2;
                center.x = p.x;
            }
            return gamePanel.getChessByP(center) != null;
        }

        return true;
    }


    public int getCount(Point tp, GamePanel gamePanel) {
        int start = 0;
        int end = 0;
        int count = 0;//统计棋子数量
        int line = line(tp);
        Point np = new Point();
        if (line == 2) {
            //y
            np.x = tp.x;
            if (tp.y > p.y) {
                //从上往下
                start = p.y + 1;
                end = tp.y;
            } else {
                //从下往上
                start = tp.y + 1;
                end = p.y;
            }
            for (int i = start; i < end; i++) {
                np.y = i;
                if (gamePanel.getChessByP(np) != null) {
                    count++;
                }
            }
        }else if (line == 3) {
            //x
            np.y = tp.y;
            if (tp.x > p.x) {
                //从左往右
                start = p.x + 1;
                end = tp.x;
            } else {
                //从右往左
                start = tp.x + 1;
                end = p.x;
            }
            System.out.println("start:" + start);
            System.out.println("end:" + end);
            for (int i = start; i < end; i++) {
                np.x = i;
                if (gamePanel.getChessByP(np) != null) {
                    count++;
                }
            }
        }
        System.out.println("棋子总数：" + count);
        return count;
    }


    public boolean isForward(Point tp) {
        int upOrDown = isUpOrDown();
        if (upOrDown == 1) {
            //上
            if (tp.y > p.y) {
                return true;
            }
        } else if (upOrDown == 2) {
            //下
            if (tp.y < p.y) {
                return true;
            }
        }

        return false;
    }


    public boolean isBack(Point tp) {
        int upOrDown = isUpOrDown();
        if (upOrDown == 1) {
            //上
            if (tp.y < p.y) {
                return true;
            }
        } else if (upOrDown == 2) {
            //下
            if (tp.y > p.y) {
                return true;
            }
        }

        return false;
    }


    public abstract boolean isAbleMove(Point tp, GamePanel gamePanel);

    public void draw(Graphics g, JPanel panel) {
        String path = "pic" + File.separator + name + player + suffix;
        Image img = Toolkit.getDefaultToolkit().getImage(path);
        g.drawImage(img, x, y, SIZE, SIZE, panel);
    }


    public void drawRect(Graphics g) {
        g.drawRect(x, y, SIZE, SIZE);
    }


    public void calXY() {
        x = MARGIN - SIZE / 2 + SPACE * (p.x - 1);
        y = MARGIN - SIZE / 2 + SPACE * (p.y - 1);
    }


    public static Point getPointFromXY(int x, int y) {
        Point p = new Point();
        p.x = (x - MARGIN + SIZE / 2) / SPACE + 1;
        p.y = (y - MARGIN + SIZE / 2) / SPACE + 1;
        if (p.x < 1 || p.x > 9 || p.y < 1 || p.y > 10) {
            return null;
        }

        return p;
    }


    public void reserve() {
        p.x = 10 - p.x;
        p.y = 11 - p.y;
        initP = p;
        calXY();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Chess{");
        sb.append("name='").append(name).append('\'');
        sb.append(", suffix='").append(suffix).append('\'');
        sb.append(", player=").append(player);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", p=").append(p);
        sb.append(", initP=").append(initP);
        sb.append('}');
        return sb.toString();
    }
}
