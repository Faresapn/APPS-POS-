package com.DavidMuheri.KadaiDen.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.DavidMuheri.KadaiDen.Setting.AppConfig;
import com.DavidMuheri.KadaiDen.Adapter.PenjualanAdapter;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.PenjualanModel;
import com.DavidMuheri.pos.R;
import com.DavidMuheri.KadaiDen.Setting.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPenjualan extends AppCompatActivity {

    public static DataPenjualan DJ;
    private PenjualanAdapter adapter;
    private List<PenjualanModel> penjualan_list;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private FloatingActionButton add;
    private FrameLayout dialog_retur;


    String id_toko = "";
    String id_produk = "";
    String jumlah = "";
    String id_kasir = "";
    String id_pelanggan = "";
    String faktur = "";
    String tanggal = "";

    private Button batal,retur;
    private RecyclerView recyclerView;
    private TextView text_info,txt_faktur;
    private EditText password;
    private String xidtoko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_penjualan);


        DJ = this;

        add = findViewById(R.id.fab_cloud);
        text_info = findViewById(R.id.text_info);
        password = findViewById(R.id.password);
        txt_faktur = findViewById(R.id.txt_faktur);
        dialog_retur = findViewById(R.id.dialog_retur);
        batal = findViewById(R.id.batal);
        retur = findViewById(R.id.retur);

        dialog_retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        xidtoko = p.get("id_toko");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        penjualan_list = new ArrayList<>();
        adapter = new PenjualanAdapter(getApplicationContext(), penjualan_list);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new DataPenjualan.GridSpacingItemDecoration(1, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(5);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        TampilPenjualan();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadPenjualan(id_toko,
                        id_produk,
                        jumlah,
                        id_kasir,
                        id_pelanggan,
                        faktur,
                        tanggal);
            }
        });

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_retur.setVisibility(View.GONE);
            }
        });
    }

    public void no_retur(final String no){
        txt_faktur.setText(no);
        dialog_retur.setVisibility(View.VISIBLE);
        add.setEnabled(false);
        password.setText("");
        password.setFocusable(true);
        retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturPassword(id_toko,password.getText().toString(), no);
            }
        });
    }

    private void ReturPassword(final String id_toko, final String pass_retur, final String xfaktur) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Mengkonfirmasi password retur ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.KONFIRMASI_RETUR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        db.ReturTransaksi(xfaktur);
                        dialog_retur.setVisibility(View.GONE);
                        add.setEnabled(true);
                        TampilPenjualan();
                        Toast.makeText(getApplicationContext(),"Penjualan berhasil diretur", Toast.LENGTH_SHORT).show();
                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(),"Maaf password retur salah !", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Maaf server tidak bisa diakses !", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage() + "zzzz", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("id_toko",id_toko);
                params.put("pass_retur", pass_retur);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retur, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CariPenjualan(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CariPenjualan(newText);
                return false;
            }
        });

        return true;
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

    public void TampilPenjualan() {

        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        penjualan_list.clear();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM transaksi", null);
        cursor.moveToFirst();

        id_toko = "";
        id_produk = "";
        jumlah = "";
        id_kasir = "";
        id_pelanggan = "";
        faktur = "";
        tanggal = "";

        if(cursor.getCount() > 0){
            text_info.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            text_info.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            PenjualanModel daftar = new PenjualanModel();
            daftar.setNo_faktur(cursor.getString(6));
            String x;
            if(cc == cursor.getCount()-1){
                x = "";
            }else{
                x = "/";
            }
            penjualan_list.add(daftar);
            id_toko = id_toko + cursor.getString(1) + x;
            id_produk = id_produk + cursor.getString(2) + x;
            jumlah = jumlah +  cursor.getString(3) + x;
            id_kasir = id_kasir + cursor.getString(4) + x;
            id_pelanggan = id_pelanggan + cursor.getString(5) + x;
            faktur = faktur + cursor.getString(6) + x;
            tanggal = tanggal + cursor.getString(7) + x;
        }

        adapter.notifyDataSetChanged();


    }

    public void CariPenjualan(String no) {

        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        penjualan_list.clear();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM transaksi WHERE faktur LIKE '%"+ no +"%'", null);
        cursor.moveToFirst();

        id_toko = "";
        id_produk = "";
        jumlah = "";
        id_kasir = "";
        id_pelanggan = "";
        faktur = "";
        tanggal = "";

        if(cursor.getCount() > 0){
            text_info.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            text_info.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            PenjualanModel daftar = new PenjualanModel();
            daftar.setNo_faktur(cursor.getString(6));
            String x;
            if(cc == cursor.getCount()-1){
                x = "";
            }else{
                x = "/";
            }
            penjualan_list.add(daftar);
            id_toko = id_toko + cursor.getString(1) + x;
            id_produk = id_produk + cursor.getString(2) + x;
            jumlah = jumlah +  cursor.getString(3) + x;
            id_kasir = id_kasir + cursor.getString(4) + x;
            id_pelanggan = id_pelanggan + cursor.getString(5) + x;
            faktur = faktur + cursor.getString(6) + x;
            tanggal = tanggal + cursor.getString(7) + x;
        }

        adapter.notifyDataSetChanged();


    }




    private void UploadPenjualan(
            final String id_toko,
            final String id_produk,
            final String jumlah,
            final String id_kasir,
            final String id_pelanggan,
            final String faktur,
            final String tanggal
    ) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Sedang upload data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BAYAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        pesan("Data berhasil diupload");
                        SQLiteHandler db1 = new SQLiteHandler(getApplicationContext());
                        db1.HapusTransaksi();
                        TampilPenjualan();
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
                params.put("id_toko", id_toko);
                params.put("id_produk", id_produk);
                params.put("jumlah", jumlah);
                params.put("id_kasir", id_kasir);
                params.put("id_pelanggan", id_pelanggan);
                params.put("faktur", faktur);
                params.put("tanggal", tanggal);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
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

    private void pesan(String pesan){
        Toast.makeText(getApplicationContext(),pesan,Toast.LENGTH_SHORT).show();
    }
}
