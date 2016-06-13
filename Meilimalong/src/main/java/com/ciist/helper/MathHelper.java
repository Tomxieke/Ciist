package com.ciist.helper;

/**
 * Created by 中联软科 on 2016/1/6.
 */
public class MathHelper {
   public static float StringToFloat(String val){
        try{
            return Float.parseFloat(val);
        }catch (Exception e){
            return 0;
        }
    }
}
