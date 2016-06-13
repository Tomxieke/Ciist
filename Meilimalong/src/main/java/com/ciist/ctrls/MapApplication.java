package com.ciist.ctrls;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

public class MapApplication extends Application {

private ArrayList<Activity>activities=new ArrayList<Activity>();
private static MapApplication instance;

private MapApplication()
{ 
} 
//鍗曚緥妯″紡涓幏鍙栧敮涓�鐨凪yApplication瀹炰緥 
public static MapApplication getInstance()
{ 
if(null == instance) 
{ 
instance = new MapApplication();
} 
return instance; 
} 
//娣诲姞Activity鍒板鍣ㄤ腑 
public void addActivity(Activity activity) 
{ 
	activities.add(activity);
} 
public void deleteActivity(Activity activity){
	activities.remove(activity);
}
//finish 
public void exit() 
{ 
  for(Activity activity:activities){
	  activity.finish();
  }
  activities.clear();
 
} 
} 
