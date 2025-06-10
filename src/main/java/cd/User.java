package cd;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String account;
    private String password;
    private String ranking; //
    private double winningrate; // 胜率
    private int totalGames; // 总对局数
    private int winGames; // 胜利对局数

    public User(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public User setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRanking() {
        return ranking;
    }

    public User setRanking(String ranking) {
        this.ranking = ranking;
        return this;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getWinGames() {
        return winGames;
    }

    public void setWinGames(int winGames) {
        this.winGames = winGames;
    }

    public double getWinningrate() {
        return winningrate;
    }

    public void setWinningRate(double winningrate) {
        this.winningrate = winningrate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id + ", " +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", ranking='" + ranking + '\'' +
                ", totalGames='" + totalGames + '\'' +
                ", winGames='" + winGames + '\'' +
                ", winningrate='" + winningrate + '\'' +
                '}';
    }
}