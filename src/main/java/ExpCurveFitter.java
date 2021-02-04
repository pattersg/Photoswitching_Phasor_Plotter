
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

//class to handle exponential curve fitting (not the AzeroFix one)
public class ExpCurveFitter {

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

	boolean LogFitTime;
	int numRestarts;
	boolean fitSingle;
	boolean fitDouble;
	boolean fitTriple;
	boolean useFitToinit = true;

	double cameraGain;
	double lambda;
	private double NA;
	private double pixSize;
	private int binFactorImage;
	double varParam;

	ImagePlus Chi2Image;
	ImagePlus wmkImage;
	ImagePlus a1Image;

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

	public void setChanneltoFit(boolean fitSingle, boolean fitDouble, boolean fitTriple) {
		this.fitSingle = fitSingle;
		this.fitDouble = fitDouble;
		this.fitTriple = fitTriple;
	}

	public void setCamergain(double cameraGain) {
		this.cameraGain = cameraGain;

	}

	public void setLamdaNA(double lambda, double NA) {
		this.lambda = lambda;
		this.NA = NA;

	}

	public void setPixSizeBinfactor(double pixSize, int binFactorImage) {
		this.pixSize = pixSize;
		this.binFactorImage = binFactorImage;
	}

	public void setVarParam(double varParam) {
		this.varParam = varParam;
	}

	public void setMaxiters(int numRestarts) {
		this.numRestarts = numRestarts;
	}

	public void psFitExponential() throws Exception {
		IJ.resetMinAndMax(img);
		// in case the image is opened without using the plugin BioFormats button
		String dir0 = IJ.getDirectory("image");
		String stackToOpen = img.getTitle();
		String id2 = dir0 + stackToOpen;
		String fExt = id2.substring(id2.lastIndexOf("."), id2.length());
		if (fExt.contains(" ") && fExt.indexOf(" ") < id2.length()) {
			fExt = fExt.substring(0, fExt.indexOf(" "));
		}
		id = id2.substring(0, id2.lastIndexOf(".")) + fExt;

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
			// the elapsed time from the start of the experiment
			// is pulled from the metadata for each image using Bioformats
			// If no time data is found in the metadata; the user is prompted to enter a
			// time interval
		} catch (Exception e) {
			e.printStackTrace();
		}
		Chi2G = new double[imageW][imageH][numCycles];
		offsetDataG = new double[imageW][imageH][numCycles];
		a1DataG = new double[imageW][imageH][numCycles];
		k1DataG = new double[imageW][imageH][numCycles];
		a2DataG = new double[imageW][imageH][numCycles];
		k2DataG = new double[imageW][imageH][numCycles];
		a3DataG = new double[imageW][imageH][numCycles];
		k3DataG = new double[imageW][imageH][numCycles];

		Chi2G_t = new double[imageW][imageH][numCycles];
		offsetDataG_t = new double[imageW][imageH][numCycles];
		a1DataG_t = new double[imageW][imageH][numCycles];
		k1DataG_t = new double[imageW][imageH][numCycles];
		a2DataG_t = new double[imageW][imageH][numCycles];
		k2DataG_t = new double[imageW][imageH][numCycles];
		a3DataG_t = new double[imageW][imageH][numCycles];
		k3DataG_t = new double[imageW][imageH][numCycles];
		// 3D arrays are initialized for the fit parameters

		if (!fitSingle && !fitDouble && !fitTriple) {
			IJ.showMessage("Fit exponential",
					"You must check Fit Single, Fit Double, or Fit Triple under the Fitting tab");
			return;
		}

		if (fitSingle) {
			a2DataG = null;
			k2DataG = null;
			a3DataG = null;
			k3DataG = null;
		}
		if (fitDouble) {
			a3DataG = null;
			k3DataG = null;
		}

		for (int cycle = 0; cycle < numCycles; cycle++) {

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
			// two 2D arrays are initialized to handle the timedata and the pixel
			// (fluorescence data) during the fitting
			//
			// IJ.log("this is thread size");
			// IJ.log(Integer.toString(threads.length));

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
							// the image is divided into equal sections based on the number of threads
							// (CPUs)
							// thus each cpu analyzes a different section of the image
							if (threadIndex == (int) Math.ceil(threads.length / 2)) {
								// this part is to monitor the progress of the fitting
								// it monitors the thread running in approximately the center of the image
								int startY = threadIndex * height / threads.length;
								int endY = (threadIndex + 1) * height / threads.length;
								int progress = (int) Math.round(((double) (y - startY) / (endY - startY)) * 100.0);
								Photoswitching_Phasor_Plotter.statusMessageArea
										.setText("Fitting pixels progress: " + progress + " %  of cycle "
												+ (cycleNum + 1) + " of " + numCycles + " total cycles");
								Photoswitching_Phasor_Plotter.statusMessageArea
										.update(Photoswitching_Phasor_Plotter.statusMessageArea.getGraphics());
							}
							for (int x = 0; x < width; x++) {
								// the fits are performed line by line within each thread section
								for (int z = 0; z < timeData.length; z++) {
									timeDataArrayOfArrays[threadIndex][z] = timeData[z];
									if (img.isHyperStack()) {
										int z2 = img.getStackIndex(img.getC(), img.getZ(),
												(cycleNum * imagesPerCycle) + z + 1) - 1;
										pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2);
									} else {
										pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y,
												(cycleNum * imagesPerCycle) + z);
									}
									// the pixel values are retrieved slightly differently depending on a hyperstack
									// or not
								}
								pixelsArrayOfArrays[threadIndex] = UtilityFunction.subtractValueFromArray(
										pixelsArrayOfArrays[threadIndex], ((cameraOffset * binFactor * binFactor)));
								// subtract the offset from the pixel values
								// camera offsets are usually 100 counts per pixel
								// binning obviously sums from multiple pixels
								double firstframeint = pixelsArrayOfArrays[threadIndex][0];
								double lastframeint = pixelsArrayOfArrays[threadIndex][pixelsArrayOfArrays[threadIndex].length
										- 1];
								// to provide an initial guess for the fitting
								double tau = findTauEstimate(timeDataArrayOfArrays[threadIndex],
										pixelsArrayOfArrays[threadIndex], firstframeint, lastframeint);
								// to provide an initial estimate of the rate constant

								double[] fitparam = initExpFittingComponents(firstframeint, lastframeint, tau, cycleNum,
										x, y);

								if (firstframeint - lastframeint < PixelThresholdCutOff) {
									// fill arrays with NaN if the threshold is not met
									if (fitTriple) {
										Chi2G[x][y][cycleNum] = Double.NaN;
										offsetDataG[x][y][cycleNum] = Double.NaN;
										a1DataG[x][y][cycleNum] = Double.NaN;
										k1DataG[x][y][cycleNum] = Double.NaN;
										a2DataG[x][y][cycleNum] = Double.NaN;
										k2DataG[x][y][cycleNum] = Double.NaN;
										a3DataG[x][y][cycleNum] = Double.NaN;
										k3DataG[x][y][cycleNum] = Double.NaN;

									}
									if (fitDouble) {
										Chi2G[x][y][cycleNum] = Double.NaN;
										offsetDataG[x][y][cycleNum] = Double.NaN;
										a1DataG[x][y][cycleNum] = Double.NaN;
										k1DataG[x][y][cycleNum] = Double.NaN;
										a2DataG[x][y][cycleNum] = Double.NaN;
										k2DataG[x][y][cycleNum] = Double.NaN;

									}
									if (fitSingle) {
										Chi2G[x][y][cycleNum] = Double.NaN;
										offsetDataG[x][y][cycleNum] = Double.NaN;
										a1DataG[x][y][cycleNum] = Double.NaN;
										k1DataG[x][y][cycleNum] = Double.NaN;

									}

								} else {

									double[] fittedParam = fitExponentialFunction(timeDataArrayOfArrays[threadIndex],
											pixelsArrayOfArrays[threadIndex], fitparam, maxiteration);
									double Chi2 = fittedParam[0];
									if (Chi2 < Chi2CutOff) {
										if (fitTriple) {
											Chi2G[x][y][cycleNum] = (float) fittedParam[0];
											offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
											a1DataG[x][y][cycleNum] = (float) fittedParam[2];
											k1DataG[x][y][cycleNum] = (float) fittedParam[3];
											a2DataG[x][y][cycleNum] = (float) fittedParam[4];
											k2DataG[x][y][cycleNum] = (float) fittedParam[5];
											a3DataG[x][y][cycleNum] = (float) fittedParam[6];
											k3DataG[x][y][cycleNum] = (float) fittedParam[7];

											// assignment for next level
											Chi2G_t[x][y][cycleNum] = Chi2G[x][y][cycleNum];
											offsetDataG_t[x][y][cycleNum] = offsetDataG[x][y][cycleNum];
											a1DataG_t[x][y][cycleNum] = a1DataG[x][y][cycleNum];
											k1DataG_t[x][y][cycleNum] = k1DataG[x][y][cycleNum];
											a2DataG_t[x][y][cycleNum] = a2DataG[x][y][cycleNum];
											k2DataG_t[x][y][cycleNum] = k2DataG[x][y][cycleNum];
											a3DataG_t[x][y][cycleNum] = a3DataG[x][y][cycleNum];
											k3DataG_t[x][y][cycleNum] = k3DataG[x][y][cycleNum];

										}
										if (fitDouble) {
											Chi2G[x][y][cycleNum] = (float) fittedParam[0];
											offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
											a1DataG[x][y][cycleNum] = (float) fittedParam[2];
											k1DataG[x][y][cycleNum] = (float) fittedParam[3];
											a2DataG[x][y][cycleNum] = (float) fittedParam[4];
											k2DataG[x][y][cycleNum] = (float) fittedParam[5];

											// assignment for next level
											Chi2G_t[x][y][cycleNum] = Chi2G[x][y][cycleNum];
											offsetDataG_t[x][y][cycleNum] = offsetDataG[x][y][cycleNum];
											a1DataG_t[x][y][cycleNum] = a1DataG[x][y][cycleNum];
											k1DataG_t[x][y][cycleNum] = k1DataG[x][y][cycleNum];
											a2DataG_t[x][y][cycleNum] = a2DataG[x][y][cycleNum];
											k2DataG_t[x][y][cycleNum] = k2DataG[x][y][cycleNum];

										}
										if (fitSingle) {
											Chi2G[x][y][cycleNum] = (float) fittedParam[0];
											offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
											a1DataG[x][y][cycleNum] = (float) fittedParam[2];
											k1DataG[x][y][cycleNum] = (float) fittedParam[3];

											// assignment for next level

											Chi2G_t[x][y][cycleNum] = Chi2G[x][y][cycleNum];
											offsetDataG_t[x][y][cycleNum] = offsetDataG[x][y][cycleNum];
											a1DataG_t[x][y][cycleNum] = a1DataG[x][y][cycleNum];
											k1DataG_t[x][y][cycleNum] = k1DataG[x][y][cycleNum];

										}
									} else {
										// if the Chi2 is not below the threshold fill the arrays with NaN
										if (fitTriple) {
											Chi2G[x][y][cycleNum] = Double.NaN;
											offsetDataG[x][y][cycleNum] = Double.NaN;
											a1DataG[x][y][cycleNum] = Double.NaN;
											k1DataG[x][y][cycleNum] = Double.NaN;
											a2DataG[x][y][cycleNum] = Double.NaN;
											k2DataG[x][y][cycleNum] = Double.NaN;
											a3DataG[x][y][cycleNum] = Double.NaN;
											k3DataG[x][y][cycleNum] = Double.NaN;

										}
										if (fitDouble) {
											Chi2G[x][y][cycleNum] = Double.NaN;
											offsetDataG[x][y][cycleNum] = Double.NaN;
											a1DataG[x][y][cycleNum] = Double.NaN;
											k1DataG[x][y][cycleNum] = Double.NaN;
											a2DataG[x][y][cycleNum] = Double.NaN;
											k2DataG[x][y][cycleNum] = Double.NaN;

										}
										if (fitSingle) {
											Chi2G[x][y][cycleNum] = Double.NaN;
											offsetDataG[x][y][cycleNum] = Double.NaN;
											a1DataG[x][y][cycleNum] = Double.NaN;
											k1DataG[x][y][cycleNum] = Double.NaN;

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
			IJ.log("time taken to finish cycle " + Integer.toString(cycle) + "(in seconds) "
					+ Double.toString(timeToCompletion / 1000));
			Photoswitching_Phasor_Plotter.statusMessageArea.setText("Fitting time: " + timeToCompletion);
			Photoswitching_Phasor_Plotter.statusMessageArea
					.update(Photoswitching_Phasor_Plotter.statusMessageArea.getGraphics());
			if (LogFitTime == true) {
				// can be helpful if running multiple cycles
				IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
			}
		} // end of cycles
		wmkImage = createWeightedRateConstantsImage();
		unMixPuxelValueUsingExponentialFit();
	}

	// *******************Utilities*******************************

	private double[] initExpFittingComponents(double firstframeint, double lastframeint, double tau, int cycleNum,
			int x, int y) {

		double guess_o = 0;
		double guess_a1 = 0;
		double guess_k1 = 0;
		double guess_a2 = 0;
		double guess_k2 = 0;
		double guess_a3 = 0;
		double guess_k3 = 0;

		// initialization

		if (cycleNum > 0) {
			if (useFitToinit) {
				if (fitTriple) {
					guess_o = offsetDataG_t[x][y][cycleNum - 1];
					guess_a1 = a1DataG_t[x][y][cycleNum - 1];
					guess_k1 = k1DataG_t[x][y][cycleNum - 1];
					guess_a2 = a2DataG_t[x][y][cycleNum - 1];
					guess_k2 = k2DataG_t[x][y][cycleNum - 1];
					guess_a3 = a3DataG_t[x][y][cycleNum - 1];
					guess_k3 = k3DataG_t[x][y][cycleNum - 1];

				}
				if (fitDouble) {

					guess_o = offsetDataG_t[x][y][cycleNum - 1];
					guess_a1 = a1DataG_t[x][y][cycleNum - 1];
					guess_k1 = k1DataG_t[x][y][cycleNum - 1];
					guess_a2 = a2DataG_t[x][y][cycleNum - 1];
					guess_k2 = k2DataG_t[x][y][cycleNum - 1];
					guess_a3 = 0;
					guess_k3 = 0;
				}
				if (fitSingle) {
					guess_o = offsetDataG_t[x][y][cycleNum - 1];
					guess_a1 = a1DataG_t[x][y][cycleNum - 1];
					guess_k1 = k1DataG_t[x][y][cycleNum - 1];
					guess_a2 = 0;
					guess_k2 = 0;
					guess_a3 = 0;
					guess_k3 = 0;
				}
			}

		} else {
			if (fitTriple) {
				guess_o = lastframeint;
				guess_a1 = (firstframeint - lastframeint) / 3;
				guess_k1 = 1 / tau;
				guess_a2 = (firstframeint - lastframeint) / 3;
				guess_k2 = 1 / tau;
				guess_a3 = (firstframeint - lastframeint) / 3;
				guess_k3 = 1 / tau;

			}
			if (fitDouble) {
				guess_o = lastframeint;
				guess_a1 = (firstframeint - lastframeint) / 2;
				guess_k1 = 1 / tau;
				guess_a2 = (firstframeint - lastframeint) / 2;
				guess_k2 = 1 / tau;
				guess_a3 = 0;
				guess_k3 = 0;
			}
			if (fitSingle) {
				guess_o = lastframeint;
				guess_a1 = firstframeint - lastframeint;
				guess_k1 = 1 / tau;
				guess_a2 = 0;
				guess_k2 = 0;
				guess_a3 = 0;
				guess_k3 = 0;
			}
		}

		double[] fitparam = { 0, guess_o, guess_a1, guess_k1, guess_a2, guess_k2, guess_a3, guess_k3 };

		return fitparam;

	}

	private double[] getTimingPerPlane(String arg, int tPoints, int currZ, int currCh) throws Exception {
		// this uses Bioformats to get the timing of the images
		// from the start of the experiment
		// if the plane delta T is not found, user is prompted for a time interval input
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

	public double findTauEstimate(double[] x, double[] y, double startIntensity, double endIntensity) {
		// this method is used to find an initial guess for the rate constant
		double tauToReturn = 1;
		double previousIntensity = startIntensity - endIntensity;
		for (int t = 0; t < y.length; t++) {
			if ((y[t] - endIntensity) <= ((startIntensity - endIntensity) * 0.37)
					&& previousIntensity >= ((startIntensity - endIntensity) * 0.37)) {
				tauToReturn = x[t];
			}
			previousIntensity = y[t] - endIntensity;
		}
		return tauToReturn;
	}

	/*
	 * @Override public double userFunction(double[] par, double x) { if (fitDouble)
	 * { return par[0] * Math.exp(-par[1] * x) + par[2] * Math.exp(-par[3] * x) +
	 * par[4]; } return par[0] * Math.exp(-par[1] * x) + par[2]; }
	 */
	private double[] fitExponentialFunction(double[] x, double[] y, double[] paramsPassed, int maxIter) {
		double[] params = new double[0];
		if (fitTriple) {
			params = new double[paramsPassed.length - 1];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);
			params[2] = Math.sqrt(params[2]);
			params[3] = Math.sqrt(params[3]);
			params[4] = Math.sqrt(params[4]);
			params[5] = Math.sqrt(params[5]);
			params[6] = Math.sqrt(params[6]);
			// The initial guesses are converted to their square root
			// The fitting equations use the squares of the components which we want to
			// force to be positive
		}
		if (fitDouble) {
			params = new double[paramsPassed.length - 3];
			System.arraycopy(paramsPassed, 1, params, 0, params.length);
			params[1] = Math.sqrt(params[1]);
			params[2] = Math.sqrt(params[2]);
			params[3] = Math.sqrt(params[3]);
			params[4] = Math.sqrt(params[4]);
			// The initial guesses are converted to their square root
			// The fitting equations use the squares of the components which we want to
			// force to be positive
		}
		double[] returnArray = new double[paramsPassed.length];
		Arrays.fill(returnArray, 0);

		double[] paramVariations = UtilityFunction.multiplyArrayByValue(params, varParam);
		CurveFitter cf = new CurveFitter(x, y);

		if (fitDouble || fitTriple) {
			// Have to use a user function for the 2 and 3 component fits
			// these are slow and not terribly accurate for the 3

			// cf.setOffsetMultiplySlopeParams(0, -1, -1);
			cf.setMaxIterations(maxIter);
			cf.doCustomFit(new UserFunction() {
				@Override
				public double userFunction(double[] par, double x) {
					if (fitTriple) {
						return par[0] + (par[1] * par[1] * Math.exp(-par[2] * par[2] * x))
								+ (par[3] * par[3] * Math.exp(-par[4] * par[4] * x))
								+ (par[5] * par[5] * Math.exp(-par[6] * par[6] * x));
					} else {
						return par[0] + (par[1] * par[1] * Math.exp(-par[2] * par[2] * x))
								+ (par[3] * par[3] * Math.exp(-par[4] * par[4] * x));
					}
				}
			}, params.length, "", params, paramVariations, false);
			double[] paramToReturn = cf.getParams();
			double Chi2ToReturn = calculateReducedChi2(cf.getResiduals(), y);
			returnArray[0] = Chi2ToReturn;
			returnArray[1] = paramToReturn[0];

			if (fitTriple) {// Since the faster and slower components can be any of the three, this section
							// rearranges them for final output
				double[] arrayOfValues = new double[3];
				arrayOfValues[0] = paramToReturn[2];
				arrayOfValues[1] = paramToReturn[4];
				arrayOfValues[2] = paramToReturn[6];

				int[] arrayOfIndices = new int[3];
				arrayOfIndices[0] = 2;
				arrayOfIndices[1] = 4;
				arrayOfIndices[2] = 6;

				int maxValueIndex = UtilityFunction.getIndexOfMaxValue(arrayOfIndices, arrayOfValues);
				int minValueIndex = UtilityFunction.getIndexOfMinValue(arrayOfIndices, arrayOfValues);
				int midValueIndex = UtilityFunction.getIndexOfMiddle(arrayOfIndices, arrayOfValues);

				returnArray[2] = paramToReturn[maxValueIndex - 1] * paramToReturn[maxValueIndex - 1];
				returnArray[3] = Math.pow(paramToReturn[maxValueIndex], 2);
				returnArray[4] = paramToReturn[midValueIndex - 1] * paramToReturn[midValueIndex - 1];
				returnArray[5] = Math.pow(paramToReturn[midValueIndex], 2);
				returnArray[6] = paramToReturn[minValueIndex - 1] * paramToReturn[minValueIndex - 1];
				returnArray[7] = Math.pow(paramToReturn[minValueIndex], 2);
			}
			if (fitDouble) {// Since the faster and slower components can be either of the two, this section
							// rearranges them for final output
				double[] arrayOfValues = new double[2];
				arrayOfValues[0] = paramToReturn[2];
				arrayOfValues[1] = paramToReturn[4];

				int[] arrayOfIndices = new int[2];
				arrayOfIndices[0] = 2;
				arrayOfIndices[1] = 4;

				int maxValueIndex = UtilityFunction.getIndexOfMaxValue(arrayOfIndices, arrayOfValues);
				int minValueIndex = UtilityFunction.getIndexOfMinValue(arrayOfIndices, arrayOfValues);

				returnArray[2] = paramToReturn[maxValueIndex - 1] * paramToReturn[maxValueIndex - 1];
				returnArray[3] = Math.pow(paramToReturn[maxValueIndex], 2);
				returnArray[4] = paramToReturn[minValueIndex - 1] * paramToReturn[minValueIndex - 1];
				returnArray[5] = Math.pow(paramToReturn[minValueIndex], 2);
			}
		} else {
			// for the single exponential with offset fit
			// we use the built in version
			// we found it to be ~5 faster than the same type in the user function
			cf = new CurveFitter(x, y);
			double errotTol = 10;
			double[] fitparam = { paramsPassed[2], paramsPassed[3], paramsPassed[1], maxiteration, numRestarts,
					errotTol };
			cf.setInitialParameters(fitparam);
			cf.doFit(11); // exponential decay with offset: (A*exp^(-k*t))+offset
			double[] fittedParam = cf.getParams();
			double R2 = cf.getFitGoodness();
			double[] residuals = cf.getResiduals();
			double Chi2 = calculateReducedChi2(residuals, y);

			returnArray[0] = Chi2;
			returnArray[1] = fittedParam[2];
			returnArray[2] = fittedParam[0];
			returnArray[3] = fittedParam[1];
		}

		IJ.log(Integer.toString(cf.getIterations()));// this prints the number of iteration actually used
		return returnArray;
	}

	// makestatic
	public double calculateReducedChi2(double[] residualArray, double[] dataArray) {
		// returns a reduced Chi2
		// pixel values are converted into photoelectrons using the camera gain
		// since we we also bin pixels, are using a camera and we are still limited by
		// diffraction
		// pixels within the PSF are expected to show some correlated noise
		// thus covariance within the PSF is used toi estimate the variance
		// along with the number of photoelectrons when calculating the weighted reduced
		// Chi2
		double AiryRadius = 1.22 * lambda / NA;
		double PSFArea = Math.PI * AiryRadius * AiryRadius;
		double numPixelsInPSF = PSFArea / (pixSize * pixSize);
		double chi2ToReturn = 0;
		double weightedAverageVariance = 0;
		double sumWeightedVariance = 0;

		residualArray = UtilityFunction.multiplyArrayByValue(residualArray, binFactorImage * binFactorImage);// in case
																												// of
																												// post-processing
																												// bin
																												// averaging
																												// to
																												// get
																												// total
																												// signal
		dataArray = UtilityFunction.multiplyArrayByValue(dataArray, binFactorImage * binFactorImage);

		residualArray = UtilityFunction.divideArrayByValue(residualArray, cameraGain);// convert to electrons
		dataArray = UtilityFunction.divideArrayByValue(dataArray, cameraGain);// convert to electrons; this will also be
																				// the uncorrelated variance
		double[] coVarArray = UtilityFunction.multiplyArrayByValue(dataArray, binFactor * binFactor / numPixelsInPSF);
		// if occuring covariance should be within a PSF. The total signal is scaled by
		// the
		// number of unbinned pixels (number of actual measurements) in a PSF
		// these would be the camera pixels which may have correlated noise
		// this is more of a problem with larger Rois
		// I don't think this is an issue with TCSPC FLIM imaging, which the field from
		// which I'm trying to borrow methods
		dataArray = UtilityFunction.addTwoArrays(dataArray, coVarArray);// total variance

		for (int da = 0; da < dataArray.length; da++) {
			if (dataArray[da] > 0)// if the data are negative or zero, do not use in Chi2 determination
				sumWeightedVariance += (1 / dataArray[da]);
		}
		weightedAverageVariance = sumWeightedVariance / dataArray.length;
		double[] weightArray = new double[dataArray.length];
		for (int wa = 0; wa < dataArray.length; wa++) {
			if (dataArray[wa] > 0)// if the data are negative or zero, do not use in Chi2 determination
				weightArray[wa] = (1 / dataArray[wa]) / weightedAverageVariance;
		}
		double[] residualArray2 = UtilityFunction.multiplyTwoArrays(residualArray, residualArray);
		// double[] arrayToSum = multiplyTwoArrays(residualArray2, weightArray);
		double[] arrayToSum = new double[residualArray2.length];
		for (int tp = 0; tp < arrayToSum.length; tp++) {
			if (dataArray[tp] > 0)
				arrayToSum[tp] = residualArray2[tp] * weightArray[tp];
			if (!Double.isInfinite(arrayToSum[tp])) {
				chi2ToReturn += arrayToSum[tp];
			}
		}
		int df;
		if (fitTriple)
			df = 8;
		if (fitDouble)
			df = 6;
		else
			df = 4;
		return (chi2ToReturn / (residualArray.length - df)) * weightedAverageVariance;
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

	public ImagePlus createWeightedRateConstantsImage() {
		// plots a weighted rate constant image
		ImagePlus imp = IJ.createImage("WeightedRateConstantsImage", "32-bit", imageW, imageH, numCycles);
		float fMax=8;
		for (int cyc = 0; cyc < numCycles; cyc++) {
			imp.setSlice(cyc + 1);
			ImageProcessor ip = imp.getProcessor();
			FloatProcessor fip = (FloatProcessor) ip.convertToFloat();
			for (int y = 0; y < imageH; y++) {
				for (int x = 0; x < imageW; x++) {
					if (Double.isNaN(k1DataG[x][y][cyc])) {
						fip.setf(x, y, Float.NaN);
					} else {
						if (fitTriple){
							float f=(float) (((a1DataG[x][y][cyc] * k1DataG[x][y][cyc])
							+ (a2DataG[x][y][cyc] * k2DataG[x][y][cyc])
							+ (a3DataG[x][y][cyc] * k3DataG[x][y][cyc]))
							/ (a1DataG[x][y][cyc] + a2DataG[x][y][cyc] + a3DataG[x][y][cyc]));


							fip.setf(x, y,((f>fMax)?0:f));
						}
						if (fitDouble){

							float f=(float)(((a1DataG[x][y][cyc] * k1DataG[x][y][cyc])
							+ (a2DataG[x][y][cyc] * k2DataG[x][y][cyc]))
							/ (a1DataG[x][y][cyc] + a2DataG[x][y][cyc]));
							//fip.setf(x, y,f);
							fip.setf(x, y,((f>fMax)?0:f));
									 
						}
						if (fitSingle)
							fip.setf(x, y, (float) (k1DataG[x][y][cyc]));
					}
				}
			}
			//fip.max(10);
			fip.min(0);
			IJ.resetMinAndMax(imp);

			
		}
		imp.show();
		return imp;
	}

	public void unMixPuxelValueUsingExponentialFit() {
		// if the fractional contributions are calculated by fits using
		// psFRET_Fit_Azero_Exponential()
		// this will transfer those to the channel arrays and used to create unmixed
		// images
		// use ROI to define exclusively one channel or another
		arrayChA = new double[imageW][imageH][numCycles];
		arrayChB = new double[imageW][imageH][numCycles];
		arrayChC = new double[imageW][imageH][numCycles];

		for (int cyc = 0; cyc < numCycles; cyc++) {
			for (int y = 0; y < imageH; y++) {
				for (int x = 0; x < imageW; x++) {

					if (fitTriple | fitDouble | fitSingle) {

						if (!Double.isNaN(a1DataG[x][y][cyc])) {
							arrayChA[x][y][cyc] = a1DataG[x][y][cyc];
						} else {
							arrayChA[x][y][cyc] = Double.NaN;
						}
					}

					if (fitTriple | fitDouble) {

						if (!Double.isNaN(a2DataG[x][y][cyc])) {
							arrayChB[x][y][cyc] = a2DataG[x][y][cyc];
						} else {
							arrayChB[x][y][cyc] = Double.NaN;
						}
					}

					if (fitTriple) {
						if (!Double.isNaN(a3DataG[x][y][cyc])) {
							arrayChC[x][y][cyc] = a3DataG[x][y][cyc];
						} else {
							arrayChC[x][y][cyc] = Double.NaN;
						}
					}

				}
			}
		}


		if (fitTriple | fitDouble | fitSingle) 
			createUnmixedImage(chA_name, arrayChA);
		if (fitTriple | fitDouble )
			createUnmixedImage(chB_name, arrayChB);
		if (fitTriple)
			createUnmixedImage(chC_name, arrayChC);

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
					if ((Double.isNaN(theArray[x][y][cyc]))) {
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
