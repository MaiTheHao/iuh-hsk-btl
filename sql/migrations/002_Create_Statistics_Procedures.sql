USE [IUH_HSK_BTL]
GO

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
