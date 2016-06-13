package com.ciist.helper;

/**
 * Created by 中联软科 on 2015/11/21.
 */
public class GISHelper {
    private static double EARTH_RADIUS = 6378.137;
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = (Math.round(s * 10000) / 10000.0);
        return s;
//        double x,y, distance;
//        x=(lng2-lng1)*Math.PI*EARTH_RADIUS*Math.cos( ((lat1+lat2)/2)*Math.PI/180)/180;
//        y=(lat2-lat1)*Math.PI*EARTH_RADIUS/180;
//        distance=Math.hypot(x,y);
//        return distance;

    }
}
