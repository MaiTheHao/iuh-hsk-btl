package main.dto;

import main.enumeration.LoaiNV;
import main.enumeration.SortDirection;

public class NhanVienGetListCriteria extends BaseGetListCriteria {
    private String tuKhoa;
    private LoaiNV loai;
    private SortDirection sapXepTen = SortDirection.NONE;

    public NhanVienGetListCriteria() {
    }

    public NhanVienGetListCriteria(String tuKhoa, LoaiNV loai, SortDirection sapXepTen, Integer limit, Integer page) {
        super(limit, page);
        this.tuKhoa = tuKhoa;
        this.loai = loai;
        if (sapXepTen != null) this.sapXepTen = sapXepTen;
    }

    public String getTuKhoa() {
        return tuKhoa;
    }

    public void setTuKhoa(String tuKhoa) {
        this.tuKhoa = tuKhoa;
    }

    public LoaiNV getLoai() {
        return loai;
    }

    public void setLoai(LoaiNV loai) {
        this.loai = loai;
    }

    public SortDirection getSapXepTen() {
        return sapXepTen;
    }

    public void setSapXepTen(SortDirection sapXepTen) {
        this.sapXepTen = sapXepTen;
    }
}
