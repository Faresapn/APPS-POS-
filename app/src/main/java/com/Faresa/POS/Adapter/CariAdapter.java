package com.Faresa.POS.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Faresa.POS.Activity.PembayaranActivity;
import com.Faresa.POS.Activity.CariPelanggan;
import com.Faresa.POS.Model.PelangganModel;
import com.Faresa.pos.R;

import java.util.List;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class CariAdapter extends RecyclerView.Adapter<CariAdapter.MyViewHolder> {


    private Context mContext;
    private List<PelangganModel> pelanggan_list;


    public CariAdapter(Context mContext, List<PelangganModel> pelanggan_list) {
        this.mContext = mContext;
        this.pelanggan_list = pelanggan_list;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pelanggan, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        PelangganModel obj = pelanggan_list.get(position);

        holder.id_pelanggan.setText(obj.getId_pelanggan());
        holder.nama.setText(obj.getNama());
        holder.hp.setText(obj.getHp());
        holder.alamat.setText(obj.getAlamat());
        holder.poin.setText(obj.getPoin());

    }

    @Override
    public int getItemCount() {
        return pelanggan_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView id_pelanggan,
                nama,
                hp,
                alamat,
                hapus,
                poin;
        LinearLayout x;

        MyViewHolder(final View view) {
            super(view);
            id_pelanggan = view.findViewById(R.id.id_pelanggan);
            nama = view.findViewById(R.id.nama);
            hp = view.findViewById(R.id.hp);
            alamat = view.findViewById(R.id.alamat);
            hapus = view.findViewById(R.id.hapus);
            hapus.setVisibility(View.GONE);
            x = view.findViewById(R.id.x1);
            poin = view.findViewById(R.id.poin);

            x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, PembayaranActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("id_pelanggan", id_pelanggan.getText().toString());
                    i.putExtra("nama", nama.getText().toString());
                    mContext.startActivity(i);
                    CariPelanggan.CP.finish();
                }
            });

        }
    }


}
