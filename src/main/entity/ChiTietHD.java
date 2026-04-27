package main.entity;

import java.util.Objects;

public class ChiTietHD {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private Integer soLuong;
    private Double donGia;
    private Double thanhTien;

    public ChiTietHD() {
    }

    public ChiTietHD(HoaDon hoaDon, SanPham sanPham, Integer soLuong, Double donGia, Double thanhTien) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setThanhTien(thanhTien);
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }

    public Double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(Double thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHD that = (ChiTietHD) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, sanPham);
    }
}
