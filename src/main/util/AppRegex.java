package main.util;

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
}
