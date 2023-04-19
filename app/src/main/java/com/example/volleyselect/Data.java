package com.example.volleyselect;

public class Data {
    private String id, nim, nama, alamat;
    public Data(String id, String nim, String nama, String alamat){
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.alamat = alamat;
    }

    public Data() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
