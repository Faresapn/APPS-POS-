package com.Faresa.POS.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.Faresa.POS.Activity.PembayaranActivity;
import com.Faresa.POS.Activity.PenjualanActivity;
import com.Faresa.POS.Helper.SqlHelper;
import com.Faresa.POS.Model.BelanjaModel;
import com.Faresa.POS.Adapter.BelanjaAdapter;
import com.Faresa.POS.Helper.SQLiteHandler;
import com.Faresa.pos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BelanjaFragment extends Fragment {

    public static BelanjaFragment BF;
    private RecyclerView recyclerView;
    private BelanjaAdapter adapter;
    private List<BelanjaModel> belanja_list;
    private SQLiteHandler db;
    private SqlHelper dbcenter;
    private Cursor cursor;
    private String[] daftar;
    private Button batal, selesai;

    public BelanjaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_belanja, container, false);

        dbcenter = new SqlHelper(v.getContext());
        batal = v.findViewById(R.id.batal);
        selesai = v.findViewById(R.id.selesai);

        belanja_list = new ArrayList<>();
        adapter = new BelanjaAdapter(v.getContext(), belanja_list);
        BF = this;

        recyclerView = v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(v.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.addItemDecoration(new BelanjaFragment.GridSpacingItemDecoration(1, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Peringatan");
                builder.setMessage("Anda yakin membatalkan belanja ini ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        SqlHelper dbcenter = new SqlHelper(v.getContext());
                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                        db.execSQL("DELETE FROM keranjang");
                        PenjualanActivity.PA.LoadTotalBelanja();
                        BelanjaFragment.BF.LoadKeranjang();
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
                Intent i = new Intent(getContext(), PembayaranActivity.class);
                i.putExtra("id_pelanggan", "0");
                i.putExtra("nama", "Pilih Pelanggan");
                startActivity(i);
            }
        });

        LoadKeranjang();

        return v;
    }

    public void LoadKeranjang() {
        dbcenter = new SqlHelper(getContext());
        SQLiteDatabase dbp = dbcenter.getReadableDatabase();
        cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
        cursor.moveToFirst();
        belanja_list.clear();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            BelanjaModel daftar = new BelanjaModel();
            daftar.setId_produk(cursor.getString(2).toString());
            daftar.setNama_produk(cursor.getString(3).toString());
            daftar.setJumlah(cursor.getString(4).toString());
            daftar.setHarga(cursor.getString(5).toString());
            daftar.setTotal(cursor.getString(6).toString());
            belanja_list.add(daftar);
        }

        adapter.notifyDataSetChanged();
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
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
