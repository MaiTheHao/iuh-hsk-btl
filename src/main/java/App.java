package main.java;

import javax.swing.*;
import java.awt.*;
import main.java.gui.Login;
import main.java.gui.MainDashboard;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private MainDashboard mainDashboard;

    public App() {
        // Khung ứng dụng
        setTitle("Hệ thống Quản lý Coffee House");
        setSize(1700, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Chuyển trang
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Danh sách trang
        Login loginPanel = new Login(this);
        mainContainer.add(loginPanel, "LOGIN_PAGE");

        this.mainDashboard = new MainDashboard(this);
        mainContainer.add(mainDashboard, "MAIN_PAGE");

        add(mainContainer);
    }

    // Điều hướng
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

