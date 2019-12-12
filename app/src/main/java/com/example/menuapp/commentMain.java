package com.example.menuapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuapp.models.commentModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class commentMain extends RecyclerView.Adapter<commentMain.commentHolder> {

    Context context;
    List<commentModel> commentList;

    public commentMain(Context context, List<commentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public commentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_comments, parent, false);

        return new commentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentHolder holder, int position) {

        //get data
        String comment = commentList.get(position).getComment();
        String username = commentList.get(position).getUsername();
        long rating = commentList.get(position).getRating();

        // convert timestamp to dd/mm/yyyy hh:mm
//        Calendar calendar = Calendar.getInstance(Locale.getDefault());
//        calendar.setTimeInMillis(Long.parseLong(timestamp));
//        String curTime = DateFormat.format("MM/dd/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.commentAuthor.setText(username);
        holder.commentField.setText(comment);
        holder.commentRating.setRating(rating);
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class commentHolder extends RecyclerView.ViewHolder {

        ImageView avatarID;
        TextView commentAuthor, commentField;
        RatingBar commentRating;

        public commentHolder(@NonNull View view) {
            super(view);
            avatarID = view.findViewById(R.id.avatar_id);
            commentAuthor = view.findViewById(R.id.comment_author);
            commentField = view.findViewById(R.id.comment_field);
            commentRating = view.findViewById(R.id.comment_rating);
        }
    }

}
