package com.kripton.paceclasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PopularSearchAdapter extends RecyclerView.Adapter<PopularSearchAdapter.ViewHolder> {
    List<String> list;

    public PopularSearchAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override

    public PopularSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_search_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularSearchAdapter.ViewHolder holder, int position) {
        String sea = list.get(position);
        holder.setData(sea);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView searches;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searches = itemView.findViewById(R.id.searches);
        }
        public void setData(String sea)
        {
            searches.setText(sea);
        }
    }
}
