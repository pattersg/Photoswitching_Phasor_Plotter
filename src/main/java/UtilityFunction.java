
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



public class UtilityFunction {


	static double[] addTwoArrays(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			IJ.showMessage("psFRET_T_Profiler", "The time and data arrays are not the same length");
			return null;
		}
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] + array2[i];
		}
		return arrayToReturn;
	}


	static double[] subtractValueFromArray(double[] array1, double theValue) {
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] - theValue;
		}
		return arrayToReturn;
	}

	public static double[] addValueToArray(double[] array1, double theValue) {
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] + theValue;
		}
		return arrayToReturn;
	}

	public static double[] divideArrayByValue(double[] array1, double theValue) {
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] / theValue;
		}
		return arrayToReturn;
	}

	public static double getMeanOfArray(double[] theArray) {
		double sum = 0;
		for (int i = 0; i < theArray.length; i++) {
			sum = sum + theArray[i];
		}
		return sum / theArray.length;
	}

	public static double getMedianOfArray(double[] theArray) {
		Arrays.sort(theArray);
		if(theArray.length%2!=0)
			return (double) theArray[theArray.length/2];
		return (double) (theArray[(theArray.length-1)/2] + theArray[(theArray.length)/2])/2;
	}

	public static double getSumOfArray(double[] theArray) {
		double sum = 0;
		for (int i = 0; i < theArray.length; i++) {
			sum = sum + theArray[i];
		}
		return sum;
	}

	public static double[] multiplyArrayByValue(double[] array1, double theValue) {
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i]*theValue;
		}
		return arrayToReturn;
	}


	public static double[] subtractArrayFromArray(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			IJ.showMessage("Phasor Plotter", "The time and data arrays are not the same length");
			return null;
		}
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] - array2[i];
		}
		return arrayToReturn;

	}

	public static double[] multiplyTwoArrays(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			IJ.showMessage("Phasor Plotter", "The time and data arrays are not the same length");
			return null;
		}
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] * array2[i];
		}
		return arrayToReturn;
	}

	public static double[] divideTwoArrays(double[] array1, double[] array2) {
		if (array1.length != array2.length) {
			IJ.showMessage("Phasor Plotter", "The time and data arrays are not the same length");
			return null;
		}
		double[] arrayToReturn = new double[array1.length];
		for (int i = 0; i < array1.length; i++) {
			arrayToReturn[i] = array1[i] / array2[i];
		}
		return arrayToReturn;
	}

	public static int getIndexOfMaxValue(int [] indexArray, double [] valueArray){
		double max = valueArray[0];
		int maxIndex = indexArray[0];
		for(int i=0; i<valueArray.length;i++){
			if(valueArray[i]>max){
				max=valueArray[i];
				maxIndex=indexArray[i];
			}
		}
		return maxIndex;
	}

	public static int getIndexOfMinValue(int [] indexArray, double [] valueArray){
		double min = valueArray[0];
		int minIndex = indexArray[0];
		for(int i=0; i<valueArray.length;i++){
			if(valueArray[i]<min){
				min=valueArray[i];
				minIndex=indexArray[i];
			}
		}
		return minIndex;
	}

	public static int getIndexOfMiddle(int [] indexArray, double [] valueArray){
		int maxIndex = getIndexOfMaxValue(indexArray, valueArray);
		int minIndex = getIndexOfMinValue(indexArray, valueArray);
		int midIndex = indexArray[indexArray.length-1];
		for(int i=0; i<indexArray.length;i++){
			if(indexArray[i]!=maxIndex && indexArray[i]!=minIndex){
				midIndex=indexArray[i];
			}
		}
		return midIndex;
	}

	public static double[][] getCalibratedPixelsFromPlotWindow(Plot thePlot) {
		//this gets the points within the ROI draw on a phasor plot
		//it allows selection of subsets of data or a way to determine
		//mean values for phase and modulation of data sets
		//the first version required rectanglular ROIs since they are easy
		//it was updated to use any ROI later
		ImagePlus plotImg = thePlot.getImagePlus();
		Roi roi = plotImg.getRoi();
		Point[] pts = roi.getContainedPoints();
		double[][] returnArray = new double [2][pts.length];
		Calibration cal = plotImg.getCalibration();
		for(int i=0;i<pts.length;i++){
			returnArray[0][i]=cal.getX(pts[i].x);
			returnArray[1][i]=-(cal.getY(pts[i].y));
		}
		return returnArray;
	}


}
