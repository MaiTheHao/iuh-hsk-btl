package main.java.gui;

import javax.swing.*;
import java.awt.*;

import main.java.dao.NhanVienDAO;
import main.java.entity.NhanVien;
import main.java.App;
import main.java.util.AppContext;
import main.java.util.AppRegex;
import main.java.util.AppColor;
import java.util.Optional;

public class Login extends JPanel {
    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private App mainFrame;

    public Login(App mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(AppColor.BACKGROUND);

        JPanel horizontalCenter = new JPanel();
        horizontalCenter.setLayout(new BoxLayout(horizontalCenter, BoxLayout.X_AXIS));
        horizontalCenter.setBackground(AppColor.BACKGROUND);

        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setMaximumSize(new Dimension(400, 500));
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chào mừng trở lại");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(47, 53, 66));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Đăng nhập để quản lý cửa hàng của bạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(116, 125, 140));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nhập liệu
        JPanel pPhone = new JPanel(new BorderLayout());
        pPhone.setBackground(Color.WHITE);
        pPhone.setMaximumSize(new Dimension(320, 60));
        JLabel lblPhone = new JLabel("Số điện thoại");
        lblPhone.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPhone.setForeground(new Color(72, 84, 96));
        txtPhone = new JTextField(20);
        txtPhone.setPreferredSize(new Dimension(300, 40));
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPhone.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pPhone.add(lblPhone, BorderLayout.NORTH);
        pPhone.add(txtPhone, BorderLayout.CENTER);

        // Ô nhập Mật khẩu
        JPanel pPass = new JPanel(new BorderLayout());
        pPass.setBackground(Color.WHITE);
        pPass.setMaximumSize(new Dimension(320, 60));
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(72, 84, 96));
        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 226)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        pPass.add(lblPass, BorderLayout.NORTH);
        pPass.add(txtPassword, BorderLayout.CENTER);

        // Điều khiển
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setMaximumSize(new Dimension(320, 45));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(AppColor.PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        loginCard.add(lblTitle);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(lblSubtitle);
        loginCard.add(Box.createVerticalStrut(40));
        loginCard.add(pPhone);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(pPass);
        loginCard.add(Box.createVerticalStrut(40));
        loginCard.add(btnLogin);

        add(Box.createVerticalGlue());
        horizontalCenter.add(Box.createHorizontalGlue());
        horizontalCenter.add(loginCard);
        horizontalCenter.add(Box.createHorizontalGlue());
        add(horizontalCenter);
        add(Box.createVerticalGlue());
    }

    // Xử lý đăng nhập
    private void handleLogin() {
        String phonenum = txtPhone.getText();
        String password = new String(txtPassword.getPassword());

        if (phonenum.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập cả Số điện thoại và Mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AppRegex.PHONE.matcher(phonenum).matches()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải có 10 chữ số và bắt đầu bằng 03, 08, 07, 05, hoặc 09)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // if (!AppRegex.PASSWORD.matcher(password).matches()) {
        //     JOptionPane.showMessageDialog(this, "Mật khẩu không hợp lệ (ít nhất 8 ký tự, có hoa, thường, số và ký tự đặc biệt)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        //     return;
        // }

        Optional<NhanVien> nv = NhanVienDAO.getInstance().authenticate(phonenum, password);
        if (nv.isPresent()) {
            AppContext.getInstance().setCurrentUser(nv.get());
            mainFrame.showMainPage();
        } else {
            JOptionPane.showMessageDialog(this, "Số điện thoại hoặc Mật khẩu không đúng", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}
