package com.Faresa.POS.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Faresa.POS.Setting.AppConfig;
import com.Faresa.POS.Helper.SQLiteHandler;
import com.Faresa.pos.R;
import com.Faresa.POS.Setting.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddPelanggan extends AppCompatActivity {

    private EditText nama, hp, alamat;
    private ProgressDialog pDialog;
    private String id_toko;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pelanggan);

        nama = findViewById(R.id.nama);
        hp = findViewById(R.id.hp);
        alamat = findViewById(R.id.alamat);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // SQLite database handler
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        id_toko = p.get("id_toko");

        Button simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nama.getText().toString().equalsIgnoreCase("") ||
                        hp.getText().toString().equalsIgnoreCase("") ||
                        alamat.getText().toString().equalsIgnoreCase("")) {
                    pesan("Data belum lengkap");
                } else {
                    savePelanggan(nama.getText().toString(),
                            hp.getText().toString(),
                            alamat.getText().toString());
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(5);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pesan(String isi) {
        Toast.makeText(getApplicationContext(), isi, Toast.LENGTH_SHORT).show();
    }

    private void savePelanggan(final String nama, final String hp, final String alamat) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Sedang menyimpan data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.ADD_PELANGGAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        pesan("Data pelanggan telah disimpan");
                        DataPelanggan.DP.LoadPelanggan();
                        finish();
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
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("hp", hp);
                params.put("alamat", alamat);
                params.put("id_toko", id_toko);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
