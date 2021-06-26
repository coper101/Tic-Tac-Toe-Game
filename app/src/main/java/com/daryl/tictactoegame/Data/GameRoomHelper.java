package com.daryl.tictactoegame.Data;

public class GameRoomHelper {

    // [["0", "0", "0"] ...] -> [[0, 0, 0] ...]
    public static int[][] getBoardInt(String board) {
        int[][] boardInt = new int[3][3];
        String board2 = removeOutBrackets(board);
        String row1 = board2.substring(1, 1+7);
        String row2 = board2.substring(12, 12+7);
        String row3 = board2.substring(23, 23+7);
        String nums = row1 + ", " + row2 + ", " + row3;
        String[] numsStr = nums.split(", ", 0);
        int count = 0;
        for (int i = 0; i < boardInt.length; i++) {
            for (int j = 0; j < boardInt.length; j++) {
                boardInt[i][j] = Integer.parseInt(numsStr[count]);
                count++;
            }
        }
        return boardInt;
    }

    private static String removeOutBrackets(String s) {
        return s.substring(1, s.length()-1);
    }

    public static int getWinner(int[][] boardInt) {
        for (int row = 0; row < boardInt.length; row++) {
            // Horizontal
            int mark = boardInt[row][0];
            if (mark != 0 &&
                    boardInt[row][1] == mark &&
                    boardInt[row][2] == mark) {
                return mark;
            }
            // Vertical
            int col = row;
            mark = boardInt[0][col];
            if (mark != 0 &&
                    boardInt[1][col] == mark &&
                    boardInt[2][col] == mark) {
                return mark;
            }
        }
        // Diagonal (Forward)
        int mark = boardInt[0][2];
        if (mark != 0 &&
                boardInt[1][1] == mark &&
                boardInt[2][0] == mark) {
            return mark;
        }

        // Diagonal (Backward)
        mark = boardInt[0][0];
        if (mark != 0 &&
                boardInt[1][1] == mark &&
                boardInt[2][2] == mark) {
            return mark;
        }

        return 0;
    }
}
