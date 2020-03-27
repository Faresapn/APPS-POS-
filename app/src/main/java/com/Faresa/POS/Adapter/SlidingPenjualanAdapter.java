package com.Faresa.POS.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.Faresa.POS.Fragment.BarangFragment;
import com.Faresa.POS.Fragment.FavoritFragment;
import com.Faresa.POS.Helper.SqlHelper;


/**
 * Created by MacBookPro on 10/5/17.
 */

public class SlidingPenjualanAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String kategori[];


    public SlidingPenjualanAdapter(FragmentManager fm, Context c) {
        super(fm);
        mContext = c;

        SqlHelper dbcenter = new SqlHelper(mContext);
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT kategori FROM produk ORDER BY kategori", null);
        cursor.moveToFirst();
        kategori = new String[cursor.getCount()];
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            kategori[i] = cursor.getString(0);
        }

    }

    @Override
    public Fragment getItem(int position) {


        Fragment frag = null;
        String k = "";
        if (position == 0) {
            frag = new BarangFragment();
            k = "Semua";
        }
        if (position == 1) {
            frag = new FavoritFragment();
            k = "Favorit";
        }

        for (int i = 0; i < kategori.length; i++){
            if (position == i+2) {
                frag = new BarangFragment();
                k = kategori[i];
            }
        }

        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putString("kategori",k);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {

        int x = kategori.length + 2;
        return x;
    }

    public CharSequence getPageTitle(int position) {

        String[] judul = new String[kategori.length + 2];

        judul[0] = "Semuanya";
        judul[1] = "Favorit";

        for (int i=0; i < kategori.length; i++){
            judul[i+2] = kategori[i];
        }
        return judul[position];
    }
}
