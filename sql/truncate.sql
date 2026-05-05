USE [IUH_HSK_BTL]
GO

-- Xóa dữ liệu theo thứ tự ngược lại với ràng buộc khóa ngoại (Foreign Key)
-- Để tránh lỗi vi phạm ràng buộc khi xóa dữ liệu.

-- 1. Xóa chi tiết hóa đơn (Phụ thuộc vào HoaDon và SanPham)
DELETE FROM [ChiTietHD];
GO

-- 2. Xóa hóa đơn (Phụ thuộc vào NhanVien và KhachHang)
DELETE FROM [HoaDon];
GO

-- 3. Xóa sản phẩm (Phụ thuộc vào LoaiSP)
DELETE FROM [SanPham];
GO

-- 4. Xóa loại sản phẩm
DELETE FROM [LoaiSP];
GO

-- 5. Xóa khách hàng
DELETE FROM [KhachHang];
GO

-- 6. Xóa nhân viên
DELETE FROM [NhanVien];
GO
