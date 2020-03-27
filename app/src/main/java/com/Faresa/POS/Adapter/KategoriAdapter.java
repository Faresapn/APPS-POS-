package com.Faresa.POS.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.Faresa.POS.Model.KategoriModel;
import com.Faresa.pos.R;

import java.util.List;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.MyViewHolder> {


    public Context mContext;
    public List<KategoriModel> kategori_list;


    public KategoriAdapter(Context mContext, List<KategoriModel> kategori_list) {
        this.mContext = mContext;
        this.kategori_list = kategori_list;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kategori, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        KategoriModel obj = kategori_list.get(position);
        holder.nama_kategori.setText(obj.getNama_kategori());
        holder.id_kategori.setText(obj.getId_kategori());
    }

    @Override
    public int getItemCount() {
        return kategori_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nama_kategori,
                id_kategori;
        public RelativeLayout kategori_layout;


        public MyViewHolder(View view) {
            super(view);
            nama_kategori = view.findViewById(R.id.nama_kategori);
            id_kategori = view.findViewById(R.id.id_kategori);
            kategori_layout = view.findViewById(R.id.layout_kategori);

            kategori_layout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    kategori_layout.setBackgroundColor(R.color.colorAccent);
                }
            });

        }
    }


}
