package main.java.dto;

import java.time.LocalDateTime;
import main.java.enumeration.TrangThaiHD;

public class HoaDonGetListCriteria extends BaseGetListCriteria {
    private String tuKhoa;
    private LocalDateTime tuNgay;
    private LocalDateTime denNgay;
    private String maNhanVien;
    private String sdtKhachHang;
    private TrangThaiHD trangThai;

    public HoaDonGetListCriteria() {
    }

    public String getTuKhoa() {
        return tuKhoa;
    }

    public void setTuKhoa(String tuKhoa) {
        this.tuKhoa = tuKhoa;
    }

    public LocalDateTime getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(LocalDateTime tuNgay) {
        this.tuNgay = tuNgay;
    }

    public LocalDateTime getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(LocalDateTime denNgay) {
        this.denNgay = denNgay;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getSdtKhachHang() {
        return sdtKhachHang;
    }

    public void setSdtKhachHang(String sdtKhachHang) {
        this.sdtKhachHang = sdtKhachHang;
    }

    public TrangThaiHD getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHD trangThai) {
        this.trangThai = trangThai;
    }
}
