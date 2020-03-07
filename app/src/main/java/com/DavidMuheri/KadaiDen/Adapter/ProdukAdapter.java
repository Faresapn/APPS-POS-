package com.DavidMuheri.KadaiDen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.DavidMuheri.KadaiDen.Activity.PenjualanActivity;
import com.DavidMuheri.KadaiDen.Fragment.BarangFragment;
import com.DavidMuheri.KadaiDen.Fragment.FavoritFragment;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.ProdukModel;
import com.DavidMuheri.KadaiDen.Setting.AppConfig;
import com.DavidMuheri.pos.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.MyViewHolder> {


    public Context mContext;
    public List<ProdukModel> produk_list;


    public ProdukAdapter(Context mContext, List<ProdukModel> produk_list) {
        this.mContext = mContext;
        this.produk_list = produk_list;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barang, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ProdukModel obj = produk_list.get(position);
        holder.id_produk.setText(obj.getId_produk());
        holder.nama_produk.setText(obj.getNama_produk());
        holder.harga.setText(obj.getHarga());
        holder.harga_indo.setText(obj.getHarga_indo());

        String x = obj.getFavorit();

        if(x != null && x.equalsIgnoreCase("no")) {
            holder.btn_favorit.setVisibility(View.VISIBLE);
            holder.btn_favorit2.setVisibility(View.GONE);
        }else{
            holder.btn_favorit.setVisibility(View.GONE);
            holder.btn_favorit2.setVisibility(View.VISIBLE);
        }


        String path = Environment.getExternalStorageDirectory() + "/alpokat/" + obj.getFoto();
        File imgFile = new File(path);
        if(!imgFile.exists()){
            new DownloadFileFoto().execute(AppConfig.HOST+"foto_produk/"+ obj.getFoto(), obj.getFoto());

        }
        Glide.with(mContext)
                .load(imgFile)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_pasir)
                        .error(R.drawable.ic_launcher_background).centerCrop()
                )
                .into(holder.foto);



    }

    @Override
    public int getItemCount() {
        return produk_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nama_produk,
                harga,
                harga_indo,
                id_produk,
                barcode;

        public ImageView foto,btn_favorit,btn_favorit2;
        public SQLiteHandler db;
        public RelativeLayout layout_barang;
        public LinearLayout papan;


        public MyViewHolder(View view) {
            super(view);
            foto = view.findViewById(R.id.foto_produk);
            nama_produk = view.findViewById(R.id.nama_produk);
            harga = view.findViewById(R.id.harga);
            harga_indo = view.findViewById(R.id.harga_indo);
            id_produk = view.findViewById(R.id.id_produk);
            layout_barang = view.findViewById(R.id.layout_barang);
            btn_favorit = view.findViewById(R.id.btn_favorit);
            btn_favorit2 = view.findViewById(R.id.btn_favorit2);
            papan = view.findViewById(R.id.papan);
            barcode = view.findViewById(R.id.barcode);

            btn_favorit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Jadi Favorit", Toast.LENGTH_SHORT).show();
                    SqlHelper dbcenter = new SqlHelper(mContext);
                    SQLiteDatabase db1 = dbcenter.getWritableDatabase();
                    db1.execSQL("UPDATE produk SET favorit = 'yes' WHERE id_produk='" + id_produk.getText().toString() + "'");
                    BarangFragment.BF.LoadProduk();
                    FavoritFragment.FF.LoadProduk();
                }
            });

            btn_favorit2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Tidak Favorit", Toast.LENGTH_SHORT).show();
                    SqlHelper dbcenter = new SqlHelper(mContext);
                    SQLiteDatabase db1 = dbcenter.getWritableDatabase();
                    db1.execSQL("UPDATE produk SET favorit = 'no' WHERE id_produk='" + id_produk.getText().toString() + "'");
                    BarangFragment.BF.LoadProduk();
                    FavoritFragment.FF.LoadProduk();
                }
            });


            db = new SQLiteHandler(mContext);
            foto.setOnClickListener(new View.OnClickListener() {
                public SqlHelper dbcenter;

                @Override
                public void onClick(View view) {


                    int ji;
                    HashMap<String, Integer> hitung = db.HitungItemBelanja(id_produk.getText().toString());
                    if (hitung.get("jumlah") == 0) {
                        db.IsiKeranjang(
                                id_produk.getText().toString(),
                                nama_produk.getText().toString(),
                                "1",
                                harga_indo.getText().toString(),
                                harga_indo.getText().toString(),
                                barcode.getText().toString()
                        );
                    } else {
                        ji = hitung.get("jumlah_produk") + 1;
                        int m = hitung.get("harga_jual");
                        int t = m * ji;
                        dbcenter = new SqlHelper(mContext);
                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                        db.execSQL("UPDATE keranjang SET jumlah ='" + ji + "'," +
                                " total='" + t + "' " +
                                " WHERE id_produk='" + id_produk.getText().toString() + "'");
                    }


                    PenjualanActivity.PA.LoadTotalBelanja();
                    PenjualanActivity.PA.LoadKeranjang();

                }
            });

            papan.setOnClickListener(new View.OnClickListener() {
                public SqlHelper dbcenter;

                @Override
                public void onClick(View view) {


                    int ji;
                    HashMap<String, Integer> hitung = db.HitungItemBelanja(id_produk.getText().toString());
                    if (hitung.get("jumlah") == 0) {
                        db.IsiKeranjang(
                                id_produk.getText().toString(),
                                nama_produk.getText().toString(),
                                "1",
                                harga_indo.getText().toString(),
                                harga_indo.getText().toString(),
                                barcode.getText().toString()
                        );
                    } else {
                        ji = hitung.get("jumlah_produk") + 1;
                        int m = hitung.get("harga_jual");
                        int t = m * ji;
                        dbcenter = new SqlHelper(mContext);
                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                        db.execSQL("UPDATE keranjang SET jumlah ='" + ji + "'," +
                                " total='" + t + "' " +
                                " WHERE id_produk='" + id_produk.getText().toString() + "'");
                    }


                    PenjualanActivity.PA.LoadTotalBelanja();
                    PenjualanActivity.PA.LoadKeranjang();

                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadFileFoto extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int count;

            try {

                URL url = new URL(params[0]);
                String nama = params[1];
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lengthofFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lengthofFile);

                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "alpokat");

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Toast.makeText(mContext,"tidak bisa membuat folder", Toast.LENGTH_LONG).show();
                        return null;
                    }
                }

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("sdcard/alpokat/" + nama);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthofFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("ANDRO_ASYNC", values[0]);
        }

    }

}
