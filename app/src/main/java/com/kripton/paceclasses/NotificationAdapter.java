package com.kripton.paceclasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    List<NotificationModel> list;
    Activity activity;
    OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onDeleteClick(int pos);
    }
    public void setOnItemClicklistener(OnItemClickListener listener)
    {
        mListener = listener;
    }
    public NotificationAdapter(List<NotificationModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ms = list.get(position).getMsg();
        String id = list.get(position).getId();
        holder.setView(ms,id,activity);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        ImageView del;
        public ViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            del = itemView.findViewById(R.id.del);
            del.setOnClickListener(new View.OnClickListener() {
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
        public void setView(String ms,String id,Activity act)
        {
            msg.setText(ms);
        }
    }
}
