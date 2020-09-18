/**
 *
 * @author aksagar
 */
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.process.*;
import ij.measure.*;
import java.awt.*;
import java.io.IOException;
import ij.util.Tools;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.io.*;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.units.quantity.Time;
import ome.units.UNITS;

public class PhasorOperation {


	//variables
	int imageH;
	int imageW;
	int imageD;
	int imageZ;
	int imageF;
	int numCycles;
	int imagesPerCycle;
	double PixelThresholdCutOff;
	double Chi2CutOff;
	double harmonic;
	double Omega;
	double[] timeData;
	double[] timeData3;
	double[][][] GmData;
	double[][][] GsData;
	double[][][] rateDataFromPhasor;
	double[][][] AZeroData;
	double[][][] Chi2G;
	double[][][] offsetDataG;
	double[][][] a1DataG;
	double[][][] k1DataG;
	double[][][] a2DataG;
	double[][][] k2DataG;
	double[][][] a3DataG;
	double[][][] k3DataG;
	double[][][] a4DataG;
	double[][][] k4DataG;
	double[][][] a5DataG;
	double[][][] k5DataG;

	//temp values OPT
	boolean usePhasortoInitialize; 
	boolean isPhasorFitDone;
	double[][][] Chi2G_t;
	double[][][] offsetDataG_t;
	double[][][] a1DataG_t;
	double[][][] k1DataG_t;
	double[][][] a2DataG_t;
	double[][][] k2DataG_t;
	double[][][] a3DataG_t;
	double[][][] k3DataG_t;
	double[][][] a4DataG_t;
	double[][][] k4DataG_t;
	double[][][] a5DataG_t;
	double[][][] k5DataG_t;
	String id;

	//need to copy these variables from the calling function 
	ImagePlus img;


	//
	int cameraOffset;
	int binFactor;
	int maxiteration;

	boolean useChA;
	boolean useChB;
	boolean useChC;
	boolean useChD;
	boolean useChE;

	//variable for image creation
	double[][][] arrayChA;
	double[][][] arrayChB;
	double[][][] arrayChC;
	double[][][] arrayChD;
	double[][][] arrayChE;


	//phasor data
	double[][][] arrayChA_p;
	double[][][] arrayChB_p;
	double[][][] arrayChC_p;
	double[][][] arrayChD_p;
	double[][][] arrayChE_p;

	double chA_Kmean;
	double chB_Kmean;
	double chC_Kmean;
	double chD_Kmean;
	double chE_Kmean;

	String chA_name;
	String chB_name;
	String chC_name;
	String chD_name;
	String chE_name;



	double[] arrayGToSend;
	double[] arraySToSend;
	double[] fracContribArray;

	double[][][] pixelSumData;
	boolean backgroundSubtract;

	double[] backGroundValues;
	double[] bkGrd;
	int threshold;
	double terminalThreshold;
	boolean useTOneHalfEstimate;
	
	boolean medianFilter;
	int applicationsMedianFilter;


	//gui element need to make sure they are independent element
	Plot phasorPlot;

    
    public void setID(String ID) {
    	this.id=ID;
    }
    
    
    public void setImagePlus(ImagePlus im) {
    	this.img=im;
    }
    

    public void setUseChannelValues(boolean chA,boolean chB,boolean chC,boolean chD,boolean chE ) {
    	this.useChA=chA;
    	this.useChB=chB;
    	this.useChC=chC;
    	this.useChD=chD;
    	this.useChE=chE;
    	
    }
    
    public void initCycleNums(int numcycles, int imagecycles) {
    	this.numCycles=numcycles;
    	this.imagesPerCycle=imagecycles;
    }
    
    public void setPixelThreshold(double pixThreshold) {
    	this.PixelThresholdCutOff=pixThreshold;
    }
    
    public void setChi2CutOff(double Chi2CutOff) {
    	this.Chi2CutOff=Chi2CutOff;
    }
    
    public void setHarmonicOmega(double harmonic, double Omega) {

    	this.harmonic=harmonic;
    	this.Omega=Omega;
    	
    }
    
    public void setCameraOffset(int cameraOffset) {
    	this.cameraOffset=cameraOffset;
    }
    
    public void setBinFactor(int binFactor) {
    	this.binFactor=binFactor;
    }
    
    public void copyStringNamesFile(String chA_name, String chB_name, String chC_name, String chD_name, String chE_name ) {
    	this.chA_name=chA_name;
    	this.chB_name=chB_name;
    	this.chC_name=chC_name;
    	this.chD_name=chD_name;
    	this.chE_name=chE_name;
    	
    }
    
    public void setToneHalfEstimate(boolean useTOneHalfEstimate) {
    	this.useTOneHalfEstimate=useTOneHalfEstimate;
    }
    
    public void copyArrayGTsend(double[] arrayGToSend, double[] arraySToSend) {
    	this.arrayGToSend=new double[5];
    	this.arraySToSend=new double[5];
    	for(int i=0;i<arraySToSend.length;i++) {
    		this.arrayGToSend[i]=arrayGToSend[i];
    		this.arraySToSend[i]=arraySToSend[i];
    	}
    	
    }
    
    public void setThreshold(int threshold, double terminalThreshold) {
    	this.threshold=threshold;
    	this.terminalThreshold=terminalThreshold;
    }
    
    
    public void setMedianFilter(boolean medianFilter, int applicationsMedianFilter) {
    	this.medianFilter=medianFilter;
    	this.applicationsMedianFilter=applicationsMedianFilter;

    }
	public void RunPhasorPlotStack() throws Exception {
		
		IJ.resetMinAndMax(img);
		//in case the image is opened without using the plugin BioFormats button
		String dir0 = IJ.getDirectory("image");
		String stackToOpen = img.getTitle();
		String id2 = dir0 + stackToOpen;
		String fExt = id2.substring(id2.indexOf("."), id2.length());
		if (fExt.contains(" ") && fExt.indexOf(" ") < id2.length()) {
			fExt = fExt.substring(0, fExt.indexOf(" "));
		}
		id = id2.substring(0, id2.indexOf(".")) + fExt;

		final ImageStack img2 = img.getStack();
		imageH = img2.getHeight();
		imageW = img2.getWidth();
		imageD = img2.getBitDepth();
		imageZ = img2.getSize();
		final int currentchannel = img.getC() - 1;
		final int currentZ = img.getZ() - 1;
		final int nSlices = img.getNSlices();
		int size = img.getNFrames();
		if (size == 1)//in case the stack is read as a Z stack instead of T stack
		{
			size = nSlices;
		}
		if (numCycles * imagesPerCycle > size) {
			IJ.showMessage("Phasor Plotter", "The number of cycles multiplied by the number images per cycle is larger than the stack");
			return;
		}
		try {
			timeData3 = getTimingPerPlane(id, size, currentZ, currentchannel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Chi2G = new double[imageW][imageH][numCycles];
		GmData = new double[imageW][imageH][numCycles];
		GsData = new double[imageW][imageH][numCycles];
		rateDataFromPhasor = new double[imageW][imageH][numCycles];
		AZeroData = new double[imageW][imageH][numCycles];
		pixelSumData = new double[imageW][imageH][numCycles];

		double freq = 1/(timeData3[imagesPerCycle-1] - timeData3[0]);        
		Omega = 2*Math.PI*freq*harmonic;

		for (int cycle = 0; cycle < numCycles; cycle++) {
			final long startTime = System.currentTimeMillis();
			double[] timeData2 = new double[imagesPerCycle];
			bkGrd = new double[imagesPerCycle];
			for (int k = 0; k < imagesPerCycle; k++) {
				timeData2[k] = (timeData3[k + (cycle * imagesPerCycle)] - timeData3[cycle * imagesPerCycle]);
				if(backgroundSubtract)
					bkGrd[k] = (backGroundValues[k + (cycle * imagesPerCycle)]);
			}
			timeData = timeData2;

			final Thread[] threads = newThreadArray();
			//IJ.log("number of threads= " + threads.length);
			final double[][] timeDataArrayOfArrays = new double[threads.length][timeData.length];
			final double[][] pixelsArrayOfArrays = new double[threads.length][timeData.length];
			final double[][] backGroundArrayOfArrays = new double[threads.length][timeData.length];

			for (int ithread = 0; ithread < threads.length; ithread++) {
				final int threadIndex = ithread;
				final int cycleNum = cycle;
				final int height = imageH;
				final int width = imageW;
				// Concurrently run in as many threads as CPUs  
				threads[ithread] = new Thread() {
					{
						setPriority(Thread.NORM_PRIORITY);
					}

					@Override
					public void run() {
						for (int y = threadIndex * height / threads.length; y < (threadIndex + 1) * height / threads.length; y++) {
							if (threadIndex == threads.length - 1) {
								int startY = threadIndex * height / threads.length;
								int endY = (threadIndex + 1) * height / threads.length;
								int progress = (int) Math.round(((double) (y - startY) / (double)(endY - startY)) * 100.0);
								//statusMessageArea.setText("Phasor progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
								// statusMessageArea.update(statusMessageArea.getGraphics());
								//IJ.showStatus("Phasor progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");
							}
							for (int x = 0; x < width; x++) {
								for (int z = 0; z < timeData.length; z++) {
									timeDataArrayOfArrays[threadIndex][z] = timeData[z];
									if(backgroundSubtract)
										backGroundArrayOfArrays[threadIndex][z] = bkGrd[z];
									if (img.isHyperStack()) {
										int z2 = img.getStackIndex(img.getC(), img.getZ(), (cycleNum * imagesPerCycle) + z + 1) - 1;
										pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2) - (cameraOffset*binFactor*binFactor);
									} else {
										pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, (cycleNum * imagesPerCycle) + z) - (cameraOffset*binFactor*binFactor);
									}
								}
								if(pixelsArrayOfArrays[threadIndex][0]>=threshold){
									double [] gmArray = new double [2];
									double [] gsArray = new double [2];
									double [] GArray = new double [3];
									double Gm = 0;
									double Gs = 0;
									if (backgroundSubtract) {
										//the calculate CoorectedGmGs method subtracts a measured background
										GArray = calculateCorrectedGmGs(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], backGroundArrayOfArrays[threadIndex], Omega);
										Gm = GArray[0];
										Gs = GArray[1];
										pixelSumData[x][y][cycleNum] = GArray[2];
										//GmData[x][y][cycleNum] = GArray[0];
										//GsData[x][y][cycleNum] = GArray[1];
										//IJ.log("GArray[0]="+GArray[0]+"     GArray[1]="+GArray[1]);
									} else {
										gmArray = calculateGm(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], backGroundArrayOfArrays[threadIndex], Omega);
										gsArray = calculateGs(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], backGroundArrayOfArrays[threadIndex], Omega);
										Gm = gmArray[0];
										Gs = gsArray[0];
										pixelSumData[x][y][cycleNum] = gmArray[1];
										//GmData[x][y][cycleNum] = calculateGm(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], backGroundArrayOfArrays[threadIndex], Omega);
										//GsData[x][y][cycleNum] = calculateGs(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], backGroundArrayOfArrays[threadIndex], Omega);
									}
									GmData[x][y][cycleNum] = Gm;
									GsData[x][y][cycleNum] = Gs;

									if (useTOneHalfEstimate) {
										//here a t1/2 is estimated and converted into a tau estimate
										//something like this is probably fine for single exponential data
										//but will likely fail for multi-exponential data
										double[] tOneHalfOffsetEst = findTOneHalfEstimate(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex]);
										double tOneHalfRateEst = Math.log(2) / tOneHalfOffsetEst[1];
										rateDataFromPhasor[x][y][cycleNum] = tOneHalfRateEst;
										double tau = 1 / tOneHalfRateEst;
										GmData[x][y][cycleNum] = 1 / (1 + (Omega * Omega * tau * tau));
										//GsData[x][y][cycleNum] = (Omega * tau) / (1 + (Omega * Omega * tau * tau));
										AZeroData[x][y][cycleNum] = pixelsArrayOfArrays[threadIndex][0];
										//pixelSumData[x][y][cycleNum] = getSumOfArray(pixelsArrayOfArrays[threadIndex]);
										//pixelSumData[x][y][cycleNum] = gmArray[1];
									} else {
										rateDataFromPhasor[x][y][cycleNum] = Omega * GmData[x][y][cycleNum] / GsData[x][y][cycleNum];
										AZeroData[x][y][cycleNum] = pixelsArrayOfArrays[threadIndex][0];
										//pixelSumData[x][y][cycleNum] = getSumOfArray(pixelsArrayOfArrays[threadIndex]);
										//pixelSumData[x][y][cycleNum] = gmArray[1];
									}
								} else {
									//if the pixel values are not above the threshold
									//fill arrays with NaN
									GmData[x][y][cycleNum] = Double.NaN;
									GsData[x][y][cycleNum] = Double.NaN;
									rateDataFromPhasor[x][y][cycleNum] = Double.NaN;
									AZeroData[x][y][cycleNum] = Double.NaN;
									pixelSumData[x][y][cycleNum] = Double.NaN;
								}
							}
						}
					}
				};
			}
			startAndJoin(threads);
			long timeToCompletion = System.currentTimeMillis() - startTime;    
			//statusMessageArea.setText("Phasor time: " + timeToCompletion);                                
			//statusMessageArea.update(statusMessageArea.getGraphics());
			//if (LogPhasorTime == true) {
			IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
			//}
			if (medianFilter) {
				for (int iter = 0; iter < applicationsMedianFilter; iter++) {
					GmData = medianFilterArray(GmData, 3);
					GsData = medianFilterArray(GsData, 3);
				}
				rateDataFromPhasor = updateRateDataArray(rateDataFromPhasor);
			}
		} //end of cycles 
		phasorPlot = plotPhasorPlot(GmData, GsData, imageW, imageH, numCycles);
		//phasorPlotWin = phasorPlot.show();
		ImagePlus phasorPlotStack = phasorPlot.getImagePlus();
		phasorPlotStack.show();

		//rateConstantImage = createImage("RateConstantsImage",rateDataFromPhasor);
		//GmDataImage = createImage("GmDataImage",GmData);
		//GsDataImage = createImage("GsDataImage",GsData);

	}

	


    public void unMixPixelValuesAndCreateImages(double[]arrayOfMeanGRef, double[]arrayOfMeanSRef) {
    	//unmixes the signals from the first image of the photoswitching cycle using the 
    	//fractional contributions determined from the phasor plots            
    	
    	
        
    	long startTime = System.currentTimeMillis();
    	//use ROI to define exclusively one channel or another
    	arrayChA =new double[imageW][imageH][numCycles];
    	arrayChB =new double[imageW][imageH][numCycles];
    	arrayChC =new double[imageW][imageH][numCycles];
    	arrayChD =new double[imageW][imageH][numCycles];
    	arrayChE =new double[imageW][imageH][numCycles];
    	for (int cyc = 0; cyc < numCycles; cyc++) {
    		for (int y = 0; y < imageH; y++) {
    			for (int x = 0; x < imageW; x++) {
    				arrayChA[x][y][cyc] = Double.NaN;
    				arrayChB[x][y][cyc] = Double.NaN;
    				arrayChC[x][y][cyc] = Double.NaN;
    				arrayChD[x][y][cyc] = Double.NaN;
    				arrayChE[x][y][cyc] = Double.NaN;
    			}
    		}
    	}


    	for (int cyc = 0; cyc < numCycles; cyc++) {
    		final Thread[] threads = newThreadArray();
    		for (int ithread = 0; ithread < threads.length; ithread++) {
    			final int threadIndex = ithread;
    			final int cycleNum = cyc;
    			final int height = imageH;
    			final int width = imageW;

    			double[] timeData = new double[imagesPerCycle];
    			for (int k = 0; k < imagesPerCycle; k++) {
    				timeData[k] = (timeData3[k + (cyc * imagesPerCycle)] - timeData3[cyc * imagesPerCycle]);
    			}

    			// Concurrently run in as many threads as CPUs  
    			threads[ithread] = new Thread() {
    				{
    					setPriority(Thread.NORM_PRIORITY);
    				}

    				@Override
    				public void run() {
    					for (int y = threadIndex * height / threads.length; y < (threadIndex + 1) * height / threads.length; y++) {
    						if (threadIndex == threads.length - 1) {
    							int startY = threadIndex * height / threads.length;
    							int endY = (threadIndex + 1) * height / threads.length;
    							int progress = (int) Math.round(((double) (y - startY) / (double)(endY - startY)) * 100.0);
    							//statusMessageArea.setText("Unmixing progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
    							//statusMessageArea.update(statusMessageArea.getGraphics());
    						}
    						for (int x = 0; x < width; x++) {
    							if (Double.isNaN(GmData[x][y][cycleNum]) || Double.isNaN(GsData[x][y][cycleNum])) {
    								arrayChA[x][y][cycleNum] = Double.NaN;
    								arrayChB[x][y][cycleNum] = Double.NaN;
    								arrayChC[x][y][cycleNum] = Double.NaN;
    								arrayChD[x][y][cycleNum] = Double.NaN;
    								arrayChE[x][y][cycleNum] = Double.NaN;
    							} else if (Double.isNaN(arrayChA[x][y][cycleNum]) && Double.isNaN(arrayChB[x][y][cycleNum]) && Double.isNaN(arrayChC[x][y][cycleNum]) && Double.isNaN(arrayChD[x][y][cycleNum]) && Double.isNaN(arrayChE[x][y][cycleNum])) {
    								double [] fracContribArray = new double [5];
    								fracContribArray = getFractionalContributions(arrayGToSend, arraySToSend, GmData[x][y][cycleNum], GsData[x][y][cycleNum]);
    								arrayChA[x][y][cycleNum] = fracContribArray[0] * pixelSumData[x][y][cycleNum];
    								arrayChB[x][y][cycleNum] = fracContribArray[1] * pixelSumData[x][y][cycleNum];
    								arrayChC[x][y][cycleNum] = fracContribArray[2] * pixelSumData[x][y][cycleNum];
    								arrayChD[x][y][cycleNum] = fracContribArray[3] * pixelSumData[x][y][cycleNum];
    								arrayChE[x][y][cycleNum] = fracContribArray[4] * pixelSumData[x][y][cycleNum];

    								//IJ.log("arrayChA[x][y][cycleNum]="+arrayChA[x][y][cycleNum]+"   fracContribArray[1]="+fracContribArray[0]+"    pixelSumData[x][y][cycleNum]="+pixelSumData[x][y][cycleNum]);
    								double ChALifetime = arraySToSend[0]/(Omega * arrayGToSend[0]);
    								double ChBLifetime = arraySToSend[1]/(Omega * arrayGToSend[1]);
    								double ChCLifetime = arraySToSend[2]/(Omega * arrayGToSend[2]);
    								double ChDLifetime = arraySToSend[3]/(Omega * arrayGToSend[3]);
    								double ChELifetime = arraySToSend[4]/(Omega * arrayGToSend[4]);

    								double theLifetime = GsData[x][y][cycleNum]/(Omega * GmData[x][y][cycleNum]);

    								double totalTime = timeData[1]-timeData[0];//exposure time

    								arrayChA[x][y][cycleNum] = (fracContribArray[0] * pixelSumData[x][y][cycleNum])/(ChALifetime*Math.exp(totalTime/theLifetime));
    								arrayChB[x][y][cycleNum] = (fracContribArray[1] * pixelSumData[x][y][cycleNum])/(ChBLifetime*Math.exp(totalTime/theLifetime));
    								arrayChC[x][y][cycleNum] = (fracContribArray[2] * pixelSumData[x][y][cycleNum])/(ChCLifetime*Math.exp(totalTime/theLifetime));
    								arrayChD[x][y][cycleNum] = (fracContribArray[3] * pixelSumData[x][y][cycleNum])/(ChDLifetime*Math.exp(totalTime/theLifetime));
    								arrayChE[x][y][cycleNum] = (fracContribArray[4] * pixelSumData[x][y][cycleNum])/(ChELifetime*Math.exp(totalTime/theLifetime));

    								//IJ.log("ChALifetime ="+ChALifetime+"   ChBLifetime ="+ChBLifetime+"   pixelSumData[x][y][cycleNum]="+pixelSumData[x][y][cycleNum]+"   fracContribArray[0]="+fracContribArray[0]+"   fracContribArray[1]="+fracContribArray[1]+"   arrayChA[x][y][cycleNum]="+arrayChA[x][y][cycleNum]+"   arrayChB[x][y][cycleNum]="+arrayChB[x][y][cycleNum]);

    							}
    						}//x
    					}//y
    				}
    			};
    		}
    		startAndJoin(threads);
    	}//cyc
    	isPhasorFitDone=true;// for hybrid fitting this needs to be true; 
    	long timeToCompletion = System.currentTimeMillis() - startTime;    
    	//statusMessageArea.setText("Unmixing time: " + timeToCompletion);                                
    	//statusMessageArea.update(statusMessageArea.getGraphics());

    	if(useChA)
    		createUnmixedImage(chA_name, arrayChA);
    	if(useChB)
    		createUnmixedImage(chB_name, arrayChB);
    	if(useChC)
    		createUnmixedImage(chC_name, arrayChC);
    	if(useChD)
    		createUnmixedImage(chD_name, arrayChD);
    	if(useChE)
    		createUnmixedImage(chE_name, arrayChE);
    } 
    
	private double[] getTimingPerPlane(String arg, int tPoints, int currZ, int currCh) throws Exception {
		//this uses Bioformats to get the timing of the images
		//from the start of the experiment
		//if the plane delta T is not found, user is prompted for a time interval input
		String fExt = arg.substring(arg.indexOf("."), arg.length());
		if (fExt.contains(" ") && fExt.indexOf(" ") < arg.length()) {
			fExt = fExt.substring(0, fExt.indexOf(" "));
		}
		String id2 = arg.substring(0, arg.indexOf(".")) + fExt;
		double[] timeStampsToReturn = new double[tPoints];
		IFormatReader reader = null;
		int series = 0;
		try {
			ServiceFactory factory = new ServiceFactory();
			OMEXMLService service = factory.getInstance(OMEXMLService.class);
			IMetadata meta = service.createOMEXMLMetadata();
			// create format reader
			reader = new ImageReader();
			reader.setMetadataStore(meta);
			// initialize file
			reader.setId(id2);

			int seriesCount = reader.getSeriesCount();

			if (series < seriesCount) {
				reader.setSeries(series);
			}
			series = reader.getSeries();
			int planeCount = meta.getPlaneCount(series);
			int tCounter = 0;
			for (int i = 0; i < planeCount; i++) {
				Time deltaT = meta.getPlaneDeltaT(series, i);
				if (deltaT == null) {
					continue;
				}
				// convert plane ZCT coordinates into image plane index
				int z = meta.getPlaneTheZ(series, i).getValue();
				int c = meta.getPlaneTheC(series, i).getValue();
				int t = meta.getPlaneTheT(series, i).getValue();
				if (z == currZ && c == currCh) {
					timeStampsToReturn[tCounter] = deltaT.value(UNITS.SECOND).doubleValue();
					tCounter++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return timeStampsToReturn;
	}


	private double[] subtractValueFromArray(double[] array1, double theValue) {
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] - theValue;
		}
		return arrayToReturn;
	}

	/**
	 * Create a Thread[] array as large as the number of processors available.
	 * From Stephan Preibisch's Multithreading.java class. See:
	 * http://repo.or.cz/w/trakem2.git?a=blob;f=mpi/fruitfly/general/MultiThreading.java;hb=HEAD
	 */
	private Thread[] newThreadArray() {
		int n_cpus = Runtime.getRuntime().availableProcessors();

		IJ.log("in thread array");
		IJ.log(Integer.toString(n_cpus));
		return new Thread[n_cpus];
	}


	public double [] calculateCorrectedGmGs(double[] timeData, double[] pixelData, double[] bkGrdData, double omega) {

		double [] G = new double [3];
		pixelData = UtilityFunction.subtractArrayFromArray(pixelData,bkGrdData);
		//background subtraction before performing the phasor calculations
		double [] g0 = calculateGm(timeData, pixelData, bkGrdData, omega);
		double [] g1 = calculateGs(timeData, pixelData, bkGrdData, omega);

		G[0] = g0[0];
		G[1] = g1[0];
		G[2] = g0[1];

		return G;
	}


	//The methods for the phasor calculation below, calculateGm and calculateGs,
	//are borrowed heavily from Matlab code included in
	//Dave Jameson's paper Anal. Biochem (2011) 410:62-69
	//with Gm representing the modulation data
	//and Gs representing the phase data

	//and Gs representing the phase data
	public double[] calculateGm(double[] timeData, double[] pixelData, double[] bkGrdData, double omega) {        
		double delta_t = (timeData[timeData.length - 1] - timeData[0])/timeData.length;                
		double [] GmArray = new double [2];
		double Gm = 0;
		double area = 0;
		for (int bin = 0; bin < timeData.length - 1; bin++) {
			if(pixelData[bin]<0 || pixelData[bin]<terminalThreshold){
				for (int bin2 = bin; bin2 < timeData.length; bin2++) {
					pixelData[bin2]=0;
				}
			}
		}

		for (int bin = 0; bin < timeData.length - 1; bin++) {
			Gm = Gm + (pixelData[bin] * Math.cos(omega * delta_t * ((double)bin + 0.5)) * delta_t);
			if(pixelData[bin]==0 || pixelData[bin + 1]==0){
				area = area + (((pixelData[bin] + pixelData[bin + 1]) * delta_t));
			}else{
				area = area + (((pixelData[bin] + pixelData[bin + 1]) * delta_t) / 2.0);
			}
		}
		Gm = Gm / area;
		GmArray[0] = Gm;
		GmArray[1] = area;
		return GmArray;
	}

	public double[] calculateGs(double[] timeData, double[] pixelData, double[] bkGrdData, double omega) {
		double delta_t = (timeData[timeData.length - 1] - timeData[0])/timeData.length;        
		double [] GsArray = new double [2];
		double Gs = 0;
		double area = 0;
		for (int bin = 0; bin < timeData.length - 1; bin++) {
			if(pixelData[bin]<0 || pixelData[bin]<terminalThreshold){
				for (int bin2 = bin; bin2 < timeData.length; bin2++) {
					pixelData[bin2]=0;
				}
			}
		}


		for (int bin = 0; bin < timeData.length - 1; bin++) {
			Gs = Gs + (pixelData[bin] * Math.sin(omega * delta_t * ((double)bin + 0.5)) * delta_t);
			if(pixelData[bin]==0 || pixelData[bin + 1]==0){
				area = area + (((pixelData[bin] + pixelData[bin + 1]) * delta_t));
			}else{
				area = area + (((pixelData[bin] + pixelData[bin + 1]) * delta_t) / 2.0);
			}
		}
		Gs = Gs / area;
		GsArray[0] = Gs;
		GsArray[1] = area;
		return GsArray;
	}



	public double[] findTOneHalfEstimate(double[] x, double[] y) {
		//estimates the time to reach 0.5 of the signal, 0.5 to 0.25 of the signal, and 0.25 to 0.125 of the signal
		//these are averaged to try to account for multiple exponential components
		//a single exponential decay should produce a similar t1/2 for all three
		//a multiple exponential decay should produce longer t1/2s for the slower components
		//not perfect but should be a better approximation
		//interpolates between points to find the x that equals 0.5, 0.25, or 0.125 of y starting
		double [] tOneHalfArray = new double [3];
		double [] tOneHalfToReturn = new double [2];       
		tOneHalfToReturn[0] = y[y.length-1];
		double previousIntensity = y[0];
		for (int t = 0; t < y.length; t++) {
			if (t>0 && y[t] <= (y[0] * 0.5) && previousIntensity >= (y[0] * 0.5)) {
				//interpolate to find t1/2

				double y0 = previousIntensity;
				double y1 = y[0] * 0.5;
				double y2 = y[t];

				double x0 = x[t-1];
				double x2 = x[t];
				double x1 = x0 + (((x2 - x0) * (y1 - y0)) / (y2 - y0));
				tOneHalfArray[0] = x1;
			}
			if (t>0 && y[t] <= (y[0] * 0.25) && previousIntensity >= (y[0] * 0.25)) {
				//interpolate to find t1/2

				double y0 = previousIntensity;
				double y1 = y[0] * 0.25;
				double y2 = y[t];

				double x0 = x[t-1];
				double x2 = x[t];
				double x1 = x0 + (((x2 - x0) * (y1 - y0)) / (y2 - y0));

				tOneHalfArray[1] = x1;
			}
			if (t>0 && y[t] <= (y[0] * 0.125) && previousIntensity >= (y[0] * 0.125)) {
				//interpolate to find t1/2

				double y0 = previousIntensity;
				double y1 = y[0] * 0.125;
				double y2 = y[t];

				double x0 = x[t-1];
				double x2 = x[t];
				double x1 = x0 + (((x2 - x0) * (y1 - y0)) / (y2 - y0));

				tOneHalfArray[2] = x1;
			}
			previousIntensity = y[t];
		}
		tOneHalfToReturn[1]= (tOneHalfArray[0]+(tOneHalfArray[1]-tOneHalfArray[0])+(tOneHalfArray[2]-tOneHalfArray[1]))/3;
		return tOneHalfToReturn;
	}

	/**
	 * Start all given threads and wait on each of them until all are done. From
	 * Stephan Preibisch's Multithreading.java class. See:
	 * http://repo.or.cz/w/trakem2.git?a=blob;f=mpi/fruitfly/general/MultiThreading.java;hb=HEAD
	 *
	 * @param threads
	 */
	public static void startAndJoin(Thread[] threads) {
		for (int ithread = 0; ithread < threads.length; ++ithread) {
			threads[ithread].setPriority(Thread.NORM_PRIORITY);
			threads[ithread].start();
		}

		try {
			for (int ithread = 0; ithread < threads.length; ++ithread) {
				threads[ithread].join();
			}
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}



	public double[][][] medianFilterArray(double[][][]theDataToPlot, int medianDimension) {
		double[][][] GDataMed = new double[imageW][imageH][numCycles];
		double[] tempArray = new double[medianDimension*medianDimension];
		int startEnd = (medianDimension-1)/2;
		for (int cyc = 0; cyc < numCycles; cyc++) {
			for (int y = 0; y < imageH; y++) {
				for (int x = 0; x < imageW; x++) {
					if(y-startEnd>=0 && y+startEnd<imageH && x-startEnd>=0 && x+startEnd<imageW){
						int counter=0;
						for(int j=(y-startEnd);j<=(y+startEnd);j++){
							for(int i=(x-startEnd);i<=(x+startEnd);i++){
								tempArray[counter]=theDataToPlot[i][j][cyc];
								counter++;
							}
						}
						GDataMed [x][y][cyc]= getMedianOfArray(tempArray);
					}else{
						GDataMed [x][y][cyc]= theDataToPlot [x][y][cyc];
					}
				}
			}
		}
		return GDataMed;
	}




	public double[][][] updateRateDataArray(double[][][]theDataArray) {
		double[][][] updateArray = new double[imageW][imageH][numCycles];
		for (int cyc = 0; cyc < numCycles; cyc++) {
			for (int y = 0; y < imageH; y++) {
				for (int x = 0; x < imageW; x++) {
					updateArray[x][y][cyc] = Omega*GmData[x][y][cyc]/GsData[x][y][cyc];
				}
			}
		}
		return updateArray;
	}


	public static Plot plotPhasorPlot(double[][][] gmData, double[][][] gsData, int width, int height, int cycleNumber) {
		//makes a phasor plot for each photoswitching cycle
		double[] GM = new double[width * height];
		double[] GS = new double[width * height];
		ImageStack plotStack = new ImageStack();
		Plot thePhasorPlot = new Plot("Phasor Plot", "G", "S");
		for (int cycle = 0; cycle < cycleNumber; cycle++) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					GM[(y * width) + x] = gmData[x][y][cycle];
					GS[(y * width) + x] = gsData[x][y][cycle];
				}
			}
			Plot plot = new Plot("Phasor Plot", "G", "S");
			plot.setLimits(-0.1, 1.1, -0.1, 0.6);
			//plot.setColor("black");
			//plot.add("line", GmSemicircle, GsSemicircle);
			plot.setColor("blue");
			plot.add("dots", GM, GS);

			plotStack.addSlice("Cycle "+cycle,plot.getProcessor(), cycle);
			if(cycle==0)
				thePhasorPlot = plot;
		}//end of cycles

		ImagePlus PlotStack = new ImagePlus("Phasor Plot", plotStack);
		thePhasorPlot.setImagePlus(PlotStack);

		//Add the semicircle overlay
		double[] GmSemicircle = new double[1001];
		double[] GsSemicircle = new double[1001];
		double fakeLifetime = 0.1;
		GmSemicircle[0] = 1.0;
		GsSemicircle[0] = 0.0;
		for (int i = 1; i < GmSemicircle.length - 1; i++) {
			GmSemicircle[i] = 1 / (1 + Math.pow((2 * Math.PI * 0.1 * i * fakeLifetime), 2));
			GsSemicircle[i] = (2 * Math.PI * 0.1 * i * fakeLifetime) / (1 + Math.pow((2 * Math.PI * 0.1 * i * fakeLifetime), 2));
		}
		GmSemicircle[GmSemicircle.length - 1] = 0.0;
		GsSemicircle[GmSemicircle.length - 1] = 0.0;

		float[] xOverlaySemiCircle = new float[GmSemicircle.length];
		float[] yOverlaySemiCircle = new float[GmSemicircle.length];

		for (int i = 0; i < GmSemicircle.length; i++) {
			xOverlaySemiCircle[i] = (float) thePhasorPlot.scaleXtoPxl(GmSemicircle[i]);
			yOverlaySemiCircle[i] = (float) thePhasorPlot.scaleYtoPxl(GsSemicircle[i]);
		}
		PolygonRoi uniSemiCir = new PolygonRoi(xOverlaySemiCircle, yOverlaySemiCircle, Roi.FREELINE);
		uniSemiCir.setStrokeWidth(1);
		uniSemiCir.setStrokeColor(Color.BLACK);
		Overlay uniSemiCirOverlay = new Overlay(uniSemiCir);
		PlotStack.setOverlay(uniSemiCirOverlay);

		return thePhasorPlot;
	}
	
    public static double getMedianOfArray(double[] theArray) {
        Arrays.sort(theArray);
        if(theArray.length%2!=0)
            return (double) theArray[theArray.length/2];
        return (double) (theArray[(theArray.length-1)/2] + theArray[(theArray.length)/2])/2;
    }




    public double[] getFractionalContributions(double[]arrayOfMeanGRef, double[]arrayOfMeanSRef, double theG, double theS){
    	//returns the contributions from the possible photoswitchable probes in the sample (up to 5 total)
    	//see the word document with the reasoning behind the trigonemetric approach here
    	double[] returnArray =new double[5];
    	double[] deltaArray =new double[5];
    	for(int k=0;k<deltaArray.length;k++){
    		deltaArray[k]=0.0;
    	}
    	double deltaTheGS;
    	double deltaChEnd;
    	double deltaChEndMinus1;
    	double recipSum=0.0;
    	double A=0.0;
    	double B=0.0;
    	double C=0.0;
    	double D=0.0;
    	double E=0.0;

    	if (useChA && useChB && useChC && useChD && useChE) {
    		//get the lines from each mean GS to all others
    		//make lines from theG and theS point to intersect at right angles with each of these lines
    		//determine the area of the right trangles formed by the lines between 
    		//each mean GS, intersecting lines, and the lines between theG,theS and the meanGS
    		//the reciprocal of the line lengths is used in the area calculations
    		//thus, the greater the area, the greater the contribution
    		for (int nl = 4; nl > 0; nl--) {            
    			for (int i = nl; i > 0; i--) {                
    				double mAB = (arrayOfMeanSRef[nl] - arrayOfMeanSRef[i - 1]) / (arrayOfMeanGRef[nl] - arrayOfMeanGRef[i - 1]);

    				double intersectS = (arrayOfMeanSRef[nl] - (arrayOfMeanGRef[nl]*mAB) + (theS*mAB*mAB) + (theG*mAB))/((mAB*mAB)+1);
    				double intersectG = (intersectS - arrayOfMeanSRef[nl] + (arrayOfMeanGRef[nl]*mAB))/mAB;

    				deltaTheGS = Math.sqrt(Math.pow((theG - intersectG), 2) + Math.pow((theS - intersectS), 2));
    				deltaChEnd = Math.sqrt(Math.pow((arrayOfMeanGRef[nl] - intersectG), 2) + Math.pow((arrayOfMeanSRef[nl] - intersectS), 2));
    				deltaChEndMinus1 = Math.sqrt(Math.pow((arrayOfMeanGRef[i-1] - intersectG), 2) + Math.pow((arrayOfMeanSRef[i - 1] - intersectS), 2));

    				deltaArray[nl] = deltaArray[nl] + (((1/deltaTheGS) * (1/deltaChEnd)) / 2);
    				deltaArray[i-1] = deltaArray[i-1]  + (((1/deltaTheGS) * (1/deltaChEndMinus1)) / 2);

    			}//i
    		}//nl
    		if (Double.isNaN(deltaArray[0]) || Double.isNaN(deltaArray[1]) || Double.isNaN(deltaArray[2]) || Double.isNaN(deltaArray[3]) || Double.isNaN(deltaArray[4])) {
    			if(Double.isNaN(deltaArray[0]))
    				A=1.0;
    			if(Double.isNaN(deltaArray[1]))
    				B=1.0;
    			if(Double.isNaN(deltaArray[2]))
    				C=1.0;
    			if(Double.isNaN(deltaArray[3]))
    				D=1.0;
    			if(Double.isNaN(deltaArray[4]))
    				E=1.0;
    		} else {
    			recipSum = deltaArray[0] + deltaArray[1] + deltaArray[2] + deltaArray[3] + deltaArray[4];
    			A = deltaArray[0] / recipSum;
    			B = deltaArray[1] / recipSum;
    			C = deltaArray[2] / recipSum;
    			D = deltaArray[3] / recipSum;
    			E = deltaArray[4] / recipSum;
    		}
    	}
    	if (useChA && useChB && useChC && useChD && !useChE) {
    		for (int nl = 3; nl > 0; nl--) {
    			for (int i = nl; i > 0; i--) {                
    				double mAB = (arrayOfMeanSRef[nl] - arrayOfMeanSRef[i - 1]) / (arrayOfMeanGRef[nl] - arrayOfMeanGRef[i - 1]);

    				double intersectS = (arrayOfMeanSRef[nl] - (arrayOfMeanGRef[nl]*mAB) + (theS*mAB*mAB) + (theG*mAB))/((mAB*mAB)+1);
    				double intersectG = (intersectS - arrayOfMeanSRef[nl] + (arrayOfMeanGRef[nl]*mAB))/mAB;

    				deltaTheGS = Math.sqrt(Math.pow((theG - intersectG), 2) + Math.pow((theS - intersectS), 2));
    				deltaChEnd = Math.sqrt(Math.pow((arrayOfMeanGRef[nl] - intersectG), 2) + Math.pow((arrayOfMeanSRef[nl] - intersectS), 2));
    				deltaChEndMinus1 = Math.sqrt(Math.pow((arrayOfMeanGRef[i-1] - intersectG), 2) + Math.pow((arrayOfMeanSRef[i - 1] - intersectS), 2));

    				deltaArray[nl] = deltaArray[nl] + (((1/deltaTheGS) * (1/deltaChEnd)) / 2);
    				deltaArray[i-1] = deltaArray[i-1]  + (((1/deltaTheGS) * (1/deltaChEndMinus1)) / 2);
    			}//i
    		}//nl
    		if (Double.isNaN(deltaArray[0]) || Double.isNaN(deltaArray[1]) || Double.isNaN(deltaArray[2]) || Double.isNaN(deltaArray[3])) {
    			if(Double.isNaN(deltaArray[0]))
    				A=1.0;
    			if(Double.isNaN(deltaArray[1]))
    				B=1.0;
    			if(Double.isNaN(deltaArray[2]))
    				C=1.0;
    			if(Double.isNaN(deltaArray[3]))
    				D=1.0;
    		} else {
    			recipSum = deltaArray[0] + deltaArray[1] + deltaArray[2] + deltaArray[3];
    			A = deltaArray[0] / recipSum;
    			B = deltaArray[1] / recipSum;
    			C = deltaArray[2] / recipSum;
    			D = deltaArray[3] / recipSum;
    			E = 0.0;
    		}
    	}
    	if (useChA && useChB && useChC && !useChD && !useChE) {
    		for (int nl = 2; nl > 0; nl--) {
    			for (int i = nl; i > 0; i--) {               
    				double mAB = (arrayOfMeanSRef[nl] - arrayOfMeanSRef[i - 1]) / (arrayOfMeanGRef[nl] - arrayOfMeanGRef[i - 1]);

    				double intersectS = (arrayOfMeanSRef[nl] - (arrayOfMeanGRef[nl]*mAB) + (theS*mAB*mAB) + (theG*mAB))/((mAB*mAB)+1);
    				double intersectG = (intersectS - arrayOfMeanSRef[nl] + (arrayOfMeanGRef[nl]*mAB))/mAB;

    				deltaTheGS = Math.sqrt(Math.pow((theG - intersectG), 2) + Math.pow((theS - intersectS), 2));
    				deltaChEnd = Math.sqrt(Math.pow((arrayOfMeanGRef[nl] - intersectG), 2) + Math.pow((arrayOfMeanSRef[nl] - intersectS), 2));
    				deltaChEndMinus1 = Math.sqrt(Math.pow((arrayOfMeanGRef[i-1] - intersectG), 2) + Math.pow((arrayOfMeanSRef[i - 1] - intersectS), 2));

    				deltaArray[nl] = deltaArray[nl] + (((1/deltaTheGS) * (1/deltaChEnd)) / 2);
    				deltaArray[i-1] = deltaArray[i-1]  + (((1/deltaTheGS) * (1/deltaChEndMinus1)) / 2);

    			}//i
    		}//nl
    		if (Double.isNaN(deltaArray[0]) || Double.isNaN(deltaArray[1]) || Double.isNaN(deltaArray[2])) {
    			if(Double.isNaN(deltaArray[0]))
    				A=1.0;
    			if(Double.isNaN(deltaArray[1]))
    				B=1.0;
    			if(Double.isNaN(deltaArray[2]))
    				C=1.0;
    		} else {
    			recipSum = deltaArray[0] + deltaArray[1] + deltaArray[2];
    			A = deltaArray[0] / recipSum;
    			B = deltaArray[1] / recipSum;
    			C = deltaArray[2] / recipSum;
    			D = 0.0;
    			E = 0.0;
    		}
    	}
    	if (useChA && useChB && !useChC && !useChD && !useChE) {
    		for (int nl = 1; nl > 0; nl--) {
    			for (int i = nl; i > 0; i--) {
    				double mAB = (arrayOfMeanSRef[nl] - arrayOfMeanSRef[i - 1]) / (arrayOfMeanGRef[nl] - arrayOfMeanGRef[i - 1]);

    				double intersectS = (arrayOfMeanSRef[nl] - (arrayOfMeanGRef[nl]*mAB) + (theS*mAB*mAB) + (theG*mAB))/((mAB*mAB)+1);
    				double intersectG = (intersectS - arrayOfMeanSRef[nl] + (arrayOfMeanGRef[nl]*mAB))/mAB;

    				deltaTheGS = Math.sqrt(Math.pow((theG - intersectG), 2) + Math.pow((theS - intersectS), 2));
    				deltaChEnd = Math.sqrt(Math.pow((arrayOfMeanGRef[nl] - intersectG), 2) + Math.pow((arrayOfMeanSRef[nl] - intersectS), 2));
    				deltaChEndMinus1 = Math.sqrt(Math.pow((arrayOfMeanGRef[i-1] - intersectG), 2) + Math.pow((arrayOfMeanSRef[i - 1] - intersectS), 2));

    				deltaArray[nl] = deltaArray[nl] + (((1/deltaTheGS) * (1/deltaChEnd)) / 2);
    				deltaArray[i-1] = deltaArray[i-1]  + (((1/deltaTheGS) * (1/deltaChEndMinus1)) / 2);

    			}//i
    		}//nl
    		if (Double.isNaN(deltaArray[0]) || Double.isNaN(deltaArray[1])) {
    			if(Double.isNaN(deltaArray[0]))
    				A=1.0;
    			if(Double.isNaN(deltaArray[1]))
    				B=1.0;
    		} else {
    			recipSum = deltaArray[0] + deltaArray[1];
    			A = deltaArray[0] / recipSum;
    			B = deltaArray[1] / recipSum;
    			C = 0.0;
    			D = 0.0;
    			E = 0.0;
    		}
    	}
    	if (useChA && !useChB && !useChC && !useChD && !useChE) {        
    		A=1.0;
    		B=0.0;
    		C=0.0;
    		D=0.0;
    		E=0.0;
    	}



    	//IJ.log("recipSum ="+recipSum+" A="+A+" B="+B+" C="+C+" D="+D+" E="+E);
    	returnArray[0]=A;
    	returnArray[1]=B;
    	returnArray[2]=C;
    	returnArray[3]=D;
    	returnArray[4]=E;
    	return returnArray;
    } 

    public ImagePlus createUnmixedImage(String channel, double[][][] theArray) {
        //plots the unmixed images
        ImagePlus imp = IJ.createImage(channel, "32-bit", imageW, imageH, numCycles);
        
        for (int cyc = 0; cyc < numCycles; cyc++) {
            imp.setSlice(cyc + 1);
            ImageProcessor ip = imp.getProcessor();
            FloatProcessor fip = (FloatProcessor) ip.convertToFloat();
            for (int y = 0; y < imageH; y++) {
                for (int x = 0; x < imageW; x++) {
                    if (Double.isNaN(theArray[x][y][cyc])) {
                        fip.setf(x, y, Float.NaN);
                    } else {
                        fip.setf(x, y, (float) theArray[x][y][cyc]);
                    }
                }
            }
            IJ.resetMinAndMax(imp);
        }
        
        imp.show();
        return imp;
    }    

    public boolean isPhasorCalcOver() {
    	return (GmData==null || GsData==null || AZeroData==null);
           

    }






}
