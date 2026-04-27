package main.entity;

import java.util.Objects;

public class LoaiSP {
    private String ma;
    private String ten;
    private String moTa;

    public LoaiSP() {
    }

    public LoaiSP(String ma) {
        setMa(ma);
    }

    public LoaiSP(String ma, String ten, String moTa) {
        setMa(ma);
        setTen(ten);
        setMoTa(moTa);
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoaiSP loaiSP = (LoaiSP) o;
        return Objects.equals(ma, loaiSP.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }
}
