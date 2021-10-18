package com.example.instagramclone;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {

    private CardView cardView;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView textView;

    public ProgressButton(Context context, View view) {

        cardView = view.findViewById(R.id.cardView);
        layout = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.textView);
    }

    public void buttonActivated() {
        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Please wait...");
    }

    public void buttonFinished() {
        progressBar.setVisibility(View.GONE);
        layout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
        textView.setText("Done");
    }

    public void buttonReset() {
        progressBar.setVisibility(View.GONE);
        layout.setBackgroundColor(cardView.getResources().getColor(R.color.blue));
        textView.setText("Submit");
    }
}
