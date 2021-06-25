package com.daryl.tictactoegame.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daryl.tictactoegame.R;

import java.util.List;

public class RoomRVAdapter
        extends RecyclerView.Adapter<RoomRVAdapter.GameRoomViewHolder> {

    private final List<GameRoom> gameRooms;
    private final Context context;
    private final int layoutResId;
    private OnItemClickedListener myClickedListener;

    public RoomRVAdapter(List<GameRoom> idolList, Context context, int layoutResId) {
        this.gameRooms = idolList;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    // Track Item View is Clicked
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        myClickedListener = onItemClickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClicked(int position);
    }

    @NonNull
    @Override
    public GameRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new GameRoomViewHolder(view, myClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GameRoomViewHolder holder, int position) {
        final GameRoom gm = gameRooms.get(position);
        holder.idTV.setText(gm.getId());
        holder.nameTV.setText(gm.getName());
    }

    private int toPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * density);
        return px;
    }

    @Override
    public int getItemCount() {
        return gameRooms.size();
    }

    // Holder
    static class GameRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView idTV, nameTV;
        Button joinBtn;
        private final OnItemClickedListener clickedListener;

        public GameRoomViewHolder(@NonNull View itemView, OnItemClickedListener clickedListener) {
            super(itemView);
            this.clickedListener = clickedListener;
            idTV = itemView.findViewById(R.id.game_room_id_text_view);
            nameTV = itemView.findViewById(R.id.game_room_name_text_view);
            joinBtn = itemView.findViewById(R.id.game_room_join_button);
            joinBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickedListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    clickedListener.onItemClicked(position);
            }
        }
    } // end of view holder class

} // end of class
