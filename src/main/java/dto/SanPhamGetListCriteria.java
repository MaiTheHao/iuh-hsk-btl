package main.java.dto;

import main.java.enumeration.SortDirection;

public class SanPhamGetListCriteria extends BaseGetListCriteria {
    private String maLoai;
    private Double giaTu;
    private Double giaDen;
    private SortDirection sapXepMa = SortDirection.NONE;
    private SortDirection sapXepGia = SortDirection.NONE;

    public SanPhamGetListCriteria() {
    }

    public SanPhamGetListCriteria(String maLoai, Double giaTu, Double giaDen, SortDirection sapXepMa, SortDirection sapXepGia, Integer limit, Integer page) {
        super(limit, page);
        this.maLoai = maLoai;
        this.giaTu = giaTu;
        this.giaDen = giaDen;
        if (sapXepMa != null) this.sapXepMa = sapXepMa;
        if (sapXepGia != null) this.sapXepGia = sapXepGia;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public Double getGiaTu() {
        return giaTu;
    }

    public void setGiaTu(Double giaTu) {
        this.giaTu = giaTu;
    }

    public Double getGiaDen() {
        return giaDen;
    }

    public void setGiaDen(Double giaDen) {
        this.giaDen = giaDen;
    }

    public SortDirection getSapXepMa() {
        return sapXepMa;
    }

    public void setSapXepMa(SortDirection sapXepMa) {
        this.sapXepMa = sapXepMa;
    }

    public SortDirection getSapXepGia() {
        return sapXepGia;
    }

    public void setSapXepGia(SortDirection sapXepGia) {
        this.sapXepGia = sapXepGia;
    }
}
