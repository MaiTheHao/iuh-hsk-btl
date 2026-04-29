package main.util;

import main.entity.NhanVien;
import main.enumeration.LoaiNV;

public class AppContext {
    private static AppContext instance;
    private NhanVien currentUser;

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public NhanVien getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(NhanVien user) {
        if (user != null) {
            System.out.println("[SESSION] User logged in: " + user.getTen() + " (ID: " + user.getMa() + ", Role: " + user.getLoai() + ") at " + new java.util.Date());
        }
        this.currentUser = user;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("[SESSION] User logged out: " + currentUser.getTen() + " at " + new java.util.Date());
        }
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.getLoai() == LoaiNV.ADMIN;
    }
}
