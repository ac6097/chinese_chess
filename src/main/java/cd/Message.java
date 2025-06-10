package cd;

import java.io.Serializable;

public class Message implements Serializable {
    private Object content;
    private Type type;
    private String from;
    private String to;

    private int fromPlayer;

    private int toPlayer;

    private String Message;

    private Object data;

    public void setMessage(String message) {
        Message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getMessage() {
        return Message;
    }

    public enum Type{
        LOGIN,
        LOGIN_FAILURE,
        REG,
        FORGET,
        REG_SUCCESS,
        REG_FAILURE,
        FORGET_SUCCESS,
        FORGET_FAILURE,
        LIST,   //获取当前服务登录的所有user
        FIGHT,
        FIGHT_SUCCESS,
        MOVE,
        EAT,
        PEACE,
        PEACE_SUCCESS,
        PEACE_FAILURE,
        SUCCESS,
        FAILURE,
        DEFEAT,
        HUIQI,
        CHECK_REG,
        REG_FOUND,
        REG_NOT_FOUND,
        WIN,
    }

    public Message(){

    }

    public Message(Object content, Type type, String from, String to) {
        this.content = content;
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(int fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public int getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(int toPlayer) {
        this.toPlayer = toPlayer;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "content=" + content +
                ", type=" + type +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", fromPlayer=" + fromPlayer +
                ", toPlayer=" + toPlayer +
                '}';
    }
}
