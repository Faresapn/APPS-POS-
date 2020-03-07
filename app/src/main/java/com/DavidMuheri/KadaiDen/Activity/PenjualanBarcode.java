package com.DavidMuheri.KadaiDen.Activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.DavidMuheri.KadaiDen.Adapter.KeranjangBarcodeAdapter;
import com.DavidMuheri.KadaiDen.Adapter.ProdukAdapter;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Model.KeranjangModel;
import com.DavidMuheri.KadaiDen.Model.ProdukModel;
import com.DavidMuheri.pos.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PenjualanBarcode extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static PenjualanBarcode PB;
    private EditText txt_barcode;
    private TextView txt_total;
    private KeranjangBarcodeAdapter keranjangBarcodeAdapter;
    private List<KeranjangModel> keranjang_list;
    private int total;
    private String id_kasir;
    private ImageView barcode_scanner;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.penjualan_barcode);


        txt_total = findViewById(R.id.txt_total);
        barcode_scanner = findViewById(R.id.barcode_scanner);
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> data = db.BacaKasir();
       barcode_scanner.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               scanow();
           }
       });
        id_kasir = data.get("id");

        PB = this;
        keranjang_list = new ArrayList<>();

        keranjangBarcodeAdapter = new KeranjangBarcodeAdapter(getApplicationContext(), keranjang_list);
        RecyclerView recyclerView = findViewById(R.id.rv_barcode);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new PenjualanBarcode.GridSpacingItemDecoration(1, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(keranjangBarcodeAdapter);

        txt_barcode = findViewById(R.id.barcode_txt);
        txt_barcode.setVisibility(View.GONE);
        txt_barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do some thing now
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SQLiteHandler db3 = new SQLiteHandler(getApplicationContext());
                HashMap<String, Boolean> c = db3.FindBarang(txt_barcode.getText().toString());
                boolean x = c.get("cari");

                if (x) {

                    int j;
                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                    HashMap<String, Integer> hitung = db.HitungItemBelanjaBarcode( String.valueOf(x));
                    int cek = hitung.get("cek");
                    Log.d("cek", String.valueOf(cek));
                    if (cek > 0) {

                        j = hitung.get("jumlah_produk") + 1;
                        int m = hitung.get("harga_jual");
                        int t = j * m;


                        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
                        SQLiteDatabase db2 = dbcenter.getWritableDatabase();

                        db2.execSQL("UPDATE keranjang SET jumlah ='" + j + "',  total='" + t + "' WHERE barcode='" +  txt_barcode.getText().toString() + "'");

                    } else {
                        SQLiteHandler db1 = new SQLiteHandler(getApplicationContext());
                        HashMap<String, String> data = db1.BacaBarang(txt_barcode.getText().toString());
                        db.IsiKeranjang(
                                data.get("id_produk"),
                                data.get("nama_produk"),
                                "1",
                                data.get("harga_indo"),
                                data.get("harga_indo"),
                                data.get("barcode")
                        );
                    }

                }else {
                    Toast.makeText(PenjualanBarcode.this,"Coba Scan Kembali", Toast.LENGTH_SHORT).show();
                }


                TampilKeranjang();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ImageButton btn_selesai = findViewById(R.id.btn_selesai);
        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PembayaranActivity.class);
                i.putExtra("id_pelanggan", "0");
                i.putExtra("nama", "Pilih Pelanggan");
                i.putExtra("mode", "barcode");
                startActivity(i);
                finish();
            }
        });


        ImageButton btn_batal = findViewById(R.id.btn_batal);
        btn_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PenjualanBarcode.this);
                builder.setTitle("Peringatan");
                builder.setMessage("Anda yakin membatalkan belanja ini ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        SqlHelper dbcenter = new SqlHelper(PenjualanBarcode.this);
                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                        db.execSQL("DELETE FROM keranjang");
                        TampilKeranjang();
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

        TampilKeranjang();


    }



    private void scanow() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(Portrait.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Your Barcode");
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this,"Result Not Found", Toast.LENGTH_SHORT).show();
            }
            else{
                txt_barcode.setText(result.getContents());
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void tutup() {
        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getWritableDatabase();
        db.execSQL("DELETE FROM keranjang");
        finish();
    }
    //masalah disini
    public void TampilKeranjang() {

        SqlHelper dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase db = dbcenter.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM keranjang ORDER BY barcode DESC", null);
        cursor.moveToFirst();
        keranjang_list.clear();
        total = 0;
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            KeranjangModel daftar = new KeranjangModel();
            daftar.setId_barang(cursor.getString(2));
            daftar.setNama_barang(cursor.getString(3));
            daftar.setHarga(cursor.getString(5));
            daftar.setJumlah(cursor.getString(4));
            keranjang_list.add(daftar);
            total += Integer.parseInt(cursor.getString(5)) * Integer.parseInt(cursor.getString(4));
        }

        DecimalFormat myFormatter = new DecimalFormat("###,###.###");
        String t = "Rp. " + myFormatter.format(total);
        txt_total.setText(t);

        keranjangBarcodeAdapter.notifyDataSetChanged();

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
