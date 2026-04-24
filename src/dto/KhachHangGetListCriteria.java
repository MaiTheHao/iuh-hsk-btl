package dto;

import enumeration.SortDirection;

public record KhachHangGetListCriteria(
    String tuKhoa,
    SortDirection sapXepTen,
    SortDirection sapXepDiem,
    Integer limit,
    Integer page
) {
    public KhachHangGetListCriteria {
        if (sapXepTen == null) sapXepTen = SortDirection.NONE;
        if (sapXepDiem == null) sapXepDiem = SortDirection.NONE;
    }
}
