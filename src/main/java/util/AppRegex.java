package main.java.util;

import java.util.regex.Pattern;

public final class AppRegex {
    private AppRegex() {}

    /**
     * Regex cho số điện thoại Việt Nam:
     * - 10 chữ số
     * - Bắt đầu bằng 03, 08, 07, 05, hoặc 09
     */
    public static final Pattern PHONE = Pattern.compile("^(03|08|07|05|09)[0-9]{8}$");

    /**
     * Regex cho mật khẩu:
     * - Ít nhất 8 ký tự
     * - Có ít nhất một chữ hoa, một chữ thường, một số và một ký tự đặc biệt
     */
    public static final Pattern PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$");

    /**
     * Regex cho mã hóa đơn:
     * - Bắt đầu bằng HD-
     * - Tiếp theo là timestamp 14 chữ số (YYYYMMDDHHmmss)
     * - Tiếp theo là mã nhân viên
     * - Cuối cùng là SDT khách hàng hoặc 'VL'
     */
    public static final Pattern MA_HOA_DON = Pattern.compile("^HD-[0-9]{14}-[A-Za-z0-9]+-([0-9]{10}|VL)$");
}
