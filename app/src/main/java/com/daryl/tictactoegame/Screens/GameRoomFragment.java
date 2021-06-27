package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daryl.tictactoegame.Data.DBHelper;
import com.daryl.tictactoegame.Data.GameDialog;
import com.daryl.tictactoegame.Data.GameRoom;
import com.daryl.tictactoegame.Data.GameRoomHelper;
import com.daryl.tictactoegame.Data.QuitDialog;
import com.daryl.tictactoegame.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class GameRoomFragment extends Fragment implements View.OnClickListener, GameDialog.OnClickDialogListener, QuitDialog.OnQuitDialogListener {

    private static final String TAG = GameRoomFragment.class.getSimpleName();

    // Data
    private GameRoom gm;
    private boolean isPlayer1;
    private boolean isTurn = true;
    private boolean isHostQuit;

    // Firebase DB
    private DatabaseReference dbRef;
    private ChildEventListener childEventListener;
    private Query query;

    // Board Game Views
    private ConstraintLayout boardCL;
    private FrameLayout r0c0, r0c1, r0c2,
                        r1c0, r1c1, r1c2,
                        r2c0, r2c1, r2c2;

    private GameDialog gameDialog;
    private QuitDialog quitDialog;
    private ImageButton quitButton;
    private TextView gameRoomIdTV, playerNumTV;
    private ImageView playersMarkIV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Toast.makeText(getContext(), "isPlayer1: " + isPlayer1 , Toast.LENGTH_SHORT).show();
        playerNumTV.setText(isPlayer1 ? "Player 1" : "Player 2");
        gameRoomIdTV.setText(gm.getId());
        // Set Player Label
        int colorInt = isPlayer1 ? R.color.blue_green_neon : R.color.white;
        int drawableInt = isPlayer1 ? R.drawable.ic_circle : R.drawable.ic_x;
        playersMarkIV.setBackground(ContextCompat.getDrawable(getContext(), drawableInt));
        playersMarkIV.setBackgroundTintList(getContext().getColorStateList(colorInt));
        updateTurn();
    }

    private void initViews(View view) {
        quitButton = view.findViewById(R.id.quit_imag_button);
        quitButton.setOnClickListener(this::onClick);
        playerNumTV = view.findViewById(R.id.game_r_player_name_text_view);
        gameRoomIdTV = view.findViewById(R.id.game_r_id_text_view);
        playersMarkIV = view.findViewById(R.id.player_mark_image_view);
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
        r2c0 = view.findViewById(R.id.r2c0);
        r2c1 = view.findViewById(R.id.r2c1);
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
            case R.id.r2c0:
                insertToBoard(2, 0);
                break;
            case R.id.r2c1:
                insertToBoard(2, 1);
                break;
            case R.id.r2c2:
                insertToBoard(2, 2);
                break;
            case R.id.quit_imag_button:
                quitRoom();
        }
    }

    private void quitRoom() {
        showQuitDialog(true);
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
            // Update Turn Count
            gm.setTurnCount(gm.getTurnCount() + 1);
            // Update Game Room in DB
            dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).setValue(gm);
        }
    }

    private void updateTurn() {
        query = dbRef.child(DBHelper.GAME_ROOMS_KEY);
        childEventListener = new ChildEventListener() {
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
                    // Check Other Player has quit the Room
                    if (gm.isQuitGame()) {
                        // Toast.makeText(getContext(), "Other Player has quit the game.", Toast.LENGTH_SHORT).show();
                        showQuitDialog(false);
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        query.addChildEventListener(childEventListener);
    }

    private boolean decideWinner() {
        int winner = gm.getWinner();
        if (winner != 0 && gm.getTurnCount() <= 9) {
            String title = "";
            String subtitle = "";
            String buttonTitle = "";
            if (winner == 1) {
                // Player 1 is Winner
                if (isPlayer1) {
                    // Display Winner
                    // Toast.makeText(getContext(), "You Won!", Toast.LENGTH_SHORT).show();
                    title = "You Won!";
                    subtitle = "You're a pro.";
                    buttonTitle = "Hooray!";
                } else {
                    // Display Loser
                    // Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
                    title = "You Lost!";
                    subtitle = "Better luck next time.";
                    buttonTitle = "Aww!";
                }
                showGameDialog(title, subtitle, buttonTitle);
                isTurn = false;
            } else if (winner == 2) {
                // Player 2 is Winner
                if (!isPlayer1) {
                    // Display Winner
                    // Toast.makeText(getContext(), "You Won!", Toast.LENGTH_SHORT).show();
                    title = "You Won!";
                    subtitle = "You're a pro.";
                    buttonTitle = "Hooray!";
                } else {
                    // Display Loser
                    // Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
                    title = "You Lost!";
                    subtitle = "Better luck next time.";
                    buttonTitle = "Aww!";
                }
                showGameDialog(title, subtitle, buttonTitle);
                isTurn = false;
            }
            return true;
        }

        if (gm.getTurnCount() == 9) {
            showGameDialog("It's a Draw", "", "Ahh!");
            return true;
        }
        return false;
    }

    private void updateBoard() {
        String board = gm.getBoard();
        int[][] boardInt = GameRoomHelper.getBoardInt(board);
        int length = boardInt.length;
        int count = 0+3;
        StringBuilder message = new StringBuilder();
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                message.append(boardInt[row][col]).append(" ");
                FrameLayout fl = (FrameLayout) boardCL.getChildAt(count);
                ImageView iv = (ImageView) fl.getChildAt(0);
                if (boardInt[row][col] == 1) {
                    iv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle));
                    iv.setBackgroundTintList(getContext().getColorStateList(R.color.blue_green_neon));
                    fl.setOnClickListener(null);
                } else if (boardInt[row][col] == 2) {
                    iv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_x));
                    iv.setBackgroundTintList(getContext().getColorStateList(R.color.white));
                    fl.setOnClickListener(null);
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
        for (int i = 3; i < boardCL.getChildCount(); i++) {
            FrameLayout fl = (FrameLayout) boardCL.getChildAt(i);
            fl.setBackground(ContextCompat.getDrawable(getContext(), bgDrawableId));
        }
    }

    private void showGameDialog(String title, String subtitle, String buttonTitle) {
        gameDialog = new GameDialog(title, subtitle, buttonTitle);
        gameDialog.setOnClickDialogListener(this::onClickDialog);
        gameDialog.show(getActivity().getSupportFragmentManager(), "Game Dialog");
    }

    @Override
    public void onClickDialog() {
        // Navigate to Home
        Navigation.findNavController(requireView()).navigate(R.id.action_gameRoomFragment_to_homeFragment);
        if (gameDialog != null) {
            gameDialog.dismiss();
        }
    }

    private void showQuitDialog(boolean isHost) {
        isHostQuit = isHost;
        quitDialog = new QuitDialog(isHostQuit);
        quitDialog.setOnQuitDialogListener(this::onQuitDialog);
        quitDialog.show(getActivity().getSupportFragmentManager(), "Quit Dialog");
    }

    @Override
    public void onQuitDialog(View view) {
        switch (view.getId()) {
            case R.id.quit_dialog_cancel_button:
                quitDialog.dismiss();
                break;
            case R.id.quit_dialog_quit_button:
                if (isHostQuit) {
                    dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).child("quitGame").setValue(true);
                }
                // Navigate to Home
                Navigation.findNavController(requireView()).navigate(R.id.action_gameRoomFragment_to_homeFragment);
                quitDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        query.removeEventListener(childEventListener);
    }

}