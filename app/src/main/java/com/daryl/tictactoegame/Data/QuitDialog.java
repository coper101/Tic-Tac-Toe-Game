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

public class QuitDialog extends DialogFragment implements View.OnClickListener {

    private OnQuitDialogListener onQuitDialogListener;
    private boolean isHostQuit;

    public QuitDialog(boolean isHostQuit) {
        this.isHostQuit = isHostQuit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quit, null);
        initDialogViews(view);
        adb.setView(view);
        return adb.create();
    }

    private void initDialogViews(View view) {
        TextView title = view.findViewById(R.id.quit_dialog_title);
        TextView subtitle = view.findViewById(R.id.quit_dialog_subtitle);
        Button quitButton = view.findViewById(R.id.quit_dialog_quit_button);
        Button cancelButton = view.findViewById(R.id.quit_dialog_cancel_button);
        if (!isHostQuit) {
            // Ok Button
            title.setText("Oops..");
            subtitle.setText("Your opponent has left the Room.");
            quitButton.setText("Home");
            quitButton.setOnClickListener(this::onClick);
            cancelButton.setVisibility(View.GONE);
        } else {
            // Quit & Cancel Buttons
            // default text
            quitButton.setOnClickListener(this::onClick);
            cancelButton.setOnClickListener(this::onClick);
        }
    }

    @Override
    public void onClick(View v) {
        onQuitDialogListener.onQuitDialog(v);
    }

    public interface OnQuitDialogListener {
        void onQuitDialog(View view);
    }

    public void setOnQuitDialogListener(OnQuitDialogListener onQuitDialogListener) {
        this.onQuitDialogListener = onQuitDialogListener;
    }
}
