package main.java.entity;

import main.java.enumeration.LoaiNV;
import java.util.Objects;

public class NhanVien {
    private String ma;
    private String ten;
    private String sdt;
    private String matKhau;
    private String anh;
    private LoaiNV loai;

    public NhanVien() {
    }

    public NhanVien(String ma) {
        setMa(ma);
    }

    public NhanVien(String ma, String ten, String sdt, String matKhau, String anh, LoaiNV loai) {
        setMa(ma);
        setTen(ten);
        setSdt(sdt);
        setMatKhau(matKhau);
        setAnh(anh);
        setLoai(loai);
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public LoaiNV getLoai() {
        return loai;
    }

    public void setLoai(LoaiNV loai) {
        this.loai = loai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(ma, nhanVien.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }
}
