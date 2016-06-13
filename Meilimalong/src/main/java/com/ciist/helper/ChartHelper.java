package com.ciist.helper;

import android.graphics.Typeface;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

/**
 * Created by 中联软科 on 2015/12/14.
 */
public class ChartHelper {
    public static void showPieChart(PieChart pieChart, PieData pieData, String chartDesc) {
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(40f);  //半径
        pieChart.setTransparentCircleRadius(44f); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆
        pieChart.setDescription("");
        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度
        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转
        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);
        // add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);
//      mChart.setOnAnimationListener(this);
        pieChart.setCenterText(chartDesc);  //饼状图中间的文字
        //设置数据
        pieChart.setData(pieData);
        // undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //最右边显示
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }

    public static void showBarChart(BarChart barChart,BarData barData){
        barChart.setDescription("");
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.zoom(0f,1.1f,0f,0f);
        barChart.setData(barData);
        Legend l = barChart.getLegend();
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(true);
        barChart.animateX(2500);
    }

    public static void showHBarChart(HorizontalBarChart barChart,BarData barData,String Description){
        barChart.setDescription(Description);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setData(barData);
        barChart.setBorderWidth(0);
        barChart.zoom(1f,1.1f,0f,0f);
        barChart.setDrawBorders(false);
        Legend l = barChart.getLegend();
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.animateY(2500);
    }
}
