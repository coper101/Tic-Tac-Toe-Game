package com.daryl.tictactoegame.Data;

import java.io.Serializable;
import java.util.Arrays;

public class GameRoom implements Serializable {

    private String id, name, p1, p2, board;
    private int winner, turn;
    private boolean p1Ready, p2Ready;

    public GameRoom() {}

    public GameRoom(String id) {
        this.id = id;
        name = "My Game Room";
        p1 = "Player 1";
        p2 = "";
        winner = Winner.NONE.ordinal();
        turn = Turn.BOTH.ordinal();
        final int[][] boardInt = new int[3][3];
        board = Arrays.deepToString(boardInt);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public boolean isP1Ready() {
        return p1Ready;
    }

    public void setP1Ready(boolean p1Ready) {
        this.p1Ready = p1Ready;
    }

    public boolean isP2Ready() {
        return p2Ready;
    }

    public void setP2Ready(boolean p2Ready) {
        this.p2Ready = p2Ready;
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", p1='" + p1 + '\'' +
                ", p2='" + p2 + '\'' +
                ", winner='" + winner + '\'' +
                ", turn='" + turn + '\'' +
                ", board='" + board + '\'' +
                ", p1Ready=" + p1Ready +
                ", p2Ready=" + p2Ready +
                '}';
    }

    public enum Turn {
        BOTH, P1, P2
    }

    public enum Winner {
        NONE, P1, P2, TIE
    }

    public enum MARK {
        EMPTY, P1, P2
    }


}
