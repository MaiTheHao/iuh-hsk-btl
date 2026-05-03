package main.java.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import main.java.dao.NhanVienDAO;
import main.java.entity.NhanVien;
import main.java.App;
import main.java.util.AppContext;
import main.java.util.AppRegex;
import main.java.util.AppColor;

/**
 * @author: Mai Thế Hào
 */
public class DangNhap extends JPanel {
    private JTextField txtSdt;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private App mainFrame;

    public DangNhap(App mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(AppColor.BACKGROUND);

        init();
        bindEvents();
    }

    private void init() {
        JPanel panelGiua = new JPanel();
        panelGiua.setLayout(new BoxLayout(panelGiua, BoxLayout.X_AXIS));
        panelGiua.setBackground(AppColor.BACKGROUND);

        JPanel cardDangNhap = new JPanel();
        cardDangNhap.setLayout(new BoxLayout(cardDangNhap, BoxLayout.Y_AXIS));
        cardDangNhap.setBackground(Color.WHITE);
        cardDangNhap.setMaximumSize(new Dimension(400, 500));
        cardDangNhap.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        JLabel lblTitle = new JLabel("Chào mừng trở lại");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(47, 53, 66));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Đăng nhập để quản lý cửa hàng của bạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(116, 125, 140));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pSdt = new JPanel(new BorderLayout());
        pSdt.setBackground(Color.WHITE);
        pSdt.setMaximumSize(new Dimension(320, 60));
        JLabel lblSdtText = new JLabel("Số điện thoại");
        lblSdtText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSdtText.setForeground(new Color(72, 84, 96));
        txtSdt = new JTextField(20);
        txtSdt.setPreferredSize(new Dimension(300, 40));
        txtSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSdt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pSdt.add(lblSdtText, BorderLayout.NORTH);
        pSdt.add(txtSdt, BorderLayout.CENTER);

        JPanel pMk = new JPanel(new BorderLayout());
        pMk.setBackground(Color.WHITE);
        pMk.setMaximumSize(new Dimension(320, 60));
        JLabel lblMkText = new JLabel("Mật khẩu");
        lblMkText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMkText.setForeground(new Color(72, 84, 96));
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setPreferredSize(new Dimension(300, 40));
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pMk.add(lblMkText, BorderLayout.NORTH);
        pMk.add(txtMatKhau, BorderLayout.CENTER);

        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setPreferredSize(new Dimension(300, 45));
        btnDangNhap.setMaximumSize(new Dimension(320, 45));
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangNhap.setBackground(AppColor.PRIMARY);
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardDangNhap.add(lblTitle);
        cardDangNhap.add(Box.createVerticalStrut(10));
        cardDangNhap.add(lblSubtitle);
        cardDangNhap.add(Box.createVerticalStrut(40));
        cardDangNhap.add(pSdt);
        cardDangNhap.add(Box.createVerticalStrut(20));
        cardDangNhap.add(pMk);
        cardDangNhap.add(Box.createVerticalStrut(40));
        cardDangNhap.add(btnDangNhap);

        add(Box.createVerticalGlue());
        panelGiua.add(Box.createHorizontalGlue());
        panelGiua.add(cardDangNhap);
        panelGiua.add(Box.createHorizontalGlue());
        add(panelGiua);
        add(Box.createVerticalGlue());
    }

    private void bindEvents() {
        btnDangNhap.addActionListener(e -> handleDangNhap());
        txtMatKhau.addActionListener(e -> handleDangNhap());
    }

    private void handleDangNhap() {
        String sdt = txtSdt.getText();
        String mk = new String(txtMatKhau.getPassword());

        if (sdt.isEmpty() || mk.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AppRegex.PHONE.matcher(sdt).matches()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<NhanVien> nv = NhanVienDAO.getInstance().authenticate(sdt, mk);
        if (nv.isPresent()) {
            AppContext.getInstance().setCurrentUser(nv.get());
            mainFrame.showMainPage();
        } else {
            JOptionPane.showMessageDialog(this, "Số điện thoại hoặc mật khẩu không đúng!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}
