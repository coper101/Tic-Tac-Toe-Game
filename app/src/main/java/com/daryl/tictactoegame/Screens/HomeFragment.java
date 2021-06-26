package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daryl.tictactoegame.Data.DBHelper;
import com.daryl.tictactoegame.Data.GameRoom;
import com.daryl.tictactoegame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    // Views
    private Button joinButton, createButton;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        joinButton = view.findViewById(R.id.join_a_game_room_button);
        createButton = view.findViewById(R.id.create_game_room_button);
        joinButton.setOnClickListener(this::onClick);
        createButton.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_game_room_button:
                insertNewGameRoom();
                break;
            case R.id.join_a_game_room_button:
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_homeFragment_to_gameRoomsFragment);
        }
    }

    private void insertNewGameRoom() {
        dbRef.child("ids").child("currentId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    // New Id
                    int currentId = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                    String id = String.valueOf(currentId + 1);
                    Log.d("firebase", currentId + "");
                    // New Game Room
                    GameRoom gm = new GameRoom(id);
                    // Insert Game Room & Insert New Current Id
                    dbRef.child(DBHelper.GAME_ROOMS_KEY).child(id).setValue(gm);
                    dbRef.child(DBHelper.IDS_KEY).child("currentId").setValue(id);
                    // Navigate to Waiting Room
                    navigateToWaitingRoom(id);
                }
            }
        });
    }

    private void navigateToWaitingRoom(String id) {
        dbRef.child(DBHelper.GAME_ROOMS_KEY).child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting data", task.getException());
                }
                else {
                    // Get Updated Game Room
                    GameRoom gm = task.getResult().getValue(GameRoom.class);
                    Log.d(TAG, gm.toString());
                    // Navigate to Waiting Room
                    HomeFragmentDirections.ActionHomeFragmentToWaitingRoomFragment directions
                            = HomeFragmentDirections.actionHomeFragmentToWaitingRoomFragment(gm);
                    Navigation.findNavController(requireView()).navigate(directions);
                }
            }
        });
    }
}