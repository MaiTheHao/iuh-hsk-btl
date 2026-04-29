package main.java.dto;

public class ThongKeTongQuanDTO {
    private double doanhThuHomNay;
    private int soHoaDonHomNay;
    private int soSPCanNhap;
    private double doanhThuThangNay;

    public ThongKeTongQuanDTO() {}

    public ThongKeTongQuanDTO(double doanhThuHomNay, int soHoaDonHomNay, int soSPCanNhap, double doanhThuThangNay) {
        this.doanhThuHomNay = doanhThuHomNay;
        this.soHoaDonHomNay = soHoaDonHomNay;
        this.soSPCanNhap = soSPCanNhap;
        this.doanhThuThangNay = doanhThuThangNay;
    }

    public double getDoanhThuHomNay() { return doanhThuHomNay; }
    public void setDoanhThuHomNay(double doanhThuHomNay) { this.doanhThuHomNay = doanhThuHomNay; }

    public int getSoHoaDonHomNay() { return soHoaDonHomNay; }
    public void setSoHoaDonHomNay(int soHoaDonHomNay) { this.soHoaDonHomNay = soHoaDonHomNay; }

    public int getSoSPCanNhap() { return soSPCanNhap; }
    public void setSoSPCanNhap(int soSPCanNhap) { this.soSPCanNhap = soSPCanNhap; }

    public double getDoanhThuThangNay() { return doanhThuThangNay; }
    public void setDoanhThuThangNay(double doanhThuThangNay) { this.doanhThuThangNay = doanhThuThangNay; }
}
