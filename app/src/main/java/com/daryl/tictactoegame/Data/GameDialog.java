package com.daryl.tictactoegame.Data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.daryl.tictactoegame.R;

public class GameDialog extends DialogFragment implements View.OnClickListener {

    private String title, subtitle, buttonTitle;
    private OnClickDialogListener onClickDialogListener;

    public GameDialog(String title, String subtitle, String buttonTitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.buttonTitle = buttonTitle;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_game, null);
        initDialogViews(view);
        adb.setView(view);
        return adb.create();
    }

    private void initDialogViews(View view) {
        TextView title = view.findViewById(R.id.game_dialog_title);
        TextView subtitle = view.findViewById(R.id.game_dialog_subtitle);
        Button button = view.findViewById(R.id.game_dialog_button);
        title.setText(this.title);
        subtitle.setText(this.subtitle);
        button.setText(buttonTitle);
        button.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        onClickDialogListener.onClickDialog();
    }

    public interface OnClickDialogListener {
        void onClickDialog();
    }

    public void setOnClickDialogListener(OnClickDialogListener onClickDialogListener) {
        this.onClickDialogListener = onClickDialogListener;
    }
}
