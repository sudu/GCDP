package com.me.GCDP.util.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2009-9-7               Hu WeiQi               create the class     </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class ChartService {
	
	private static Log log = LogFactory.getLog(ChartService.class);
	
	//private static String chfont = "黑体";
	
	private static Font titleFont = null;
	
	private static Font axisFont = null;
	
	private static Font legendFont = null;
	
	static{
		try{
			ClassLoader loader = ChartService.class.getClassLoader();
			File fontFile = new File(loader.getResource("/font/simhei.ttf").getPath());
			titleFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			titleFont = titleFont.deriveFont(Font.BOLD, 14F);
			axisFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			axisFont = axisFont.deriveFont(Font.PLAIN, 12F);
			legendFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			legendFont = legendFont.deriveFont(Font.PLAIN, 12F);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成仪表盘图
	 * @param session
	 * @param title 标题
	 * @param data Double 数据
	 * @param decimalpattern 数字格式
	 * @param tickData Double[] 刻度数据 0-最小刻度 1-最大刻度
	 * @param dataRange Double[] 红黄绿的范围 0<绿<1<黄<红<2
	 * @param textInChart String 图表中央显示的文字
	 * @param width 宽
	 * @param height 长
	 * @return
	 */
	public static String createDialChart(HttpSession session, String title, Double data,
			String decimalpattern, Double[] tickData, Double[] dataRange, String textInChart, 
			int width, int height){
		DialPlot plot = new DialPlot(new DefaultValueDataset(data)); 
		plot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		plot.setBackgroundPaint(Color.white);

		//开始设置显示框架结构
		StandardDialFrame simpledialframe = new StandardDialFrame();
        simpledialframe.setBackgroundPaint(Color.white);
        simpledialframe.setStroke(new BasicStroke(1f));
        simpledialframe.setForegroundPaint(Color.darkGray);
        plot.setDialFrame(simpledialframe);
        
        //GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
        GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(196, 238, 248));
        DialBackground dialbackground = new DialBackground(gradientpaint);
        dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        plot.setBackground(dialbackground);
        
        //设置显示在表盘中央位置的信息
        DialTextAnnotation dialtextannotation = new DialTextAnnotation(textInChart);
        dialtextannotation.setFont(legendFont);
        dialtextannotation.setRadius(0.55D);
        plot.addLayer(dialtextannotation);
        DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
        dialvalueindicator.setNumberFormat(new DecimalFormat(decimalpattern));
        dialvalueindicator.setFont(legendFont.deriveFont(Font.PLAIN,12F));
        dialvalueindicator.setOutlineStroke(new BasicStroke(0f));
        dialvalueindicator.setRadius(0.4D);
        plot.addLayer(dialvalueindicator);
        
        //根据表盘的直径大小（0.88），设置总刻度范围
        StandardDialScale standarddialscale = new StandardDialScale(tickData[0], tickData[1], 220D, -260D, (tickData[1]-tickData[0])/5, 5 );
        standarddialscale.setTickRadius(0.88D);
        standarddialscale.setTickLabelOffset(0.15D);
        standarddialscale.setTickLabelFont(legendFont);
        standarddialscale.setTickLabelPaint(Color.darkGray);
        standarddialscale.setTickLabelFormatter(new DecimalFormat(decimalpattern));
        standarddialscale.setMajorTickStroke(new BasicStroke(1f));
        standarddialscale.setMinorTickStroke(new BasicStroke(1f));
        plot.addScale(0, standarddialscale);
        
        //设置刻度范围（红色）
        StandardDialRange standarddialrange = new StandardDialRange(dataRange[2], dataRange[3], Color.red);
        standarddialrange.setInnerRadius(0.52D);
        standarddialrange.setOuterRadius(0.56D);
        plot.addLayer(standarddialrange);
        //设置刻度范围（橘黄色）           
        StandardDialRange standarddialrange1 = new StandardDialRange(dataRange[1], dataRange[2], Color.orange);
        standarddialrange1.setInnerRadius(0.52D);
        standarddialrange1.setOuterRadius(0.56D);
        plot.addLayer(standarddialrange1);
        //设置刻度范围（绿色）               
        StandardDialRange standarddialrange2 = new StandardDialRange(dataRange[0], dataRange[1], Color.green);
        standarddialrange2.setInnerRadius(0.52D);
        standarddialrange2.setOuterRadius(0.56D);
        plot.addLayer(standarddialrange2);

        //设置指针
        DialPointer pointer = new DialPointer.Pin();
        pointer.setRadius(0.68D);
        plot.addLayer(pointer);
        
        DialCap dialcap = new DialCap();
        dialcap.setRadius(0.1D);
        plot.setCap(dialcap);

        JFreeChart chart = new JFreeChart(title, plot);
        chart.setBackgroundPaint(Color.white);
		
		TextTitle textTitle = chart.getTitle();
	    textTitle.setFont(titleFont);
	    
	    String filename = "";
		try{
			filename = ServletUtilities.saveChartAsJPEG(chart, width, height, session);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return filename;
	}
	
	/**
	 * 生成多Y轴的曲线图
	 * @param title 图标题
	 * @param data 数据 List<Map>
	 * @param legendName 图例名称
	 * @param domainName X轴标题
	 * @param domainValue Map中X轴的Key
	 * @param rangeName 多Y轴标题 (String[])
	 * @param rangeValue Map中Y轴的Key
	 * @param displayLegend 是否显示图例
	 * @param pattern X轴日期格式
	 * @param decimalpattern Y轴数字格式
	 * @param width 宽
	 * @param height 高
	 */
	public static String createMultipleAxisChart(HttpSession session, String title, List<Map> data,String[] legendName,
			String domainName, String domainValue, String[] rangeName, String[] rangeValue,
			boolean displayLegend, String pattern,String[] decimalpattern, int width, int height){
		
		XYPlot plot = new XYPlot();
		plot.setBackgroundPaint(Color.white);
		int seriescount = rangeValue.length;
		for(int i = 0 ; i < seriescount ; i ++){
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			TimeSeries s = new TimeSeries(legendName[i],rangeName[i],rangeName[i]);
			for(Map m : data){
				try{
					s.add(new Minute((Date)m.get(domainValue)), (Double)m.get(rangeValue[i]));
				}catch(Exception e){
					log.warn(e.getMessage());
				}
			}
			dataset.addSeries(s);
			plot.setDataset(i,dataset);
			
			XYLineAndShapeRenderer xyLine = new XYLineAndShapeRenderer();
			xyLine.setSeriesShapesVisible(0, false);
	        plot.setRenderer(i, xyLine);
	        plot.mapDatasetToRangeAxis(i, i);
	        
	        NumberAxis axis = new NumberAxis(rangeName[i]);	    
	        axis.setAutoRange(true);
	        axis.setAutoRangeIncludesZero(false);	        
			
			if(decimalpattern[i] != null && !decimalpattern[i].equals("")){
				DecimalFormat df = new DecimalFormat(decimalpattern[i]); 
				axis.setNumberFormatOverride(df);
			}
			
			plot.setRangeAxis(i, axis);
			if(i%2==0){
				plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
			}else{
				plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_RIGHT);
			}
		}
		
		DateAxis dateaxis = new DateAxis(domainName);
		dateaxis.setDateFormatOverride(new SimpleDateFormat(pattern));
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setAutoRange(true);
        plot.setDomainAxis(dateaxis);
        
        JFreeChart chart = new JFreeChart(title, plot);
        chart.setBackgroundPaint(Color.white);
        chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(legendFont);
		legend.setVisible(displayLegend);
		
		TextTitle textTitle = chart.getTitle();
	    textTitle.setFont(titleFont);
		
	    String filename = "";
		try{
			filename = ServletUtilities.saveChartAsJPEG(chart, width, height, session);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return filename;
	}
	
	/**
	 * 生成X轴为时间的单Y轴 系列1为areaChart 系列2为lineChart
	 * @param title 图标题
	 * @param data 数据 List<Map>
	 * @param legendName 图例名称
	 * @param domainName X轴标题
	 * @param domainValue Map中X轴的Key
	 * @param rangeName Y轴标题 
	 * @param rangeValue Map中Y轴的Key
	 * @param displayLegend 是否显示图例
	 * @param pattern X轴日期格式
	 * @param decimalpattern Y轴数字格式
	 * @param width 宽
	 * @param height 高
	 */
	public static String createAreaLineChart(HttpSession session, String title, List<Map> data,String[] legendName,
			String domainName, String domainValue, String rangeName, String[] rangeValue,
			boolean displayLegend, String pattern,String decimalpattern, int width, int height){
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		TimeSeriesCollection dataset2 = new TimeSeriesCollection();
		try{
				TimeSeries s = new TimeSeries(legendName[0]);
				for(Map m : data){
					try{
						s.add(new Second((Date)m.get(domainValue)), (Double)m.get(rangeValue[0]));
					}catch(Exception e){
						log.warn(e.getMessage());
					}
				}
				dataset.addSeries(s);
				
				TimeSeries s2 = new TimeSeries(legendName[1]);
				for(Map m : data){
					try{
						s2.add(new Second((Date)m.get(domainValue)), (Double)m.get(rangeValue[1]));
					}catch(Exception e){
						log.warn(e.getMessage());
					}
				}
				dataset2.addSeries(s2);
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, domainName, rangeName, dataset2, 
				PlotOrientation.VERTICAL,true, true, true
			);
		
		chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundAlpha(1F);
		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(0.8F);
		plot.setRangeMinorGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.GRAY);
		
		plot.setDataset(1, dataset);
		
		XYItemRenderer render = plot.getRenderer(0);
		render.setSeriesPaint(0, new Color(0,0,220));
		
		/*XYLineAndShapeRenderer xyLine = new XYLineAndShapeRenderer();
		xyLine.setSeriesShapesVisible(0, false);
		xyLine.setSeriesPaint(0, new Color(0,0,220));
        plot.setRenderer(1, xyLine);*/
        
        XYAreaRenderer xyArea = new XYAreaRenderer();
        xyArea.setSeriesPaint(0, new Color(0,220,0));
        plot.setRenderer(1, xyArea);
		
		NumberAxis ra = (NumberAxis)plot.getRangeAxis();
		ra.setLabelFont(axisFont);
		ra.setLabelPaint(Color.BLACK);
		if(decimalpattern != null && !decimalpattern.equals("")){
			DecimalFormat df = new DecimalFormat(decimalpattern); 
			ra.setNumberFormatOverride(df);
		}
		
		DateAxis dateaxis = new DateAxis(domainName);
		dateaxis.setLabelFont(axisFont);
		dateaxis.setDateFormatOverride(new SimpleDateFormat(pattern));
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setAutoRange(true);
        plot.setDomainAxis(dateaxis);
		
		TextTitle textTitle = chart.getTitle();
	    textTitle.setFont(titleFont);
	    
	    LegendTitle legend = chart.getLegend();
		legend.setItemFont(legendFont);
		legend.setVisible(displayLegend);
		legend.setBorder(0, 0, 0, 0);
		legend.setItemPaint(Color.BLACK);
	    
	    String filename = "";
		try{
			filename = ServletUtilities.saveChartAsJPEG(chart, width, height, session);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return filename;
	}
	
	/**
	 * 生成X轴为时间的单Y轴lineChart
	 * @param title 图标题
	 * @param data 数据 List<Map>
	 * @param legendName 图例名称
	 * @param domainName X轴标题
	 * @param domainValue Map中X轴的Key
	 * @param rangeName Y轴标题 
	 * @param rangeValue Map中Y轴的Key
	 * @param displayLegend 是否显示图例
	 * @param pattern X轴日期格式
	 * @param decimalpattern Y轴数字格式
	 * @param width 宽
	 * @param height 高
	 */
	public static String createLineChart(HttpSession session, String title, List<Map> data,String[] legendName,
			String domainName, String domainValue, String rangeName, String[] rangeValue,
			boolean displayLegend, String pattern,String decimalpattern, int width, int height){
		
		int seriescount = rangeValue.length;
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		try{
			for(int i = 0 ; i < seriescount ; i ++){
				TimeSeries s = new TimeSeries(legendName[i]);
				for(Map m : data){
					try{
						s.add(new Second((Date)m.get(domainValue)), (Double)m.get(rangeValue[i]));					
					}catch(Exception e){
						log.warn(e.getMessage());
					}
				}
				dataset.addSeries(s);
			}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, domainName, rangeName, dataset, 
				PlotOrientation.VERTICAL,true, true, true
			);
		
		chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeMinorGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.GRAY);
		
		XYLineAndShapeRenderer xyRenderer = (XYLineAndShapeRenderer)plot.getRenderer();
		xyRenderer.setSeriesPaint(0,Color.RED);
		xyRenderer.setSeriesPaint(1,Color.BLUE);
		xyRenderer.setSeriesPaint(2,Color.GREEN);
		xyRenderer.setSeriesPaint(3,Color.ORANGE);
		
		NumberAxis ra = (NumberAxis)plot.getRangeAxis();
		ra.setAutoRangeIncludesZero(true);
		ra.setLabelFont(axisFont);
		ra.setLabelPaint(Color.BLACK);
		if(decimalpattern != null && !decimalpattern.equals("")){
			DecimalFormat df = new DecimalFormat(decimalpattern); 
			ra.setNumberFormatOverride(df);
		}
		
		DateAxis dateaxis = new DateAxis(domainName);
		dateaxis.setLabelFont(axisFont);
		dateaxis.setDateFormatOverride(new SimpleDateFormat(pattern));
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setAutoRange(true);
        plot.setDomainAxis(dateaxis);
		
		TextTitle textTitle = chart.getTitle();
	    textTitle.setFont(titleFont);
	    
	    LegendTitle legend = chart.getLegend();
		legend.setItemFont(legendFont);
		legend.setVisible(displayLegend);
		legend.setBorder(0, 0, 0, 0);
		legend.setItemPaint(Color.BLACK);
	    
	    String filename = "";
		try{
			filename = ServletUtilities.saveChartAsJPEG(chart, width, height, session);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return filename;
	}
	
	
	/**
	 * 生成X轴为时间的AreaChart
	 * 
	 * @param session HttpSession
	 * @param title 图标题
	 * @param data 数据 List<Map>
	 * @param legendName 图例名称
	 * @param domainName X轴标题
	 * @param domainValue Map中X轴的Key
	 * @param rangeName Y轴标题
	 * @param rangeValue Map中Y轴的Key
	 * @param displayLegend 是否显示图例
	 * @param pattern X轴日期格式
	 * @param decimalpattern Y轴数字格式
	 * @param width 宽
	 * @param height 高
	 */
	public static String createAreaChart(HttpSession session, String title, List<Map> data,String[] legendName,
			String domainName, String domainValue, String rangeName, String[] rangeValue,
			boolean displayLegend, String pattern,String decimalpattern, int width, int height){
		
		int seriescount = rangeValue.length;
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
        
		try{
			for(int i = 0 ; i < seriescount ; i ++){
				TimeSeries s = new TimeSeries(legendName[i]);
				for(Map m : data){
					try{
						s.add(new Minute((Date)m.get(domainValue)), (Double)m.get(rangeValue[i]));
					}catch(Exception e){
						log.warn(e.getMessage());
					}
				}
				dataset.addSeries(s);
			}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		JFreeChart chart = ChartFactory.createXYAreaChart(
			title, domainName, rangeName, dataset, 
			PlotOrientation.VERTICAL, true, true, true
		);
		
		chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundAlpha(1F);
		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(0.8F);
		plot.setRangeMinorGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.GRAY);
		
		NumberAxis ra = (NumberAxis)plot.getRangeAxis();
		ra.setLabelFont(axisFont);
		ra.setLabelPaint(Color.BLACK);
		if(decimalpattern != null && !decimalpattern.equals("")){
			DecimalFormat df = new DecimalFormat("0 M"); 
			ra.setNumberFormatOverride(df);
		}
		
		DateAxis dateaxis = new DateAxis(domainName);
		dateaxis.setLabelFont(axisFont);
		dateaxis.setDateFormatOverride(new SimpleDateFormat(pattern));
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setAutoRange(true);
        plot.setDomainAxis(dateaxis);
        
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(axisFont);
		legend.setVisible(displayLegend);
		
		TextTitle textTitle = chart.getTitle();
	    textTitle.setFont(titleFont);
		
		XYItemRenderer render = plot.getRenderer();
		render.setSeriesPaint(0, new Color(0,220,0));
		render.setSeriesPaint(1, new Color(220,0,0));
		
		String filename = "";
		try{
			filename = ServletUtilities.saveChartAsJPEG(chart, width, height, session);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return filename;
		
	}

}
