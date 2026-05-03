USE master

CREATE DATABASE [IUH_HSK_BTL]
GO

USE [IUH_HSK_BTL]
GO

CREATE TABLE [LoaiSP] (
  [ma] varchar(255) PRIMARY KEY,
  [ten] nvarchar(255) NOT NULL,
  [moTa] nvarchar(max)
)
GO

CREATE TABLE [SanPham] (
  [ma] varchar(255) PRIMARY KEY,
  [ten] nvarchar(255) NOT NULL,
  [moTa] nvarchar(max),
  [anh] nvarchar(max),
  [gia] decimal(18,2) NOT NULL,
  [soLuong] int NOT NULL DEFAULT (0),
  [maLoai] varchar(255),
  [trangThai] varchar(20) NOT NULL CHECK ([trangThai] IN ('ACTIVE', 'INACTIVE')) DEFAULT 'ACTIVE'
)
GO

CREATE TABLE [NhanVien] (
  [ma] varchar(255) PRIMARY KEY,
  [ten] nvarchar(255) NOT NULL,
  [sdt] varchar(20) UNIQUE NOT NULL,
  [matKhau] varchar(255) NOT NULL,
  [anh] nvarchar(max),
  [loai] varchar(20) NOT NULL CHECK ([loai] IN ('ADMIN', 'STAFF'))
)
GO

CREATE TABLE [KhachHang] (
  [sdt] varchar(20) PRIMARY KEY,
  [ten] nvarchar(255) NOT NULL,
  [diem] int DEFAULT (0)
)
GO

CREATE TABLE [HoaDon] (
  [ma] varchar(255) PRIMARY KEY,
  [maNV] varchar(255),
  [sdtKH] varchar(20),
  [ngayLap] datetime DEFAULT (GETDATE()),
  [tongTien] decimal(18,2),
  [vat] decimal(18,2),
  [trangThai] varchar(20) NOT NULL CHECK ([trangThai] IN ('PENDING', 'PAID', 'CANCELLED')) DEFAULT 'PAID'
)
GO

CREATE TABLE [ChiTietHD] (
  [maHD] varchar(255),
  [maSP] varchar(255),
  [soLuong] int NOT NULL,
  [donGia] decimal(18,2) NOT NULL,
  [thanhTien] decimal(18,2) NOT NULL,
  PRIMARY KEY ([maHD], [maSP])
)
GO

ALTER TABLE [SanPham] ADD FOREIGN KEY ([maLoai]) REFERENCES [LoaiSP] ([ma])
GO

ALTER TABLE [HoaDon] ADD FOREIGN KEY ([maNV]) REFERENCES [NhanVien] ([ma])
GO

ALTER TABLE [HoaDon] ADD FOREIGN KEY ([sdtKH]) REFERENCES [KhachHang] ([sdt])
GO

ALTER TABLE [ChiTietHD] ADD FOREIGN KEY ([maHD]) REFERENCES [HoaDon] ([ma])
GO

ALTER TABLE [ChiTietHD] ADD FOREIGN KEY ([maSP]) REFERENCES [SanPham] ([ma])
GO

/*
Mai Thế Hào
sp_GetDoanhThuTheoNgay(@startDate, @endDate)
desc:
  Trả về doanh thu theo ngày trong khoảng thời gian nhất định, chỉ tính các hóa đơn có trạng thái 'PAID'.
  Sắp xếp kết quả theo ngày tăng dần.
return:
- DATETIME Ngay: Ngày lập hóa đơn
- INT SoHoaDon: Số lượng hóa đơn đã thanh toán trong ngày
- DECIMAL(18,2) DoanhThu: Tổng doanh thu của ngày đó
*/
CREATE OR ALTER PROCEDURE sp_GetDoanhThuTheoNgay
    @startDate DATETIME,
    @endDate DATETIME
AS
BEGIN
    SELECT 
        CAST(ngayLap AS DATE) AS Ngay,
        COUNT(ma) AS SoHoaDon,
        SUM(tongTien) AS DoanhThu
    FROM HoaDon
    WHERE ngayLap >= @startDate AND ngayLap <= @endDate
      AND trangThai = 'PAID'
    GROUP BY CAST(ngayLap AS DATE)
    ORDER BY Ngay ASC;
END;
GO

/*
Mai Thế Hào
sp_GetTopSanPhamBanChay(@startDate, @endDate, @limit)
desc:
  Trả về danh sách top sản phẩm bán chạy nhất trong khoảng thời gian nhất định, chỉ tính các hóa đơn có trạng thái 'PAID'.
  Sắp xếp kết quả theo số lượng đã bán giảm dần.
return:
- VARCHAR(255) MaSP: Mã sản phẩm
- NVARCHAR(255) TenSP: Tên sản phẩm
- INT SoLuongDaBan: Tổng số lượng đã bán của sản phẩm
- DECIMAL(18,2) TongTien: Tổng doanh thu từ sản phẩm đó
*/
CREATE OR ALTER PROCEDURE sp_GetTopSanPhamBanChay
    @startDate DATETIME,
    @endDate DATETIME,
    @limit INT
AS
BEGIN
    SELECT TOP (@limit)
        sp.ma AS MaSP,
        sp.ten AS TenSP,
        SUM(ct.soLuong) AS SoLuongDaBan,
        SUM(ct.thanhTien) AS TongTien
    FROM ChiTietHD ct
    JOIN SanPham sp ON ct.maSP = sp.ma
    JOIN HoaDon hd ON ct.maHD = hd.ma
    WHERE hd.ngayLap >= @startDate AND hd.ngayLap <= @endDate
      AND hd.trangThai = 'PAID'
    GROUP BY sp.ma, sp.ten
    ORDER BY SoLuongDaBan DESC;
END;
GO

/*
Mai Thế Hào
sp_GetTongQuan()
desc:
  Trả về tổng quan về doanh thu hôm nay, số hóa đơn hôm nay, số sản phẩm sắp hết hàng (soLuong < 10), và tổng doanh thu tháng này.
return:
- DECIMAL(18,2) DoanhThuHomNay: Doanh thu của ngày hôm nay
- INT SoHoaDonHomNay: Số lượng hóa đơn đã thanh toán của ngày hôm nay
- INT SoSPCanNhap: Số lượng sản phẩm có số lượng tồn kho nhỏ hơn 10
- DECIMAL(18,2) DoanhThuThangNay: Tổng doanh thu của tháng hiện tại
*/
CREATE OR ALTER PROCEDURE sp_GetTongQuan
AS
BEGIN
    DECLARE @today DATE = CAST(GETDATE() AS DATE);
    
    -- 1. Doanh thu hôm nay
    DECLARE @revenueToday DECIMAL(18,2) = (
        SELECT ISNULL(SUM(tongTien), 0) FROM HoaDon 
        WHERE CAST(ngayLap AS DATE) = @today AND trangThai = 'PAID'
    );
    
    -- 2. Số hóa đơn hôm nay
    DECLARE @invoicesToday INT = (
        SELECT COUNT(*) FROM HoaDon 
        WHERE CAST(ngayLap AS DATE) = @today AND trangThai = 'PAID'
    );
    
    -- 3. Số sản phẩm sắp hết hàng (soLuong < 10)
    DECLARE @lowStockCount INT = (
        SELECT COUNT(*) FROM SanPham WHERE soLuong < 10 AND trangThai = 'ACTIVE'
    );
    
    -- 4. Tổng doanh thu tháng này
    DECLARE @revenueMonth DECIMAL(18,2) = (
        SELECT ISNULL(SUM(tongTien), 0) FROM HoaDon 
        WHERE MONTH(ngayLap) = MONTH(GETDATE()) AND YEAR(ngayLap) = YEAR(GETDATE()) 
          AND trangThai = 'PAID'
    );

    SELECT 
        @revenueToday AS DoanhThuHomNay,
        @invoicesToday AS SoHoaDonHomNay,
        @lowStockCount AS SoSPCanNhap,
        @revenueMonth AS DoanhThuThangNay;
END;
GO
