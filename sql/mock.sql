USE [IUH_HSK_BTL]
GO

-- 1. Mock dữ liệu cho LoaiSP (Loại Sản Phẩm)
INSERT INTO [LoaiSP] ([ma], [ten], [moTa]) VALUES
('L001', N'Cà Phê', N'Các loại cà phê pha máy và pha phin truyền thống'),
('L002', N'Trà Trái Cây', N'Trà kết hợp với trái cây tươi mát'),
('L003', N'Bánh Ngọt', N'Các loại bánh ngọt ăn kèm'),
('L004', N'Đồ Ăn Nhanh', N'Các món ăn nhẹ phục vụ tại quán')
GO

-- 2. Mock dữ liệu cho SanPham (Sản Phẩm)
INSERT INTO [SanPham] ([ma], [ten], [moTa], [anh], [gia], [soLuong], [maLoai], [trangThai]) VALUES
('SP001', N'Cà Phê Đen', N'Cà phê đen nguyên chất pha phin', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxMYFgcKbRABQX11eCu210VAlcJLwP5D5ZsA&s', 25000, 100, 'L001', 'ACTIVE'),
('SP002', N'Cà Phê Sữa', N'Cà phê pha sữa đặc truyền thống', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTLVv_fXdR3wISl-m5tsuml27UbVSnNgoDmVg&s', 29000, 100, 'L001', 'ACTIVE'),
('SP003', N'Bạc Xỉu', N'Nhiều sữa ít cà phê', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTLVv_fXdR3wISl-m5tsuml27UbVSnNgoDmVg&s', 32000, 80, 'L001', 'ACTIVE'),
('SP004', N'Trà Đào Cam Sả', N'Trà đào thơm nồng hương sả', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPDNAqUxxdq0bEvZps9brGSXIN2xNNDHydtQ&s', 45000, 50, 'L002', 'ACTIVE'),
('SP005', N'Trà Vải', N'Trà vải ngọt thanh kèm trái vải tươi', 'https://www.unileverfoodsolutions.com.vn/dam/global-ufs/mcos/phvn/vietnam/calcmenu/recipes/VN-recipes/other/sweet-lychee-tea/main-header.jpg', 42000, 40, 'L002', 'ACTIVE'),
('SP006', N'Bánh Tiramisu', N'Bánh quy cà phê béo ngậy', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQAiXYrVVskpU9gwukqhF5k1vEhU0O7fx8TQA&s', 55000, 20, 'L003', 'ACTIVE'),
('SP007', N'Bánh Croissant', N'Bánh sừng bò bơ tỏi', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR25XH5z6E7Kr2xIt5r3wqb7zyQxfDlDKQpXg&s', 35000, 30, 'L003', 'ACTIVE'),
('SP008', N'Mì Ý Bolonese', N'Mì Ý sốt bò băm', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQfVIA847VmwQhxr9tTJ_75DyS91XNn1bLHwA&s', 65000, 15, 'L004', 'ACTIVE')
GO

-- 3. Mock dữ liệu cho NhanVien (Nhân Viên)
INSERT INTO [NhanVien] ([ma], [ten], [sdt], [matKhau], [loai]) VALUES
('admin01', N'Admin Hào', '0832690938', 'SystemAdmin@123', 'ADMIN'),
('admin02', N'Admin Vỹ', '0985138313', 'SystemAdmin@123', 'ADMIN'),
('admin03', N'Admin Nhựt', '0355455564', 'SystemAdmin@123', 'ADMIN')
GO

-- 4. Mock dữ liệu cho KhachHang (Khách Hàng)
INSERT INTO [KhachHang] ([sdt], [ten], [diem]) VALUES
('0911222333', N'Phạm Văn Đồng', 100),
('0944555666', N'Lê Thị Tuyết', 250),
('0977888999', N'Hoàng Văn Nam', 50),
('0900000000', N'Khách Vãng Lai', 0)
GO

-- 5. Mock dữ liệu cho HoaDon (Hóa Đơn)
INSERT INTO [HoaDon] ([ma], [maNV], [sdtKH], [ngayLap], [tongTien], [vat], [trangThai]) VALUES
('HD001', 'admin02', '0911222333', GETDATE(), 74000, 0.08, 'PAID'),
('HD002', 'admin03', '0944555666', GETDATE(), 45000, 0.08, 'PAID'),
('HD003', 'admin02', '0900000000', GETDATE(), 100000, 0.10, 'PAID')
GO

-- 6. Mock dữ liệu cho ChiTietHD (Chi Tiết Hóa Đơn)
INSERT INTO [ChiTietHD] ([maHD], [maSP], [soLuong], [donGia], [thanhTien]) VALUES
('HD001', 'SP001', 1, 25000, 25000),
('HD001', 'SP002', 1, 29000, 29000),
('HD001', 'SP007', 1, 20000, 20000),
('HD002', 'SP004', 1, 45000, 45000),
('HD003', 'SP006', 1, 55000, 55000),
('HD003', 'SP005', 1, 45000, 45000)
GO
