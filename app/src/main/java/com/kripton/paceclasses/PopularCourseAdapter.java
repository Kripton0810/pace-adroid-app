package com.kripton.paceclasses;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class PopularCourseAdapter extends RecyclerView.Adapter<PopularCourseAdapter.ViewHolder> {
    List<PopularCourseModel> list;
    Activity act;

    public PopularCourseAdapter(List<PopularCourseModel> list, Activity act) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public PopularCourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCourseAdapter.ViewHolder holder, int position) {
        String id= list.get(position).getId();
        String cname= list.get(position).getCourse_name();
        String iname= list.get(position).getInstructor_name();
        String rating= list.get(position).getRatings();
        String image_url= list.get(position).getImage_url();
        String fees = list.get(position).getCourse_fees();
        holder.setData(id,rating,cname,iname,fees,image_url,act);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rating_card,course_name,instructor_name,course_fees;
        ImageView course_image,add_to_wishlist;
        View v;
        boolean add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rating_card = itemView.findViewById(R.id.rating_card);
            course_name = itemView.findViewById(R.id.course_name);
            v = itemView;
            instructor_name = itemView.findViewById(R.id.instructor_name);
            course_fees = itemView.findViewById(R.id.course_fees);
            course_image = itemView.findViewById(R.id.course_image);
            add_to_wishlist = itemView.findViewById(R.id.add_to_wishlist);
            add = false;
        }
        public void setData(String id,String rating, String cname, String iname, String fees, String img_url, Activity act)
        {
            rating_card.setText(rating);
            course_name.setText(cname);
            instructor_name.setText(iname);
            course_fees.setText(fees);
//            Glide.with(course_image)
//                    .load(act.getDrawable(Integer.parseInt(img_url)));
            v.setOnClickListener(view->{
                Intent intent = new Intent(act,CourseDetials.class);
                intent.putExtra("cid",id);
                act.startActivity(intent);
            });
            Glide.with(act)
                    .load(img_url)
                    .placeholder(R.drawable.ic_person_icon)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(course_image);
            add_to_wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!add)
                    {
                        add_to_wishlist.setImageDrawable(act.getDrawable(R.drawable.ic_heart));
                        add = true;
                    }else
                    {
                        add = false;
                        add_to_wishlist.setImageDrawable(act.getDrawable(R.drawable.ic_un_heart));
                    }
                }
            });


        }
    }
}
