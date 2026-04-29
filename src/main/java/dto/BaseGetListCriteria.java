package main.java.dto;

public abstract class BaseGetListCriteria {
    protected Integer limit;
    protected Integer page;

    public BaseGetListCriteria() {
    }

    public BaseGetListCriteria(Integer limit, Integer page) {
        this.limit = limit;
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page != null && page > 0 ? page : 1;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getOffset() {
        return (limit != null && limit > 0 && page != null && page > 0) ? (page - 1) * limit : 0;
    }

    public boolean isPaginate() {
        return limit != null && limit > 0;
    }
}
