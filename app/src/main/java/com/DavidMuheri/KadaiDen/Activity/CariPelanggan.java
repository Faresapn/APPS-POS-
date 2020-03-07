package com.DavidMuheri.KadaiDen.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.DavidMuheri.KadaiDen.Adapter.CariAdapter;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.PelangganModel;
import com.DavidMuheri.KadaiDen.Setting.AppConfig;
import com.DavidMuheri.pos.R;
import com.DavidMuheri.KadaiDen.Setting.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CariPelanggan extends AppCompatActivity {

    public static CariPelanggan CP;
    private CariAdapter adapter;
    private List<PelangganModel> pelanggan_list;
    private String id_toko;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_pelanggan);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        id_toko = p.get("id_toko");

        FloatingActionButton add = findViewById(R.id.fab_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddPelanggan.class);
                startActivity(i);
            }
        });

        CP = this;

        pelanggan_list = new ArrayList<>();
        adapter = new CariAdapter(getApplicationContext(), pelanggan_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new CariPelanggan.GridSpacingItemDecoration(1, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        TampilPelanggan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.mn_singkron) {
            Toast.makeText(getApplicationContext(),"Sinkronkan data...", Toast.LENGTH_SHORT).show();
            LoadPelanggan();
            Toast.makeText(getApplicationContext(),"Sinkronkan data selesai", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pelanggan, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cariPelanggan(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cariPelanggan(newText);
                return false;
            }
        });

        return true;
    }


    public void cariPelanggan(final String cari) {
        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        pelanggan_list.clear();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM pelanggan WHERE nama LIKE '%" + cari + "%' ORDER BY nama ASC", null);
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            PelangganModel daftar = new PelangganModel();
            daftar.setId_pelanggan(cursor.getString(1));
            daftar.setNama(cursor.getString(2));
            daftar.setHp(cursor.getString(3));
            daftar.setAlamat(cursor.getString(4));
            daftar.setPoin(cursor.getString(5));
            pelanggan_list.add(daftar);
        }

        adapter.notifyDataSetChanged();
    }

    public void TampilPelanggan() {

        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        pelanggan_list.clear();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM pelanggan ORDER BY nama ASC", null);
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            PelangganModel daftar = new PelangganModel();
            daftar.setId_pelanggan(cursor.getString(1));
            daftar.setNama(cursor.getString(2));
            daftar.setHp(cursor.getString(3));
            daftar.setAlamat(cursor.getString(4));
            daftar.setPoin("Jumlah Poin : " + cursor.getString(5));
            pelanggan_list.add(daftar);
        }

        adapter.notifyDataSetChanged();

    }

    public void LoadPelanggan() {




        // Creating volley request obj
        JsonArrayRequest MasukReq = new JsonArrayRequest(AppConfig.LIST_PELANGGAN + id_toko,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        db.HapusPelanggan();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                db.TambahPelanggan(
                                        obj.getString("id_pelanggan"),
                                        obj.getString("nama"),
                                        obj.getString("hp"),
                                        obj.getString("alamat"),
                                        obj.getString("poin")
                                );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        TampilPelanggan();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        AppController.getInstance().addToRequestQueue(MasukReq);
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
}
