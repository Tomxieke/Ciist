package com.ciist.entities;

import android.content.Context;

/**
 * Created by CIIST on 2015/11/10 0010.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class Actor {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    String name;
    String picName;

    public int getPicID() {
        return picID;
    }

    public void setPicID(int picID) {
        this.picID = picID;
    }

    int picID;

    public Actor(String name, String picName,int _picID) {
        this.name = name;
        this.picName = picName;
        this.picID=_picID;
    }

    public int getImageResourceId(Context context) {
        try {
            return context.getResources().getIdentifier(this.picName, "drawable", context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
