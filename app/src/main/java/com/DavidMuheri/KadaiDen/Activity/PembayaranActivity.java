package com.DavidMuheri.KadaiDen.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.DavidMuheri.KadaiDen.Helper.SQLiteHandler;
import com.DavidMuheri.KadaiDen.Helper.SqlHelper;
import com.DavidMuheri.KadaiDen.Print.DeviceListActivity;
import com.DavidMuheri.KadaiDen.Print.UnicodeFormatter;
import com.DavidMuheri.pos.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class PembayaranActivity extends AppCompatActivity implements Runnable {

    private double total;
    private TextView total_belanja;
    private TextView jumlah_item;
    private TextView uang_kembali;
    private TextView jumlah_bayar;
    private TextView nama_pelanggan;
    private EditText pembayaran;
    private SqlHelper dbcenter;
    private Cursor cursor;
    private Button bayar;
    private String id_kasir, id_toko, nama_kasir, nama_toko, alamat, hp, tanggal;
    private String[] header,footer;
    private CheckBox cek_struk;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8,t9,t0,t000,hapus;

    private String[] id_produk;
    private String[] jumlah;
    private String id_pelanggan;

    private LinearLayout lay_bayar;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private String faktur, mode;

    private String jbayar = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        total_belanja = findViewById(R.id.total_belanja);
        jumlah_item = findViewById(R.id.jumlah_item);
        uang_kembali = findViewById(R.id.uang_kembali);
        pembayaran = findViewById(R.id.pembayaran);
        bayar = findViewById(R.id.bayar);
        jumlah_bayar = findViewById(R.id.jumlah_bayar);
        LinearLayout layPelanggan = findViewById(R.id.LayPilPel);
        nama_pelanggan = findViewById(R.id.nama_pelanggan);
        TextView idpelanggan = findViewById(R.id.id_pelanggan);
        cek_struk = findViewById(R.id.cek_struk);
        lay_bayar = findViewById(R.id.lay_bayar);

        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        t7 = findViewById(R.id.t7);
        t8 = findViewById(R.id.t8);
        t9 = findViewById(R.id.t9);
        t0 = findViewById(R.id.t0);
        t000 = findViewById(R.id.t000);
        hapus = findViewById(R.id.tb);

        BtnCalulator();

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            lay_bayar.setOrientation(LinearLayout.VERTICAL);
        } else {
            lay_bayar.setOrientation(LinearLayout.HORIZONTAL);
        }

        Intent intent = getIntent();
        mode = (intent.getStringExtra("mode"));
        id_pelanggan = (intent.getStringExtra("id_pelanggan"));
        String nama = (intent.getStringExtra("nama"));

        nama_pelanggan.setText(nama);
        idpelanggan.setText(id_pelanggan);


        // SQLite database handler
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> p = db.BacaKasir();
        id_kasir = p.get("id_kasir");
        nama_kasir = p.get("nama_kasir");
        id_toko = p.get("id_toko");
        nama_toko = p.get("nama_toko");
        alamat = p.get("alamat");
        hp = p.get("hp");

        String xheader = p.get("header");
        String xfooter = p.get("footer");

        header = xheader.split(" _ ");
        footer = xfooter.split(" _ ");


        LoadTotalBelanja();

        bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                dbcenter = new SqlHelper(getApplicationContext());
                SQLiteDatabase dbp = dbcenter.getReadableDatabase();
                cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
                cursor.moveToFirst();

                id_produk = new String[cursor.getCount()];
                jumlah = new String[cursor.getCount()];

                for (int cc = 0; cc < cursor.getCount(); cc++) {
                    cursor.moveToPosition(cc);
                    id_produk[cc] = cursor.getString(2);
                    jumlah[cc] = cursor.getString(4);
                }

                char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray();
                StringBuilder sb1 = new StringBuilder();
                Random random1 = new Random();
                for (int i = 0; i < 5; i++)
                {
                    char c1 = chars1[random1.nextInt(chars1.length)];
                    sb1.append(c1);
                }
                String random_string = sb1.toString();

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                faktur = df.format(c) + "-" + random_string;

                SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd");
                tanggal = tgl.format(c);

                try {
                    mBluetoothSocket.close();
                } catch (Exception e) {
                    Log.e("Tag", "Exe ", e);
                }

                if(cek_struk.isChecked()) {
                    autoConnect();
                }else{
                    ProsesBayar(Arrays.toString(id_produk), Arrays.toString(jumlah), faktur, tanggal);
                }



            }
        });

        layPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mBluetoothSocket != null)
                        mBluetoothSocket.close();
                } catch (Exception e) {
                    Log.e("Tag", "Exe ", e);
                }
                Intent i = new Intent(getApplicationContext(), CariPelanggan.class);
                startActivity(i);
                finish();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(5);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cek_struk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cek_struk.isChecked()){
                    try {
                            mBluetoothSocket.close();
                    } catch (Exception e) {
                        Log.e("Tag", "Exe ", e);
                    }

                }else{
                    try {
                        if (mBluetoothSocket != null)
                            mBluetoothSocket.close();
                    } catch (Exception e) {
                        Log.e("Tag", "Exe ", e);
                    }
                }
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "1";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = "1";
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "2";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });


        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "3";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });


        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "4";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "5";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "6";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });


        t7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "7";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "8";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "9";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "0";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = w;
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });

        t000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                String w = "000";
                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = "0";
                }else{
                    String y = x + w;
                    jbayar = y;
                }
                hitung();
            }
        });



        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = jbayar;
                int j = x.length()-1;

                if(x.equalsIgnoreCase("") || x.equalsIgnoreCase("0") ){
                    jbayar = "0";
                }else{
                    String y = x.substring(0,j);
                    jbayar = y;
                }
                hitung();
            }
        });


    }

    private void autoConnect(){

        try {
            mBluetoothSocket = null;
//            mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(PembayaranActivity.this, "Message1", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent i = new Intent(PembayaranActivity.this, DeviceListActivity.class);
                startActivityForResult(i, REQUEST_CONNECT_DEVICE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
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

    public void LoadTotalBelanja() {
        dbcenter = new SqlHelper(getApplicationContext());
        SQLiteDatabase dbp = dbcenter.getReadableDatabase();
        cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
        total = 0;
        int jitem = 0;
        if (cursor.getCount() > 0) {
            for (int cc = 0; cc < cursor.getCount(); cc++) {
                cursor.moveToPosition(cc);
                total = total + Integer.valueOf(cursor.getString(6));
                jitem = jitem + Integer.valueOf(cursor.getString(4));
            }
        }

        total_belanja.setText(String.format("%,.0f", total));
        jumlah_item.setText(jitem + "");
    }

    private void ProsesBayar(final String id_produk,
                             final String jumlah,
                             final String faktur,
                             final String tanggal) {


        // Tag used to cancel the request
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        db.TambahTransaksi(id_toko, id_produk, jumlah, id_kasir, id_pelanggan, faktur, tanggal);
        finish();
        Toast.makeText(getApplicationContext(),"Terimakasih, Transaksi telah Selesai !", Toast.LENGTH_SHORT).show();
        Intent i;
        if(mode.equalsIgnoreCase("manual")) {
            PenjualanActivity.PA.tutup();
            i = new Intent(getApplicationContext(), PenjualanActivity.class);
        }else{
            PenjualanBarcode.PB.tutup();
            i = new Intent(getApplicationContext(), PenjualanBarcode.class);
        }
        startActivity(i);
    }



    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    assert mExtra != null;
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this, "Connecting...", mBluetoothDevice.getName() + " : "+ mBluetoothDevice.getAddress(), false, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PembayaranActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    cek_struk.setChecked(false);
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }


    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            print();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }


    private void print(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket.getOutputStream();
                    String BILL;
                    BILL = "";
                    for(int i = 0; i < header.length; i++){
                        BILL += "\n" +  StringUtils.center(header[i], 31);
                    }
                    BILL += "\n===============================";

                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String tanggal = df.format(c);

                    SimpleDateFormat jf = new SimpleDateFormat("hh:mm:ss");
                    String jam = jf.format(c);

                    BILL += "\n" + String.format("%1$-9s %2$-1s %3$-11s","No Faktur",":",faktur);
                    BILL += "\n" + String.format("%1$-9s %2$-1s %3$-11s","Tanggal",":",tanggal);
                    BILL += "\n" + String.format("%1$-9s %2$-1s %3$-11s","Pukul",":",jam);
                    BILL += "\n" + String.format("%1$-9s %2$-1s %3$-11s","Kasir",":",nama_kasir);

                    if(!id_pelanggan.equalsIgnoreCase("0")) {
                        BILL += "\n" + String.format("%1$-9s %2$-1s %3$-11s","Pelanggan",":",nama_pelanggan.getText().toString());
                    }


                    BILL += "\n-------------------------------";

                    dbcenter = new SqlHelper(getApplicationContext());
                    SQLiteDatabase dbp = dbcenter.getReadableDatabase();
                    cursor = dbp.rawQuery("SELECT * FROM keranjang", null);
                    cursor.moveToFirst();
                    for (int cc = 0; cc < cursor.getCount(); cc++) {
                        cursor.moveToPosition(cc);
                        String nama = cursor.getString(3);
                        String jumlah = cursor.getString(4);
                        String harga = cursor.getString(5);
                        String total = cursor.getString(6);

                        double hrg = Integer.valueOf(harga);
                        harga = String.format("%,.0f", hrg);

                        double ttl = Integer.valueOf(total);
                        total = String.format("%,.0f", ttl);

                        BILL += "\n- " + nama + "\n";
                        BILL += String.format("%1$-1s %2$-15s %3$13s","", jumlah + " x " + harga, total);

                    }
                    BILL += "\n-------------------------------";
                    BILL += "\n" + String.format("%1$-13s %2$4s %3$11s","Total Belanja",": Rp.",total_belanja.getText());
                    BILL += "\n" + String.format("%1$-13s %2$4s %3$11s","Jumlah Bayar",": Rp.",jumlah_bayar.getText());
                    BILL += "\n" + String.format("%1$-13s %2$4s %3$11s","Uang Kembali",": Rp.",uang_kembali.getText());
                    BILL += "\n\n";

                    for(int i = 0; i < footer.length; i++){
                        BILL += "\n" +  StringUtils.center(footer[i], 31);
                    }

                    BILL += "\n\n\n\n";
                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));




                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
        ProsesBayar(Arrays.toString(id_produk), Arrays.toString(jumlah), faktur, tanggal);
    }


    private void BtnCalulator(){



    }

    private void hitung(){
        if (jbayar.length() > 0) {
            double to, by, kb;
            by = Integer.valueOf(jbayar);
            jumlah_bayar.setText(String.format("%,.0f", by));

            int y = Integer.valueOf(jbayar);
            if (y >= total) {
                to = total;
                kb = by - to;

                uang_kembali.setText(String.format("%,.0f", kb));
                bayar.setText("Bayar");
                bayar.setTextColor(Color.parseColor("#ffffff"));
                bayar.setBackgroundResource(R.drawable.bg_bayar);
            } else {
                uang_kembali.setText("0");
                bayar.setText("Uang belum cukup");
                bayar.setTextColor(Color.parseColor("#309A42"));
                bayar.setBackgroundResource(R.drawable.bg_outline);
            }
        } else {
            uang_kembali.setText("0");
            jumlah_bayar.setText("0");
        }
    }

}
