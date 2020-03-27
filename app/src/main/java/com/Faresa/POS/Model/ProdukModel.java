package com.Faresa.POS.Model;

/**
 * Created by MacBookPro on 10/12/17.
 */

public class ProdukModel {
    private String id_produk,
            nama_produk,
            foto,
            harga,
            harga_indo,
            favorit;

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getHarga_indo() {
        return harga_indo;
    }

    public void setHarga_indo(String harga_indo) {
        this.harga_indo = harga_indo;
    }

    public String getFavorit() {
        return favorit;
    }

    public void setFavorit(String favorit) {
        this.favorit = favorit;
    }


}
