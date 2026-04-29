package main.java.enumeration;

public enum LoaiNV {
    ADMIN, STAFF;

    public static LoaiNV fromString(String value) {
        if (value == null) return STAFF;
        try {
            return LoaiNV.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return STAFF;
        }
    }
}
