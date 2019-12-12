package com.example.menuapp;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Toast;

public class PopUpClass {

    long rating;

    public void showPopUpWindow(final View view) {

        LayoutInflater inflater = (LayoutInflater) view.getContext()
                .getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        final View popupView = inflater.inflate(R.layout.activity_pop_up_view,null);

        // Height x Width Contraints
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        // make items outside of window inactive
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set location of window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // Init elements of window
        final RatingBar popUpRatingBar = popupView.findViewById(R.id.pop_up_rating_bar);

        Button submitBtn = popupView.findViewById(R.id.pop_up_submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = (long) popUpRatingBar.getRating();
                Toast.makeText(view.getContext(), String.valueOf(rating), Toast.LENGTH_SHORT).show();
            }
        });

        // Handler deals with inactive zone clicks
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Close when when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public long getRating(){
        return rating;
    }
}
