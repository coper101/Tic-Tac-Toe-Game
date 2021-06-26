package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.daryl.tictactoegame.Data.DBHelper;
import com.daryl.tictactoegame.Data.GameRoom;
import com.daryl.tictactoegame.Data.GameRoomHelper;
import com.daryl.tictactoegame.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class GameRoomFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = GameRoomFragment.class.getSimpleName();

    // Data
    private GameRoom gm;
    private boolean isPlayer1;
    private boolean isTurn = true;

    // Firebase DB
    private DatabaseReference dbRef;

    // Board Game Views
    private ConstraintLayout boardCL;
    private FrameLayout r0c0, r0c1, r0c2,
                        r1c0, r1c1, r1c2,
                        r2c0, r2c1, r2c2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Back Navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_room, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GameRoomFragmentArgs args = GameRoomFragmentArgs.fromBundle(getArguments());
        gm = args.getGameRoom();
        isPlayer1 = args.getIsPlayer1();
        Toast.makeText(getContext(), "isPlayer1: " + isPlayer1 , Toast.LENGTH_SHORT).show();
        updateTurn();
    }

    private void initViews(View view) {
        boardCL = view.findViewById(R.id.board_constraint_layout);
        // 1st row
        r0c0 = view.findViewById(R.id.r0c0);
        r0c1 = view.findViewById(R.id.r0c1);
        r0c2 = view.findViewById(R.id.r0c2);
        // 2nd row
        r1c0 = view.findViewById(R.id.r1c0);
        r1c1 = view.findViewById(R.id.r1c1);
        r1c2 = view.findViewById(R.id.r1c2);
        // 3rd row
        r2c0 = view.findViewById(R.id.r2c1);
        r2c1 = view.findViewById(R.id.r2c0);
        r2c2 = view.findViewById(R.id.r2c2);

        // 1st row
        r0c0.setOnClickListener(this::onClick);
        r0c1.setOnClickListener(this::onClick);
        r0c2.setOnClickListener(this::onClick);
        // 2nd row
        r1c0.setOnClickListener(this::onClick);
        r1c1.setOnClickListener(this::onClick);
        r1c2.setOnClickListener(this::onClick);
        // 3rd row
        r2c0.setOnClickListener(this::onClick);
        r2c1.setOnClickListener(this::onClick);
        r2c2.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.r0c0:
                insertToBoard(0, 0);
                break;
            case R.id.r0c1:
                insertToBoard(0, 1);
                break;
            case R.id.r0c2:
                insertToBoard(0, 2);
                break;
            case R.id.r1c0:
                insertToBoard(1, 0);
                break;
            case R.id.r1c1:
                insertToBoard(1, 1);
                break;
            case R.id.r1c2:
                insertToBoard(1, 2);
                break;
            case R.id.r2c1:
                insertToBoard(2, 0);
                break;
            case R.id.r2c0:
                insertToBoard(2, 1);
                break;
            case R.id.r2c2:
                insertToBoard(2, 2);
        }
    }

    private void insertToBoard(int row, int col) {
        if (isTurn) {
            // Update Board with New Marks
            int[][] updatedBoard = GameRoomHelper.getBoardInt(gm.getBoard());
            updatedBoard[row][col] = isPlayer1 ? 1 : 2;
            gm.setBoard(Arrays.deepToString(updatedBoard));
            // Update Winner
            int isWinner = GameRoomHelper.getWinner(updatedBoard);
            gm.setWinner(isWinner);
            // Update Turn
            gm.setTurn(isPlayer1 ? 2 : 1);
            Toast.makeText(getContext(), "New Turn: " + gm.getTurn(), Toast.LENGTH_SHORT).show();
            // Update Game Room in DB
            dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).setValue(gm);
        }
    }

    private void updateTurn() {
        Query query = dbRef.child(DBHelper.GAME_ROOMS_KEY);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idKey = snapshot.getKey();
                // String idVal = String.valueOf(snapshot.getValue());
                // Log.d(TAG, "Game Room Key: " +  idKey);
                // Log.d(TAG, "Game Room Val: " + idVal);
                if (gm.getId().equals(idKey)) {
                    // Update Game Board if there are Changes
                    GameRoom gm1 = snapshot.getValue(GameRoom.class);
                    gm = gm1;
                    Log.d(TAG, gm.toString());
                    // Refresh Board with New Marks
                    updateBoard();
                    // Show Winner, Loser or Tie
                    boolean hasWinner = decideWinner();
                    if (hasWinner)
                        return;
                    // Take Turns
                    GameRoom.Turn turn = GameRoom.Turn.values()[gm1.getTurn()];
                    switchTurns(turn);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private boolean decideWinner() {
        int winner = gm.getWinner();
        if (winner != 0) {
            if (winner == 1) {
                // Player 1 is Winner
                if (isPlayer1) {
                    // Display Winner
                    Toast.makeText(getContext(), "You Won!", Toast.LENGTH_SHORT).show();
                } else {
                    // Display Loser
                    Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
                }
                isTurn = false;
            } else if (winner == 2) {
                // Player 2 is Winner
                if (!isPlayer1) {
                    // Display Winner
                    Toast.makeText(getContext(), "You Won!", Toast.LENGTH_SHORT).show();
                } else {
                    // Display Loser
                    Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
                }
                isTurn = false;
            }
            return true;
        }
        return false;
    }

    private void updateBoard() {
        String board = gm.getBoard();
        int[][] boardInt = GameRoomHelper.getBoardInt(board);
        int length = boardInt.length;
        int count = 0;
        StringBuilder message = new StringBuilder();
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                message.append(boardInt[row][col]).append(" ");
                FrameLayout fl = (FrameLayout) boardCL.getChildAt(count);
                ImageView iv = (ImageView) fl.getChildAt(0);
                if (boardInt[row][col] == 1) {
                    iv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle));
                } else if (boardInt[row][col] == 2) {
                    iv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_close));
                } else {
                    iv.setBackground(null);
                }
                count++;
            }
        }
    }

    private void switchTurns(GameRoom.Turn turn) {
        switch (turn) {
            case P1:
                // Set Player 1's Turn
                setTurn(isPlayer1);
                break;
            case P2:
                // Set Player 2's Turn
                setTurn(!isPlayer1);
                break;
            case BOTH:
                // Either player's Turn
                setTurn(true);
        }
    }

    private void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
        int bgDrawableId = isTurn ? R.drawable.mark_box : R.drawable.mark_box_not_turn;
        for (int i = 0; i < boardCL.getChildCount(); i++) {
            FrameLayout fl = (FrameLayout) boardCL.getChildAt(i);
            fl.setBackground(ContextCompat.getDrawable(getContext(), bgDrawableId));
        }
    }

}