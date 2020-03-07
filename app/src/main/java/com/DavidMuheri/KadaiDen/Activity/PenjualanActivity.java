package com.DavidMuheri.KadaiDen.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.DavidMuheri.KadaiDen.Adapter.BelanjaAdapter;
import com.DavidMuheri.KadaiDen.Adapter.SlidingPenjualanAdapter;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.BelanjaModel;
import com.DavidMuheri.pos.R;
import com.DavidMuheri.KadaiDen.Tab.SlidingTabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PenjualanActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static PenjualanActivity PA;
    private TextView total_belanja, jumlah_item;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private String id_toko;
    private LinearLayout daftar_belanja, daftar_barang;

    private BelanjaAdapter adapter;
    private List<BelanjaModel> belanja_list;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_lanscape);

        total_belanja = findViewById(R.id.total_belanja);
        jumlah_item = findViewById(R.id.jumlah_item);
        ImageButton batal = findViewById(R.id.batal);
        ImageButton selesai = findViewById(R.id.selesai);
        daftar_belanja = findViewById(R.id.daftar_belanja);
        daftar_barang = findViewById(R.id.daftar_barang);



        int orientation = this.getResources().getConfiguration().orientation;
        LinearLayout.LayoutParams param_belanja,param_barang;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            param_belanja = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    8.0f);
            param_barang = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    4.0f);
        }else{
            param_belanja = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    9.0f);
            param_barang = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    3.0f);
        }


        daftar_belanja.setLayoutParams(param_belanja);
        daftar_barang.setLayoutParams(param_barang);


        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PenjualanActivity.this);
                builder.setTitle("Peringatan");
                builder.setMessage("Anda yakin membatalkan belanja ini ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        SqlHelper dbcenter = new SqlHelper(PenjualanActivity.this);
                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                        db.execSQL("DELETE FROM keranjang");
                        PenjualanActivity.PA.LoadTotalBelanja();
                        PenjualanActivity.PA.LoadKeranjang();
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

        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PembayaranActivity.class);
                i.putExtra("id_pelanggan", "0");
                i.putExtra("nama", "Pilih Pelanggan");
                i.putExtra("mode", "manual");
                startActivity(i);
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(PenjualanActivity.this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        id_toko = p.get("id_toko");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setElevation(5);
        }


        PA = this;

        ViewPager mViewPager = findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new SlidingPenjualanAdapter(getSupportFragmentManager(), this));
        SlidingTabLayout mSlidingTabLayout = findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.black));
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
        mSlidingTabLayout.setViewPager(mViewPager);

        LoadTotalBelanja();

        belanja_list = new ArrayList<>();
        adapter = new BelanjaAdapter(getApplicationContext(), belanja_list);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new PenjualanActivity.GridSpacingItemDecoration(1, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        LoadKeranjang();
    }



    public void tutup() {
        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getWritableDatabase();
        db.execSQL("DELETE FROM keranjang");
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void LoadTotalBelanja() {
        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase dbp = dbcenter.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
        int total = 0;
        int jitem = 0;
        if (cursor.getCount() > 0) {
            for (int cc = 0; cc < cursor.getCount(); cc++) {
                cursor.moveToPosition(cc);
                total = total + Integer.valueOf(cursor.getString(6));
                jitem = jitem + Integer.valueOf(cursor.getString(4));
            }
        }

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String x = formatRupiah.format(total);

        total_belanja.setText(String.valueOf(x));
        jumlah_item.setText(jitem + "");
    }



    public void LoadKeranjang() {
        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase dbp = dbcenter.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
        cursor.moveToFirst();
        belanja_list.clear();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            BelanjaModel daftar = new BelanjaModel();
            daftar.setId_produk(cursor.getString(2));
            daftar.setNama_produk(cursor.getString(3));
            daftar.setJumlah(cursor.getString(4));
            daftar.setHarga(cursor.getString(5));
            daftar.setTotal(cursor.getString(6));
            belanja_list.add(daftar);
        }

        adapter.notifyDataSetChanged();
        if(cursor.getCount()>1) {
            recyclerView.smoothScrollToPosition(cursor.getCount() - 1);
        }
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
