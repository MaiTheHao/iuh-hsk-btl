package main.java.dto;

import main.java.enumeration.SortDirection;

public class LoaiSPGetListCriteria extends BaseGetListCriteria {
    private SortDirection sapXepMa = SortDirection.NONE;
    private SortDirection sapXepTen = SortDirection.NONE;

    public LoaiSPGetListCriteria() {
    }

    public LoaiSPGetListCriteria(SortDirection sapXepMa, SortDirection sapXepTen, Integer limit, Integer page) {
        super(limit, page);
        if (sapXepMa != null) this.sapXepMa = sapXepMa;
        if (sapXepTen != null) this.sapXepTen = sapXepTen;
    }

    public SortDirection getSapXepMa() {
        return sapXepMa;
    }

    public void setSapXepMa(SortDirection sapXepMa) {
        this.sapXepMa = sapXepMa;
    }

    public SortDirection getSapXepTen() {
        return sapXepTen;
    }

    public void setSapXepTen(SortDirection sapXepTen) {
        this.sapXepTen = sapXepTen;
    }
}
