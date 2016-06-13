package com.ciist.util;

import java.util.ArrayList; 
import java.util.List;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.hw.ciist.util.Hutils.Ciist_entity;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

public class ChartUtils {

	// --------------------------------------折线图-------------------------------------------------
	/**
	 * 设置折线图风格
	 * @param mLineChart
	 * @param lineData
	 * @param color
	 */
	public static void setChartStyle(LineChart mLineChart, LineData lineData, int color) { 
		
		mLineChart.setDrawBorders(false); // 是否在折线图上添加边框 
		//mLineChart.setDescription("折线图");// 数据描述 
		
		// 如果没有数据的时候，会显示这个，类似listview的emtpyview 
//		mLineChart .setNoDataTextDescription("数据马龙"); 
		
		/*如果mLineChart.setDrawGridBackground(false)
		那么mLineChart.setGridBackgroundColor(Color.CYAN) */

		mLineChart.getAxisLeft().setEnabled(false);//隐藏左边坐标轴
		mLineChart.getAxisRight().setEnabled(false);//隐藏右边边坐标轴
		mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//让x轴在下面

		mLineChart.animateXY(1000, 2000);//动画

		mLineChart.setDrawGridBackground(false); 
		mLineChart.setGridBackgroundColor(Color.CYAN); 
		mLineChart.setTouchEnabled(true); // 触摸
		mLineChart.setDragEnabled(true); // 拖拽
		mLineChart.setScaleEnabled(true); // 缩放
		mLineChart.setPinchZoom(false); 
		mLineChart.setBackgroundColor(color); // 设置背景颜色
		mLineChart.setData(lineData); // 设置x，y数据
		
		// 设置比例图标示，就是那个一组y的value的 Legend 
		Legend legend = mLineChart.getLegend(); 
		// legend.setPosition(LegendPosition.BELOW_CHART_CENTER);
		legend.setForm(LegendForm.CIRCLE);// 样式 
		legend.setFormSize(4f);//字体 
		legend.setTextColor(Color.BLUE);// 颜色 
		mLineChart.animateX(2000); // 沿x轴动画，时间2000毫秒
	} 

	/**
	 * 设置内容数据
	 * 
	 * @param count
	 * @return
	 */
	public static LineData makeLineData(int count,ArrayList<Ciist_entity> data,String description) {
		// x轴显示的数据
		ArrayList<String> x = new ArrayList<String>();
		for (int i = 0; i < count; i++) { 
			x.add(data.get(i).Title);
		}
		// y轴显示的数据
		List<Entry> y = new ArrayList<Entry>();
		for (int i = 0; i < count; i++) {
//			float val = (float) (Math.random() * 100);
			float val = StringToFloat(data.get(i).Author);
			Entry entry = new Entry(val, i);
			y.add(entry);
		}
		
		LineDataSet mLineDataSet = new LineDataSet(y, description); 
		mLineDataSet.setLineWidth(3.0f); 
		mLineDataSet.setCircleSize(5.0f); 
		mLineDataSet.setColor(Color.DKGRAY); 
		mLineDataSet.setCircleColor(Color.GREEN); 

		
		mLineDataSet.setDrawHighlightIndicators(true);

		mLineDataSet.setHighLightColor(Color.CYAN); 
		mLineDataSet.setValueTextSize(10.0f); 

		 mLineDataSet.setDrawCircleHole(true); 
		 mLineDataSet.setDrawCubic(true); //
		 mLineDataSet.setCubicIntensity(0.2f); // 

		 
		 mLineDataSet.setDrawFilled(true);
		 mLineDataSet.setFillAlpha(128);
		 mLineDataSet.setFillColor(Color.GREEN);

		mLineDataSet.setCircleColorHole(Color.YELLOW);

		// 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式
		// mLineDataSet.setValueFormatter(new ValueFormatter() {
		//
		// @Override
		// public String getFormattedValue(float value, Entry entry, int
		// dataSetIndex,
		// ViewPortHandler viewPortHandler) {
		// int n = (int) value;
		// String s = "y:" + n;
		// return s;
		// // return null;
		// }
		// });

		ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();
		mLineDataSets.add(mLineDataSet);
		LineData lineData = new LineData(x, mLineDataSets);
		return lineData;
	}
	
	// ------------------------------------柱形图-----------------------------------------------------
	/**
	 * 设置barChart
	 * 
	 * @param barChart
	 * @param barData
	 */
	public static void setBarChart(BarChart barChart, BarData barData) {
		barChart.setDrawBorders(false); // 是否添加边框
		barChart.setDescription("");// 数据描述

		// 如果没有数据的时候，会显示这个，类似ListView的EmptyView
		// barChart.setNoDataTextDescription("You need to provide data for the chart.");      
                 
        barChart.setDrawGridBackground(false); // 是否显示表格颜色      
        barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度      
        
        barChart.setTouchEnabled(true); // 设置是否可以触摸      
        barChart.setDragEnabled(true);// 是否可以拖拽      
        barChart.setScaleEnabled(true);// 是否可以缩放      
        barChart.setPinchZoom(false);//       

		barChart.getAxisLeft().setEnabled(false);//隐藏左边坐标轴
		barChart.getAxisRight().setEnabled(false);//隐藏右边边坐标轴
		barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//让x轴在下面

		barChart.animateXY(1000, 2000);//动画

		// barChart.setBackgroundColor();// 设置背景
        barChart.setDrawBarShadow(true);  
        barChart.setData(barData); // 设置数据      
        Legend mLegend = barChart.getLegend(); // 设置比例图标示  
      
        mLegend.setForm(LegendForm.CIRCLE);// 样式      
        mLegend.setFormSize(4f);// 字体      
        mLegend.setTextColor(Color.BLUE);// 颜色      
          
//      X轴设定  
//      XAxis xAxis = barChart.getXAxis();  
//      xAxis.setPosition(XAxisPosition.BOTTOM);  
        barChart.animateX(2500); // 立即执行的动画,x轴    
    }  
  
	/**
	 * 得到barChart的数据
	 * 
	 * @param count
	 * @param data
	 * @return
	 */
    public static BarData getBarData(int count, ArrayList<Ciist_entity> data,String description) {  
        ArrayList<String> xValues = new ArrayList<String>();  
        for (int i = 0; i < count; i++) {  
            xValues.add(data.get(i).Title);  
        }  
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();  
        for (int i = 0; i < count; i++) {      
			// float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
        	float value = StringToFloat(data.get(i).Author);
            yValues.add(new BarEntry(value, i));      
        }  
        // y轴的数据集合      
        BarDataSet barDataSet = new BarDataSet(yValues, description);   
        barDataSet.setColor(Color.rgb(114, 188, 223));  
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();      
        barDataSets.add(barDataSet); // add the datasets      
        BarData barData = new BarData(xValues, barDataSets);  
        return barData;  
    }  
	
 // -----------------------------------饼状形----------------------------------------------------
    
    /**
	 * 得到PieChart的数据
	 * 
	 * @param count
	 * @param data
	 * @return
	 */
	public static PieData getPieData(int count, ArrayList<Ciist_entity> data) {
		ArrayList<String> xValues = new ArrayList<String>(); // xVals用来表示每个饼块上的内容
		 for (int i = 0; i < count; i++) {
		 xValues.add(/*"Quarterly" + (i + 1)*/data.get(i).Title);
		 }
		// xValues.add("进行中");
		// xValues.add("已完成");
		// xValues.add("超期");
		// xValues.add("未汇报");
		// xValues.add("催办");
		// xValues.add("新汇报");

		ArrayList<Entry> yValues = new ArrayList<Entry>(); // yVals用来表示封装每个饼块的实际数据
		// 饼图数据
		/**
		 * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38 所以 14代表的百分比就是14%
		 */
		// float quarterly1 = 40;
		// float quarterly2 = 52;
		// float quarterly3 = 10;
		// float quarterly4 = 10;
		// float quarterly5 = 10;
		// float quarterly6 = 10;
		// // float quarterly4 = 38;
		// yValues.add(new Entry(quarterly1, 0));
		// yValues.add(new Entry(quarterly2, 1));
		// yValues.add(new Entry(quarterly3, 2));
		// yValues.add(new Entry(quarterly4, 3));
		// yValues.add(new Entry(quarterly5, 4));
		// yValues.add(new Entry(quarterly6, 5));
		// yValues.add(new Entry(quarterly4, 3));
		for (int i = 0; i < count; i++) {      
			// float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
        	float value = StringToFloat(data.get(i).Author);
            yValues.add(new Entry(value, i));      
        }  
		
		// y轴的集合
		PieDataSet pieDataSet = new PieDataSet(yValues, ""/* 显示在比例图上 */);
		pieDataSet.setSliceSpace(0f); // 设置个饼状图之间的距离
		
		// 饼图颜色
		ArrayList<Integer> colors = new ArrayList<Integer>();
		
		for (int i = 0; i < count; i++) {
			 colors.add(Color.rgb(205, 205, 205));
			 colors.add(Color.rgb(114, 188, 223));
			 colors.add(Color.rgb(255, 123, 124));
			 colors.add(Color.rgb(57, 135, 200));
			 colors.add(Color.rgb(205, 205, 205));
			 colors.add(Color.rgb(114, 188, 223));
		}
		pieDataSet.setColors(colors);
		// DisplayMetrics metrics = null ;//=getResources().getDisplayMetrics();
		// float px = 5 * (metrics.densityDpi / 160f);
		// pieDataSet.setSelectionShift(px); // 选中态多出的长度
		pieDataSet.setValueTextSize(12f);
		PieData pieData = new PieData(xValues, pieDataSet);
		return pieData;
	}
    
    
    /**
     * 设置PieChart
     * 
     * @param pieChart
     * @param pieData
     * @param chartDesc
     */
 	public static void setPieChart(PieChart pieChart, PieData pieData, String chartDesc) {
 		pieChart.setHoleColorTransparent(true);
 		pieChart.setHoleRadius(40f); // 半径
 		pieChart.setTransparentCircleRadius(44f); // 半透明圈
 		
 		// pieChart.setHoleRadius(0) //实心圆
 		pieChart.setDescription("");
 		// mChart.setDrawYValues(true);
 		pieChart.setDrawCenterText(true); // 饼状图中间可以添加文字
 		pieChart.setDrawHoleEnabled(true);
 		pieChart.setRotationAngle(90); // 初始旋转角度
 		
 		// draws the corresponding description value into the slice
 		// mChart.setDrawXValues(true);
 		// enable rotation of the chart by touch
 		pieChart.setRotationEnabled(true); // 可以手动旋转
 		// display percentage values
 		pieChart.setUsePercentValues(true); // 显示成百分比
 		// mChart.setUnit(" €");
 		// mChart.setDrawUnitsInChart(true);
 		// add a selection listener
 		// mChart.setOnChartValueSelectedListener(this);
 		// mChart.setTouchEnabled(false);
 		// mChart.setOnAnimationListener(this);
 		pieChart.setCenterText(chartDesc); // 饼状图中间的文字
 		// 设置数据
 		pieChart.setData(pieData);
 		// undo all highlights
 		// pieChart.highlightValues(null);
 		// pieChart.invalidate();

 		Legend mLegend = pieChart.getLegend(); // 设置比例图
 		mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER); // 最下边显示
 		// mLegend.setForm(LegendForm.LINE); //设置比例图的形状，默认是方形
 		mLegend.setXEntrySpace(7f);
 		mLegend.setYEntrySpace(5f);

 		pieChart.animateXY(1000, 1000); // 设置动画
 		// mChart.spin(2000, 0, 360);
 	}
    /**
	 * String 转换 float
	 * 
	 * @param str
	 * @return
	 */
	public static float StringToFloat(String str) {
		try {
			float f = Float.parseFloat(str);
			return f;
		} catch (Exception e) {
			System.out.println(e + "");
		}
		return 0;
	}
}
