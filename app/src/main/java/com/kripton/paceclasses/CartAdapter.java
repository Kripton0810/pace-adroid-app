package com.kripton.paceclasses;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    List<CartModel> list;
    Activity activity;

    public CartAdapter(List<CartModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onDeleteClick(int pos);
    }
    public void setOnItemClicklistener(OnItemClickListener listener)
    {
        mListener = listener;
    }
    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_body_layout,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        String cname = list.get(position).getCname();
        String img = list.get(position).getImage();
        String price = list.get(position).getPrice();
        String id = list.get(position).getId();
        String cid = list.get(position).getCourseid();
        holder.setData(cid,id,cname,img,price,activity);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView course_image,rem;
        Button buy;
        TextView cname,rup;
        public ViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            course_image = itemView.findViewById(R.id.course_image);
            rem = itemView.findViewById(R.id.rem);
            buy = itemView.findViewById(R.id.buy);
            cname = itemView.findViewById(R.id.cname);
            rup = itemView.findViewById(R.id.rup);
            rem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (listener!=null)
                    {
                        listener.onDeleteClick(pos);
                    }
                }
            });
        }
        public void setData(String cid,String id,String name,String cimg,String price,Activity act)
        {
            Glide.with(act)
                    .load(cimg)
                    .placeholder(R.drawable.ic_person_icon)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(course_image);
            rup.setText("Rs. "+price);
            cname.setText(name);
            buy.setOnClickListener(click->{
                Intent intent = new Intent(act,CourseDetials.class);
                intent.putExtra("cid",cid);
                act.startActivity(intent);
            });

        }
    }
}
