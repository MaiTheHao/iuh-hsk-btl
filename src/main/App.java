package main;

import javax.swing.*;
import java.awt.*;
import main.gui.Login;
import main.gui.MainDashboard;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private MainDashboard mainDashboard;

    public App() {
        setTitle("Hệ thống Quản lý Coffee House");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 1. Trang Đăng nhập
        Login loginPanel = new Login(this);
        mainContainer.add(loginPanel, "LOGIN_PAGE");

        // 2. Trang Dashboard Chính
        this.mainDashboard = new MainDashboard(this);
        mainContainer.add(mainDashboard, "MAIN_PAGE");

        add(mainContainer);
    }

    public void showMainPage() {
        mainDashboard.renderUI();
        cardLayout.show(mainContainer, "MAIN_PAGE");
    }

    public void showLoginPage() {
        cardLayout.show(mainContainer, "LOGIN_PAGE");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}

