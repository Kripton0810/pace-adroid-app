package com.kripton.paceclasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllCourseDisplayAdapter extends RecyclerView.Adapter<AllCourseDisplayAdapter.ViewHolder> {
    List<AllCoursesDisplayModel> list;
    Activity mAct;

    public AllCourseDisplayAdapter(List<AllCoursesDisplayModel> list, Activity mAct) {
        this.list = list;
        this.mAct = mAct;
    }

    @NonNull
    @Override
    public AllCourseDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_courses,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCourseDisplayAdapter.ViewHolder holder, int position) {
        String title = list.get(position).getCourse_name_big();
        List<PopularCourseModel> modelList = list.get(position).getModelList();
        holder.setData(title,modelList,mAct);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView course_name_big;
        RecyclerView popular_course_rv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            popular_course_rv = itemView.findViewById(R.id.popular_course_rv);
            course_name_big = itemView.findViewById(R.id.course_name_big);
        }
        public void setData(String title, List<PopularCourseModel> modelList, Activity mAct)
        {
            course_name_big.setText(title);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            popular_course_rv.setLayoutManager(layoutManager);
            PopularCourseAdapter popularCourseAdapter = new PopularCourseAdapter(modelList,mAct);
            popular_course_rv.setAdapter(popularCourseAdapter);
            popularCourseAdapter.notifyDataSetChanged();

        }
    }
}
