package main.java.dto;

import java.time.LocalDate;

public class DoanhThuNgayDTO {
    private LocalDate ngay;
    private int soHoaDon;
    private double doanhThu;

    public DoanhThuNgayDTO() {}

    public DoanhThuNgayDTO(LocalDate ngay, int soHoaDon, double doanhThu) {
        this.ngay = ngay;
        this.soHoaDon = soHoaDon;
        this.doanhThu = doanhThu;
    }

    public LocalDate getNgay() { return ngay; }
    public void setNgay(LocalDate ngay) { this.ngay = ngay; }

    public int getSoHoaDon() { return soHoaDon; }
    public void setSoHoaDon(int soHoaDon) { this.soHoaDon = soHoaDon; }

    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
}
