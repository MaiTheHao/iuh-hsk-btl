package main.dto;

import main.enumeration.SortDirection;

public class KhachHangGetListCriteria extends BaseGetListCriteria {
    private String tuKhoa;
    private SortDirection sapXepTen = SortDirection.NONE;
    private SortDirection sapXepDiem = SortDirection.NONE;

    public KhachHangGetListCriteria() {
    }

    public KhachHangGetListCriteria(String tuKhoa, SortDirection sapXepTen, SortDirection sapXepDiem, Integer limit, Integer page) {
        super(limit, page);
        this.tuKhoa = tuKhoa;
        if (sapXepTen != null) this.sapXepTen = sapXepTen;
        if (sapXepDiem != null) this.sapXepDiem = sapXepDiem;
    }

    public String getTuKhoa() {
        return tuKhoa;
    }

    public void setTuKhoa(String tuKhoa) {
        this.tuKhoa = tuKhoa;
    }

    public SortDirection getSapXepTen() {
        return sapXepTen;
    }

    public void setSapXepTen(SortDirection sapXepTen) {
        this.sapXepTen = sapXepTen;
    }

    public SortDirection getSapXepDiem() {
        return sapXepDiem;
    }

    public void setSapXepDiem(SortDirection sapXepDiem) {
        this.sapXepDiem = sapXepDiem;
    }
}
