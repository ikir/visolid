package lidardataplot11;
public class Transform11 {
	double [][] indata,indataD,indataN,data;
	int wnd;
	double kf;
	int k;
	double [] H;
	double [] Q;
	public double[][] smooth_r(double [][] indata, int wnd) {
		this.indata = indata;
		this.wnd = wnd;
        System.out.println(indata[0].length +" - 0 ind;  total:"+ indata.length);
		double [][]data = new double [indata.length][indata[0].length];
		int c = 0;
		int r=0;
		int z = 0,k1 = 0,k2 = 0;
		for (r=0; r<indata[0].length; r++) {
			for (c=0; c<indata.length; c++) {
				double sumdata=0;
				int di = (wnd-1)/2;
				if ((r>di)&&(r+di<indata[0].length)) {
					for (int i=-di; i<=di; i++) {
						sumdata = sumdata + indata[c][r+i];
					}
					data[c][r] = sumdata/wnd;
				}
				else {
					if (r<=di) {
						k1=0;
				        k2=wnd-(di-r);
				        z=k2;
					}
					if (r+di>=indata[0].length) {
						k1=di+r-wnd;
				        k2=indata[0].length-1;
				        z=k2-k1;
					}
					for (int i=k1; i<=k2; i++) {
						sumdata = sumdata + indata[c][i];
					}
					data[c][r] = sumdata/z;
				}
			}
		}
		return data;
	}
	
	public double[][] smooth_c(double [][] indata, int wnd) {
		this.indata = indata;
		this.wnd = wnd;
        System.out.println(indata[0].length +" - 0 ind;  total:"+ indata.length);
		double [][]data = new double [indata.length][indata[0].length];
		int c = 0;
		int r=0;
		int z = 0,k1 = 0,k2 = 0;
		for (c=0; c<indata.length; c++) {
			for (r=0; r<indata[0].length; r++) {
				double sumdata=0;
				int di = (wnd-1)/2;
				if ((c>di)&&(c+di<indata.length)) {
					for (int i=-di; i<=di; i++) {
						sumdata = sumdata + indata[c+i][r];
					}
					data[c][r] = sumdata/wnd;
				}
				else {
					if (c<=di) {
						k1=0;
				        k2=wnd-(di-c);
				        z=k2;
					}
					if (c+di>=indata.length) {
						k1=di+c-wnd;
				        k2=indata.length-1;
				        z=k2-k1;
					}
					for (int i=k1; i<=k2; i++) {
						sumdata = sumdata + indata[i][r];
					}
					data[c][r] = sumdata/z;
				}
			}
		}
		return data;
	}
	
	public double[][] noise(double [][] indataD, double [][] indataN) {
		this.indataD = indataD;
		this.indataN = indataN;
		double [][]data = new double [indataD.length][indataD[0].length];
		System.out.println("r "+indataD[0].length +" c " +indataD.length);
		for (int r=0; r<indataD[0].length; r++) {
			for (int c=0; c<indataD.length; c++) {
				data[c][r] = indataD[c][r] - indataN[c][r];
				//System.out.print(data[c][r]+" ");
			}
			//System.out.println();
		}
		return data;
	}
	
	public double[][] kf(double [][] data, double kf) {
		this.data = data;
		this.kf = kf;
		for (int r=0; r<data[0].length; r++) {
			for (int c=0; c<data.length; c++) {
				data[c][r] = data[c][r] - kf/10;
				//System.out.print(data[c][r]+" ");
			}
			//System.out.println();
		}
		return data;
	}
	
	public double[][] H(double [][] data, double [] H) {
		this.data = data;
		this.H = H;
		if (H!=null)
			for (int r=0; r<data[0].length; r++) {
				for (int c=0; c<data.length; c++) {
					data[c][r] = data[c][r]*H[r]/10000;
					//System.out.print(data[c][r]+" ");
				}
			}
		return data;
	}
	
	public double[][] Q(double [][] data, double [] Q) {
		this.data = data;
		this.Q = Q;
			for (int r=0; r<data[0].length; r++) {
				for (int c=0; c<data.length; c++) {
					data[c][r] = data[c][r]*9000/Q[c];
					//System.out.print(data[c][r]+" ");
				}
			}
		return data;
	}
	
	public double[][] min(double [][] indata, int k) {
		this.indata = indata;
		this.k = k;
		double [][]data = new double [indata.length][indata[0].length];
		for (int r=0; r<indata[0].length; r++) {
			for (int c=0; c<indata.length; c++) {
				data[c][r] = indata[c][r] - k;
			}
		} 
		return data;
	}
	public double[] minmax(double [][] indata) {
		this.indata = indata;
		double max = 0;
		double min = 100000;
		for (int r=0; r<indata[0].length; r++) {
			for (int c=0; c<indata.length; c++) {
				if (indata[c][r]>max) max = indata[c][r];
				if (indata[c][r]<min) min = indata[c][r];

        	}
        }
		double []minmax = {min,max};
		return minmax;
	}
	
	public double[][] addblock(double [][] indata) {
		this.indata = indata;
		double [][]data = new double [indata.length*2-1][indata[0].length];
		System.out.println("nL "+(indata.length*2-1)+" rr "+indata[0].length);
		for (int r=0; r<indata[0].length; r++) {
			for (int c=0; c<indata.length*2-2; c=c+2) {
				if (c>indata.length*2-2) break;
				data[c][r] = indata[(c/2)+1][r];
				data[c+1][r] = (indata[(c/2)][r]+indata[(c/2)+1][r])/2;
				System.out.println("r "+r +" c " +c);
			}
		} 
		return data;
	}
	
}
