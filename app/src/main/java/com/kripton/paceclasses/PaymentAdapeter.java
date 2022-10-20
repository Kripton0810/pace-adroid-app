package com.kripton.paceclasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentAdapeter extends RecyclerView.Adapter<PaymentAdapeter.viewHolder> {
    List<PaymentModel> list;
    Activity act;

    public PaymentAdapeter(List<PaymentModel> list, Activity act) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_detials_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String method = list.get(position).getMethod();
        String cname = list.get(position).getCname();
        String id = list.get(position).getId();
        String created = list.get(position).getCreated_at();
        String amount = list.get(position).getAmount();
        holder.setData(method,id,amount,created,cname,act);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txid,cname,amt,date;
        ImageView method;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txid = itemView.findViewById(R.id.txid);
            cname = itemView.findViewById(R.id.cname);
            amt = itemView.findViewById(R.id.amt);
            date = itemView.findViewById(R.id.date);
            method = itemView.findViewById(R.id.method);

        }
        public void setData(String met, String id, String  am, String dat, String cnm, Activity act)
        {
            txid.setText(id);
            cname.setText(cnm);
            amt.setText(am);
            date.setText(dat);
            if (met.equalsIgnoreCase("upi"))
            {
                method.setImageDrawable(act.getDrawable(R.drawable.ic_upi_svg));
            }
            if (met.equalsIgnoreCase("netbanking"))
            {
                method.setImageDrawable(act.getDrawable(R.drawable.ic_bank_svgrepo_com));
            }
            if (met.equalsIgnoreCase("card"))
            {
                method.setImageDrawable(act.getDrawable(R.drawable.ic_debit_card_payment));
            }
        }
    }
}
