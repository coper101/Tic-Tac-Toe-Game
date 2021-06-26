package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daryl.tictactoegame.Data.DBHelper;
import com.daryl.tictactoegame.Data.GameRoom;
import com.daryl.tictactoegame.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class WaitingRoomFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = WaitingRoomFragment.class.getSimpleName();

    // Data
    private GameRoom gm;
    private boolean isPlayer2;

    // Views
    private View view;
    private Button readyButton;
    private TextView waitingMessage;

    // Firebase DB
    private DatabaseReference dbRef;
    private ChildEventListener childEvenListener;
    private Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        // Disable Back Navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting_room, container, false);
        initViews(view);
        this.view = view;
        return view;
    }

    private void initViews(View view) {
        readyButton = view.findViewById(R.id.ready_button);
        waitingMessage = view.findViewById(R.id.waiting_msg_text_view);
        waitingMessage.setVisibility(View.GONE);
        readyButton.setOnClickListener(this::onClick);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        WaitingRoomFragmentArgs args = WaitingRoomFragmentArgs.fromBundle(bundle);
        gm = args.getGameRoom();
        isPlayer2 = args.getIsPlayer2();
        Toast.makeText(getContext(), "isPlayer2: " + isPlayer2, Toast.LENGTH_SHORT).show();
        allReady();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ready_button) {
            waitingMessage.setVisibility(View.VISIBLE);
            if (!isPlayer2) {
                // Player 1 is Ready
                dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).child("p1Ready").setValue(true);
            } else {
                // Player 2 is Ready
                dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).child("p2Ready").setValue(true);
            }
            readyButton.setVisibility(View.GONE);
        }
    }

    private void allReady() {
        query = dbRef.child(DBHelper.GAME_ROOMS_KEY);

        childEvenListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idKey = snapshot.getKey();
                String idVal = String.valueOf(snapshot.getValue());
                Log.d(TAG, "Game Room Key: " +  idKey);
                Log.d(TAG, "Game Room Val: " + idVal);
                GameRoom gm1 = snapshot.getValue(GameRoom.class);
                if (idKey.equals(gm.getId())) {
                    if (gm1.isP1Ready() && gm1.isP2Ready()) {
                        // Start Game - if all players are ready
                        navigateToGameRoom(gm1);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addChildEventListener(childEvenListener);
    }

    private void navigateToGameRoom(GameRoom gm1) {
        WaitingRoomFragmentDirections.ActionWaitingRoomFragmentToGameRoomFragment directions
                = WaitingRoomFragmentDirections.actionWaitingRoomFragmentToGameRoomFragment();
        directions.setGameRoom(gm1);
        directions.setIsPlayer1(!isPlayer2);
        if (getView() != null) {
            Navigation.findNavController(requireView()).navigate(directions);
        } else {
            Toast.makeText(getContext(), "View is Null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        query.removeEventListener(childEvenListener);
    }
}