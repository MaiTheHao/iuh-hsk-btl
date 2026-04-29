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
  [vat] decimal(5,2),
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