package ltd.newbee.mall.util;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {

    // Total number of records
    private int totalCount;
    // Number of records per page
    private int pageSize;
    // Total number of pages
    private int totalPage;
    // Current page number
    private int currPage;
    // List of data
    private List<?> list;

    /**
     * Pagination
     *
     * @param list       List of data
     * @param totalCount Total number of records
     * @param pageSize   Number of records per page
     * @param currPage   Current page number
     */
    public PageResult(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
