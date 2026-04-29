package main.enumeration;

public enum TrangThaiSP {
    ACTIVE, INACTIVE;

    public static TrangThaiSP fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return TrangThaiSP.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
