package game;

import cd.GamePanel;
import game.Chess;

import java.awt.*;

/**
 * @Author: duan
 * @Date: Create in 8:15 2021/3/24
 */
public class Shi extends Chess {
    public Shi(int player, Point p) {
        super("shi", player, p);
    }

    public Shi(int player, int px) {
        this(player, new Point(px, 1));
    }

    @Override
    public boolean isAbleMove(Point tp, GamePanel gamePanel) {
        return line(tp) == 1 && isHome(tp) && getStep(tp) == 1;
    }
}
