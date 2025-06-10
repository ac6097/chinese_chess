package game;

import game.Ma;


/**
 * @Author: duan
 * @Date: Create in 8:49 2021/3/24
 * 简单工厂模式
 */
public class ChessFactory {
    private ChessFactory() {

    }

    public static game.Chess create(String name, int player, int px) {
        if ("boss".equals(name)) {
            return new game.Boss(player, px);
        } else if ("shi".equals(name)) {
            return new game.Shi(player, px);
        } else if ("xiang".equals(name)) {
            return new game.Xiang(player, px);
        } else if ("ma".equals(name)) {
            return new Ma(player, px);
        } else if ("che".equals(name)) {
            return new Che(player, px);
        } else if ("pao".equals(name)) {
            return new Pao(player, px);
        } else if ("bing".equals(name)) {
            return new Bing(player, px);
        }

        return null;
    }
}
