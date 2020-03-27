package com.Faresa.POS.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.Faresa.POS.Activity.DataPenjualan;
import com.Faresa.POS.Model.PenjualanModel;
import com.Faresa.pos.R;

import java.util.List;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class PenjualanAdapter extends RecyclerView.Adapter<PenjualanAdapter.MyViewHolder> {


    public Context mContext;
    public List<PenjualanModel> penjualan_list;


    public PenjualanAdapter(Context mContext, List<PenjualanModel> penjualan_list) {
        this.mContext = mContext;
        this.penjualan_list = penjualan_list;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_penjualan, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        PenjualanModel obj = penjualan_list.get(position);

        holder.no_faktur.setText(obj.getNo_faktur());

    }

    @Override
    public int getItemCount() {
        return penjualan_list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView no_faktur;
        public Button retur;


        MyViewHolder(View view) {
            super(view);
            no_faktur = view.findViewById(R.id.no_faktur);
            retur = view.findViewById(R.id.btn_retur);

            retur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataPenjualan.DJ.no_retur(no_faktur.getText().toString());

                }
            });
        }
    }




}
