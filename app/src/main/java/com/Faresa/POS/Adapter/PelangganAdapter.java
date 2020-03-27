package com.Faresa.POS.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Faresa.POS.Setting.AppConfig;
import com.Faresa.POS.Activity.DataPelanggan;
import com.Faresa.POS.Model.PelangganModel;
import com.Faresa.pos.R;
import com.Faresa.POS.Setting.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MacBookPro on 27/11/17.
 */


public class PelangganAdapter extends RecyclerView.Adapter<PelangganAdapter.MyViewHolder> {


    public Context mContext;
    public List<PelangganModel> pelanggan_list;


    public PelangganAdapter(Context mContext, List<PelangganModel> pelanggan_list) {
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

    private void hapusPelanggan(final String id_pelanggan) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.HAPUS_PELANGGAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        DataPelanggan.DP.LoadPelanggan();
                    } else {
                        // Error in login. Get the error message
                        pesan("Error Boss");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pesan("Json error 1: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pesan(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id_pelanggan", id_pelanggan);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void pesan(String isi) {
        Toast.makeText(mContext, isi, Toast.LENGTH_SHORT).show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView id_pelanggan,
                nama,
                hp,
                alamat,
                hapus,
                poin;


        MyViewHolder(View view) {
            super(view);
            id_pelanggan = view.findViewById(R.id.id_pelanggan);
            nama = view.findViewById(R.id.nama);
            hp = view.findViewById(R.id.hp);
            alamat = view.findViewById(R.id.alamat);
            hapus = view.findViewById(R.id.hapus);
            poin = view.findViewById(R.id.poin);

            hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DataPelanggan.DP);
                    builder.setTitle("Peringatan");
                    builder.setMessage("Anda yakin menghapus belanja ini ?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            hapusPelanggan(id_pelanggan.getText().toString());
                            DataPelanggan.DP.LoadPelanggan();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();


                }
            });


        }
    }


}
