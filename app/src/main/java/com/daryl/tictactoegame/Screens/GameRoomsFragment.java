package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daryl.tictactoegame.Data.DBHelper;
import com.daryl.tictactoegame.Data.GameRoom;
import com.daryl.tictactoegame.R;
import com.daryl.tictactoegame.Data.RoomRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class GameRoomsFragment extends Fragment implements RoomRVAdapter.OnItemClickedListener {

    private static final String TAG = GameRoomsFragment.class.getSimpleName();

    // RV Components
    private RoomRVAdapter roomRVAdapter;
    private ArrayList<GameRoom> gameRooms;
    private RecyclerView gameRoomRV;

    // Firebase DB
    private DatabaseReference dbRef;
    private ValueEventListener valueEventListener;
    private Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Back Navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getContext(), "cant go back", Toast.LENGTH_SHORT).show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_rooms, container, false);
        initRVComp(view);
        return view;
    }

    private void initRVComp(View view) {
        gameRoomRV = view.findViewById(R.id.game_rooms_recycler_view);
        gameRooms = new ArrayList<>();
        roomRVAdapter = new RoomRVAdapter(gameRooms, getContext(), R.layout.room_list_item);
        roomRVAdapter.setOnItemClickedListener(this::onItemClicked);
        gameRoomRV.setAdapter(roomRVAdapter);
        GridLayoutManager gridLayoutMan = new GridLayoutManager(getContext(), 2);
        gameRoomRV.setLayoutManager(gridLayoutMan);

        loadGameRooms();
    }

    private void loadGameRooms() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameRooms.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Log.d(TAG, postSnapshot.getKey());
                    GameRoom gm = postSnapshot.getValue(GameRoom.class);
                    Log.d(TAG, gm.getBoard());
                    // Add Available Game Rooms - with Player 2 Missing
                    if (gm.getP2().equals("")) {
                        gameRooms.add(gm);
                    }
                }
                roomRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.toString());
            }
        };
        query = dbRef.child(DBHelper.GAME_ROOMS_KEY);
        query.addValueEventListener(valueEventListener);
    }

    @Override
    public void onItemClicked(int position) {
        GameRoom gm = gameRooms.get(position);
        // Insert Player 2 - Game Room is now full
        dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).child("p2").setValue("Player 2");

        dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    // Get Updated Game Room
                    GameRoom gm = task.getResult().getValue(GameRoom.class);
                    Log.d("Updated Game Room", gm.toString());
                    navigateToWaitingRoom(gm);
                }
            }
        });
    }

    private void navigateToWaitingRoom(GameRoom gm) {
        GameRoomsFragmentDirections.ActionGameRoomsFragmentToWaitingRoomFragment directions =
                GameRoomsFragmentDirections.actionGameRoomsFragmentToWaitingRoomFragment(gm);
        directions.setIsPlayer2(true);
        Navigation.findNavController(requireView()).navigate(directions);
        query.removeEventListener(valueEventListener);
    }

}