package game;

import cd.GamePanel;
import game.Chess;

import java.awt.*;

/**
 * @Author: duan
 * @Date: Create in 8:15 2021/3/24
 */
public class Pao extends Chess {
    public Pao(int player, Point p) {
        super("pao", player, p);
    }

    public Pao(int player, int px) {
        this(player, new Point(px, 3));
    }

    @Override
    public boolean isAbleMove(Point tp, GamePanel gamePanel) {
        Chess c = gamePanel.getChessByP(tp);
        if (c != null) {
            if (c.getPlayer() != this.player) {
                //吃子
                return line(tp) > 1 && getCount(tp, gamePanel) == 1;
            }
        } else {
            //移动
            return line(tp) > 1 && getCount(tp, gamePanel) == 0;
        }

        return false;
    }
}
