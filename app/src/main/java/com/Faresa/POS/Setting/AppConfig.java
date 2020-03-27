package com.Faresa.POS.Setting;


public class AppConfig {

    public static String HOST = "http://pos.suryaintisolusi.com/";
    public static String SERVER                 = HOST + "android/";
    public static String ADD_PELANGGAN          = SERVER + "add_pelanggan.php";
    public static String LIST_PELANGGAN         = SERVER + "list_pelanggan.php?id_toko=";
    public static String LOGIN                  = SERVER + "login.php";
    public static String LIST_PRODUK            = SERVER + "list_barang.php?id_toko=";
    public static String FOTO                   = HOST   + "foto_produk/";
    public static String BAYAR                  = SERVER + "tes.php";
    public static String HAPUS_PELANGGAN        = SERVER + "hapus_pelanggan.php?id_pelanggan=";
    public static String DATA_KASIR             = SERVER + "data_kasir.php";
    public static String UPDATE_KASIR           = SERVER + "update_kasir.php";
    public static String UPDATE_PASSWORD        = SERVER + "update_password.php";
    public static String KONFIRMASI_RETUR       = SERVER + "retur_password.php";
}