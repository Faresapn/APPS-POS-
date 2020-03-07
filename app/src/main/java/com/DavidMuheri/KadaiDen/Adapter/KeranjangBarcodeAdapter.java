package com.DavidMuheri.KadaiDen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.DavidMuheri.KadaiDen.Model.KeranjangModel;
import com.DavidMuheri.pos.R;


import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class KeranjangBarcodeAdapter extends RecyclerView.Adapter<KeranjangBarcodeAdapter.MyViewHolder>{


    private Context mContext;
    private List<KeranjangModel> list;


    public KeranjangBarcodeAdapter(Context mContext, List<KeranjangModel> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keranjang_barcode, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nama_barang, jumlah, total, id_barang;
        RelativeLayout keranjang_klik;
        MyViewHolder(View view) {
            super(view);
            nama_barang = view.findViewById(R.id.nama_barang);
            jumlah = view.findViewById(R.id.jumlah);
            total = view.findViewById(R.id.total);
            id_barang = view.findViewById(R.id.id_barang);
            keranjang_klik = view.findViewById(R.id.keranjang_klik);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"ResourceAsColor", "ResourceType", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        KeranjangModel obj = list.get(position);
        DecimalFormat myFormatter = new DecimalFormat("###,###.###");
        int h = Integer.parseInt(obj.getHarga()) * Integer.parseInt(obj.getJumlah());

        String x = myFormatter.format(Double.parseDouble(obj.getHarga()));
        String y = myFormatter.format(h);

        holder.nama_barang.setText(obj.getNama_barang());
        holder.jumlah.setText(obj.getJumlah() + " x " + x );
        holder.total.setText("Rp. " + y);
        holder.id_barang.setText(obj.getId_barang());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
