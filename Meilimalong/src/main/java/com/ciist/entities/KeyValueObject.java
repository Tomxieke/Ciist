package com.ciist.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 中联软科 on 2015/12/16.
 */
public class KeyValueObject {
    String KeyName;
    int KeyValues;
    String UpKeyName;
    ArrayList<KeyValueObject> obj = new ArrayList<KeyValueObject>();

    public String getKeyName() {
        return KeyName;
    }

    public void setKeyName(String keyName) {
        KeyName = keyName;
    }

    public int getKeyValues() {
        return KeyValues;
    }

    public void setKeyValues(int keyValues) {
        KeyValues = keyValues;
    }

    public String getUpKeyName() {
        return UpKeyName;
    }

    public void setUpKeyName(String upKeyName) {
        UpKeyName = upKeyName;
    }

    public ArrayList<KeyValueObject> getObj() {
        return obj;
    }

    public void setObj(ArrayList<KeyValueObject> obj) {
        this.obj = obj;
    }

}
