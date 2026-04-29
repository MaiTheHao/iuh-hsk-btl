package main.java.entity;

import java.util.Objects;

public class KhachHang {
    private String sdt;
    private String ten;
    private Integer diem;

    public KhachHang() {
    }

    public KhachHang(String sdt) {
        setSdt(sdt);
    }

    public KhachHang(String sdt, String ten, Integer diem) {
        setSdt(sdt);
        setTen(ten);
        setDiem(diem);
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Integer getDiem() {
        return diem;
    }

    public void setDiem(Integer diem) {
        this.diem = diem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(sdt, khachHang.sdt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sdt);
    }
}
