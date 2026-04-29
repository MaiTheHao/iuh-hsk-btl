package main.java.dto;

public class TopSanPhamDTO {
    private String maSP;
    private String tenSP;
    private int soLuongDaBan;
    private double tongTien;

    public TopSanPhamDTO() {}

    public TopSanPhamDTO(String maSP, String tenSP, int soLuongDaBan, double tongTien) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuongDaBan = soLuongDaBan;
        this.tongTien = tongTien;
    }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getSoLuongDaBan() { return soLuongDaBan; }
    public void setSoLuongDaBan(int soLuongDaBan) { this.soLuongDaBan = soLuongDaBan; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
