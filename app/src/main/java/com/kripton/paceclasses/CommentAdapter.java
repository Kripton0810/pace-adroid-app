package com.kripton.paceclasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<CommentsModel> list;
    Activity act;

    public CommentAdapter(List<CommentsModel> list, Activity act) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        String username,rating,comment,dayago,image;
        username = list.get(position).getUsername();
        rating = list.get(position).getReview();
        comment = list.get(position).getComment();
        dayago = list.get(position).getDayago();
        image = list.get(position).getImage();
        holder.setData(username,rating,comment,dayago,image,act);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username,rating,comment,dayago;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            rating = itemView.findViewById(R.id.rating);
            comment = itemView.findViewById(R.id.comment);
            dayago = itemView.findViewById(R.id.dayago);
        }
        public void setData(String unm, String rate, String comt, String ago, String img, Activity act)
        {
            username.setText(unm);
            rating.setText(rate);
            comment.setText(comt);
            dayago.setText(ago);
            Glide.with(act)
                    .load(img)
                    .placeholder(R.drawable.ic_person_icon)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);
        }
    }
}
