package com.DavidMuheri.KadaiDen.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.DavidMuheri.KadaiDen.Helper.SessionManager;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Setting.AppConfig;
import com.DavidMuheri.KadaiDen.Setting.AppController;
import com.DavidMuheri.pos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private String id_toko;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout penjualan = findViewById(R.id.penjualan);
        RelativeLayout pelanggan = findViewById(R.id.pelanggan);
        RelativeLayout setting = findViewById(R.id.setting);
        RelativeLayout logout = findViewById(R.id.logout);
        TextView nama = findViewById(R.id.nama_kasir);
        TextView toko = findViewById(R.id.nama_toko);


        // Progress dialog
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        nama.setText(p.get("nama_kasir"));
        toko.setText(p.get("nama_toko"));
        id_toko = p.get("id_toko");

        if(p.isEmpty()){
            session.setLogin(false);
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

        penjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PilihMode.class);
//                Intent i = new Intent(getApplicationContext(), PenjualanBarcode.class);
                startActivity(i);
            }
        });

        pelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DataPelanggan.class);
                startActivity(i);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void logoutUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan");
        builder.setMessage("Anda yakin ingin Keluar");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                session.setLogin(false);
                db.HapusUser();
                finish();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cloud, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.cloud) {
            Intent i = new Intent(getApplicationContext(), DataPenjualan.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.data) {
            Toast.makeText(getApplicationContext(),"Sinkron data", Toast.LENGTH_SHORT).show();
            SingkronProduk();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SingkronProduk() {


        pDialog.setMessage("Sinkron Produk ...");
        showDialog();

        // Creating volley request obj
        JsonArrayRequest MasukReq = new JsonArrayRequest(AppConfig.LIST_PRODUK + id_toko,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        hideDialog();
                        db.HapusProduk();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                DownloadFoto(obj.getString("foto_produk"));
                                db.TambahProduk(obj.getString("id_produk"),
                                        obj.getString("nama_produk"),
                                        obj.getString("foto_produk"),
                                        obj.getString("harga"),
                                        obj.getString("harga_indo"),
                                        obj.getString("last_update"),
                                        "no",
                                        obj.getString("kategori"),
                                        obj.getString("barcode"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(MasukReq);
        hideDialog();
        Toast.makeText(getApplicationContext(),"Sinkron data selesai", Toast.LENGTH_SHORT).show();
    }

    private void DownloadFoto(final  String foto){

        String path = Environment.getExternalStorageDirectory() + "/alpokat/" + foto;
        File imgFile = new File(path);
        if(!imgFile.exists()){
            new MainActivity.DownloadFileFoto().execute(AppConfig.HOST+"foto_produk/"+foto,foto);
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
                        Toast.makeText(getApplicationContext(),"tidak bisa membuat folder", Toast.LENGTH_LONG).show();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
