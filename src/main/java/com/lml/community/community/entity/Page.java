package com.lml.community.community.entity;

/**
 * 封装分页相关的信息
 */
public class Page {

    // current page
    private int current = 1;
    // upper limit
    private int limit = 10;
    // total data count(to calculate page count)
    private int rows;
    // search path(get each page's link)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /*
     * get current page's start row
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /*
     * get total page count
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }
     /*
      * get start page
      */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
