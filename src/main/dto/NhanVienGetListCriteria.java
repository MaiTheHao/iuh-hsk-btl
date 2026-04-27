package main.dto;

import main.enumeration.LoaiNV;
import main.enumeration.SortDirection;

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
