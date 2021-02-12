
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

public class AzeroCurveFitter {

	// variables
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
	int binFactorImage;
	// temp values OPT
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

	// need to copy these variables from the calling function
	ImagePlus img;

	//
	int cameraOffset;
	int binFactor;
	int maxiteration;
	double cameraGain;

	boolean useChA;
	boolean useChB;
	boolean useChC;
	boolean useChD;
	boolean useChE;

	// variable for image creation
	double[][][] arrayChA;
	double[][][] arrayChB;
	double[][][] arrayChC;
	double[][][] arrayChD;
	double[][][] arrayChE;

	// phasor data
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

	double varParam;
	double iterCount = 0;
	double cycleCountForavgIter = 0;

	boolean useBinning;
	int spatialBinningNum;

	// function to copy all the variables to the local variables

	// functions

	// curve fitting function to perform the curve fitting locally

	public void psFRETFitAzeroExponential() throws Exception {
		// this method is the same as above except that the rate constants are fixed and
		// only the fractional components are fitted

		// the above string operation should be in the main function

		final ImageStack img2 = img.getStack();
		imageH = img2.getHeight();
		imageW = img2.getWidth();
		imageD = img2.getBitDepth();
		imageZ = img2.getSize();
		final int currentchannel = img.getC() - 1;
		final int currentZ = img.getZ() - 1;
		final int nSlices = img.getNSlices();
		int size = img.getNFrames();
		if (size == 1)// in case the stack is read as a Z stack instead of T stack
		{
			size = nSlices;
		}
		if (numCycles * imagesPerCycle > size) {
			IJ.showMessage("Pixel Fitter",
					"The number of cycles multiplied by the number images per cycle is larger than the stack");
			return;
		}
		try {
			timeData3 = getTimingPerPlane(id, size, currentZ, currentchannel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Chi2G = new double[imageW][imageH][numCycles];
		offsetDataG = new double[imageW][imageH][numCycles];
		a1DataG = new double[imageW][imageH][numCycles];
		a2DataG = new double[imageW][imageH][numCycles];
		a3DataG = new double[imageW][imageH][numCycles];
		a4DataG = new double[imageW][imageH][numCycles];
		a5DataG = new double[imageW][imageH][numCycles];

		// temp values for next cycle
		// usePhasortoInitialize=true;

		Chi2G_t = new double[imageW][imageH][numCycles];
		offsetDataG_t = new double[imageW][imageH][numCycles];
		a1DataG_t = new double[imageW][imageH][numCycles];
		a2DataG_t = new double[imageW][imageH][numCycles];
		a3DataG_t = new double[imageW][imageH][numCycles];
		a4DataG_t = new double[imageW][imageH][numCycles];
		a5DataG_t = new double[imageW][imageH][numCycles];

		for (int cycle = 0; cycle < numCycles; cycle++) {
			IJ.log("for each cycle");
			// printSystemTime();

			final long startTime = System.currentTimeMillis();

			double[] timeData2 = new double[imagesPerCycle];
			for (int k = 0; k < imagesPerCycle; k++) {

				timeData2[k] = (timeData3[k + (cycle * imagesPerCycle)] - timeData3[cycle * imagesPerCycle]);

			}
			timeData = timeData2;

			final Thread[] threads = newThreadArray();
			// IJ.log("number of threads= " + threads.length);
			final double[][] timeDataArrayOfArrays = new double[threads.length][timeData.length];
			final double[][] pixelsArrayOfArrays = new double[threads.length][timeData.length];

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
						for (int y = threadIndex * height / threads.length; y < (threadIndex + 1) * height
								/ threads.length; y++) {
							if (threadIndex == threads.length - 1) {
								int startY = threadIndex * height / threads.length;
								int endY = (threadIndex + 1) * height / threads.length;
								int progress = (int) Math.round(((double) (y - startY) / (endY - startY)) * 100);

								// should make this functional
								Photoswitching_Phasor_Plotter.statusMessageArea
										.setText("Fitting pixels progress: " + progress + " %  of cycle "
												+ (cycleNum + 1) + " of " + numCycles + " total cycles");
								Photoswitching_Phasor_Plotter.statusMessageArea
										.update(Photoswitching_Phasor_Plotter.statusMessageArea.getGraphics());
							}
							for (int x = 0; x < width; x++) {
								for (int z = 0; z < timeData.length; z++) {
									timeDataArrayOfArrays[threadIndex][z] = timeData[z];
									if (img.isHyperStack()) {
										int z2;

										z2 = img.getStackIndex(img.getC(), img.getZ(),
												(cycleNum * imagesPerCycle) + z + 1) - 1;

										//////
										// useBinning = true;
										// spatialBinningNum = 5;
										if (useBinning) { // binning introduced

											int bin = spatialBinningNum;

											if ((x > (width - bin) | (y > (height - bin))))
												continue; // avoiding corner pixels
											int sizeOfVoxels = bin * bin;// array to allocate for getting voxels
											float[] voxels = new float[sizeOfVoxels];
											double pixelValue = 0;
											try {
												img2.getVoxels(x, y, z2, bin, bin, 1, voxels);// crashes for x or y=511
											} catch (IndexOutOfBoundsException e) {
												IJ.log(Integer.toString(x) + "x, y=" + Integer.toString(y));
											}
											// IJ.log(Integer.toString(voxels.length));

											for (int iVoxel = 0; iVoxel < voxels.length; iVoxel++) {
												pixelValue += voxels[iVoxel];
											}

											pixelsArrayOfArrays[threadIndex][z] = pixelValue / voxels.length;
										} else {

											pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2);

										}
										/////

										//pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2);

									} else {
										pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y,
												(cycleNum * imagesPerCycle) + z);
									}
								}
								pixelsArrayOfArrays[threadIndex] = subtractValueFromArray(
										pixelsArrayOfArrays[threadIndex], cameraOffset * binFactor * binFactor);
								double firstframeint = pixelsArrayOfArrays[threadIndex][0];
								double lastframeint = pixelsArrayOfArrays[threadIndex][pixelsArrayOfArrays[threadIndex].length
										- 1];

								// initialization for this round of fitting
								double[] fitparam = initAzeorExponentialFit(x, y, cycleNum);

								if ((firstframeint - lastframeint) < PixelThresholdCutOff) {
									Chi2G[x][y][cycleNum] = Double.NaN;
									offsetDataG[x][y][cycleNum] = Double.NaN;
									a1DataG[x][y][cycleNum] = Double.NaN;
									a2DataG[x][y][cycleNum] = Double.NaN;
									a3DataG[x][y][cycleNum] = Double.NaN;
									a4DataG[x][y][cycleNum] = Double.NaN;
									a5DataG[x][y][cycleNum] = Double.NaN;

								} else {

									// if (cycleNum>0){
									// maxiteration=maxiteration/2;
									// }
									double[] fittedParam = fitAzeroExponentialFunction(
											timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex],
											fitparam, maxiteration);
									double Chi2 = fittedParam[0];

									if (Chi2 < Chi2CutOff) {
										offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
										a1DataG[x][y][cycleNum] = (float) fittedParam[2];
										a2DataG[x][y][cycleNum] = (float) fittedParam[3];
										a3DataG[x][y][cycleNum] = (float) fittedParam[4];
										a4DataG[x][y][cycleNum] = (float) fittedParam[5];
										a5DataG[x][y][cycleNum] = (float) fittedParam[6];

										if (cycleNum < (numCycles - 1)) {// OPT copy fitted param for initialization for
																			// next cycle //newcode
											offsetDataG_t[x][y][cycleNum] = (float) fittedParam[1];
											a1DataG_t[x][y][cycleNum] = (float) fittedParam[2];
											a2DataG_t[x][y][cycleNum] = (float) fittedParam[3];
											a3DataG_t[x][y][cycleNum] = (float) fittedParam[4];
											a4DataG_t[x][y][cycleNum] = (float) fittedParam[5];
											a5DataG_t[x][y][cycleNum] = (float) fittedParam[6];
										}

									} else {
										Chi2G[x][y][cycleNum] = Double.NaN;
										offsetDataG[x][y][cycleNum] = Double.NaN;
										a1DataG[x][y][cycleNum] = Double.NaN;
										a2DataG[x][y][cycleNum] = Double.NaN;
										a3DataG[x][y][cycleNum] = Double.NaN;
										a4DataG[x][y][cycleNum] = Double.NaN;
										a5DataG[x][y][cycleNum] = Double.NaN;

										if (cycleNum < (numCycles - 1)) {
											offsetDataG_t[x][y][cycleNum] = Double.NaN;
											;
											a1DataG_t[x][y][cycleNum] = (float) Double.NaN;
											a2DataG_t[x][y][cycleNum] = (float) Double.NaN;
											a3DataG_t[x][y][cycleNum] = (float) Double.NaN;
											a4DataG_t[x][y][cycleNum] = (float) Double.NaN;
											a5DataG_t[x][y][cycleNum] = (float) Double.NaN;
										}
									}
								}
							}
						}
					}
				};
			}
			startAndJoin(threads);
			long timeToCompletion = System.currentTimeMillis() - startTime;

			IJ.log("time taken to finish (in seconds) " + Double.toString(timeToCompletion / 1000));
			Photoswitching_Phasor_Plotter.statusMessageArea.setText("Fitting time: " + timeToCompletion);
			Photoswitching_Phasor_Plotter.statusMessageArea
					.update(Photoswitching_Phasor_Plotter.statusMessageArea.getGraphics());
			// if (LogFitTime == true) {
			// IJ.log("Image " + id + " cycle " + cycle + " processing time = " +
			// (timeToCompletion / 1000) + " sec");
			// }

			double averageCycleCount = iterCount / cycleCountForavgIter;
			iterCount = 0;
			cycleCountForavgIter = 0;

			IJ.log("Average fitting cycle count for this round " + Double.toString(averageCycleCount));
			IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
			IJ.log("fitting function over psFRET_Fit_Azero_Exponential");
			// printSystemTime();

		} // end of cycles

	}

	double GetBinnedVoxel(ImageStack img2, int x, int y, int height, int width, int z, int nSlices) {
		// binning functionality (spatial)
		double sum = 0;
		if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < nSlices) {
			sum = img2.getVoxel(x, y, z);

		}

		return sum;

	}

	private double[] initAzeorExponentialFit(int x, int y, int cycleNum) {

		double guess_o = 0;
		double guess_a1 = 0;
		double guess_a2 = 0;
		double guess_a3 = 0;
		double guess_a4 = 0;
		double guess_a5 = 0;

		if (usePhasortoInitialize && isPhasorFitDone && (cycleNum == 0)) {// this should be for the first cycle only,
																			// add a condition
			// now testing for first cycle only
			// initialize using the data from phasor
			// later expand and compare with subsequent cycles
			// IJ.log("came here");
			guess_o = 0;
			guess_a1 = arrayChA_p[x][y][0];
			guess_a2 = arrayChB_p[x][y][0];
			guess_a3 = arrayChC_p[x][y][0];
			guess_a4 = arrayChD_p[x][y][0];
			guess_a5 = arrayChE_p[x][y][0];
		}

		if (cycleNum > 0) {

			// copying from the previous cycle for initializing value for the subsequent
			// cycle
			guess_o = offsetDataG_t[x][y][cycleNum - 1];
			guess_a1 = a1DataG_t[x][y][cycleNum - 1];
			guess_a2 = a2DataG_t[x][y][cycleNum - 1];
			guess_a3 = a3DataG_t[x][y][cycleNum - 1];
			guess_a4 = a4DataG_t[x][y][cycleNum - 1];
			guess_a5 = a5DataG_t[x][y][cycleNum - 1];

		}

		double[] fitData = { 0, guess_o, guess_a1, guess_a2, guess_a3, guess_a4, guess_a5 };
		return fitData;

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

	/**
	 * Create a Thread[] array as large as the number of processors available. From
	 * Stephan Preibisch's Multithreading.java class. See:
	 * http://repo.or.cz/w/trakem2.git?a=blob;f=mpi/fruitfly/general/MultiThreading.java;hb=HEAD
	 */
	private Thread[] newThreadArray() {
		int n_cpus = Runtime.getRuntime().availableProcessors();

		IJ.log("in thread array");
		IJ.log(Integer.toString(n_cpus));
		return new Thread[n_cpus];
	}

	// Set useChannel values

	public void setVarParam(double varParam) {
		this.varParam = varParam;
	}

	public void setUseChannelValues(boolean chA, boolean chB, boolean chC, boolean chD, boolean chE) {
		this.useChA = chA;
		this.useChB = chB;
		this.useChC = chC;
		this.useChD = chD;
		this.useChE = chE;

	}

	public void setImagePlus(ImagePlus im) {
		this.img = im;

	}

	public void setChannelMean(double chA, double chB, double chC, double chD, double chE) {
		this.chA_Kmean = chA;
		this.chB_Kmean = chB;
		this.chC_Kmean = chC;
		this.chD_Kmean = chD;
		this.chE_Kmean = chE;

	}

	public void initCycleNums(int numcycles, int imagecycles) {
		this.numCycles = numcycles;
		this.imagesPerCycle = imagecycles;
	}

	public void setPixelThreshold(double pixThreshold) {
		this.PixelThresholdCutOff = pixThreshold;
	}

	public void setChi2CutOff(double Chi2CutOff) {
		this.Chi2CutOff = Chi2CutOff;
	}

	public void setHarmonicOmega(double harmonic, double Omega) {

		this.harmonic = harmonic;
		this.Omega = Omega;

	}

	public void setIteration(int maxiteration) {
		this.maxiteration = maxiteration;
	}

	public void copyPhasorData(double[][][] arrayChA, double[][][] arrayChB, double[][][] arrayChC,
			double[][][] arrayChD, double[][][] arrayChE) {
		this.arrayChA_p = arrayChA;
		this.arrayChB_p = arrayChB;
		this.arrayChC_p = arrayChC;
		this.arrayChD_p = arrayChD;
		this.arrayChE_p = arrayChE;

		// should we use the directly the variables or copy? make sure this is efficient

	}

	public void copyStringNamesFile(String chA_name, String chB_name, String chC_name, String chD_name,
			String chE_name) {
		this.chA_name = chA_name;
		this.chB_name = chB_name;
		this.chC_name = chC_name;
		this.chD_name = chD_name;
		this.chE_name = chE_name;

	}

	public void setID(String ID) {
		this.id = ID;
	}

	public void setPhasorBooleans(boolean usePhasortoInitialize, boolean isPhasorFitDone) {
		this.usePhasortoInitialize = usePhasortoInitialize;
		this.isPhasorFitDone = isPhasorFitDone;
	}

	public void setCameraOffset(int cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public void setBinFactor(int binFactor) {
		this.binFactor = binFactor;
	}

	public void setbinFactorImage(int binFactorImage) {
		this.binFactorImage = binFactorImage;
	}

	public void setCameraGain(double cameraGain) {
		this.cameraGain = cameraGain;
	}

	public void setBinning(boolean useBinning) {
		this.useBinning = useBinning;

	}
	public void setNumBins(int numBins) {
		spatialBinningNum = numBins;
		IJ.log("bins used "+Boolean.toString(useBinning)+" "+Integer.toString(numBins));

	}
	// *******************Utilities*******************************

	// makestatic

	private double[] getTimingPerPlane(String arg, int tPoints, int currZ, int currCh) throws Exception {
		// this uses Bioformats to get the timing of the images
		// from the start of the experiment
		// if the plane delta T is not found, user is prompted for a time interval input
		String fExt = arg.substring(arg.indexOf("."), arg.length());
		if (fExt.contains(" ") && fExt.indexOf(" ") < arg.length()) {
			fExt = fExt.substring(0, fExt.indexOf(" "));
		}
		String id2 = arg.substring(0, arg.indexOf(".")) + fExt;
		double[] timeStampsToReturn = new double[tPoints];// 901
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
			for (int i = 0; i < planeCount; i++) {// 905
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

	private double[] fitAzeroExponentialFunction(double[] x, double[] y, double[] paramsPassed, int maxIter) {

		// IJ.log("inside the fit function start");
		// this is the method fitting only the fractional contributions
		// it assumes that rate constants have been measured previously and input into
		// the plugin gui
		double[] params = new double[0];
		if (useChA && useChB && useChC && useChD && useChE) {
			params = new double[paramsPassed.length - 1];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);// converting to the square root and then including the squares in the
												// equation forces these to be positive
			params[2] = Math.sqrt(params[2]);
			params[3] = Math.sqrt(params[3]);
			params[4] = Math.sqrt(params[4]);
			params[5] = Math.sqrt(params[5]);
		}
		if (useChA && useChB && useChC && useChD && !useChE) {
			params = new double[paramsPassed.length - 2];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);// converting to the square root and then including the squares in the
												// equation forces these to be positive
			params[2] = Math.sqrt(params[2]);
			params[3] = Math.sqrt(params[3]);
			params[4] = Math.sqrt(params[4]);
		}
		if (useChA && useChB && useChC && !useChD && !useChE) {
			params = new double[paramsPassed.length - 3];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);// converting to the square root and then including the squares in the
												// equation forces these to be positive
			params[2] = Math.sqrt(params[2]);
			params[3] = Math.sqrt(params[3]);
		}
		if (useChA && useChB && !useChC && !useChD && !useChE) {
			params = new double[paramsPassed.length - 4];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);// converting to the square root and then including the squares in the
												// equation forces these to be positive
			params[2] = Math.sqrt(params[2]);
		}
		if (useChA && !useChB && !useChC && !useChD && !useChE) {
			params = new double[paramsPassed.length - 5];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);// converting to the square root and then including the squares in the
												// equation forces these to be positive
		}

		double[] paramVariations = UtilityFunction.multiplyArrayByValue(params, varParam); // OPT// use this for
																							// variation in the init
																							// function
		// x, y length 300

		// For good performance, it is advisable to set also the typical variation range
		// of the initial parameters by the
		// getMinimizer().setInitialParamVariations(double[]) method (especially if one
		// or more of the initialParams are zero).
		CurveFitter cf = new CurveFitter(x, y);
		CurveFitter cf1 = new CurveFitter(x, y);
		cf.setOffsetMultiplySlopeParams(-1, -1, -1);
		cf.setMaxIterations(maxIter);

		// should set the initial pa
		// cf.setRestarts(4);

		cf.doCustomFit(new UserFunction() {
			@Override
			public double userFunction(double[] par, double x) {
				if (useChA && useChB && useChC && useChD && useChE) {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x))
							+ (par[2] * par[2] * Math.exp(-chB_Kmean * x))
							+ (par[3] * par[3] * Math.exp(-chC_Kmean * x))
							+ (par[4] * par[4] * Math.exp(-chD_Kmean * x))
							+ (par[5] * par[5] * Math.exp(-chE_Kmean * x));
				}
				if (useChA && useChB && useChC && useChD && !useChE) {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x))
							+ (par[2] * par[2] * Math.exp(-chB_Kmean * x))
							+ (par[3] * par[3] * Math.exp(-chC_Kmean * x))
							+ (par[4] * par[4] * Math.exp(-chD_Kmean * x));
				}
				if (useChA && useChB && useChC && !useChD && !useChE) {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x))
							+ (par[2] * par[2] * Math.exp(-chB_Kmean * x))
							+ (par[3] * par[3] * Math.exp(-chC_Kmean * x));
				}
				if (useChA && useChB && !useChC && !useChD && !useChE) {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x))
							+ (par[2] * par[2] * Math.exp(-chB_Kmean * x));
				}
				if (useChA && !useChB && !useChC && !useChD && !useChE) {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x));
				} else {
					return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x));
				}
			}
		}, params.length, "", params, paramVariations, false);

		// params.length, "", params, null, false);
		double[] paramToReturn = cf.getParams();
		double Chi2ToReturn = Photoswitching_Phasor_Plotter.calculateReducedChi2(cf.getResiduals(), y);
		double[] returnArray = new double[paramsPassed.length];
		Arrays.fill(returnArray, 0);
		returnArray[0] = Chi2ToReturn;
		returnArray[1] = paramToReturn[0];
		// IJ.log(Integer.toString(cf.getIterations()));// this prints the number of
		// iteration actually used

		iterCount += cf.getIterations();
		cycleCountForavgIter++;

		// IJ.log("chi2 calculated "+Double.toString(Chi2ToReturn)+" param 0 =
		// "+Double.toString(paramToReturn[0]));
		if (useChA) {
			returnArray[2] = paramToReturn[1] * paramToReturn[1];// these are the square roots; convert back to the
																	// fractional contributions
		}
		if (useChB) {
			returnArray[3] = paramToReturn[2] * paramToReturn[2];
		}
		if (useChC) {
			returnArray[4] = paramToReturn[3] * paramToReturn[3];
		}
		if (useChD) {
			returnArray[5] = paramToReturn[4] * paramToReturn[4];
		}
		if (useChE) {
			returnArray[6] = paramToReturn[5] * paramToReturn[5];
		}
		return returnArray;
	}

	public void unMixPixelValuesAndCreateImagesUsingFit() {
		// if the fractional contributions are calculated by fits using
		// psFRET_Fit_Azero_Exponential()
		// this will transfer those to the channel arrays and used to create unmixed
		// images
		long startTime = System.currentTimeMillis();
		// use ROI to define exclusively one channel or another
		arrayChA = new double[imageW][imageH][numCycles];
		arrayChB = new double[imageW][imageH][numCycles];
		arrayChC = new double[imageW][imageH][numCycles];
		arrayChD = new double[imageW][imageH][numCycles];
		arrayChE = new double[imageW][imageH][numCycles];
		for (int cyc = 0; cyc < numCycles; cyc++) {
			for (int y = 0; y < imageH; y++) {
				for (int x = 0; x < imageW; x++) {
					if (!Double.isNaN(a1DataG[x][y][cyc])) {
						arrayChA[x][y][cyc] = a1DataG[x][y][cyc];
					} else {
						arrayChA[x][y][cyc] = Double.NaN;
					}
					if (!Double.isNaN(a2DataG[x][y][cyc])) {
						arrayChB[x][y][cyc] = a2DataG[x][y][cyc];
					} else {
						arrayChB[x][y][cyc] = Double.NaN;
					}
					if (!Double.isNaN(a3DataG[x][y][cyc])) {
						arrayChC[x][y][cyc] = a3DataG[x][y][cyc];
					} else {
						arrayChC[x][y][cyc] = Double.NaN;
					}
					if (!Double.isNaN(a4DataG[x][y][cyc])) {
						arrayChD[x][y][cyc] = a4DataG[x][y][cyc];
					} else {
						arrayChD[x][y][cyc] = Double.NaN;
					}
					if (!Double.isNaN(a5DataG[x][y][cyc])) {
						arrayChE[x][y][cyc] = a5DataG[x][y][cyc];
					} else {
						arrayChE[x][y][cyc] = Double.NaN;
					}
				}
			}
		}

		// Photoswitching_Phasor_Plotter.statusMessageArea.setText("Unmixing time: " +
		// timeToCompletion); //TBD
		Photoswitching_Phasor_Plotter.statusMessageArea
				.update(Photoswitching_Phasor_Plotter.statusMessageArea.getGraphics());

		if (useChA)
			createUnmixedImage(chA_name, arrayChA);
		if (useChB)
			createUnmixedImage(chB_name, arrayChB);
		if (useChC)
			createUnmixedImage(chC_name, arrayChC);
		if (useChD)
			createUnmixedImage(chD_name, arrayChD);
		if (useChE)
			createUnmixedImage(chE_name, arrayChE);
	}

	public ImagePlus createUnmixedImage(String channel, double[][][] theArray) {
		// plots the unmixed images
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

}
