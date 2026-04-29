package main.java.enumeration;

public enum TrangThaiHD {
    PENDING, PAID, CANCELLED;

    public static TrangThaiHD fromString(String value) {
        if (value == null) return PENDING;
        try {
            return TrangThaiHD.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
