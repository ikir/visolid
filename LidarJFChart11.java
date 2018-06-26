package lidardataplot11;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.title.*;
import org.jfree.ui.*;

public class LidarJFChart11 {
    int wnd = 1;
    double k = 0;
    int width = 900;
    int height = 700;
    int r,c,i,nc;
    double [][] data,dataD,dataN;
    double [] H = null;
    String [] Hlabel;
    String [] Tlabel;
    double [] Col1;
    double [] Q;
    double min;
    double max;
    String [][] sdata;
    JFreeChart chart;
    ChartPanel chartPanel;
    JFrame frame = new JFrame("LIDAR data");
    JButton but_d = new JButton("Data");
    JButton but_n = new JButton("Noise");
    JRadioButton rb = new JRadioButton("H");
    JComboBox<Integer> cb = new JComboBox<Integer>();
    
    JPanel pan_instr = new JPanel();
    Fileload fileload = new Fileload();
	JSlider js_k = new JSlider(JSlider.HORIZONTAL,0,50,0);

    public LidarJFChart11(String title) {
    	js_k.setName("k");
		js_k.setPaintTicks(true);
        but_d.addActionListener(fileload);
        but_n.addActionListener(fileload);
		rb.setSelected(false);
        frame.add(pan_instr,BorderLayout.NORTH);
    	pan_instr.add(but_d);
    	pan_instr.add(but_n);
    	pan_instr.add(rb);
    	cb.addItem(3);
        cb.addItem(5);
        cb.addItem(7);
    	pan_instr.add(cb);
    	pan_instr.add(js_k);
		js_k.addChangeListener(new MyListener());
		rb.addChangeListener(new MyListener());
		cb.addItemListener(CBListener);
        frame.setSize(width,height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public JFreeChart createXYZChart(double [][]data) {
    	
        NumberAxis xAxis = new NumberAxis("time");
        NumberAxis yAxis = new NumberAxis("height");
		Transform11 tr = new Transform11();
		data = tr.noise(dataD,dataN);
		data = tr.Q(data,Q); //нормировка по количеству выстрелов
		data = tr.kf(data,k);
        data = tr.smooth_r(data, wnd); //сглаживание по высоте
		data = tr.H(data,H);		//нормировка по высоте
        data = tr.smooth_r(data, wnd); //сглаживание по высоте

		/*data = tr.addblock(data); //добавление блоков по горизонтали
        data = tr.addblock(data); //добавление блоков по горизонтали
        data = tr.smooth_c(data, wnd); //сглаживание по вр.интервалам
*/        
        double [] minmax = tr.minmax(data);
        min = minmax[0];
        max = minmax[1];
        //data  = tr.min(data,(int)min); // вычитание мин.компонента
        System.out.println("minValue ="+min);
        System.out.println("maxValue ="+max);      
        XYZDataset xyzset = new XYZArrayDataset(data);
        XYBlockRenderer renderer = new XYBlockRenderer();
        
        PaintScale paintScale = new HeatPaintScale();
        renderer.setPaintScale(paintScale);
        
//        TickUnitSource units = NumberAxis.createIntegerTickUnits();
//        xAxis.setStandardTickUnits(units);
//        xAxis.setRange(0,17);
        
        
        XYPlot plot = new XYPlot(xyzset, xAxis, yAxis, renderer);
        renderer.setBlockHeight(1.0f);
        renderer.setBlockWidth(1.0f);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart("Contour Plot",JFreeChart.DEFAULT_TITLE_FONT,plot,false);
        
        SymbolAxis yl = new SymbolAxis("UTC", Tlabel);
        SymbolAxis xl = new SymbolAxis("Height, km", Hlabel);
        ((XYPlot) chart.getPlot()).setDomainAxis(yl);
        ((XYPlot) chart.getPlot()).setRangeAxis(xl);

        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setUpperBound(100);
        scaleAxis.setLabel("Signal");
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.black);
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        PaintScaleLegend legend = new PaintScaleLegend(paintScale, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        legend.setPadding(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(50);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }
   private class XYZArrayDataset extends AbstractXYZDataset{
      double[][] data;
      int rowCount;
      int columnCount;
      XYZArrayDataset(double[][] data){
         this.data = data;
         rowCount = data.length;
         columnCount = data[0].length;
      }
      public int getSeriesCount(){
         return 1;
      }
      public Comparable getSeriesKey(int series){
         return "serie";
      }
      public int getItemCount(int series){
         return rowCount*columnCount;
      }
      public double getXValue(int series,int item){
         return (int)(item/columnCount);
      }
      public double getYValue(int series,int item){
         return item % columnCount;
      }
      public double getZValue(int series,int item){
         return data[(int)(item/columnCount)][item % columnCount];
      }
      public Number getX(int series,int item){
         return new Double((int)(item/columnCount));
      }
      public Number getY(int series,int item){
         return new Double(item % columnCount);
      }
      public Number getZ(int series,int item){
         return new Double(data[(int)(item/columnCount)][item % columnCount]);
      }
   }
    public static void main(String[] args) {
        LidarJFChart11 Lidar = new LidarJFChart11("Lidar Data");
    }
    
    class MyListener implements ChangeListener {
    	public void stateChanged(ChangeEvent e) {
    		if (e.getSource() instanceof JRadioButton) {
    			JRadioButton rb = (JRadioButton)e.getSource();
		        if (!rb.isSelected()) H = null;
		        else H = Col1;
    		} else {
    			JSlider src = (JSlider) e.getSource();
	    			int k1 = (int)src.getValue();
	                if(Math.abs(k1-k) < 1)
	                	return;
	                k = k1;
	            System.out.println("mylistener "+k+"; "+k/10);
    		}
    		if(chartPanel != null)
            	frame.remove(chartPanel);
            chart = createXYZChart(data);
            chartPanel = new ChartPanel(chart,width,height,16,16,width*10,height*10,true,true,true,true,true,true);
            frame.add(chartPanel,BorderLayout.CENTER);
            frame.setVisible(true);
    	}
    }
    
    ItemListener CBListener = new ItemListener() {
        public void itemStateChanged(ItemEvent itemEvent) {
        	if (itemEvent.getStateChange() == 1) {
			int it = cb.getSelectedIndex();
			wnd = (it+1)*2+1;
          System.out.println(wnd+", Position: " + cb.getSelectedIndex());
          if(chartPanel != null)
          	frame.remove(chartPanel);
          chart = createXYZChart(data);
          chartPanel = new ChartPanel(chart,width,height,16,16,width*10,height*10,true,true,true,true,true,true);
          frame.add(chartPanel,BorderLayout.CENTER);
          frame.setVisible(true);
        	}
        }
      };
    
    public abstract class PaintScaleAdapter implements PaintScale {
        public double getUpperBound() {
			return max;
        }
        public double getLowerBound() {
            return min;
        }
    }

    public class HeatPaintScale extends PaintScaleAdapter {
        public Paint getPaint(double value) {
            double normalizedValue = (value - min) / (max - min);
            double saturation = Math.max(0.7, Math.abs(2 * normalizedValue - 1));
            double red = 0;
            double blue = 0.7;
            double hue = blue - normalizedValue * (blue - red);
            return Color.getHSBColor((float) hue, (float) saturation, 1);
        }
    }

    public class Fileload implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		String line = "";
            String cvsSplitBy = "\t";
    		JButton but = (JButton) e.getSource();
    		System.out.println("Choosen:"+but.getText());
    		JFileChooser fc = new JFileChooser("/Users/Ilya/Documents/Scilab");	
    		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    		int returnVal = fc.showOpenDialog(null);
    		String fp = fc.getSelectedFile().getAbsolutePath();
    		if(returnVal == JFileChooser.APPROVE_OPTION) {
    			System.out.println("File: " + fp);
    		}
    		
    		try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
	          	r = 0;
	          	sdata = new String[40][412];
	              for (r=0;r<412;r++) {
	            	  	  line = br.readLine();
	                  String[] country = line.split(cvsSplitBy);
	                  //System.out.println("cl "+country.length);
	                  for (c=0; c<country.length-1; c++) {
	                	  		sdata[c][r] = country[c];
	                	  		System.out.print(sdata[c][r]+"  ");

	                  }
	                  System.out.println();
	              }
	              nc = c;
	          } catch (IOException ex) {
	              ex.printStackTrace();
	          }
    		if(but.getText().equals("Data")) {
    			dataD = new double[nc-2][r-77];
    			dataN = new double[nc-2][r-77];
    			Col1 = new double[r-77];
    			Hlabel = new String[r-77];
    			Tlabel = new String[nc-2];
    			Q = new double[nc-2];
    			System.out.println("nc= "+nc);
    			for (int c=0; c<nc-2; c++) {
    				Q[c] = Double.parseDouble(sdata[c+2][0]);
    				if (sdata[c+2][9].substring(15, 16).equals(":"))
    					Tlabel[c] = sdata[c+2][9].substring(11, 15);
        				else Tlabel[c] = sdata[c+2][9].substring(11, 16);
	                System.out.println(c+ "; Q= "+Q[c]);
              	}
    			try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
    				for (r=0;r<sdata[0].length-77;r++) {
	                  for (int c=0; c<nc-2; c++) {
	                	  	//System.out.println("sdata "+sdata[c][r]);
	                	  	dataD[c][r] = Double.parseDouble(sdata[c+2][r+77]);
    	              	}
    	                Col1[r] = Double.parseDouble(sdata[0][r+77]);
    	                Hlabel[r] = Double.toString(Col1[r]);
    	                System.out.println(r+ "; H= "+Col1[r]);
    	              }
    	          } catch (IOException ex) {
    	              ex.printStackTrace();
    	          }
    		}
    		if(but.getText().equals("Noise")) {
    			try (BufferedReader br = new BufferedReader(new FileReader(fp))) {
    				System.out.println("L "+nc);
    				for (r=0;r<sdata[0].length-77;r++) {
	                  for (int c=0; c<nc-2; c++) {
	                	  	dataN[c][r] = Double.parseDouble(sdata[c+2][r+77]);
    	              	}
    	              }
    	          } catch (IOException ex) {
    	              ex.printStackTrace();
    	          }
    		}
            chart = createXYZChart(data);
            chartPanel = new ChartPanel(chart,width,height,16,16,width*10,height*10,true,true,true,true,true,true);
            frame.add(chartPanel,BorderLayout.CENTER);
            frame.setVisible(true);

    	}
    }
}

