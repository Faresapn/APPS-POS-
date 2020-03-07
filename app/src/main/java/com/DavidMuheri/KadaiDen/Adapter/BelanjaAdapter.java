package com.DavidMuheri.KadaiDen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.DavidMuheri.KadaiDen.Activity.PenjualanActivity;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.BelanjaModel;
import com.DavidMuheri.pos.R;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class BelanjaAdapter extends RecyclerView.Adapter<BelanjaAdapter.MyViewHolder> {


    public Context mContext;
    public List<BelanjaModel> belanja_list;


    public BelanjaAdapter(Context mContext, List<BelanjaModel> belanja_list) {
        this.mContext = mContext;
        this.belanja_list = belanja_list;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_belanja, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        BelanjaModel obj = belanja_list.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        String hargax = formatRupiah.format(Double.parseDouble(obj.getHarga()));
        String totalx = formatRupiah.format(Double.parseDouble(obj.getTotal()));

        holder.id_produk.setText(obj.getId_produk());
        holder.nama_produk.setText(obj.getNama_produk());
        holder.harga.setText(hargax);
        holder.jumlah.setText(obj.getJumlah());
        holder.total.setText(totalx);

    }

    @Override
    public int getItemCount() {
        return belanja_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView id_produk,
                nama_produk,
                harga,
                total,
                jumlah,
                plus,
                minus;


        public MyViewHolder(View view) {
            super(view);
            nama_produk = view.findViewById(R.id.nama_produk);
            harga = view.findViewById(R.id.harga_jual);
            id_produk = view.findViewById(R.id.id_produk);
            total = view.findViewById(R.id.total_belanja);
            jumlah = view.findViewById(R.id.jumlah);
            plus = view.findViewById(R.id.plus);
            minus = view.findViewById(R.id.minus);


            plus.setOnClickListener(new View.OnClickListener() {
                public SqlHelper dbcenter;

                @Override
                public void onClick(View view) {

                    SQLiteHandler db = new SQLiteHandler(mContext);
                    HashMap<String, Integer> hitung = db.HitungItemBelanja(id_produk.getText().toString());
                    int m = hitung.get("harga_jual");
                    int x = Integer.valueOf(jumlah.getText().toString());
                    int y = x + 1;
                    int t = y * m;
                    jumlah.setText(y + "");


                    dbcenter = new SqlHelper(mContext);
                    SQLiteDatabase db1 = dbcenter.getWritableDatabase();
                    db1.execSQL("UPDATE keranjang SET jumlah ='" + y + "'," +
                            " total='" + t + "' " +
                            " WHERE id_produk='" + id_produk.getText().toString() + "'");

                    PenjualanActivity.PA.LoadTotalBelanja();
                    PenjualanActivity.PA.LoadKeranjang();


                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                public SqlHelper dbcenter;

                @Override
                public void onClick(View view) {

                    SQLiteHandler db = new SQLiteHandler(mContext);
                    HashMap<String, Integer> hitung = db.HitungItemBelanja(id_produk.getText().toString());
                    int m = hitung.get("harga_jual");


                    int x = Integer.valueOf(jumlah.getText().toString());
                    if (x > 1) {
                        int y = x - 1;
                        jumlah.setText(y + "");
                        int t = y * m;

                        dbcenter = new SqlHelper(mContext);
                        SQLiteDatabase db1 = dbcenter.getWritableDatabase();
                        db1.execSQL("UPDATE keranjang SET jumlah ='" + y + "'," +
                                " total='" + t + "' " +
                                " WHERE id_produk='" + id_produk.getText().toString() + "'");

                        PenjualanActivity.PA.LoadTotalBelanja();
                        PenjualanActivity.PA.LoadKeranjang();

                    } else {

                        // Do nothing but close the dialog
                        SqlHelper dbcenter = new SqlHelper(mContext);
                        SQLiteDatabase db2 = dbcenter.getWritableDatabase();
                        db2.execSQL("DELETE FROM keranjang WHERE id_produk='" + id_produk.getText().toString() + "'");
                        PenjualanActivity.PA.LoadTotalBelanja();
                        PenjualanActivity.PA.LoadKeranjang();

                    }

                }
            });

        }
    }


}
