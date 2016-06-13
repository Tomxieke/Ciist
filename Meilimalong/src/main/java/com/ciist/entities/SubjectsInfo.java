package com.ciist.entities;

import java.io.Serializable;

/**
 * Created by CIIST on 2015/11/11 0011.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class SubjectsInfo implements Serializable {

    public SubjectsInfo(String _name,String _code){
        this._subjectName=_name;
        this._subjectCode=_code;
    }
    public String get_subjectName() {
        return _subjectName;
    }

    public void set_subjectName(String _subjectName) {
        this._subjectName = _subjectName;
    }

    public String get_subjectCode() {
        return _subjectCode;
    }

    public void set_subjectCode(String _subjectCode) {
        this._subjectCode = _subjectCode;
    }

    private String _subjectName;
    private String _subjectCode;

}
