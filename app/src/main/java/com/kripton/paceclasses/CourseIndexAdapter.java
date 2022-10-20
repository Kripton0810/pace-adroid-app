package com.kripton.paceclasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseIndexAdapter extends RecyclerView.Adapter<CourseIndexAdapter.ViewHolder> {
    List<String> list;

    public CourseIndexAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CourseIndexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_curicuram_datalayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseIndexAdapter.ViewHolder holder, int position) {
        String nm = list.get(position);
        holder.setdata(String.valueOf(position+1),nm);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,sno;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sno = itemView.findViewById(R.id.sno);
            title = itemView.findViewById(R.id.title);
        }
        public void setdata(String sn,String tit)
        {
            sno.setText(sn);
            title.setText(tit);
        }
    }
}
