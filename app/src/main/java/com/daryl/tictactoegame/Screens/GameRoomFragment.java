package com.daryl.tictactoegame.Screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daryl.tictactoegame.R;

public class GameRoomFragment extends Fragment {

    // Views

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_room, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

    }
}