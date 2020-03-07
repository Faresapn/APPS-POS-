package com.DavidMuheri.KadaiDen.Model;


public class KeranjangModel {
    private String id_faktur,

                    id_barang,
                    id_promo,
                    nama_barang,
                    harga,
                    jumlah;

    public String getId_faktur() {
        return id_faktur;
    }

    public void setId_faktur(String id_faktur) {
        this.id_faktur = id_faktur;
    }


    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public String getId_promo() {
        return id_promo;
    }

    public void setId_promo(String id_promo) {
        this.id_promo = id_promo;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

}
