package main.dto;

import main.enumeration.SortDirection;

public record LoaiSPGetListCriteria(
    SortDirection sapXepMa,
    SortDirection sapXepTen,
    Integer limit,
    Integer page
) {
    public LoaiSPGetListCriteria {
        if (sapXepMa == null) sapXepMa = SortDirection.NONE;
        if (sapXepTen == null) sapXepTen = SortDirection.NONE;
    }
}
