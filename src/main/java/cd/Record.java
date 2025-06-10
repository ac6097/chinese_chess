package cd;

import java.awt.*;
import java.io.Serializable;

import game.Chess;

public class Record implements Serializable {

    private Chess chess;

    private Point start;

    private Point end;

    private Chess eatenChess;


    public Record(Chess chess, Point start, Point end, Chess eatenChess) {
        this.chess = chess;
        this.start = start;
        this.end = end;
        this.eatenChess = eatenChess;
    }

    public Record() {

    }

    public Chess getChess() {
        return chess;
    }

    public void setChess(Chess chess) {
        this.chess = chess;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Chess getEatenChess() {
        return eatenChess;
    }

    public void setEatenChess(Chess eatenChess) {
        this.eatenChess = eatenChess;
    }

    @Override
    public String toString() {
        return "Record{" +
                "chess=" + chess +
                ", start=" + start +
                ", end=" + end +
                ", eatenChess=" + eatenChess +
                '}';
    }
}
