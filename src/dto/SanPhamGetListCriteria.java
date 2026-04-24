package dto;

import enumeration.SortDirection;

public record SanPhamGetListCriteria(
    String maLoai,
    Double giaTu,
    Double giaDen,
    SortDirection sapXepMa,
    SortDirection sapXepGia,
    Integer limit,
    Integer page
) {
    public SanPhamGetListCriteria {
        if (sapXepMa == null) sapXepMa = SortDirection.NONE;
        if (sapXepGia == null) sapXepGia = SortDirection.NONE;
    }
}
