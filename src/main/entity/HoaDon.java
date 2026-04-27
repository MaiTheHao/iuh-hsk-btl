package main.entity;

import main.enumeration.TrangThaiHD;
import java.time.LocalDateTime;
import java.util.Objects;

public class HoaDon {
    private String ma;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private LocalDateTime ngayLap;
    private Double tongTien;
    private Double vat;
    private TrangThaiHD trangThai;

    public HoaDon() {
    }

    public HoaDon(String ma) {
        setMa(ma);
    }

    public HoaDon(String ma, NhanVien nhanVien, KhachHang khachHang, LocalDateTime ngayLap, Double tongTien, Double vat, TrangThaiHD trangThai) {
        setMa(ma);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
        setNgayLap(ngayLap);
        setTongTien(tongTien);
        setVat(vat);
        setTrangThai(trangThai);
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public TrangThaiHD getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHD trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(ma, hoaDon.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }
}
