package com.ciist.entities;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ItemInfo {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    private String title;
    private String pubdate;
    private String linkurl;
    private String infosource;

    private String telnum;

    public int getVisitCount() {
        return VisitCount;
    }

    public double getJuli() {
        return juli;
    }

    public void setJuli(double juli) {
        this.juli = juli;
    }

    private double juli;

    public void setVisitCount(int visitCount) {
        VisitCount = visitCount;
    }

    private int VisitCount;

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    public double getLongtidue() {
        return longtidue;
    }

    public void setLongtidue(double longtidue) {
        this.longtidue = longtidue;
    }

    public double getLatidue() {
        return latidue;
    }

    public void setLatidue(double latidue) {
        this.latidue = latidue;
    }

    private double longtidue;
    private double latidue;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBak1() {
        return bak1;
    }

    public void setBak1(String bak1) {
        this.bak1 = bak1;
    }

    public String getBak2() {
        return bak2;
    }

    public void setBak2(String bak2) {
        this.bak2 = bak2;
    }

    public String getBak3() {
        return bak3;
    }

    public void setBak3(String bak3) {
        this.bak3 = bak3;
    }

    public String getBak4() {
        return bak4;
    }

    public void setBak4(String bak4) {
        this.bak4 = bak4;
    }

    public String getBak5() {
        return bak5;
    }

    public void setBak5(String bak5) {
        this.bak5 = bak5;
    }

    private String source;
    private String author;
    private String bak1;
    private String bak2;
    private String bak3;
    private String bak4;
    private String bak5;
    private String depcode;
    private int ispub;

    public String getDepcode() {
        return depcode;
    }

    public void setDepcode(String depcode) {
        this.depcode = depcode;
    }

    public int getIspub() {
        return ispub;
    }

    public void setIspub(int ispub) {
        this.ispub = ispub;
    }

    public String getInfosource() {
        return infosource;
    }

    public void setInfosource(String infosource) {
        this.infosource = infosource;
    }


    public String getInfotype() {
        return infotype;
    }

    public void setInfotype(String infotype) {
        this.infotype = infotype;
    }

    private String infotype;

    public String getInfoauthor() {
        return infoauthor;
    }

    public void setInfoauthor(String infoauthor) {
        this.infoauthor = infoauthor;
    }

    private String infoauthor;

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    private String imgsrc;

    @Override
    public String toString() {
        return title + "[" + pubdate + "]";
    }
}
