package com.ciist.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ResultInfo {
    private PageInfo pageinfo = new PageInfo();
    private List<String> SimpleDatas = new ArrayList<String>();
    private List<ChatMsgEntity> ChatMsgEntityDatas = new ArrayList<ChatMsgEntity>();

    public List<ItemInfo> getSimpleItemObj() {
        return SimpleItemObj;
    }

    public List<ChatMsgEntity> getChatMsgEntityDatas() {
        return ChatMsgEntityDatas;
    }

    public void setChatMsgEntityDatas(List<ChatMsgEntity> chatMsgEntityDatas) {
        ChatMsgEntityDatas = chatMsgEntityDatas;
    }

    public void setSimpleItemObj(List<ItemInfo> simpleItemObj) {
        SimpleItemObj = simpleItemObj;
    }

    private List<ItemInfo> SimpleItemObj = new ArrayList<ItemInfo>();
    private String MessageInfo = "";

    public PageInfo getPageinfo() {
        return pageinfo;
    }

    public void setPageinfo(PageInfo pageinfo) {
        this.pageinfo = pageinfo;
    }

    public List<String> getSimpleDatas() {
        return SimpleDatas;
    }

    public void setSimpleDatas(List<String> simpleDatas) {
        SimpleDatas = simpleDatas;
    }

    public String getMessageInfo() {
        return MessageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        MessageInfo = messageInfo;
    }

    public String getResultCode() {
        return ResultCode;
    }

    public void setResultCode(String resultCode) {
        ResultCode = resultCode;
    }

    private String ResultCode = "";
}
