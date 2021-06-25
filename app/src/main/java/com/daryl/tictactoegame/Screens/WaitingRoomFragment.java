package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

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

    // Views
    private Button readyButton;
    private TextView waitingMessage;

    // Firebase DB
    private DatabaseReference dbRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting_room, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        readyButton = view.findViewById(R.id.ready_button);
        waitingMessage = view.findViewById(R.id.waiting_msg_text_view);
        readyButton.setOnClickListener(this::onClick);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        WaitingRoomFragmentArgs arg = WaitingRoomFragmentArgs.fromBundle(bundle);
        gm = arg.getGameRoom();
        allReady();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ready_button) {
            Navigation.findNavController(requireView()).navigate(R.id.action_waitingRoomFragment_to_gameRoomFragment);
        }
    }

    private void allReady() {
        Query query = dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String id = snapshot.getKey();
                Log.d(TAG, "key: " + id);
                Log.d(TAG, "value: " + snapshot.getValue());
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
        });
    }
}