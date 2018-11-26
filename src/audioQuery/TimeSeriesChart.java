//package audioQuery;


  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.DefaultCategoryDataset;

  
public class TimeSeriesChart {  
    ChartPanel frame1; 
    JFreeChart chart;
    double[][] data;
    DefaultCategoryDataset dateset;
    
    public void setdata(double[][] stanDevs){
    	//data = new double[7][25];
    	for(int i = 0; i < stanDevs.length; i++){
    		for(int j = 0; j < stanDevs[0].length; j++){
    			data[i][j] = stanDevs[i][j];
    			//System.out.println("i,j: " + stanDevs[i][j]);
    		}
    	}
    	
    	for(int i = 0; i < stanDevs.length; i++){
    		for(int j = 0; j < stanDevs[0].length; j++){
    			
    			dateset.setValue(data[i][j], Integer.toString(i), Integer.toString(j));
    		}
    	}
    		
    	
    	JFreeChart chart=ChartFactory.createLineChart(
    			"test",  //图表标题
    			"month",  //X轴lable
    			"sales",  //Y轴lable
    			dateset, //数据集
    			PlotOrientation.VERTICAL,
    			//图表放置模式水平/垂直 
    			true, //显示lable
    			false,  //显示提示
    			false //显示urls
    			);
    	frame1=new ChartPanel(chart,true);
    }
    
    public TimeSeriesChart(){  
    	dateset = new DefaultCategoryDataset();;
    	data = new double[8][50];
    }

}