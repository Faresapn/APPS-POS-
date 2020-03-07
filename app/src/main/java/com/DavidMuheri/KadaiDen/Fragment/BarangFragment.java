package com.DavidMuheri.KadaiDen.Fragment;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.ProdukModel;
import com.DavidMuheri.KadaiDen.Adapter.ProdukAdapter;
import com.DavidMuheri.pos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarangFragment extends Fragment {

    private CardView cardcari;
    private ImageView hapus_cari;

    private ProdukAdapter adapter;
    private List<ProdukModel> produk_list;
    private TextView txt_cari;
    private String kategori;

    @SuppressLint("StaticFieldLeak")
    public static BarangFragment BF;

    public BarangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_barang, container, false);
        kategori = this.getArguments().getString("kategori");



        FloatingActionButton cari = v.findViewById(R.id.fab_cari);
        cardcari = v.findViewById(R.id.cardcari);
        txt_cari = v.findViewById(R.id.text_cari);
        hapus_cari = v.findViewById(R.id.hapus_cari);


        hapus_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_cari.setText("");
                cardcari.setVisibility(View.INVISIBLE);
            }
        });

        BF = this;



        produk_list = new ArrayList<>();
        adapter = new ProdukAdapter(v.getContext(), produk_list);

        int orientation = this.getResources().getConfiguration().orientation;
        int x;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //code for portrait mode
            x = 2;
        } else {
            //code for landscape mode
            x = 3;
        }
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(v.getContext(), x);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new BarangFragment.GridSpacingItemDecoration(1, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        LoadProduk();

        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardcari.getVisibility() == View.VISIBLE) {
                    cardcari.setVisibility(View.INVISIBLE);
                } else {
                    cardcari.setVisibility(View.VISIBLE);
                }
            }
        });

        txt_cari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do some thing now
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                LoadCari(txt_cari.getText().toString());
                if(txt_cari.getText().toString().length()>0){
                    hapus_cari.setVisibility(View.VISIBLE);
                }else{
                    hapus_cari.setVisibility(View.GONE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do something at this time
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadProduk();
    }

    public void LoadProduk() {
        SqlHelper dbcenter = new SqlHelper(getContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        produk_list.clear();
        Cursor cursor;
        if(kategori.equalsIgnoreCase("Semua")) {
            cursor = db.rawQuery("SELECT * FROM produk", null);
        }else{
            cursor = db.rawQuery("SELECT * FROM produk WHERE kategori='" + kategori + "'", null);
        }
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            ProdukModel daftar = new ProdukModel();
            daftar.setId_produk(cursor.getString(1));
            daftar.setNama_produk(cursor.getString(2));
            daftar.setFoto(cursor.getString(3));
            daftar.setHarga(cursor.getString(4));
            daftar.setHarga_indo(cursor.getString(5));
            daftar.setFavorit(cursor.getString(7));
            produk_list.add(daftar);
        }

        adapter.notifyDataSetChanged();
    }

    public void LoadCari(String cari) {
        SqlHelper dbcenter = new SqlHelper(getContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        produk_list.clear();

        Cursor cursor;
        if(kategori.equalsIgnoreCase("Semua")) {
            cursor = db.rawQuery("SELECT * FROM produk WHERE nama_produk LIKE '%" + cari + "%'", null);
        }else{
            cursor = db.rawQuery("SELECT * FROM produk WHERE nama_produk LIKE '%" + cari + "%' AND kategori='" + kategori + "'", null);
        }
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            ProdukModel daftar = new ProdukModel();
            daftar.setId_produk(cursor.getString(1));
            daftar.setNama_produk(cursor.getString(2));
            daftar.setFoto(cursor.getString(3));
            daftar.setHarga(cursor.getString(4));
            daftar.setHarga_indo(cursor.getString(5));
            produk_list.add(daftar);
        }

        adapter.notifyDataSetChanged();
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
