package com.daryl.tictactoegame.Data;

import java.io.Serializable;
import java.util.Arrays;

public class GameRoom implements Serializable {

    private String id, name, p1, p2, winner, turn, board;

    public GameRoom() {}

    public GameRoom(String id) {
        this.id = id;
        name = "My Game Room";
        p1 = "Player 1";
        p2 = "";
        winner = "";
        turn = "";
        int[][] boardInt = new int[3][3];
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

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }
}
