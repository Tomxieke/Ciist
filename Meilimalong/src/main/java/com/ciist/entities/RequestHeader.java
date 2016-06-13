package com.ciist.entities;

/**
 * Created by 中联软科 on 2015/11/23.
 */
public class RequestHeader {
    public String SKEY;
    public int PageIndex;
    public int PageSize;
    public String Identify;

    public String getSKEY() {
        return SKEY;
    }

    public void setSKEY(String SKEY) {
        this.SKEY = SKEY;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public String getIdentify() {
        return Identify;
    }

    public void setIdentify(String identify) {
        Identify = identify;
    }
}
