package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dbRef.child(DBHelper.GAME_ROOMS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
        });
    }

    @Override
    public void onItemClicked(int position) {
        GameRoom gm = gameRooms.get(position);
        // Insert Player 2 - Game Room is now full
        dbRef.child(DBHelper.GAME_ROOMS_KEY).child(gm.getId()).child("p2").setValue("Player 2");
        GameRoomsFragmentDirections.ActionGameRoomsFragmentToWaitingRoomFragment directions =
                GameRoomsFragmentDirections.actionGameRoomsFragmentToWaitingRoomFragment(gm);
        Navigation.findNavController(requireView()).navigate(directions);
    }


}