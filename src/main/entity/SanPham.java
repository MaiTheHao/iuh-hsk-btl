package main.entity;

import main.enumeration.TrangThaiSP;
import java.util.Objects;

public class SanPham {
    private String ma;
    private String ten;
    private String moTa;
    private String anh;
    private Double gia;
    private Integer soLuong;
    private LoaiSP loai;
    private TrangThaiSP trangThai;

    public SanPham() {
    }

    public SanPham(String ma) {
        setMa(ma);
    }

    public SanPham(String ma, String ten, String moTa, String anh, Double gia, Integer soLuong, LoaiSP loai, TrangThaiSP trangThai) {
        setMa(ma);
        setTen(ten);
        setMoTa(moTa);
        setAnh(anh);
        setGia(gia);
        setSoLuong(soLuong);
        setLoai(loai);
        setTrangThai(trangThai);
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

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public LoaiSP getLoai() {
        return loai;
    }

    public void setLoai(LoaiSP loai) {
        this.loai = loai;
    }

    public TrangThaiSP getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiSP trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return Objects.equals(ma, sanPham.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }
}
