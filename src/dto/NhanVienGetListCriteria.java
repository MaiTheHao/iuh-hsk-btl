package dto;

import enumeration.LoaiNV;
import enumeration.SortDirection;

public record NhanVienGetListCriteria(
    String tuKhoa,
    LoaiNV loai,
    SortDirection sapXepTen,
    Integer limit,
    Integer page
) {
    public NhanVienGetListCriteria {
        if (sapXepTen == null) sapXepTen = SortDirection.NONE;
    }
}
