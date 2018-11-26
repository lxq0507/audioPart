//package audioQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame; 


public class WaveFileReadDemo {  
  
    /** 
     * @param args 
     */  
	static final int WINDOW = 1024 * 8;	//window size for STFT
	//static final int PRATE = 30;		//rate to select the peak point in coordinate of time and frequency
	static final int FRETARGET = 100000;	//frequency of target zone
	static final double TIMETARGET = 0.3;	//time of target zone
	static final int LMSIZE = 2;		//size of landmarks per window
	
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        String filename = args[0];
        
        HashMap<Integer, Integer> fps = new HashMap<>();
        double[][] stanDevs = new double[8][500];
        //HashMap<Integer, Integer> midDiscrete = new HashMap<>();
        setTable(fps, "flowers.wav", 0, stanDevs);
        
        //System.out.println(fps);
        
        setTable(fps, "interview.wav", 1, stanDevs);
        setTable(fps, "movie.wav", 2, stanDevs);
        setTable(fps, "musicvideo.wav", 3, stanDevs);
        setTable(fps, "sports.wav", 4, stanDevs);
        setTable(fps, "StarCraft.wav", 5, stanDevs);
        setTable(fps, "traffic.wav", 6, stanDevs);
        
        //System.out.println(Arrays.toString(stanDevs[0]));
        
        
               
        int[] count = {0,0,0,0,0,0,0};
        
        WaveFileReader reader = new WaveFileReader(filename);       
        
        long sampleRate = reader.getSampleRate();
        
        if(reader.isSuccess()){  
            int[] data = reader.getData()[0]; //get first channel
            
            LinkedList<Landmark> landmarks = new LinkedList<Landmark>();
            int countLM = 0;
            
            //get landmarks
            for(int i = 0; i < data.length - WINDOW; i = i+WINDOW){
            	Complex[] x = new Complex[WINDOW];
            	for(int j = 0; j < WINDOW; j++){
            		x[j] = new Complex(data[i+j], 0);
            	}
            	Complex[] y = fft(x);
            	
            	double avg = (double)0;
            	double sum = (double)0;
            	int count1 = 0;
            	for(int j = 0; j < WINDOW; j++){
            		if(y[j].re() > 0){
            			sum += y[j].re();
            			count1++;
            		}
            	}
            	avg = sum/count1;
            	
            	//sort frequency
            	int[] lmNumber = new int[LMSIZE];
            	for(int k = 0; k < LMSIZE; k++){
            		int maxNUmber = 0;
            		for(int j = 0; j < WINDOW; j++){
            			if(y[j].re() > y[maxNUmber].re()){
            				maxNUmber = j;
            			}
            		}
            		lmNumber[k] = maxNUmber;
            		y[maxNUmber] = new Complex(0, 0);
            	}
            	for(int k = 0; k < LMSIZE; k++){
            		Landmark lm = new Landmark((double)sampleRate / WINDOW * lmNumber[k], (double)i/sampleRate);//landmark contains time and frequency 
        			landmarks.add(lm);
        			countLM++;
            	}
            	
                double stanDev = (double)0;
            	for(int j = 0; j < WINDOW; j++){
            		if(y[j].re() > 0){
            			stanDev += (y[j].re() - avg) * (y[j].re() - avg);   
            		}
            	}
            	stanDev = stanDev/WINDOW;
            	stanDev = Math.sqrt(stanDev);
            	stanDevs[7][i/WINDOW] = stanDev;
            	
            }
            
            //System.out.println("Test's countLM: " + countLM);
    
            //get fingerprint
            int countFP = 0;
            java.util.Iterator<Landmark> itOut = landmarks.iterator();
            

            while(itOut.hasNext()){
            	java.util.Iterator<Landmark> itIn = landmarks.iterator();
            	Landmark lm = itOut.next();
            	
            	while(itIn.hasNext()){
            		Landmark lmIn = itIn.next();
            		double difFreq = lmIn.freq() - lm.freq();
            		double difTime = lmIn.time() - lm.time();
            		if(difFreq > (0 - FRETARGET) && difFreq < FRETARGET && difTime > TIMETARGET && difTime < 2 * TIMETARGET){
            			int fingerPrint = ((int)difFreq) * 10 + (int)((difTime - 1) * 10);
            			
            			
            			if(fps.containsKey(fingerPrint)){
            				countFP++;
                			
	            			int id = fps.get(fingerPrint);
	            			//System.out.println(id&1);
	            			
	            			//7 bits represent 7 files in database
	            			for(int i = 0; i < 7; i++){
	            				if((id & (1 << i)) != 0){
	            					count[i]++;
	            				}
	            			}
	            			
            			}
            			break;
            		}
            	}
            	
            }
            
            
            
            
            
            JFrame mainF = new JFrame("Main Window");
            mainF.setSize(500, 500);  
            mainF.setLocation(500, 300);  
            mainF.setBackground(null);  
            mainF.setLayout(null);
            //mainF.setVisible(true);
            
              
            Button button = new Button("点击我");  
            button.setSize(50, 25);  
            //button.setBorderPainted(false);  
            button.setLocation(50, 50);  
            button.addActionListener(new ActionListener(){  
                //单击按钮执行的方法  
                public void actionPerformed(ActionEvent e) {  
                    //closeThis();  
                    //创建新的窗口  
                	JFrame frame=new JFrame("Java数据统计图");  
                    frame.setLayout(new GridLayout(2,2,10,10));  
                    
                    TimeSeriesChart tsc = new TimeSeriesChart();
                    tsc.setdata(stanDevs);
                    frame.add(tsc.frame1);    //添加折线图  
                    frame.setBounds(50, 50, 3000, 600);  
                    frame.setVisible(true); 
                }  
                  
            });
            
            mainF.add(button);
//            int max = 0;
//            for(int i = 0; i < 7; i++){
//            	if(count[i] > count[max]){
//            		max = i;
//            	}
//            }
//            
//            int total = count[max] * 10 / 9;
            
            
            System.out.println("number of matching fingerprint: ");
           	System.out.println("flowers.wav: " + count[0]);
           	System.out.println("interview.wav: " + count[1]);
           	System.out.println("movie.wav: " + count[2]);
           	System.out.println("musicvideo.wav: " + count[3]);
           	System.out.println("sports.wav: " + count[4]);
           	System.out.println("StarCraft.wav: " + count[5]);
           	System.out.println("traffic.wav: " + count[6]);          
            
        }  
        else{  
            System.err.println(filename + "File Error");  
        }  
    }  
    
    
    public static void setTable(HashMap<Integer, Integer> fps, String filename, int id, double[][] stanDevs){
    	
    	WaveFileReader reader = new WaveFileReader(filename);       
        
        long sampleRate = reader.getSampleRate();
        //System.out.println(filename + "'s sampleRate: " + sampleRate);
        
        if(reader.isSuccess()){  
            int[] data = reader.getData()[0]; //get first channel
            
            LinkedList<Landmark> landmarks = new LinkedList<Landmark>();
            int countLM = 0;
            
            //get landmarks window by window
            for(int i = 0; i < data.length - WINDOW; i = i+WINDOW){
            	Complex[] x = new Complex[WINDOW];
            	for(int j = 0; j < WINDOW; j++){
            		x[j] = new Complex(data[i+j], 0);
            	}
            	Complex[] y = fft(x);
            	
            	
            	//sort frequency
            	int[] lmNumber = new int[LMSIZE];
            	for(int k = 0; k < LMSIZE; k++){
            		int maxNUmber = 0;
            		for(int j = 0; j < WINDOW; j++){
            			if(y[j].re() > y[maxNUmber].re()){
            				maxNUmber = j;
            			}
            		}
            		lmNumber[k] = maxNUmber;
            		y[maxNUmber] = new Complex(0, 0);
            	}
            	for(int k = 0; k < LMSIZE; k++){
            		Landmark lm = new Landmark((double)sampleRate / WINDOW * lmNumber[k], (double)i/sampleRate);//landmark contains time and frequency 
        			landmarks.add(lm);
        			countLM++;
            	}
            	
            	
            	double avg = (double)0;
            	double sum = (double)0;
            	int count = 0;
            	for(int j = 0; j < WINDOW; j++){
            		if(y[j].re() > 0){
            			sum += y[j].re();
            			count++;
            		}
            	}
            	avg = sum/count;
            	
            	double stanDev = (double)0;
            	for(int j = 0; j < WINDOW; j++){
            		if(y[j].re() > 0){
            			stanDev += (y[j].re() - avg) * (y[j].re() - avg);   
            		}
            	}
            	stanDev = stanDev/WINDOW;
            	stanDev = Math.sqrt(stanDev);
            	stanDevs[id][i/WINDOW] = stanDev;
            	
//            	for(int j = 0; j < WINDOW; j++){
//            		if(y[j].re() > PRATE * avg){
//            			Landmark lm = new Landmark((double)sampleRate / WINDOW * j, (double)i/sampleRate);//landmark contains time and frequency 
//            			landmarks.add(lm);
//            			countLM++;
//            		}
//            	}
            	
            	
            }
            
            //System.out.println(filename + "'s landmark number: " + countLM);

            
            //get fingerprint with landmarks
            java.util.Iterator<Landmark> itOut = landmarks.iterator();
            int countFP = 0;
            
            //iterate landmarks
            
            while(itOut.hasNext()){
            	java.util.Iterator<Landmark> itIn = landmarks.iterator();
            	Landmark lm = itOut.next();
            	
            	//for each landmark, try to find a target zone
            	while(itIn.hasNext()){
            		Landmark lmIn = itIn.next();
            		double difFreq = lmIn.freq() - lm.freq();
            		double difTime = lmIn.time() - lm.time();
            		
            		//frequency and time limitation of target zone
            		if(difFreq > (0 - FRETARGET) && difFreq < FRETARGET && difTime > TIMETARGET && difTime < 2 * TIMETARGET){           			
            			int fingerPrint = ((int)difFreq) * 10 + (int)((difTime - 1) * 10);//fingerprint composed of frequency and time
            			countFP++;
            			
            			
            			//insert fingerprint into hash table
            			if(fps.containsKey(fingerPrint)){
            				if((fps.get(fingerPrint)&(1 << id)) == 0){
            					fps.put(fingerPrint, fps.get(fingerPrint) + (1 << id));
            				}
            			}else{            				
            				fps.put(fingerPrint, 1 << id);
            			}
            			break;
            		}
            	}
            }  
//            if(id == 0){
//            	System.out.println("Flower's FP: " + Arrays.toString(flowerFP));
//            }
            //System.out.println("countFP: " + countFP);
        }
            
        else{  
            System.err.println(filename + "File Error");  
        }
    }
    
    
 // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + n/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
    
} 