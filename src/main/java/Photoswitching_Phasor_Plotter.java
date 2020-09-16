/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pattersg
 */
import ij.*;
import ij.IJ;
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

//public class Photoswitching_Phasor_Plotter extends javax.swing.JFrame implements MouseListener, MouseMotionListener, Measurements, KeyListener  {
public class Photoswitching_Phasor_Plotter extends javax.swing.JFrame implements  MouseListener, MouseMotionListener, Measurements, KeyListener{    
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
    ImagePlus img;
    ImageCanvas canvas;
    ImageCanvas canvas2;
    PlotWindow pwin;
    PlotWindow pwin2;
    public double[] yAxis;
    public double[] xAxis;
    String xLabel;
    String yLabel;
    boolean listenersRemoved;
    int xpoint, ypoint;
    ImagePlus rateConstantImage;
    ImagePlus GmDataImage;
    ImagePlus GsDataImage;
    ImagePlus Chi2Image;
    ImagePlus wmkImage;
    ImagePlus a1Image;
    ImagePlus k1Image;
    ImagePlus a2Image;
    ImagePlus k2Image;
    ImagePlus a3Image;
    ImagePlus k3Image;
    ImagePlus a4Image;
    ImagePlus k4Image;
    ImagePlus a5Image;
    ImagePlus k5Image;
    ImagePlus offsetImage;
    boolean chWarnOff;
    boolean yLog;
    boolean LogPhasorTime;
    int cameraOffset;
    int threshold;
    PlotWindow phasorPlotWin;
    Plot phasorPlot;
    ImagePlus phasorPlotImg;
    ImageProcessor imP;
    boolean medianFilter;
    int applicationsMedianFilter;
    boolean backgroundSubtract;
    double[] backGroundValues;
    double[] bkGrd;
    
    double terminalThreshold;
    
    boolean LogFitTime;
    int maxiteration;
    int numRestarts;
    boolean fitSingle;
    static boolean fitDouble;
    static boolean fitTriple;
    static int binFactor;
    static double cameraGain;
    private static double lambda;
    private static double NA;
    private static double pixSize;
    private static int binFactorImage;
    double varParam;

    boolean useTOneHalfEstimate;
    
    double chA_Gmean;
    double chA_Smean;
    double chB_Gmean;
    double chB_Smean;
    double chC_Gmean;
    double chC_Smean;
    double chD_Gmean;
    double chD_Smean;
    double chE_Gmean;
    double chE_Smean;
    
    double[][] phasorPlotROIChA;
    double[][] phasorPlotROIChB;
    double[][] phasorPlotROIChC;
    double[][] phasorPlotROIChD;
    double[][] phasorPlotROIChE;
    
    String chA_name;
    String chB_name;
    String chC_name;
    String chD_name;
    String chE_name;
    
    boolean useChA;
    boolean useChB;
    boolean useChC;
    boolean useChD;
    boolean useChE;
    
    double[][][] arrayChA;
    double[][][] arrayChB;
    double[][][] arrayChC;
    double[][][] arrayChD;
    double[][][] arrayChE;
    
    double[] arrayGToSend;
    double[] arraySToSend;
    double[] fracContribArray;
    
    double[][][] pixelSumData;
    
    boolean useManualTime;
    
    double chA_Kmean;
    double chB_Kmean;
    double chC_Kmean;
    double chD_Kmean;
    double chE_Kmean;
    
    AzeroCurveFitter cfAzeroInst=new AzeroCurveFitter();
            
    /**
     * Creates new form Photoswitching_Phasor_Plotter
     */
    public Photoswitching_Phasor_Plotter() {
        initComponents();
        setVisible(true);
        useTOneHalfEstimate = useTOneEstimateCB.isSelected(); 
        backgroundSubtract = backgroundSubtractCB.isSelected();
        medianFilter = medianFilterCB.isSelected();
        fitSingle = checkFitSingle.isSelected();
        fitDouble = checkFitDouble.isSelected();
        fitTriple = checkFitTriple.isSelected();
        statusMessageArea.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        Control = new javax.swing.JPanel();
        fitExponential = new javax.swing.JButton();
        makeRateConstantImage = new javax.swing.JButton();
        ExaminePixels = new javax.swing.JButton();
        MakeSavePhasorParameterImage = new javax.swing.JButton();
        UseLogScale = new javax.swing.JCheckBox();
        selectFromPhasorPlot = new javax.swing.JButton();
        manualTimeCB = new javax.swing.JCheckBox();
        HybridFitCB=new javax.swing.JCheckBox();
        fitAzeroFixedExponential = new javax.swing.JButton();
        Phasor = new javax.swing.JPanel();
        HarmonicToPlotLabel = new javax.swing.JLabel();
        FrequencyToPlotTF = new javax.swing.JFormattedTextField();
        medianFilterCB = new javax.swing.JCheckBox();
        applicationsMedianFilterLabel = new javax.swing.JLabel();
        applicationMedFilterTF = new javax.swing.JFormattedTextField();
        useTOneEstimateCB = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        thresholdLabel = new javax.swing.JLabel();
        thresholdTF = new javax.swing.JFormattedTextField();
        terminalThresholdTF = new javax.swing.JFormattedTextField();
        terminalThresholdLabel = new javax.swing.JLabel();
        PhasorStack = new javax.swing.JButton();
        logPhasorTimes = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        backGrdSubtract = new javax.swing.JButton();
        backgroundSubtractCB = new javax.swing.JCheckBox();
        Fitting = new javax.swing.JPanel();
        pixelThresholdCutoffLabel = new javax.swing.JLabel();
        PixelThresholdCutOffTF = new javax.swing.JFormattedTextField();
        maxIterationsLabel = new javax.swing.JLabel();
        maxIterationsTF = new javax.swing.JFormattedTextField();
        numRestartsLabel = new javax.swing.JLabel();
        numRestartsTF = new javax.swing.JFormattedTextField();
        checkFitSingle = new javax.swing.JCheckBox();
        checkFitDouble = new javax.swing.JCheckBox();
        Chi2CutoffLabel = new javax.swing.JLabel();
        Chi2CutOffTF = new javax.swing.JFormattedTextField();
        checkFitTriple = new javax.swing.JCheckBox();
        varParamLabel = new javax.swing.JLabel();
        varParamTF = new javax.swing.JFormattedTextField();
        Experimental = new javax.swing.JPanel();
        numCyclesLabel = new javax.swing.JLabel();
        numCyclesTF = new javax.swing.JFormattedTextField();
        imagesPerCycleLabel = new javax.swing.JLabel();
        imagesPerCycleTF = new javax.swing.JFormattedTextField();
        cameraOffsetTF = new javax.swing.JFormattedTextField();
        cameraOffsetLabel = new javax.swing.JLabel();
        binFactorLabel = new javax.swing.JLabel();
        binFactorTF = new javax.swing.JFormattedTextField();
        cameraGainLabel = new javax.swing.JLabel();
        cameraGainTF = new javax.swing.JFormattedTextField();
        ObjectiveNAText = new javax.swing.JLabel();
        ObjectiveNATF = new javax.swing.JTextField();
        emissionLambdaTF = new javax.swing.JTextField();
        emissionLambdaText = new javax.swing.JLabel();
        emissionLambdaText1 = new javax.swing.JLabel();
        pixelSizeTF = new javax.swing.JTextField();
        binFactorImageTF = new javax.swing.JTextField();
        binFactorImageText = new javax.swing.JLabel();
        ImagePixelSizeText = new javax.swing.JLabel();
        imagePixelSizeTF = new javax.swing.JTextField();
        ImagePixelSizeText1 = new javax.swing.JLabel();
        Unmixing = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        chA_Gmean_TF = new javax.swing.JFormattedTextField();
        chA_Smean_TF = new javax.swing.JFormattedTextField();
        chB_Gmean_TF = new javax.swing.JFormattedTextField();
        chB_Smean_TF = new javax.swing.JFormattedTextField();
        chC_Gmean_TF = new javax.swing.JFormattedTextField();
        chC_Smean_TF = new javax.swing.JFormattedTextField();
        chD_Gmean_TF = new javax.swing.JFormattedTextField();
        chD_Smean_TF = new javax.swing.JFormattedTextField();
        chE_Gmean_TF = new javax.swing.JFormattedTextField();
        chE_Smean_TF = new javax.swing.JFormattedTextField();
        unMixSignals = new javax.swing.JButton();
        getChannelAROI = new javax.swing.JButton();
        getChannelBROI = new javax.swing.JButton();
        getChannelCROI = new javax.swing.JButton();
        getChannelDROI = new javax.swing.JButton();
        getChannelEROI = new javax.swing.JButton();
        chA_Name_TF = new javax.swing.JFormattedTextField();
        chB_Name_TF = new javax.swing.JFormattedTextField();
        chC_Name_TF = new javax.swing.JFormattedTextField();
        chD_Name_TF = new javax.swing.JFormattedTextField();
        chE_Name_TF = new javax.swing.JFormattedTextField();
        useChA_CB = new javax.swing.JCheckBox();
        useChB_CB = new javax.swing.JCheckBox();
        useChC_CB = new javax.swing.JCheckBox();
        useChD_CB = new javax.swing.JCheckBox();
        useChE_CB = new javax.swing.JCheckBox();
        readReferenceData = new javax.swing.JButton();
        saveUnmixed = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        chA_Kmean_TF = new javax.swing.JFormattedTextField();
        chB_Kmean_TF = new javax.swing.JFormattedTextField();
        chC_Kmean_TF = new javax.swing.JFormattedTextField();
        chD_Kmean_TF = new javax.swing.JFormattedTextField();
        chE_Kmean_TF = new javax.swing.JFormattedTextField();
        unMixSignalsUsingFit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusMessageArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("psPhasor Plotter");
        setResizable(false);

        fitExponential.setText("Fit Pixels with Exponential");
        fitExponential.setMaximumSize(new java.awt.Dimension(200, 30));
        fitExponential.setPreferredSize(new java.awt.Dimension(200, 30));
        fitExponential.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitExponentialActionPerformed(evt);
            }
        });

        makeRateConstantImage.setText("Remake rate constant image");
        makeRateConstantImage.setToolTipText("");
        makeRateConstantImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeRateConstantImageActionPerformed(evt);
            }
        });

        ExaminePixels.setText("Examine pixels");
        ExaminePixels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExaminePixelsActionPerformed(evt);
            }
        });

        MakeSavePhasorParameterImage.setText("Save phasor parameters");
        MakeSavePhasorParameterImage.setMaximumSize(new java.awt.Dimension(200, 30));
        MakeSavePhasorParameterImage.setPreferredSize(new java.awt.Dimension(200, 30));
        MakeSavePhasorParameterImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MakeSavePhasorParameterImageActionPerformed(evt);
            }
        });

        UseLogScale.setText("Set log scale for pixel viewer");
        UseLogScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UseLogScaleActionPerformed(evt);
            }
        });

        selectFromPhasorPlot.setText("Select From Phasor Plot");
        selectFromPhasorPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFromPhasorPlotActionPerformed(evt);
            }
        });

        manualTimeCB.setText("Manually input time interval");
        manualTimeCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                manualTimeCBStateChanged(evt);
            }
        });
        manualTimeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualTimeCBActionPerformed(evt);
                
            }
        });
        
        //hybrid fit
        HybridFitCB.setText("Use hybrid fitting");
        HybridFitCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                //manualTimeCBStateChanged(evt);
                hybridFittateChanged(evt);
            }
        });
        HybridFitCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               // manualTimeCBActionPerformed(evt);
                hybridStateCBActionPerformed(evt);
                
            }
        });

        fitAzeroFixedExponential.setText("Fit Pixels for Azero with Fixed Exponential");
        fitAzeroFixedExponential.setMaximumSize(new java.awt.Dimension(200, 30));
        fitAzeroFixedExponential.setPreferredSize(new java.awt.Dimension(200, 30));
        fitAzeroFixedExponential.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fitAzeroFixedExponentialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ControlLayout = new javax.swing.GroupLayout(Control);
        Control.setLayout(ControlLayout);
        ControlLayout.setHorizontalGroup(
            ControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ControlLayout.createSequentialGroup()
                .addContainerGap(105, Short.MAX_VALUE)
                .addGroup(ControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(manualTimeCB)
                    .addComponent(HybridFitCB)
                    .addComponent(selectFromPhasorPlot, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MakeSavePhasorParameterImage, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fitExponential, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(makeRateConstantImage, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UseLogScale)
                    .addComponent(ExaminePixels, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ControlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fitAzeroFixedExponential, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
		ControlLayout.setVerticalGroup(
            ControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ControlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fitExponential, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fitAzeroFixedExponential, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(manualTimeCB)
                .addGap(13, 13, 13)
                .addComponent(HybridFitCB)
                .addGap(41, 41, 41)
                .addComponent(makeRateConstantImage)
                .addGap(44, 44, 44)
                .addComponent(ExaminePixels, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UseLogScale)
                .addGap(18, 18, 18)
                .addComponent(selectFromPhasorPlot)
                .addGap(18, 18, 18)
                .addComponent(MakeSavePhasorParameterImage, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Control", Control);

        HarmonicToPlotLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        HarmonicToPlotLabel.setText("Harmonic to plot");

        FrequencyToPlotTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        FrequencyToPlotTF.setText("1");
        FrequencyToPlotTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FrequencyToPlotTFActionPerformed(evt);
            }
        });
        FrequencyToPlotTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                FrequencyToPlotTFPropertyChange(evt);
            }
        });

        medianFilterCB.setSelected(true);
        medianFilterCB.setText("Median filter 3x3");
        medianFilterCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medianFilterCBActionPerformed(evt);
            }
        });

        applicationsMedianFilterLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        applicationsMedianFilterLabel.setText("Applications of  median filter ");

        applicationMedFilterTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        applicationMedFilterTF.setText("1");
        applicationMedFilterTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applicationMedFilterTFActionPerformed(evt);
            }
        });
        applicationMedFilterTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                applicationMedFilterTFPropertyChange(evt);
            }
        });

        useTOneEstimateCB.setText("Use T1/2 estimate");
        useTOneEstimateCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useTOneEstimateCBStateChanged(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        thresholdLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        thresholdLabel.setText("Pixel threshold (time zero)");

        thresholdTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        thresholdTF.setText("500");
        thresholdTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thresholdTFActionPerformed(evt);
            }
        });
        thresholdTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                thresholdTFPropertyChange(evt);
            }
        });

        terminalThresholdTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        terminalThresholdTF.setText("0.00");
        terminalThresholdTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terminalThresholdTFActionPerformed(evt);
            }
        });
        terminalThresholdTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                terminalThresholdTFPropertyChange(evt);
            }
        });

        terminalThresholdLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        terminalThresholdLabel.setText("Terminal pixel threshold");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(thresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(terminalThresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(terminalThresholdTF, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(thresholdTF))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thresholdTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(terminalThresholdTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(terminalThresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        PhasorStack.setText("Run phasor analysis on pixels");
        PhasorStack.setMaximumSize(new java.awt.Dimension(200, 30));
        PhasorStack.setMinimumSize(new java.awt.Dimension(200, 30));
        PhasorStack.setPreferredSize(new java.awt.Dimension(200, 30));
        PhasorStack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhasorStackActionPerformed(evt);
            }
        });

        logPhasorTimes.setText("Log the time required for the phasor");
        logPhasorTimes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logPhasorTimesActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        backGrdSubtract.setText("Get Background To Subtract");
        backGrdSubtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backGrdSubtractActionPerformed(evt);
            }
        });

        backgroundSubtractCB.setText("Background Subtract");
        backgroundSubtractCB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                backgroundSubtractCBStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(backGrdSubtract))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(backgroundSubtractCB)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(backgroundSubtractCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backGrdSubtract)
                .addContainerGap())
        );

        javax.swing.GroupLayout PhasorLayout = new javax.swing.GroupLayout(Phasor);
        Phasor.setLayout(PhasorLayout);
        PhasorLayout.setHorizontalGroup(
            PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PhasorLayout.createSequentialGroup()
                .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PhasorLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(medianFilterCB)
                            .addComponent(useTOneEstimateCB))
                        .addGap(0, 228, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(PhasorLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logPhasorTimes)
                    .addComponent(PhasorStack, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PhasorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PhasorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PhasorLayout.createSequentialGroup()
                            .addGap(222, 222, 222)
                            .addComponent(applicationMedFilterTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(applicationsMedianFilterLabel))
                    .addGroup(PhasorLayout.createSequentialGroup()
                        .addComponent(HarmonicToPlotLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(FrequencyToPlotTF, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(85, 85, 85))
        );
        PhasorLayout.setVerticalGroup(
            PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PhasorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PhasorStack, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(logPhasorTimes)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(useTOneEstimateCB)
                .addGap(18, 18, 18)
                .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HarmonicToPlotLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FrequencyToPlotTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(medianFilterCB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PhasorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applicationsMedianFilterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(applicationMedFilterTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jTabbedPane2.addTab("Phasor", Phasor);

        pixelThresholdCutoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pixelThresholdCutoffLabel.setText("Pixel threshold");

        PixelThresholdCutOffTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        PixelThresholdCutOffTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        PixelThresholdCutOffTF.setText("500");
        PixelThresholdCutOffTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PixelThresholdCutOffTFActionPerformed(evt);
            }
        });
        PixelThresholdCutOffTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                PixelThresholdCutOffTFPropertyChange(evt);
            }
        });

        maxIterationsLabel.setText("Max iterations");

        maxIterationsTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        maxIterationsTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        maxIterationsTF.setText("2000");
        maxIterationsTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxIterationsTFActionPerformed(evt);
            }
        });
        maxIterationsTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                maxIterationsTFPropertyChange(evt);
            }
        });

        numRestartsLabel.setText("Number restarts");

        numRestartsTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        numRestartsTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        numRestartsTF.setText("2");
        numRestartsTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numRestartsTFActionPerformed(evt);
            }
        });
        numRestartsTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                numRestartsTFPropertyChange(evt);
            }
        });

        checkFitSingle.setSelected(true);
        checkFitSingle.setText("Fit Single Exponential with Offset (Reasonable fit time)");
        checkFitSingle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkFitSingleItemStateChanged(evt);
            }
        });
        checkFitSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkFitSingleActionPerformed(evt);
            }
        });

        checkFitDouble.setText("Fit Double Exponential with Offset (Slow)");
        checkFitDouble.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkFitDoubleItemStateChanged(evt);
            }
        });
        checkFitDouble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkFitDoubleActionPerformed(evt);
            }
        });

        Chi2CutoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        Chi2CutoffLabel.setText("Chi2 cutoff");

        Chi2CutOffTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        Chi2CutOffTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Chi2CutOffTF.setText("100.0");
        Chi2CutOffTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Chi2CutOffTFActionPerformed(evt);
            }
        });
        Chi2CutOffTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Chi2CutOffTFPropertyChange(evt);
            }
        });

        checkFitTriple.setText("Fit Triple Exponential with Offset (Really slow)");
        checkFitTriple.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkFitTripleItemStateChanged(evt);
            }
        });
        checkFitTriple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkFitTripleActionPerformed(evt);
            }
        });

        varParamLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        varParamLabel.setText("Vary parameters");

        varParamTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        varParamTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        varParamTF.setText("0.1");
        varParamTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                varParamTFActionPerformed(evt);
            }
        });
        varParamTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                varParamTFPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout FittingLayout = new javax.swing.GroupLayout(Fitting);
        Fitting.setLayout(FittingLayout);
        FittingLayout.setHorizontalGroup(
            FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FittingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkFitDouble)
                            .addComponent(checkFitTriple))
                        .addContainerGap(72, Short.MAX_VALUE))
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addComponent(checkFitSingle)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FittingLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addComponent(Chi2CutoffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Chi2CutOffTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addComponent(varParamLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(varParamTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addComponent(pixelThresholdCutoffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PixelThresholdCutOffTF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FittingLayout.createSequentialGroup()
                        .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maxIterationsLabel)
                            .addComponent(numRestartsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maxIterationsTF)
                            .addComponent(numRestartsTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(62, 62, 62))
        );
        FittingLayout.setVerticalGroup(
            FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FittingLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxIterationsLabel)
                    .addComponent(maxIterationsTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numRestartsLabel)
                    .addComponent(numRestartsTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(varParamLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(varParamTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(61, 61, 61)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pixelThresholdCutoffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PixelThresholdCutOffTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(FittingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Chi2CutoffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Chi2CutOffTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                .addComponent(checkFitSingle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkFitDouble)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkFitTriple)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Fitting", Fitting);

        numCyclesLabel.setText("Number of cycles");

        numCyclesTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        numCyclesTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        numCyclesTF.setText("1");
        numCyclesTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numCyclesTFActionPerformed(evt);
            }
        });
        numCyclesTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                numCyclesTFPropertyChange(evt);
            }
        });

        imagesPerCycleLabel.setText("Images per cycle");

        imagesPerCycleTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        imagesPerCycleTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        imagesPerCycleTF.setText("300");
        imagesPerCycleTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagesPerCycleTFActionPerformed(evt);
            }
        });
        imagesPerCycleTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                imagesPerCycleTFPropertyChange(evt);
            }
        });

        cameraOffsetTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        cameraOffsetTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cameraOffsetTF.setText("100");
        cameraOffsetTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraOffsetTFActionPerformed(evt);
            }
        });
        cameraOffsetTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cameraOffsetTFPropertyChange(evt);
            }
        });

        cameraOffsetLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cameraOffsetLabel.setText("Camera Offset");

        binFactorLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        binFactorLabel.setText("Camera Bin Factor");

        binFactorTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        binFactorTF.setText("4");
        binFactorTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                binFactorTFActionPerformed(evt);
            }
        });
        binFactorTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                binFactorTFPropertyChange(evt);
            }
        });

        cameraGainLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cameraGainLabel.setText("Camera Gain");

        cameraGainTF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        cameraGainTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cameraGainTF.setText("2.17");
        cameraGainTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cameraGainTFActionPerformed(evt);
            }
        });
        cameraGainTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cameraGainTFPropertyChange(evt);
            }
        });

        ObjectiveNAText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ObjectiveNAText.setText("Objective NA");

        ObjectiveNATF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        ObjectiveNATF.setText("1.4");
        ObjectiveNATF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ObjectiveNATFActionPerformed(evt);
            }
        });
        ObjectiveNATF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                ObjectiveNATFPropertyChange(evt);
            }
        });

        emissionLambdaTF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        emissionLambdaTF.setText("500");
        emissionLambdaTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emissionLambdaTFActionPerformed(evt);
            }
        });
        emissionLambdaTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                emissionLambdaTFPropertyChange(evt);
            }
        });

        emissionLambdaText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        emissionLambdaText.setText("Emission wavelength (nm)");

        emissionLambdaText1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        emissionLambdaText1.setText("Unbinned pixel size (nm)");

        pixelSizeTF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        pixelSizeTF.setText("39");
        pixelSizeTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pixelSizeTFActionPerformed(evt);
            }
        });
        pixelSizeTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                pixelSizeTFPropertyChange(evt);
            }
        });

        binFactorImageTF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        binFactorImageTF.setText("1");
        binFactorImageTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                binFactorImageTFActionPerformed(evt);
            }
        });
        binFactorImageTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                binFactorImageTFPropertyChange(evt);
            }
        });

        binFactorImageText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        binFactorImageText.setText("Image Bin Factor (Post)");

        ImagePixelSizeText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ImagePixelSizeText.setText("Final Image Pixel size =");

        imagePixelSizeTF.setEditable(false);
        imagePixelSizeTF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        imagePixelSizeTF.setText("120");
        imagePixelSizeTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagePixelSizeTFActionPerformed(evt);
            }
        });
        imagePixelSizeTF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                imagePixelSizeTFPropertyChange(evt);
            }
        });

        ImagePixelSizeText1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ImagePixelSizeText1.setText("nm");

        javax.swing.GroupLayout ExperimentalLayout = new javax.swing.GroupLayout(Experimental);
        Experimental.setLayout(ExperimentalLayout);
        ExperimentalLayout.setHorizontalGroup(
            ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExperimentalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ExperimentalLayout.createSequentialGroup()
                        .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emissionLambdaText1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExperimentalLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(binFactorImageText, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(binFactorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pixelSizeTF, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                            .addComponent(binFactorTF)))
                    .addGroup(ExperimentalLayout.createSequentialGroup()
                        .addGap(0, 105, Short.MAX_VALUE)
                        .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ObjectiveNATF, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ExperimentalLayout.createSequentialGroup()
                                .addComponent(emissionLambdaText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emissionLambdaTF, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExperimentalLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ExperimentalLayout.createSequentialGroup()
                        .addComponent(cameraGainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cameraGainTF, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(ExperimentalLayout.createSequentialGroup()
                        .addComponent(cameraOffsetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cameraOffsetTF, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ExperimentalLayout.createSequentialGroup()
                        .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imagesPerCycleLabel)
                            .addComponent(numCyclesLabel))
                        .addGap(18, 18, 18)
                        .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(numCyclesTF)
                            .addComponent(imagesPerCycleTF, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(binFactorImageTF, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExperimentalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ImagePixelSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(imagePixelSizeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ImagePixelSizeText1)
                .addGap(18, 18, 18))
            .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ExperimentalLayout.createSequentialGroup()
                    .addGap(130, 130, 130)
                    .addComponent(ObjectiveNAText)
                    .addContainerGap(188, Short.MAX_VALUE)))
        );
        ExperimentalLayout.setVerticalGroup(
            ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ExperimentalLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(ObjectiveNATF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emissionLambdaTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emissionLambdaText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pixelSizeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emissionLambdaText1))
                .addGap(8, 8, 8)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(binFactorTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(binFactorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(binFactorImageTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(binFactorImageText))
                .addGap(47, 47, 47)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numCyclesLabel)
                    .addComponent(numCyclesTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imagesPerCycleLabel)
                    .addComponent(imagesPerCycleTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cameraOffsetTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cameraOffsetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cameraGainTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cameraGainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ImagePixelSizeText)
                    .addComponent(imagePixelSizeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ImagePixelSizeText1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ExperimentalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ExperimentalLayout.createSequentialGroup()
                    .addGap(44, 44, 44)
                    .addComponent(ObjectiveNAText)
                    .addContainerGap(432, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("Experimental", Experimental);

        jLabel1.setText("meanG");

        jLabel2.setText("meanS");

        jLabel3.setText("Channel A name");

        jLabel6.setText("Channel B name");

        jLabel7.setText("Channel C name");

        jLabel8.setText("Channel D name");

        jLabel9.setText("Channel E name");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Use");
        jLabel4.setAlignmentY(0.0F);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("channel");
        jLabel5.setAlignmentY(0.0F);
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        chA_Gmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chA_Gmean_TF.setText("0");
        chA_Gmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chA_Gmean_TFActionPerformed(evt);
            }
        });
        chA_Gmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chA_Gmean_TFPropertyChange(evt);
            }
        });

        chA_Smean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chA_Smean_TF.setText("0");
        chA_Smean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chA_Smean_TFActionPerformed(evt);
            }
        });
        chA_Smean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chA_Smean_TFPropertyChange(evt);
            }
        });

        chB_Gmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chB_Gmean_TF.setText("0");
        chB_Gmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chB_Gmean_TFActionPerformed(evt);
            }
        });
        chB_Gmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chB_Gmean_TFPropertyChange(evt);
            }
        });

        chB_Smean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chB_Smean_TF.setText("0");
        chB_Smean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chB_Smean_TFActionPerformed(evt);
            }
        });
        chB_Smean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chB_Smean_TFPropertyChange(evt);
            }
        });

        chC_Gmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chC_Gmean_TF.setText("0");
        chC_Gmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chC_Gmean_TFActionPerformed(evt);
            }
        });
        chC_Gmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chC_Gmean_TFPropertyChange(evt);
            }
        });

        chC_Smean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chC_Smean_TF.setText("0");
        chC_Smean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chC_Smean_TFActionPerformed(evt);
            }
        });
        chC_Smean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chC_Smean_TFPropertyChange(evt);
            }
        });

        chD_Gmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chD_Gmean_TF.setText("0");
        chD_Gmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chD_Gmean_TFActionPerformed(evt);
            }
        });
        chD_Gmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chD_Gmean_TFPropertyChange(evt);
            }
        });

        chD_Smean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chD_Smean_TF.setText("0");
        chD_Smean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chD_Smean_TFActionPerformed(evt);
            }
        });
        chD_Smean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chD_Smean_TFPropertyChange(evt);
            }
        });

        chE_Gmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chE_Gmean_TF.setText("0");
        chE_Gmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chE_Gmean_TFActionPerformed(evt);
            }
        });
        chE_Gmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chE_Gmean_TFPropertyChange(evt);
            }
        });

        chE_Smean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chE_Smean_TF.setText("0");
        chE_Smean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chE_Smean_TFActionPerformed(evt);
            }
        });
        chE_Smean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chE_Smean_TFPropertyChange(evt);
            }
        });

        unMixSignals.setText("Unmix Signals");
        unMixSignals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unMixSignalsActionPerformed(evt);
            }
        });

        getChannelAROI.setBackground(new java.awt.Color(0, 0, 0));
        getChannelAROI.setText("get Channel A ROI");
        getChannelAROI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getChannelAROIActionPerformed(evt);
            }
        });

        getChannelBROI.setText("get Channel B ROI");
        getChannelBROI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getChannelBROIActionPerformed(evt);
            }
        });

        getChannelCROI.setText("get Channel C ROI");
        getChannelCROI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getChannelCROIActionPerformed(evt);
            }
        });

        getChannelDROI.setText("get Channel D ROI");
        getChannelDROI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getChannelDROIActionPerformed(evt);
            }
        });

        getChannelEROI.setText("get Channel E ROI");
        getChannelEROI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getChannelEROIActionPerformed(evt);
            }
        });

        chA_Name_TF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        chA_Name_TF.setText("ChannelA");
        chA_Name_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chA_Name_TFActionPerformed(evt);
            }
        });
        chA_Name_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chA_Name_TFPropertyChange(evt);
            }
        });

        chB_Name_TF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        chB_Name_TF.setText("ChannelB");
        chB_Name_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chB_Name_TFActionPerformed(evt);
            }
        });
        chB_Name_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chB_Name_TFPropertyChange(evt);
            }
        });

        chC_Name_TF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        chC_Name_TF.setText("ChannelC");
        chC_Name_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chC_Name_TFActionPerformed(evt);
            }
        });
        chC_Name_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chC_Name_TFPropertyChange(evt);
            }
        });

        chD_Name_TF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        chD_Name_TF.setText("ChannelD");
        chD_Name_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chD_Name_TFActionPerformed(evt);
            }
        });
        chD_Name_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chD_Name_TFPropertyChange(evt);
            }
        });

        chE_Name_TF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        chE_Name_TF.setText("ChannelE");
        chE_Name_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chE_Name_TFActionPerformed(evt);
            }
        });
        chE_Name_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chE_Name_TFPropertyChange(evt);
            }
        });

        useChA_CB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useChA_CBStateChanged(evt);
            }
        });

        useChB_CB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useChB_CBStateChanged(evt);
            }
        });

        useChC_CB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useChC_CBStateChanged(evt);
            }
        });

        useChD_CB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useChD_CBStateChanged(evt);
            }
        });

        useChE_CB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useChE_CBStateChanged(evt);
            }
        });

        readReferenceData.setText("Read Reference Data");
        readReferenceData.setActionCommand("");
        readReferenceData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readReferenceDataActionPerformed(evt);
            }
        });

        saveUnmixed.setText("Save Unmixed Images");
        saveUnmixed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveUnmixedActionPerformed(evt);
            }
        });

        jLabel10.setText("meanK");

        chA_Kmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chA_Kmean_TF.setText("0");
        chA_Kmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chA_Kmean_TFActionPerformed(evt);
            }
        });
        chA_Kmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chA_Kmean_TFPropertyChange(evt);
            }
        });

        chB_Kmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chB_Kmean_TF.setText("0");
        chB_Kmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chB_Kmean_TFActionPerformed(evt);
            }
        });
        chB_Kmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chB_Kmean_TFPropertyChange(evt);
            }
        });

        chC_Kmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chC_Kmean_TF.setText("0");
        chC_Kmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chC_Kmean_TFActionPerformed(evt);
            }
        });
        chC_Kmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chC_Kmean_TFPropertyChange(evt);
            }
        });

        chD_Kmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chD_Kmean_TF.setText("0");
        chD_Kmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chD_Kmean_TFActionPerformed(evt);
            }
        });
        chD_Kmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chD_Kmean_TFPropertyChange(evt);
            }
        });

        chE_Kmean_TF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        chE_Kmean_TF.setText("0");
        chE_Kmean_TF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chE_Kmean_TFActionPerformed(evt);
            }
        });
        chE_Kmean_TF.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chE_Kmean_TFPropertyChange(evt);
            }
        });

        unMixSignalsUsingFit.setText("Unmix Signals Using Fit");
        unMixSignalsUsingFit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unMixSignalsUsingFitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UnmixingLayout = new javax.swing.GroupLayout(Unmixing);
        Unmixing.setLayout(UnmixingLayout);
        UnmixingLayout.setHorizontalGroup(
            UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UnmixingLayout.createSequentialGroup()
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UnmixingLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, UnmixingLayout.createSequentialGroup()
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(chA_Name_TF, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                                        .addComponent(chB_Name_TF))
                                    .addComponent(chC_Name_TF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, UnmixingLayout.createSequentialGroup()
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chD_Name_TF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chE_Name_TF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(UnmixingLayout.createSequentialGroup()
                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(UnmixingLayout.createSequentialGroup()
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(getChannelDROI, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(getChannelCROI, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(getChannelBROI, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(getChannelAROI, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(getChannelEROI, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chB_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chC_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chD_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chE_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel1)
                                        .addComponent(chA_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UnmixingLayout.createSequentialGroup()
                                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chA_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chE_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(chC_Smean_TF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(chD_Smean_TF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(chB_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(useChB_CB)
                                                .addGap(18, 18, 18)
                                                .addComponent(chB_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(useChC_CB)
                                                .addGap(18, 18, 18)
                                                .addComponent(chC_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(useChD_CB)
                                                .addGap(18, 18, 18)
                                                .addComponent(chD_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(useChE_CB)
                                                .addGap(18, 18, 18)
                                                .addComponent(chE_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(useChA_CB)
                                                .addGap(18, 18, 18)
                                                .addComponent(chA_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(UnmixingLayout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(UnmixingLayout.createSequentialGroup()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel10))))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, UnmixingLayout.createSequentialGroup()
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UnmixingLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(readReferenceData))
                                    .addGroup(UnmixingLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(unMixSignals)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(saveUnmixed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(unMixSignalsUsingFit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        UnmixingLayout.setVerticalGroup(
            UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UnmixingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chA_Name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chB_Name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chC_Name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chD_Name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chE_Name_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UnmixingLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(getChannelAROI))
                    .addGroup(UnmixingLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel10))
                        .addGap(4, 4, 4)
                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UnmixingLayout.createSequentialGroup()
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(useChA_CB)
                                    .addComponent(chA_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chA_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chB_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getChannelBROI))
                                    .addComponent(useChB_CB)
                                    .addComponent(chB_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chC_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getChannelCROI)
                                        .addComponent(chC_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(chC_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(useChC_CB)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(chD_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getChannelDROI)
                                        .addComponent(chD_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(chD_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(useChD_CB)))
                                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UnmixingLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(useChE_CB))
                                    .addGroup(UnmixingLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(chE_Gmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(getChannelEROI)
                                                .addComponent(chE_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(chE_Kmean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(UnmixingLayout.createSequentialGroup()
                                .addComponent(chA_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chB_Smean_TF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(23, 23, 23)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(readReferenceData)
                    .addComponent(saveUnmixed))
                .addGap(18, 18, 18)
                .addGroup(UnmixingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unMixSignals)
                    .addComponent(unMixSignalsUsingFit))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Unmixing", Unmixing);

        statusMessageArea.setColumns(20);
        statusMessageArea.setRows(1);
        statusMessageArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(statusMessageArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    



//**************Action handling ******************************    
    
    private void fitExponentialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitExponentialActionPerformed
        boolean imageOpenAlready = false;
        String[] imageTitles = WindowManager.getImageTitles();
        for (String imageTitle : imageTitles) {
            if (imageTitle.contains("RateConstantsImage")) {
                imageOpenAlready = true;
            }
        }
        if (imageOpenAlready) {
            IJ.showMessage("Pixel Fitter", "Please close the existing rate constants image\nSorry, I'm getting confused");
            return;
        }
        img = IJ.getImage();
        ImageWindow iwin = img.getWindow();
        iwin.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Chi2G = null;
                offsetDataG = null; 
                a1DataG = null;
                k1DataG = null;
                a2DataG = null;
                k2DataG = null;
                a3DataG = null;
                k3DataG = null;
            }
        });
        if (img.isHyperStack() && chWarnOff == false) {
            GenericDialog wfc = new GenericDialog("Channel confirmations");
            wfc.addMessage("Have you selected the channel\nyou wish to fit?");
            wfc.addCheckbox("Do not show this warning", false);
            wfc.showDialog();
            if (wfc.wasCanceled()) {
                return;
            }
            chWarnOff = wfc.getNextBoolean();
        }
        try {
            psFRET_Fit_exponential();
        } catch (Exception ex) {
            Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_fitExponentialActionPerformed

    private void makeRateConstantImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeRateConstantImageActionPerformed
        if (rateDataFromPhasor == null) {
            IJ.showMessage("Pixel Fitter", "No rate constant data available");
        } else {
            boolean imageOpenAlready = false;
            String[] imageTitles = WindowManager.getImageTitles();
            for (String imageTitle : imageTitles) {
                if (imageTitle.contains("RateConstantsImage")) {
                    imageOpenAlready = true;
                }
            }
            if (!imageOpenAlready) {
                rateConstantImage = createImage("RateConstantsImage",rateDataFromPhasor);
            }
        }
    }//GEN-LAST:event_makeRateConstantImageActionPerformed

    private void PhasorStackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PhasorStackActionPerformed
        boolean imageOpenAlready = false;
        IJ.log("doing phasor analysis");
        String[] imageTitles = WindowManager.getImageTitles();
        for (String imageTitle : imageTitles) {
            if (imageTitle.contains("RateConstantsImage")) {
                imageOpenAlready = true;
            }
        }
        if (imageOpenAlready) {
            IJ.showMessage("Pixel Fitter", "Please close the existing rate constants image\nSorry, I'm getting confused");
            return;
        }
        img = IJ.getImage();
        ImageWindow iwin = img.getWindow();
        iwin.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent e)
                {
                    GmData = null;
                    GsData = null;
                    rateDataFromPhasor = null;
                    //Chi2G = null;
                    backGroundValues=null;
                }
            });
            if (img.isHyperStack() && chWarnOff == false) {
                GenericDialog wfc = new GenericDialog("Channel confirmations");
                wfc.addMessage("Have you selected the channel\nyou wish to fit?");
                wfc.addCheckbox("Do not show this warning", false);
                wfc.showDialog();
                if (wfc.wasCanceled()) {
                    return;
                }
                chWarnOff = wfc.getNextBoolean();
            }
            try {
                run_PhasorPlot_on_Stack();
            } catch (Exception ex) {
                Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_PhasorStackActionPerformed

    private void ExaminePixelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExaminePixelsActionPerformed
        if (a1DataG == null || k1DataG == null || offsetDataG == null || Chi2G == null) {
            img = IJ.getImage();
            if (img.getTitle().contains("RateConstantsImage")) {
                IJ.showMessage("Pixel Fitter", "Data image selected\nPlease select the raw image data set");
                return;
            }
            boolean imageOpenAlready = false;
            String[] imageTitles = WindowManager.getImageTitles();
            for (String imageTitle : imageTitles) {
                if (imageTitle.contains("WeightedRateConstantsImage")) {
                    imageOpenAlready = true;
                }
            }
            if (!imageOpenAlready && k1DataG != null) {
                wmkImage = createWeightedRateConstantsImage();
            }
            //in case the image is opened without using the plugin BioFormats button
            String dir0 = IJ.getDirectory("image");
            String stackToOpen = img.getTitle();
            String id2 = dir0 + stackToOpen;
            String fExt = id2.substring(id2.lastIndexOf("."), id2.length());
            if (fExt.contains(" ") && fExt.indexOf(" ") < id2.length()) {
                fExt = fExt.substring(0, fExt.indexOf(" "));
            }
            id = id2.substring(0, id2.lastIndexOf(".")) + fExt;

            ImageStack img2 = img.getStack();
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
                IJ.showMessage("Pixel Fitter", "The number of cycles multiplied by the number images per cycle is larger than the stack");
                return;
            }
            String id3 = id.substring(0, id.lastIndexOf("."));
            File f1 = new File(id3 + "_K1Image.tif");
            File f2 = new File(id3 + "_A1Image.tif");
            File f3 = new File(id3 + "_OffsetImage.tif");
            File f4 = new File(id3 + "_Chi2Image.tif");            
            File f5 = new File(id3 + "_K2Image.tif");
            File f6 = new File(id3 + "_A2Image.tif");            
            File f7 = new File(id3 + "_K3Image.tif");
            File f8 = new File(id3 + "_A3Image.tif");
            
            if (!f1.exists() || !f2.exists() || !f3.exists() || !f4.exists()) {
                IJ.showMessage("Pixel Fitter", "The analyzed data sets were not found in the directory with your image\n" + dir0 + "\nHave you fit this dataset already?");
                return;
            }
            try {
                timeData3 = getTimingPerPlane(id, size, currentZ, currentchannel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            offsetImage = new Opener().openImage(id3 + "_OffsetImage.tif");
            offsetDataG = new double[imageW][imageH][numCycles];
            ImageStack stack = offsetImage.getStack();
            if (stack.getSize() != numCycles) {
                IJ.showMessage("Pixel Fitter", "The size of offsetImage dataset does not match the number of cycles");
                return;
            }
            for (int cyc = 0; cyc < numCycles; cyc++) {
                ImageProcessor ip = stack.getProcessor(cyc + 1);
                for (int x = 0; x < imageW; x++) {
                    for (int y = 0; y < imageH; y++) {
                        offsetDataG[x][y][cyc] = ip.getPixelValue(x, y);
                    }
                }
            }
            offsetImage.close();
            
            Chi2Image = new Opener().openImage(id3 + "_Chi2Image.tif");
            Chi2G = new double[imageW][imageH][numCycles];
            stack = Chi2Image.getStack();
            if (stack.getSize() != numCycles) {
                IJ.showMessage("Pixel Fitter", "The size of Chi2Image dataset does not match the number of cycles");
                return;
            }
            for (int cyc = 0; cyc < numCycles; cyc++) {
                ImageProcessor ip = stack.getProcessor(cyc + 1);
                for (int x = 0; x < imageW; x++) {
                    for (int y = 0; y < imageH; y++) {
                        Chi2G[x][y][cyc] = ip.getPixelValue(x, y);
                    }
                }
            }
            Chi2Image.close();

            a1Image = new Opener().openImage(id3 + "_A1Image.tif");
            a1DataG = new double[imageW][imageH][numCycles];
            stack = a1Image.getStack();
            if (stack.getSize() != numCycles) {
                IJ.showMessage("Pixel Fitter", "The size of a1Image dataset does not match the number of cycles");
                return;
            }
            for (int cyc = 0; cyc < numCycles; cyc++) {
                ImageProcessor ip = stack.getProcessor(cyc + 1);
                for (int x = 0; x < imageW; x++) {
                    for (int y = 0; y < imageH; y++) {
                        a1DataG[x][y][cyc] = ip.getPixelValue(x, y);
                    }
                }
            }
            a1Image.close();

            k1Image = new Opener().openImage(id3 + "_K1Image.tif");
            k1DataG = new double[imageW][imageH][numCycles];
            stack = k1Image.getStack();
            if (stack.getSize() != numCycles) {
                IJ.showMessage("Pixel Fitter", "The size of rateConstantImage dataset does not match the number of cycles");
                return;
            }
            for (int cyc = 0; cyc < numCycles; cyc++) {
                ImageProcessor ip = stack.getProcessor(cyc + 1);
                for (int x = 0; x < imageW; x++) {
                    for (int y = 0; y < imageH; y++) {
                        k1DataG[x][y][cyc] = ip.getPixelValue(x, y);
                    }
                }
            }
            k1Image.close();
            
            if (f5.exists()) {
                a2Image = new Opener().openImage(id3 + "_A2Image.tif");
                a2DataG = new double[imageW][imageH][numCycles];
                stack = a2Image.getStack();
                if (stack.getSize() != numCycles) {
                    IJ.showMessage("Pixel Fitter", "The size of a1Image dataset does not match the number of cycles");
                    return;
                }
                for (int cyc = 0; cyc < numCycles; cyc++) {
                    ImageProcessor ip = stack.getProcessor(cyc + 1);
                    for (int x = 0; x < imageW; x++) {
                        for (int y = 0; y < imageH; y++) {
                            a2DataG[x][y][cyc] = ip.getPixelValue(x, y);
                        }
                    }
                }
                a2Image.close();
            }
            
            if (f6.exists()) {
                k2Image = new Opener().openImage(id3 + "_K2Image.tif");
                k2DataG = new double[imageW][imageH][numCycles];
                stack = k2Image.getStack();
                if (stack.getSize() != numCycles) {
                    IJ.showMessage("Pixel Fitter", "The size of rateConstantImage dataset does not match the number of cycles");
                    return;
                }
                for (int cyc = 0; cyc < numCycles; cyc++) {
                    ImageProcessor ip = stack.getProcessor(cyc + 1);
                    for (int x = 0; x < imageW; x++) {
                        for (int y = 0; y < imageH; y++) {
                            k2DataG[x][y][cyc] = ip.getPixelValue(x, y);
                        }
                    }
                }
                k2Image.close();
            }

            if (f7.exists()) {
                a3Image = new Opener().openImage(id3 + "_A3Image.tif");
                a3DataG = new double[imageW][imageH][numCycles];
                stack = a2Image.getStack();
                if (stack.getSize() != numCycles) {
                    IJ.showMessage("Pixel Fitter", "The size of a1Image dataset does not match the number of cycles");
                    return;
                }
                for (int cyc = 0; cyc < numCycles; cyc++) {
                    ImageProcessor ip = stack.getProcessor(cyc + 1);
                    for (int x = 0; x < imageW; x++) {
                        for (int y = 0; y < imageH; y++) {
                            a3DataG[x][y][cyc] = ip.getPixelValue(x, y);
                        }
                    }
                }
                a3Image.close();
            }

            if (f8.exists()) {
                k3Image = new Opener().openImage(id3 + "_K3Image.tif");
                k3DataG = new double[imageW][imageH][numCycles];
                stack = k2Image.getStack();
                if (stack.getSize() != numCycles) {
                    IJ.showMessage("Pixel Fitter", "The size of rateConstantImage dataset does not match the number of cycles");
                    return;
                }
                for (int cyc = 0; cyc < numCycles; cyc++) {
                    ImageProcessor ip = stack.getProcessor(cyc + 1);
                    for (int x = 0; x < imageW; x++) {
                        for (int y = 0; y < imageH; y++) {
                            k3DataG[x][y][cyc] = ip.getPixelValue(x, y);
                        }
                    }
                }
                k3Image.close();
            }
            
            wmkImage = createWeightedRateConstantsImage();
            setUpListeners();
            
        } else {
            boolean imageOpenAlready = false;
            String[] imageTitles = WindowManager.getImageTitles();
            for (String imageTitle : imageTitles) {
                if (imageTitle.contains("WeightedRateConstantsImage")) {
                    imageOpenAlready = true;
                }
            }
            if (!imageOpenAlready && k1DataG != null) {
                wmkImage = createWeightedRateConstantsImage();
            }
            setUpListeners();
        }
    }//GEN-LAST:event_ExaminePixelsActionPerformed

    private void MakeSavePhasorParameterImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MakeSavePhasorParameterImageActionPerformed
        if (rateDataFromPhasor == null || GmData == null || GsData == null) {//Chi2G == null) {
            IJ.showMessage("Pixel Fitter", "Fit parameter data unavailable");
        } else {
            String id2 = id.substring(0, id.indexOf("."));
            File f1 = new File(id2 + "_RateConstantsImage.tif");
            File f2 = new File(id2 + "_GmDataImage.tif");
            File f3 = new File(id2 + "_GsDataImage.tif");
            //File f4 = new File(id2 + "_Chi2Image.tif.tif");
            if (f1.exists() || f2.exists() || f3.exists()){// || f4.exists()) {
                GenericDialog gdEx = new GenericDialog("Phasor Plotter");
                gdEx.addMessage("Analyzed results files present for this image\n " + id2);
                gdEx.addCheckbox("Overwrite?", false);
                gdEx.showDialog();
                if (gdEx.wasCanceled()) {
                    return;
                }
                boolean overWrite = gdEx.getNextBoolean();
                if (overWrite) {
                    ImagePlus imp = createImage("RateConstantsImage",rateDataFromPhasor);
                    IJ.saveAs(imp, "Tiff", id2 + "_RateConstantsImage.tif");
                    imp.close();
                    imp = createImage("GmDataImage",GmData);
                    IJ.saveAs(imp, "Tiff", id2 + "_GmDataImage.tif");
                    imp.close();
                    imp = createImage("GsDataImage",GsData);
                    IJ.saveAs(imp, "Tiff", id2 + "_GsDataImage.tif");
                    imp.close();
                    //imp = createChi2Image();
                    //IJ.saveAs(imp, "Tiff", id2 + "_Chi2Image.tif");
                    //imp.close();
                }
            } else {
                ImagePlus imp = createImage("RateConstantsImage", rateDataFromPhasor);
                IJ.saveAs(imp, "Tiff", id2 + "_RateConstantsImage.tif");
                imp.close();
                imp = createImage("GmDataImage", GmData);
                IJ.saveAs(imp, "Tiff", id2 + "_GmDataImage.tif");
                imp.close();
                imp = createImage("GsDataImage", GsData);
                IJ.saveAs(imp, "Tiff", id2 + "_GsDataImage.tif");
                imp.close();
                //imp = createChi2Image();
                //IJ.saveAs(imp, "Tiff", id2 + "_Chi2Image.tif");
                //imp.close();
            }
        }
    }//GEN-LAST:event_MakeSavePhasorParameterImageActionPerformed

    private void UseLogScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UseLogScaleActionPerformed
        if (UseLogScale.isSelected()) {
            yLog = true;
        }
        if (!UseLogScale.isSelected()) {
            yLog = false;
        }
    }//GEN-LAST:event_UseLogScaleActionPerformed

    private void logPhasorTimesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logPhasorTimesActionPerformed
        if (logPhasorTimes.isSelected()) {
            LogPhasorTime = true;
        }
        if (!logPhasorTimes.isSelected()) {
            LogPhasorTime = false;
        }
    }//GEN-LAST:event_logPhasorTimesActionPerformed

    private void numCyclesTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numCyclesTFActionPerformed
        try {
            numCycles = Integer.parseInt(numCyclesTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_numCyclesTFActionPerformed

    private void numCyclesTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_numCyclesTFPropertyChange
        try {
            numCycles = Integer.parseInt(numCyclesTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_numCyclesTFPropertyChange

    private void imagesPerCycleTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagesPerCycleTFActionPerformed
        try {
            imagesPerCycle = Integer.parseInt(imagesPerCycleTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_imagesPerCycleTFActionPerformed

    private void imagesPerCycleTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_imagesPerCycleTFPropertyChange
        try {
            imagesPerCycle = Integer.parseInt(imagesPerCycleTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_imagesPerCycleTFPropertyChange

    private void FrequencyToPlotTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FrequencyToPlotTFActionPerformed
        try {
            harmonic = Double.parseDouble(FrequencyToPlotTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_FrequencyToPlotTFActionPerformed

    private void FrequencyToPlotTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_FrequencyToPlotTFPropertyChange
        try {
            harmonic = Double.parseDouble(FrequencyToPlotTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_FrequencyToPlotTFPropertyChange

    private void cameraOffsetTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraOffsetTFActionPerformed
        try {
            cameraOffset = Integer.parseInt(cameraOffsetTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cameraOffsetTFActionPerformed

    private void cameraOffsetTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cameraOffsetTFPropertyChange
        try {
            cameraOffset = Integer.parseInt(cameraOffsetTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cameraOffsetTFPropertyChange

    private void thresholdTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thresholdTFActionPerformed
        try {
            threshold = Integer.parseInt(thresholdTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_thresholdTFActionPerformed

    private void thresholdTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_thresholdTFPropertyChange
        try {
            threshold = Integer.parseInt(thresholdTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_thresholdTFPropertyChange

    private void selectFromPhasorPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFromPhasorPlotActionPerformed
        //double[] phasorPlotROI = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[][] phasorPlotROI = getCalibratedPixelsFromPlotWindow(phasorPlot);
            //IJ.log("xPos="+phasorPlotROI[0]+"  yPos="+phasorPlotROI[1]+"   cWidth="+phasorPlotROI[2]+"   cHeight="+phasorPlotROI[3]);
        double[][][] phasorFilteredData = filterDataFromPhasor(phasorPlotROI);
        ImagePlus filteredImage = createImage("filteredImage",phasorFilteredData);
    }//GEN-LAST:event_selectFromPhasorPlotActionPerformed

    private void medianFilterCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medianFilterCBActionPerformed
        if (medianFilterCB.isSelected()) {
            medianFilter = true;
        }
        if (!medianFilterCB.isSelected()) {
            medianFilter = false;
        }
    }//GEN-LAST:event_medianFilterCBActionPerformed

    private void backgroundSubtractCBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_backgroundSubtractCBStateChanged
        if (backgroundSubtractCB.isSelected()) {
            backgroundSubtract = true;
        }
        if (!backgroundSubtractCB.isSelected()) {
            backgroundSubtract = false;
            backGroundValues=null;
        }
    }//GEN-LAST:event_backgroundSubtractCBStateChanged

    private void backGrdSubtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backGrdSubtractActionPerformed
        if (!backgroundSubtractCB.isSelected()) {
            backgroundSubtractCB.setSelected(true);
            backgroundSubtract = true;
        }
        backGroundValues=getTAxisProfile();
    }//GEN-LAST:event_backGrdSubtractActionPerformed

    private void applicationMedFilterTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applicationMedFilterTFActionPerformed
        try {
            applicationsMedianFilter = Integer.parseInt(applicationMedFilterTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_applicationMedFilterTFActionPerformed

    private void applicationMedFilterTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_applicationMedFilterTFPropertyChange
        try {
            applicationsMedianFilter = Integer.parseInt(applicationMedFilterTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_applicationMedFilterTFPropertyChange

    private void PixelThresholdCutOffTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PixelThresholdCutOffTFActionPerformed
        try {
            PixelThresholdCutOff = Double.parseDouble(PixelThresholdCutOffTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_PixelThresholdCutOffTFActionPerformed

    private void PixelThresholdCutOffTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_PixelThresholdCutOffTFPropertyChange
        try {
            PixelThresholdCutOff = Double.parseDouble(PixelThresholdCutOffTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_PixelThresholdCutOffTFPropertyChange

    private void maxIterationsTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxIterationsTFActionPerformed
        try {
            maxiteration = Integer.parseInt(maxIterationsTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_maxIterationsTFActionPerformed

    private void maxIterationsTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_maxIterationsTFPropertyChange
        try {
            maxiteration = Integer.parseInt(maxIterationsTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_maxIterationsTFPropertyChange

    private void numRestartsTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numRestartsTFActionPerformed
        try {
            numRestarts = Integer.parseInt(numRestartsTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_numRestartsTFActionPerformed

    private void numRestartsTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_numRestartsTFPropertyChange
        try {
            numRestarts = Integer.parseInt(numRestartsTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_numRestartsTFPropertyChange

    private void checkFitSingleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkFitSingleItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            fitSingle = true;
            fitDouble = false;
            fitTriple = false;
            checkFitDouble.setSelected(false);
            checkFitTriple.setSelected(false);
        }
    }//GEN-LAST:event_checkFitSingleItemStateChanged

    private void checkFitDoubleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkFitDoubleItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            fitSingle = false;
            fitDouble = true;
            fitTriple = false;
            checkFitSingle.setSelected(false);
            checkFitTriple.setSelected(false);
        }
    }//GEN-LAST:event_checkFitDoubleItemStateChanged

    private void Chi2CutOffTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Chi2CutOffTFActionPerformed
        Chi2CutOff = Double.parseDouble(Chi2CutOffTF.getText());
    }//GEN-LAST:event_Chi2CutOffTFActionPerformed

    private void Chi2CutOffTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_Chi2CutOffTFPropertyChange
        Chi2CutOff = Double.parseDouble(Chi2CutOffTF.getText());
    }//GEN-LAST:event_Chi2CutOffTFPropertyChange

    private void checkFitTripleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkFitTripleItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            fitSingle = false;
            fitDouble = false;
            fitTriple = true;
            checkFitSingle.setSelected(false);
            checkFitDouble.setSelected(false);
        }
    }//GEN-LAST:event_checkFitTripleItemStateChanged

    private void varParamTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_varParamTFActionPerformed
        varParam = Double.parseDouble(varParamTF.getText());
    }//GEN-LAST:event_varParamTFActionPerformed

    private void varParamTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_varParamTFPropertyChange
        varParam = Double.parseDouble(varParamTF.getText());
    }//GEN-LAST:event_varParamTFPropertyChange

    private void binFactorTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_binFactorTFActionPerformed
        try {
            binFactor = Integer.parseInt(binFactorTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_binFactorTFActionPerformed

    private void binFactorTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_binFactorTFPropertyChange
        try {
            binFactor = Integer.parseInt(binFactorTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_binFactorTFPropertyChange

    private void cameraGainTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cameraGainTFActionPerformed
        try {
            cameraGain = Double.parseDouble(cameraGainTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cameraGainTFActionPerformed

    private void cameraGainTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cameraGainTFPropertyChange
        try {
            cameraGain = Double.parseDouble(cameraGainTF.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cameraGainTFPropertyChange

    private void ObjectiveNATFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ObjectiveNATFActionPerformed
        NA = Double.parseDouble(ObjectiveNATF.getText());
    }//GEN-LAST:event_ObjectiveNATFActionPerformed

    private void ObjectiveNATFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_ObjectiveNATFPropertyChange
        NA = Double.parseDouble(ObjectiveNATF.getText());
    }//GEN-LAST:event_ObjectiveNATFPropertyChange

    private void emissionLambdaTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emissionLambdaTFActionPerformed
        lambda = Double.parseDouble(emissionLambdaTF.getText());
    }//GEN-LAST:event_emissionLambdaTFActionPerformed

    private void emissionLambdaTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_emissionLambdaTFPropertyChange
        lambda = Double.parseDouble(emissionLambdaTF.getText());
    }//GEN-LAST:event_emissionLambdaTFPropertyChange

    private void pixelSizeTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixelSizeTFActionPerformed
        pixSize = Double.parseDouble(pixelSizeTF.getText());
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_pixelSizeTFActionPerformed

    private void pixelSizeTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_pixelSizeTFPropertyChange
        pixSize = Double.parseDouble(pixelSizeTF.getText());
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_pixelSizeTFPropertyChange

    private void binFactorImageTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_binFactorImageTFActionPerformed
        binFactorImage = Integer.parseInt(binFactorImageTF.getText());
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_binFactorImageTFActionPerformed

    private void binFactorImageTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_binFactorImageTFPropertyChange
        binFactorImage = Integer.parseInt(binFactorImageTF.getText());
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_binFactorImageTFPropertyChange

    private void imagePixelSizeTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagePixelSizeTFActionPerformed
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_imagePixelSizeTFActionPerformed

    private void imagePixelSizeTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_imagePixelSizeTFPropertyChange
        imagePixelSizeTF.setText(String.valueOf((int)pixSize*binFactor*binFactorImage));
    }//GEN-LAST:event_imagePixelSizeTFPropertyChange

    private void terminalThresholdTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_terminalThresholdTFActionPerformed
        terminalThreshold=Double.parseDouble(terminalThresholdTF.getText());
    }//GEN-LAST:event_terminalThresholdTFActionPerformed

    private void terminalThresholdTFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_terminalThresholdTFPropertyChange
        terminalThreshold=Double.parseDouble(terminalThresholdTF.getText());
    }//GEN-LAST:event_terminalThresholdTFPropertyChange

    private void chA_Gmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chA_Gmean_TFActionPerformed
        chA_Gmean = Double.parseDouble(chA_Gmean_TF.getText());
    }//GEN-LAST:event_chA_Gmean_TFActionPerformed

    private void chA_Gmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chA_Gmean_TFPropertyChange
        chA_Gmean = Double.parseDouble(chA_Gmean_TF.getText());
    }//GEN-LAST:event_chA_Gmean_TFPropertyChange

    private void chA_Smean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chA_Smean_TFActionPerformed
        chA_Smean = Double.parseDouble(chA_Smean_TF.getText());
    }//GEN-LAST:event_chA_Smean_TFActionPerformed

    private void chA_Smean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chA_Smean_TFPropertyChange
        chA_Smean = Double.parseDouble(chA_Smean_TF.getText());
    }//GEN-LAST:event_chA_Smean_TFPropertyChange

    private void chB_Gmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chB_Gmean_TFActionPerformed
        chB_Gmean = Double.parseDouble(chB_Gmean_TF.getText());
    }//GEN-LAST:event_chB_Gmean_TFActionPerformed

    private void chB_Gmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chB_Gmean_TFPropertyChange
        chB_Gmean = Double.parseDouble(chB_Gmean_TF.getText());
    }//GEN-LAST:event_chB_Gmean_TFPropertyChange

    private void chB_Smean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chB_Smean_TFActionPerformed
        chB_Smean = Double.parseDouble(chB_Smean_TF.getText());
    }//GEN-LAST:event_chB_Smean_TFActionPerformed

    private void chB_Smean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chB_Smean_TFPropertyChange
        chB_Smean = Double.parseDouble(chB_Smean_TF.getText());
    }//GEN-LAST:event_chB_Smean_TFPropertyChange

    private void chC_Gmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chC_Gmean_TFActionPerformed
        chC_Gmean = Double.parseDouble(chC_Gmean_TF.getText());
    }//GEN-LAST:event_chC_Gmean_TFActionPerformed

    private void chC_Gmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chC_Gmean_TFPropertyChange
        chC_Gmean = Double.parseDouble(chC_Gmean_TF.getText());
    }//GEN-LAST:event_chC_Gmean_TFPropertyChange

    private void chC_Smean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chC_Smean_TFActionPerformed
        chC_Smean = Double.parseDouble(chC_Smean_TF.getText());
    }//GEN-LAST:event_chC_Smean_TFActionPerformed

    private void chC_Smean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chC_Smean_TFPropertyChange
        chC_Smean = Double.parseDouble(chC_Smean_TF.getText());
    }//GEN-LAST:event_chC_Smean_TFPropertyChange

    private void unMixSignalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unMixSignalsActionPerformed
        arrayGToSend = new double[5];
        arrayGToSend[0]=chA_Gmean;
        arrayGToSend[1]=chB_Gmean;
        arrayGToSend[2]=chC_Gmean;
        arrayGToSend[3]=chD_Gmean;
        arrayGToSend[4]=chE_Gmean;
        arraySToSend = new double[5];
        arraySToSend[0]=chA_Smean;
        arraySToSend[1]=chB_Smean;
        arraySToSend[2]=chC_Smean;
        arrayGToSend[3]=chD_Smean;
        arrayGToSend[4]=chE_Smean;
        if(GmData==null || GsData==null || AZeroData==null)
            IJ.showMessage("Phasor plotter", "Please run the phasor plotting on the dataset first");
        unMixPixelValuesAndCreateImages(arrayGToSend, arraySToSend);
        writeReferenceDataToFile();
    }//GEN-LAST:event_unMixSignalsActionPerformed

    private void getChannelAROIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getChannelAROIActionPerformed
        
        phasorPlotROIChA = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[] phasorFilteredDataMeans = getMeanFromFilteredData(phasorPlotROIChA);
        chA_Gmean_TF.setText(String.valueOf(phasorFilteredDataMeans[0]));
        chA_Smean_TF.setText(String.valueOf(phasorFilteredDataMeans[1]));
        useChA_CB.setSelected(true);
        
    }//GEN-LAST:event_getChannelAROIActionPerformed

    private void getChannelBROIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getChannelBROIActionPerformed
        
        phasorPlotROIChB = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[] phasorFilteredDataMeans = getMeanFromFilteredData(phasorPlotROIChB);
        chB_Gmean_TF.setText(String.valueOf(phasorFilteredDataMeans[0]));
        chB_Smean_TF.setText(String.valueOf(phasorFilteredDataMeans[1]));
        useChB_CB.setSelected(true);
    }//GEN-LAST:event_getChannelBROIActionPerformed

    private void getChannelCROIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getChannelCROIActionPerformed
        
        phasorPlotROIChC = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[] phasorFilteredDataMeans = getMeanFromFilteredData(phasorPlotROIChC);
        chC_Gmean_TF.setText(String.valueOf(phasorFilteredDataMeans[0]));
        chC_Smean_TF.setText(String.valueOf(phasorFilteredDataMeans[1]));
        useChC_CB.setSelected(true);
    }//GEN-LAST:event_getChannelCROIActionPerformed

    private void useTOneEstimateCBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useTOneEstimateCBStateChanged
        if (useTOneEstimateCB.isSelected()) {
            useTOneHalfEstimate = true;
        }
        if (!useTOneEstimateCB.isSelected()) {
            useTOneHalfEstimate = false;
        }
    }//GEN-LAST:event_useTOneEstimateCBStateChanged

    private void chA_Name_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chA_Name_TFActionPerformed
        chA_name = chA_Name_TF.getText();
    }//GEN-LAST:event_chA_Name_TFActionPerformed

    private void chA_Name_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chA_Name_TFPropertyChange
        chA_name = chA_Name_TF.getText();
    }//GEN-LAST:event_chA_Name_TFPropertyChange

    private void chB_Name_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chB_Name_TFActionPerformed
        chB_name = chB_Name_TF.getText();
    }//GEN-LAST:event_chB_Name_TFActionPerformed

    private void chB_Name_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chB_Name_TFPropertyChange
        chB_name = chB_Name_TF.getText();
    }//GEN-LAST:event_chB_Name_TFPropertyChange

    private void chC_Name_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chC_Name_TFActionPerformed
        chC_name = chC_Name_TF.getText();
    }//GEN-LAST:event_chC_Name_TFActionPerformed

    private void chC_Name_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chC_Name_TFPropertyChange
        chC_name = chC_Name_TF.getText();
    }//GEN-LAST:event_chC_Name_TFPropertyChange

    private void getChannelDROIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getChannelDROIActionPerformed
        phasorPlotROIChD = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[] phasorFilteredDataMeans = getMeanFromFilteredData(phasorPlotROIChD);
        chD_Gmean_TF.setText(String.valueOf(phasorFilteredDataMeans[0]));
        chD_Smean_TF.setText(String.valueOf(phasorFilteredDataMeans[1]));
        useChD_CB.setSelected(true);
    }//GEN-LAST:event_getChannelDROIActionPerformed

    private void chD_Gmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chD_Gmean_TFActionPerformed
        chD_Gmean = Double.parseDouble(chD_Gmean_TF.getText());
    }//GEN-LAST:event_chD_Gmean_TFActionPerformed

    private void chD_Gmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chD_Gmean_TFPropertyChange
        chD_Gmean = Double.parseDouble(chD_Gmean_TF.getText());
    }//GEN-LAST:event_chD_Gmean_TFPropertyChange

    private void chD_Smean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chD_Smean_TFActionPerformed
        chD_Smean = Double.parseDouble(chD_Smean_TF.getText());
    }//GEN-LAST:event_chD_Smean_TFActionPerformed

    private void chD_Smean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chD_Smean_TFPropertyChange
        chD_Smean = Double.parseDouble(chD_Smean_TF.getText());
    }//GEN-LAST:event_chD_Smean_TFPropertyChange

    private void chD_Name_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chD_Name_TFActionPerformed
        chD_name = chD_Name_TF.getText();
    }//GEN-LAST:event_chD_Name_TFActionPerformed

    private void chD_Name_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chD_Name_TFPropertyChange
        chD_name = chD_Name_TF.getText();
    }//GEN-LAST:event_chD_Name_TFPropertyChange

    private void getChannelEROIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getChannelEROIActionPerformed
        phasorPlotROIChE = getCalibratedPixelsFromPlotWindow(phasorPlot);
        double[] phasorFilteredDataMeans = getMeanFromFilteredData(phasorPlotROIChE);
        chE_Gmean_TF.setText(String.valueOf(phasorFilteredDataMeans[0]));
        chE_Smean_TF.setText(String.valueOf(phasorFilteredDataMeans[1]));
        useChE_CB.setSelected(true);
    }//GEN-LAST:event_getChannelEROIActionPerformed

    private void chE_Gmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chE_Gmean_TFActionPerformed
        chE_Gmean = Double.parseDouble(chE_Gmean_TF.getText());
    }//GEN-LAST:event_chE_Gmean_TFActionPerformed

    private void chE_Gmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chE_Gmean_TFPropertyChange
        chE_Gmean = Double.parseDouble(chE_Gmean_TF.getText());
    }//GEN-LAST:event_chE_Gmean_TFPropertyChange

    private void chE_Smean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chE_Smean_TFActionPerformed
        chE_Smean = Double.parseDouble(chE_Smean_TF.getText());
    }//GEN-LAST:event_chE_Smean_TFActionPerformed

    private void chE_Smean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chE_Smean_TFPropertyChange
        chE_Smean = Double.parseDouble(chE_Smean_TF.getText());
    }//GEN-LAST:event_chE_Smean_TFPropertyChange

    private void chE_Name_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chE_Name_TFActionPerformed
        chE_name = chE_Name_TF.getText();
    }//GEN-LAST:event_chE_Name_TFActionPerformed

    private void chE_Name_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chE_Name_TFPropertyChange
        chE_name = chE_Name_TF.getText();
    }//GEN-LAST:event_chE_Name_TFPropertyChange

    private void useChA_CBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useChA_CBStateChanged
        if (useChA_CB.isSelected()) {
            useChA = true;
        }
        if (!useChA_CB.isSelected()) {
            useChA = false;
        }
        //set for other instances

    }//GEN-LAST:event_useChA_CBStateChanged

    private void useChB_CBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useChB_CBStateChanged
        if (useChB_CB.isSelected()) {
            useChB = true;
            useChA_CB.setSelected(true);
        }
        if (!useChB_CB.isSelected()) {
            useChB = false;
        }

    }//GEN-LAST:event_useChB_CBStateChanged

    private void useChC_CBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useChC_CBStateChanged
        if (useChC_CB.isSelected()) {
            useChC = true;
            useChB_CB.setSelected(true);
        }
        if (!useChC_CB.isSelected()) {
            useChC = false;
        }

    }//GEN-LAST:event_useChC_CBStateChanged

    private void useChD_CBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useChD_CBStateChanged
        if (useChD_CB.isSelected()) {
            useChD = true;
            useChC_CB.setSelected(true);
        }
        if (!useChD_CB.isSelected()) {
            useChD = false;
        }

    }//GEN-LAST:event_useChD_CBStateChanged

    private void useChE_CBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useChE_CBStateChanged
        if (useChE_CB.isSelected()) {
            useChE = true;
            useChD_CB.setSelected(true);
        }
        if (!useChE_CB.isSelected()) {
            useChE = false;
        }

    }//GEN-LAST:event_useChE_CBStateChanged

    private void readReferenceDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readReferenceDataActionPerformed
        readReferenceDataFromFile();
    }//GEN-LAST:event_readReferenceDataActionPerformed

    private void manualTimeCBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_manualTimeCBStateChanged
        if (manualTimeCB.isSelected()) {
            useManualTime = true;
        }
        if (!manualTimeCB.isSelected()) {
            useManualTime = false;
        }
    }//GEN-LAST:event_manualTimeCBStateChanged

    private void manualTimeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualTimeCBActionPerformed
        if (manualTimeCB.isSelected()) {
            useManualTime = true;
        }
        if (!manualTimeCB.isSelected()) {
            useManualTime = false;
        }

    }//GEN-LAST:event_manualTimeCBActionPerformed
    
    private void hybridFittateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_manualTimeCBStateChanged
        if (HybridFitCB.isSelected()) {
        	usePhasortoInitialize = true;
        }
        if (!HybridFitCB.isSelected()) {
        	usePhasortoInitialize = false;
        }
    }//GEN-LAST:event_manualTimeCBStateChanged

    private void hybridStateCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualTimeCBActionPerformed
        if (HybridFitCB.isSelected()) {
        	usePhasortoInitialize = true;
        }
        if (!HybridFitCB.isSelected()) {
        	usePhasortoInitialize = false;
        }

    }//GEN-LAST:event_manualTimeCBActionPerformed

    private void saveUnmixedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveUnmixedActionPerformed
       saveUnmixedImages();
    }//GEN-LAST:event_saveUnmixedActionPerformed

    private void chA_Kmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chA_Kmean_TFActionPerformed
        chA_Kmean = Double.parseDouble(chA_Kmean_TF.getText());
    }//GEN-LAST:event_chA_Kmean_TFActionPerformed

    private void chA_Kmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chA_Kmean_TFPropertyChange
        chA_Kmean = Double.parseDouble(chA_Kmean_TF.getText());
    }//GEN-LAST:event_chA_Kmean_TFPropertyChange

    private void chB_Kmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chB_Kmean_TFActionPerformed
        chB_Kmean = Double.parseDouble(chB_Kmean_TF.getText());
    }//GEN-LAST:event_chB_Kmean_TFActionPerformed

    private void chB_Kmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chB_Kmean_TFPropertyChange
        chB_Kmean = Double.parseDouble(chB_Kmean_TF.getText());
    }//GEN-LAST:event_chB_Kmean_TFPropertyChange

    private void chC_Kmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chC_Kmean_TFActionPerformed
        chC_Kmean = Double.parseDouble(chC_Kmean_TF.getText());
    }//GEN-LAST:event_chC_Kmean_TFActionPerformed

    private void chC_Kmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chC_Kmean_TFPropertyChange
        chC_Kmean = Double.parseDouble(chC_Kmean_TF.getText());
    }//GEN-LAST:event_chC_Kmean_TFPropertyChange

    private void chD_Kmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chD_Kmean_TFActionPerformed
        chD_Kmean = Double.parseDouble(chD_Kmean_TF.getText());
    }//GEN-LAST:event_chD_Kmean_TFActionPerformed

    private void chD_Kmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chD_Kmean_TFPropertyChange
        chD_Kmean = Double.parseDouble(chD_Kmean_TF.getText());
    }//GEN-LAST:event_chD_Kmean_TFPropertyChange

    private void chE_Kmean_TFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chE_Kmean_TFActionPerformed
        chE_Kmean = Double.parseDouble(chE_Kmean_TF.getText());
    }//GEN-LAST:event_chE_Kmean_TFActionPerformed

    private void chE_Kmean_TFPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chE_Kmean_TFPropertyChange
        chE_Kmean = Double.parseDouble(chE_Kmean_TF.getText());
    }//GEN-LAST:event_chE_Kmean_TFPropertyChange

    private void unMixSignalsUsingFitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unMixSignalsUsingFitActionPerformed
        unMixPixelValuesAndCreateImagesUsingFit();
    }//GEN-LAST:event_unMixSignalsUsingFitActionPerformed

    private void fitAzeroFixedExponentialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fitAzeroFixedExponentialActionPerformed
        boolean imageOpenAlready = false;
        String[] imageTitles = WindowManager.getImageTitles();
        for (String imageTitle : imageTitles) {
            if (imageTitle.contains("RateConstantsImage")) {
                imageOpenAlready = true;
            }
        }
        if (imageOpenAlready) {
            IJ.showMessage("Pixel Fitter", "Please close the existing rate constants image\nSorry, I'm getting confused");
            return;
        }
        if(!useChA){
            IJ.showMessage("Pixel Fitter", "You must check at least one Use Channel under the Unmixing Tab\nChannels should be used in alphabetical order");
            return;
        }

        IJ.log("in the fitting function");
        img = IJ.getImage();
        ImageWindow iwin = img.getWindow();
        iwin.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Chi2G = null;
                offsetDataG = null; 
                a1DataG = null;
                a2DataG = null;
                a3DataG = null;
                a4DataG = null;
                a5DataG = null;
            }
        });
        if (img.isHyperStack() && chWarnOff == false) {
            GenericDialog wfc = new GenericDialog("Channel confirmations");
            wfc.addMessage("Have you selected the channel\nyou wish to fit?");
            wfc.addCheckbox("Do not show this warning", false);
            wfc.showDialog();
            if (wfc.wasCanceled()) {
                return;
            }
            chWarnOff = wfc.getNextBoolean();
        }
        IJ.log("outside try");
        try {
        	IJ.log("before fit function called");
            psFRET_Fit_Azero_Exponential();
            
        } catch (Exception ex) {
            Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }//GEN-LAST:event_fitAzeroFixedExponentialActionPerformed

    private void checkFitSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkFitSingleActionPerformed
        fitSingle = checkFitSingle.isSelected();
    }//GEN-LAST:event_checkFitSingleActionPerformed

    private void checkFitDoubleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkFitDoubleActionPerformed
        fitDouble = checkFitDouble.isSelected();
    }//GEN-LAST:event_checkFitDoubleActionPerformed

    private void checkFitTripleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkFitTripleActionPerformed
        fitTriple = checkFitTriple.isSelected();
    }//GEN-LAST:event_checkFitTripleActionPerformed

    
    
    
    
    
    
    
    
    
 
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
    	//new ImageJ();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Photoswitching_Phasor_Plotter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Photoswitching_Phasor_Plotter().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField Chi2CutOffTF;
    private javax.swing.JLabel Chi2CutoffLabel;
    private javax.swing.JPanel Control;
    private javax.swing.JButton ExaminePixels;
    private javax.swing.JPanel Experimental;
    private javax.swing.JPanel Fitting;
    private javax.swing.JFormattedTextField FrequencyToPlotTF;
    private javax.swing.JLabel HarmonicToPlotLabel;
    private javax.swing.JLabel ImagePixelSizeText;
    private javax.swing.JLabel ImagePixelSizeText1;
    private javax.swing.JButton MakeSavePhasorParameterImage;
    private javax.swing.JTextField ObjectiveNATF;
    private javax.swing.JLabel ObjectiveNAText;
    private javax.swing.JPanel Phasor;
    private javax.swing.JButton PhasorStack;
    private javax.swing.JFormattedTextField PixelThresholdCutOffTF;
    private javax.swing.JPanel Unmixing;
    private javax.swing.JCheckBox UseLogScale;
    private javax.swing.JFormattedTextField applicationMedFilterTF;
    private javax.swing.JLabel applicationsMedianFilterLabel;
    private javax.swing.JButton backGrdSubtract;
    private javax.swing.JCheckBox backgroundSubtractCB;
    private javax.swing.JTextField binFactorImageTF;
    private javax.swing.JLabel binFactorImageText;
    private javax.swing.JLabel binFactorLabel;
    private javax.swing.JFormattedTextField binFactorTF;
    private javax.swing.JLabel cameraGainLabel;
    private javax.swing.JFormattedTextField cameraGainTF;
    private javax.swing.JLabel cameraOffsetLabel;
    private javax.swing.JFormattedTextField cameraOffsetTF;
    private javax.swing.JFormattedTextField chA_Gmean_TF;
    private javax.swing.JFormattedTextField chA_Kmean_TF;
    private javax.swing.JFormattedTextField chA_Name_TF;
    private javax.swing.JFormattedTextField chA_Smean_TF;
    private javax.swing.JFormattedTextField chB_Gmean_TF;
    private javax.swing.JFormattedTextField chB_Kmean_TF;
    private javax.swing.JFormattedTextField chB_Name_TF;
    private javax.swing.JFormattedTextField chB_Smean_TF;
    private javax.swing.JFormattedTextField chC_Gmean_TF;
    private javax.swing.JFormattedTextField chC_Kmean_TF;
    private javax.swing.JFormattedTextField chC_Name_TF;
    private javax.swing.JFormattedTextField chC_Smean_TF;
    private javax.swing.JFormattedTextField chD_Gmean_TF;
    private javax.swing.JFormattedTextField chD_Kmean_TF;
    private javax.swing.JFormattedTextField chD_Name_TF;
    private javax.swing.JFormattedTextField chD_Smean_TF;
    private javax.swing.JFormattedTextField chE_Gmean_TF;
    private javax.swing.JFormattedTextField chE_Kmean_TF;
    private javax.swing.JFormattedTextField chE_Name_TF;
    private javax.swing.JFormattedTextField chE_Smean_TF;
    private javax.swing.JCheckBox checkFitDouble;
    private javax.swing.JCheckBox checkFitSingle;
    private javax.swing.JCheckBox checkFitTriple;
    private javax.swing.JTextField emissionLambdaTF;
    private javax.swing.JLabel emissionLambdaText;
    private javax.swing.JLabel emissionLambdaText1;
    private javax.swing.JButton fitAzeroFixedExponential;
    private javax.swing.JButton fitExponential;
    private javax.swing.JButton getChannelAROI;
    private javax.swing.JButton getChannelBROI;
    private javax.swing.JButton getChannelCROI;
    private javax.swing.JButton getChannelDROI;
    private javax.swing.JButton getChannelEROI;
    private javax.swing.JTextField imagePixelSizeTF;
    private javax.swing.JLabel imagesPerCycleLabel;
    private javax.swing.JFormattedTextField imagesPerCycleTF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JCheckBox logPhasorTimes;
    private javax.swing.JButton makeRateConstantImage;
    private javax.swing.JCheckBox manualTimeCB;
    private javax.swing.JCheckBox HybridFitCB;
    private javax.swing.JLabel maxIterationsLabel;
    private javax.swing.JFormattedTextField maxIterationsTF;
    private javax.swing.JCheckBox medianFilterCB;
    private javax.swing.JLabel numCyclesLabel;
    private javax.swing.JFormattedTextField numCyclesTF;
    private javax.swing.JLabel numRestartsLabel;
    private javax.swing.JFormattedTextField numRestartsTF;
    private javax.swing.JTextField pixelSizeTF;
    private javax.swing.JLabel pixelThresholdCutoffLabel;
    private javax.swing.JButton readReferenceData;
    private javax.swing.JButton saveUnmixed;
    private javax.swing.JButton selectFromPhasorPlot;
    private javax.swing.JTextArea statusMessageArea;
    private javax.swing.JLabel terminalThresholdLabel;
    private javax.swing.JFormattedTextField terminalThresholdTF;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JFormattedTextField thresholdTF;
    private javax.swing.JButton unMixSignals;
    private javax.swing.JButton unMixSignalsUsingFit;
    private javax.swing.JCheckBox useChA_CB;
    private javax.swing.JCheckBox useChB_CB;
    private javax.swing.JCheckBox useChC_CB;
    private javax.swing.JCheckBox useChD_CB;
    private javax.swing.JCheckBox useChE_CB;
    private javax.swing.JCheckBox useTOneEstimateCB;
    private javax.swing.JLabel varParamLabel;
    private javax.swing.JFormattedTextField varParamTF;
    // End of variables declaration//GEN-END:variables

    





    
    
//********************Fitting**********************************    
    
    
    
    public void psFRET_Fit_exponential() throws Exception {
        IJ.resetMinAndMax(img);
        //in case the image is opened without using the plugin BioFormats button
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
        if (size == 1)//in case the stack is read as a Z stack instead of T stack
        {
            size = nSlices;
        }
        if (numCycles * imagesPerCycle > size) {
            IJ.showMessage("Pixel Fitter", "The number of cycles multiplied by the number images per cycle is larger than the stack");
            return;
        }
        try {
            timeData3 = getTimingPerPlane(id, size, currentZ, currentchannel);
            //the elapsed time from the start of the experiment 
            //is pulled from the metadata for each image using Bioformats
            //If no time data is found in the metadata; the user is prompted to enter a time interval
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
//3D arrays are initialized for the fit parameters        
        
        if(!fitSingle && !fitDouble && !fitTriple){
            IJ.showMessage("Fit exponential", "You must check Fit Single, Fit Double, or Fit Triple under the Fitting tab");
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
            //IJ.log("number of threads= " + threads.length);
            final double[][] timeDataArrayOfArrays = new double[threads.length][timeData.length];
            final double[][] pixelsArrayOfArrays = new double[threads.length][timeData.length];
            //two 2D arrays are initialized to handle the timedata and the pixel (fluorescence data) during the fitting
            
            IJ.log("this is thread size");
            IJ.log(Integer.toString(threads.length));
            
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
                            //the image is divided into equal sections based on the number of threads (CPUs)
                            //thus each cpu analyzes a different section of the image
                            if (threadIndex == (int) Math.ceil(threads.length/2)) {
                                //this part is to monitor the progress of the fitting
                                //it monitors the thread running in approximately the center of the image
                                int startY = threadIndex * height / threads.length;
                                int endY = (threadIndex + 1) * height / threads.length;
                                int progress = (int) Math.round(((double) (y - startY) / (endY - startY)) * 100.0);
                                statusMessageArea.setText("Fitting pixels progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
                                statusMessageArea.update(statusMessageArea.getGraphics());
                            }
                            for (int x = 0; x < width; x++) {
                                //the fits are performed line by line within each thread section
                                for (int z = 0; z < timeData.length; z++) {
                                    timeDataArrayOfArrays[threadIndex][z] = timeData[z];
                                    if (img.isHyperStack()) {
                                        int z2 = img.getStackIndex(img.getC(), img.getZ(), (cycleNum * imagesPerCycle) + z + 1) - 1;
                                        pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2);
                                    } else {
                                        pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, (cycleNum * imagesPerCycle) + z);
                                    }
                                    //the pixel values are retrieved slightly differently depending on a hyperstack or not 
                                }
                                pixelsArrayOfArrays[threadIndex] = subtractValueFromArray(pixelsArrayOfArrays[threadIndex], ((cameraOffset*binFactor*binFactor)));
                                //subtract the offset from the pixel values
                                //camera offsets are usually 100 counts per pixel
                                //binning obviously sums from multiple pixels
                                double firstframeint = pixelsArrayOfArrays[threadIndex][0];
                                double lastframeint = pixelsArrayOfArrays[threadIndex][pixelsArrayOfArrays[threadIndex].length - 1];
                                //to provide an initial guess for the fitting
                                double tau = findTauEstimate(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], firstframeint, lastframeint);
                                //to provide an initial estimate of the rate constant
                                double guess_o = 0;
                                double guess_a1 = 0;
                                double guess_k1 = 0;
                                double guess_a2 = 0;
                                double guess_k2 = 0;
                                double guess_a3 = 0;
                                double guess_k3 = 0;

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

                                double[] fitparam = {
                                    0,
                                    guess_o,
                                    guess_a1,
                                    guess_k1,
                                    guess_a2,
                                    guess_k2,
                                    guess_a3,
                                    guess_k3};

                                if (firstframeint - lastframeint < PixelThresholdCutOff) {
                                    //fill arrays with NaN if the threshold is not met
                                    if (fitTriple) {
                                        Chi2G[x][y][cycleNum] = Double.NaN;
                                        offsetDataG[x][y][cycleNum] = Double.NaN;
                                        a1DataG[x][y][cycleNum] = Double.NaN;
                                        k1DataG[x][y][cycleNum] = Double.NaN;
                                        a2DataG[x][y][cycleNum] = Double.NaN;
                                        k2DataG[x][y][cycleNum] = Double.NaN;
                                        a3DataG[x][y][cycleNum] = Double.NaN;
                                        k3DataG[x][y][cycleNum] = Double.NaN;
                                        
                                        //GmData[x][y][cycleNum] = Double.NaN;
                                        //GsData[x][y][cycleNum] = Double.NaN;
                                        //AZeroData[x][y][cycleNum] = Double.NaN;
                                    }
                                    if (fitDouble) {
                                        Chi2G[x][y][cycleNum] = Double.NaN;
                                        offsetDataG[x][y][cycleNum] = Double.NaN;
                                        a1DataG[x][y][cycleNum] = Double.NaN;
                                        k1DataG[x][y][cycleNum] = Double.NaN;
                                        a2DataG[x][y][cycleNum] = Double.NaN;
                                        k2DataG[x][y][cycleNum] = Double.NaN;
                                        
                                        //GmData[x][y][cycleNum] = Double.NaN;
                                        //GsData[x][y][cycleNum] = Double.NaN;
                                        //AZeroData[x][y][cycleNum] = Double.NaN;

                                    }
                                    if (fitSingle) {
                                        Chi2G[x][y][cycleNum] = Double.NaN;
                                        offsetDataG[x][y][cycleNum] = Double.NaN;
                                        a1DataG[x][y][cycleNum] = Double.NaN;
                                        k1DataG[x][y][cycleNum] = Double.NaN;
                                        
                                        //GmData[x][y][cycleNum] = Double.NaN;
                                        //GsData[x][y][cycleNum] = Double.NaN;
                                        //AZeroData[x][y][cycleNum] = Double.NaN;
                                    }

                                } else {

                                    double[] fittedParam = fitExponentialFunction(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], fitparam, maxiteration);
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
                                            
                                            double totalA = fittedParam[2]+fittedParam[4]+fittedParam[6];
                                            
                                            //GmData[x][y][cycleNum] = ((fittedParam[2]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3]))))) + ((fittedParam[4]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[5])*(1/fittedParam[5]))))) + ((fittedParam[6]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[7])*(1/fittedParam[7])))));
                                            //GsData[x][y][cycleNum] = ((fittedParam[2]/totalA)*((Omega*1/fittedParam[3])/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3]))))) + ((fittedParam[4]/totalA)*((Omega*1/fittedParam[5])/(1+((Omega*Omega)*(1/fittedParam[5])*(1/fittedParam[5]))))) + ((fittedParam[6]/totalA)*((Omega*1/fittedParam[7])/(1+((Omega*Omega)*(1/fittedParam[7])*(1/fittedParam[7])))));
                                            //AZeroData[x][y][cycleNum] = pixelsArrayOfArrays[threadIndex][0];
                                                                                                                               
                                        }
                                        if (fitDouble) {
                                            Chi2G[x][y][cycleNum] = (float) fittedParam[0];
                                            offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
                                            a1DataG[x][y][cycleNum] = (float) fittedParam[2];
                                            k1DataG[x][y][cycleNum] = (float) fittedParam[3];
                                            a2DataG[x][y][cycleNum] = (float) fittedParam[4];
                                            k2DataG[x][y][cycleNum] = (float) fittedParam[5];

                                            double totalA = fittedParam[2]+fittedParam[4];
                                            
                                            //GmData[x][y][cycleNum] = ((fittedParam[2]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3]))))) + ((fittedParam[4]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[5])*(1/fittedParam[5])))));
                                            //GsData[x][y][cycleNum] = ((fittedParam[2]/totalA)*((Omega*1/fittedParam[3])/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3]))))) + ((fittedParam[4]/totalA)*((Omega*1/fittedParam[5])/(1+((Omega*Omega)*(1/fittedParam[5])*(1/fittedParam[5])))));
                                            //AZeroData[x][y][cycleNum] = pixelsArrayOfArrays[threadIndex][0];

                                        }
                                        if (fitSingle) {
                                            Chi2G[x][y][cycleNum] = (float) fittedParam[0];
                                            offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
                                            a1DataG[x][y][cycleNum] = (float) fittedParam[2];
                                            k1DataG[x][y][cycleNum] = (float) fittedParam[3];
 
                                            double totalA = fittedParam[2];
                                            
                                            //GmData[x][y][cycleNum] = ((fittedParam[2]/totalA)*(1/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3])))));
                                            //GsData[x][y][cycleNum] = ((fittedParam[2]/totalA)*((Omega*1/fittedParam[3])/(1+((Omega*Omega)*(1/fittedParam[3])*(1/fittedParam[3])))));
                                            //AZeroData[x][y][cycleNum] = pixelsArrayOfArrays[threadIndex][0];

                                        }
                                    } else {
                                        //if the Chi2 is not below the threshold fill the arrays with NaN
                                        if (fitTriple) {
                                            Chi2G[x][y][cycleNum] = Double.NaN;
                                            offsetDataG[x][y][cycleNum] = Double.NaN;
                                            a1DataG[x][y][cycleNum] = Double.NaN;
                                            k1DataG[x][y][cycleNum] = Double.NaN;
                                            a2DataG[x][y][cycleNum] = Double.NaN;
                                            k2DataG[x][y][cycleNum] = Double.NaN;
                                            a3DataG[x][y][cycleNum] = Double.NaN;
                                            k3DataG[x][y][cycleNum] = Double.NaN;
                                            
                                            //GmData[x][y][cycleNum] = Double.NaN;
                                            //GsData[x][y][cycleNum] = Double.NaN;
                                            //AZeroData[x][y][cycleNum] = Double.NaN;
                                        }
                                        if (fitDouble) {
                                            Chi2G[x][y][cycleNum] = Double.NaN;
                                            offsetDataG[x][y][cycleNum] = Double.NaN;
                                            a1DataG[x][y][cycleNum] = Double.NaN;
                                            k1DataG[x][y][cycleNum] = Double.NaN;
                                            a2DataG[x][y][cycleNum] = Double.NaN;
                                            k2DataG[x][y][cycleNum] = Double.NaN;
                                            
                                            //GmData[x][y][cycleNum] = Double.NaN;
                                            //GsData[x][y][cycleNum] = Double.NaN;
                                            //AZeroData[x][y][cycleNum] = Double.NaN;
                                        }
                                        if (fitSingle) {
                                            Chi2G[x][y][cycleNum] = Double.NaN;
                                            offsetDataG[x][y][cycleNum] = Double.NaN;
                                            a1DataG[x][y][cycleNum] = Double.NaN;
                                            k1DataG[x][y][cycleNum] = Double.NaN;
                                            
                                            //GmData[x][y][cycleNum] = Double.NaN;
                                            //GsData[x][y][cycleNum] = Double.NaN;
                                            //AZeroData[x][y][cycleNum] = Double.NaN;
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
            statusMessageArea.setText("Fitting time: " + timeToCompletion);                                
            statusMessageArea.update(statusMessageArea.getGraphics());
            if (LogFitTime == true) {
                //can be helpful if running multiple cycles
                IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
            }
        } //end of cycles 
        wmkImage = createWeightedRateConstantsImage();
    }

    public void printSystemTime() {
    	
    	Calendar now = Calendar.getInstance();
    	int year = now.get(Calendar.YEAR);
    	int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
    	int day = now.get(Calendar.DAY_OF_MONTH);
    	int hour = now.get(Calendar.HOUR_OF_DAY);
    	int minute = now.get(Calendar.MINUTE);
    	int second = now.get(Calendar.SECOND);
    	int millis = now.get(Calendar.MILLISECOND);
    	String output=String.format("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
    	IJ.log(output);
    	
    }
    
    //newchanges
    public void psAzeroExponentialFit() throws Exception {
    	
        IJ.resetMinAndMax(img);
        //in case the image is opened without using the plugin BioFormats button
        String dir0 = IJ.getDirectory("image");
        String stackToOpen = img.getTitle();
        String id2 = dir0 + stackToOpen;
        String fExt = id2.substring(id2.lastIndexOf("."), id2.length());
        if (fExt.contains(" ") && fExt.indexOf(" ") < id2.length()) {
            fExt = fExt.substring(0, fExt.indexOf(" "));
        }
        id = id2.substring(0, id2.lastIndexOf(".")) + fExt;
        
        //call the init
        
        //call the new fitting function here
        
        initAZeroFitting();//initializes all the parameters for fitting class
        cfAzeroInst.psFRETFitAzeroExponential(); //call to the main exponential function, replaces the call to psFRET_Fit_Azero_Exponential() in the previous program/
        
        //what about the values used in the subsequent function
        
    }
    
    public void psFRET_Fit_Azero_Exponential() throws Exception {
        //this method is the same as above except that the rate constants are fixed and only the fractional components are fitted
        

        IJ.resetMinAndMax(img);
        //in case the image is opened without using the plugin BioFormats button
        String dir0 = IJ.getDirectory("image");
        String stackToOpen = img.getTitle();
        String id2 = dir0 + stackToOpen;
        String fExt = id2.substring(id2.lastIndexOf("."), id2.length());
        if (fExt.contains(" ") && fExt.indexOf(" ") < id2.length()) {
            fExt = fExt.substring(0, fExt.indexOf(" "));
        }
        id = id2.substring(0, id2.lastIndexOf(".")) + fExt;

        //the init should go here
        
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
            IJ.showMessage("Pixel Fitter", "The number of cycles multiplied by the number images per cycle is larger than the stack");
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
        
        
        //temp values for next cycle
        //usePhasortoInitialize=true; 
        
        Chi2G_t = new double[imageW][imageH][numCycles];
        offsetDataG_t = new double[imageW][imageH][numCycles];
        a1DataG_t = new double[imageW][imageH][numCycles];
        a2DataG_t = new double[imageW][imageH][numCycles];
        a3DataG_t= new double[imageW][imageH][numCycles];
        a4DataG_t = new double[imageW][imageH][numCycles];
        a5DataG_t = new double[imageW][imageH][numCycles];
        
   
        for (int cycle = 0; cycle < numCycles; cycle++) {
            IJ.log("for each cycle");
            printSystemTime();
            
            final long startTime = System.currentTimeMillis();
            
            IJ.log(Long.toString(startTime));
            double[] timeData2 = new double[imagesPerCycle];
            for (int k = 0; k < imagesPerCycle; k++) {
                timeData2[k] = (timeData3[k + (cycle * imagesPerCycle)] - timeData3[cycle * imagesPerCycle]);
            }
            timeData = timeData2;

            final Thread[] threads = newThreadArray();
            //IJ.log("number of threads= " + threads.length);
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
                        for (int y = threadIndex * height / threads.length; y < (threadIndex + 1) * height / threads.length; y++) {
                            if (threadIndex == threads.length - 1) {
                                int startY = threadIndex * height / threads.length;
                                int endY = (threadIndex + 1) * height / threads.length;
                                int progress = (int) Math.round(((double) (y - startY) / (endY - startY)) * 100);
                                statusMessageArea.setText("Fitting pixels progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
                                statusMessageArea.update(statusMessageArea.getGraphics());
                            }
                            for (int x = 0; x < width; x++) {
                            	for (int z = 0; z < timeData.length; z++) {
                            		timeDataArrayOfArrays[threadIndex][z] = timeData[z];
                            		if (img.isHyperStack()) {
                            			int z2 = img.getStackIndex(img.getC(), img.getZ(), (cycleNum * imagesPerCycle) + z + 1) - 1;
                            			pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, z2);
                            		} else {
                            			pixelsArrayOfArrays[threadIndex][z] = img2.getVoxel(x, y, (cycleNum * imagesPerCycle) + z);
                            		}
                            	}
                            	pixelsArrayOfArrays[threadIndex] = subtractValueFromArray(pixelsArrayOfArrays[threadIndex], cameraOffset*binFactor*binFactor);
                            	double firstframeint = pixelsArrayOfArrays[threadIndex][0];
                            	double lastframeint = pixelsArrayOfArrays[threadIndex][pixelsArrayOfArrays[threadIndex].length - 1];
                            	//double tau = findTauEstimate(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], firstframeint, lastframeint); //OPT not sure if needed commenting to test
                            	
                            	
                            	//initialization for this round of fitting
                            	double[] fitparam=initAzeorExponentialFit(x,y,cycleNum);
//                            	
//                            	double guess_o = 0;
//                            	double guess_a1 = 0;
//                            	double guess_a2 = 0;                                
//                            	double guess_a3 = 0;
//                            	double guess_a4 = 0;                                
//                            	double guess_a5 = 0;
//                            	
//
//                            	
//                            	
//                        		if(usePhasortoInitialize&&isPhasorFitDone) {
//                        			//now testing for first cycle only
//                        			//initialize using the data from phasor
//                        			//later expand and compare with subsequent cycles
//                            		guess_o = 	0;
//                            		guess_a1 = 	arrayChA[x][y][0];
//                            		guess_a2 = 	arrayChB[x][y][0];                                
//                            		guess_a3 = 	arrayChC[x][y][0];
//                            		guess_a4 = 	arrayChD[x][y][0];                                
//                            		guess_a5 = 	arrayChE[x][y][0];
//                        		}
//                        	
//                            	
//                            	if(cycleNum>0)
//                            	{
//
//                            		//copying from the previous cycle for initializing value for the subsequent cycle
//                            		guess_o = 	offsetDataG_t[x][y][cycleNum-1];
//                            		guess_a1 = 	a1DataG_t[x][y][cycleNum-1] ;
//                            		guess_a2 = 	a2DataG_t[x][y][cycleNum-1];                                
//                            		guess_a3 = 	a3DataG_t[x][y][cycleNum-1];
//                            		guess_a4 = 	a4DataG_t[x][y][cycleNum-1];                                
//                            		guess_a5 = 	a5DataG_t[x][y][cycleNum-1];
//
//
//                            	}
//                            	
//                            	double[] fitparam= {0,guess_o, guess_a1, guess_a2,guess_a3,guess_a4,guess_a5};
                            	
                            	


                            	if (firstframeint - lastframeint < PixelThresholdCutOff) {
                            		Chi2G[x][y][cycleNum] = Double.NaN;
                            		offsetDataG[x][y][cycleNum] = Double.NaN;
                            		a1DataG[x][y][cycleNum] = Double.NaN;
                            		a2DataG[x][y][cycleNum] = Double.NaN;
                            		a3DataG[x][y][cycleNum] = Double.NaN;
                            		a4DataG[x][y][cycleNum] = Double.NaN;
                            		a5DataG[x][y][cycleNum] = Double.NaN;

                            	} else {

                            		double[] fittedParam = fitAzeroExponentialFunction(timeDataArrayOfArrays[threadIndex], pixelsArrayOfArrays[threadIndex], fitparam, maxiteration);
                            		double Chi2 = fittedParam[0];

                            		if (Chi2 < Chi2CutOff) {
                            			offsetDataG[x][y][cycleNum] = (float) fittedParam[1];
                            			a1DataG[x][y][cycleNum] = (float) fittedParam[2];
                            			a2DataG[x][y][cycleNum] = (float) fittedParam[3];
                            			a3DataG[x][y][cycleNum] = (float) fittedParam[4];
                            			a4DataG[x][y][cycleNum] = (float) fittedParam[5];
                            			a5DataG[x][y][cycleNum] = (float) fittedParam[6];
                            			
                            			if(cycleNum<(numCycles-1)) {//OPT copy fitted param for initialization for next cycle 
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

                            			if(cycleNum<(numCycles-1)) {
                            				offsetDataG_t[x][y][cycleNum] = Double.NaN;;
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
            statusMessageArea.setText("Fitting time: " + timeToCompletion);                                
            statusMessageArea.update(statusMessageArea.getGraphics());
            if (LogFitTime == true) {
                IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
            }
            IJ.log("fitting function over psFRET_Fit_Azero_Exponential");
            printSystemTime();
           
        } //end of cycles 

    }
    
    
    public double findTauEstimate(double[] x, double[] y, double startIntensity, double endIntensity) {
        //this method is used to find an initial guess for the rate constant
        double tauToReturn = 1;
        double previousIntensity = startIntensity - endIntensity;
        for (int t = 0; t < y.length; t++) {
            if ((y[t] - endIntensity) <= ((startIntensity - endIntensity) * 0.37) && previousIntensity >= ((startIntensity - endIntensity) * 0.37)) {
                tauToReturn = x[t];
            }
            previousIntensity = y[t] - endIntensity;
        }
        return tauToReturn;
    }
/*
    @Override
    public double userFunction(double[] par, double x) {
        if (fitDouble) {
            return par[0] * Math.exp(-par[1] * x) + par[2] * Math.exp(-par[3] * x) + par[4];
        }
        return par[0] * Math.exp(-par[1] * x) + par[2];
    }
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
            //The initial guesses are converted to their square root
            //The fitting equations use the squares of the components which we want to force to be positive
        }
        if (fitDouble) {
            params = new double[paramsPassed.length - 3];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);
            params[2] = Math.sqrt(params[2]);
            params[3] = Math.sqrt(params[3]);
            params[4] = Math.sqrt(params[4]);
            //The initial guesses are converted to their square root
            //The fitting equations use the squares of the components which we want to force to be positive
        }
        double[] returnArray = new double[paramsPassed.length];
        Arrays.fill(returnArray, 0);

        double[] paramVariations = multiplyArrayByValue(params, varParam);
        CurveFitter cf = new CurveFitter(x, y);

        if (fitDouble || fitTriple) {
            //Have to use a user function for the 2 and 3 component fits
            //these are slow and not terribly accurate for the 3
            
            //cf.setOffsetMultiplySlopeParams(0, -1, -1);
            cf.setMaxIterations(maxIter);
            cf.doCustomFit(new UserFunction() {
                @Override
                public double userFunction(double[] par, double x) {
                    if (fitTriple) {
                        //return par[0] + par[1] * Math.exp(-par[2] * x) + par[3] * Math.exp(-par[4] * x) + par[5] * Math.exp(-par[6] * x);
                        //return par[0] * (1 + par[1] * par[1] * Math.exp(-par[2] * par[2] * x) + par[3] * par[3] * Math.exp(-par[4] * par[4] * x) + par[5] * par[5] * Math.exp(-par[6] * par[6] * x));
                        return par[0] + (par[1] * par[1] * Math.exp(-par[2] * par[2] * x)) + (par[3] * par[3] * Math.exp(-par[4] * par[4] * x)) + (par[5] * par[5] * Math.exp(-par[6] * par[6] * x));
                    } else {
                        //return par[0] + par[1] * Math.exp(-par[2] * x) + par[3] * Math.exp(-par[4] * x);
                        //return par[0] * (1 + par[1] * par[1] * Math.exp(-par[2] * par[2] * x) + par[3] * par[3] * Math.exp(-par[4] * par[4] * x));
                        return par[0] + (par[1] * par[1] * Math.exp(-par[2] * par[2] * x)) + (par[3] * par[3] * Math.exp(-par[4] * par[4] * x));
                    }
                }
            }, params.length, "", params, paramVariations, false);
            double[] paramToReturn = cf.getParams();
            double Chi2ToReturn = calculateReducedChi2(cf.getResiduals(), y);
            returnArray[0] = Chi2ToReturn;
            returnArray[1] = paramToReturn[0];

            if (fitTriple) {//Since the faster and slower components can be any of the three, this section rearranges them for final output          
                double[] arrayOfValues = new double[3];
                arrayOfValues[0] = paramToReturn[2];
                arrayOfValues[1] = paramToReturn[4];
                arrayOfValues[2] = paramToReturn[6];

                int[] arrayOfIndices = new int[3];
                arrayOfIndices[0] = 2;
                arrayOfIndices[1] = 4;
                arrayOfIndices[2] = 6;

                int maxValueIndex = getIndexOfMaxValue(arrayOfIndices, arrayOfValues);
                int minValueIndex = getIndexOfMinValue(arrayOfIndices, arrayOfValues);
                int midValueIndex = getIndexOfMiddle(arrayOfIndices, arrayOfValues);

                returnArray[2] = paramToReturn[maxValueIndex - 1] * paramToReturn[maxValueIndex - 1];
                returnArray[3] = Math.pow(paramToReturn[maxValueIndex], 2);
                returnArray[4] = paramToReturn[midValueIndex - 1] * paramToReturn[midValueIndex - 1];
                returnArray[5] = Math.pow(paramToReturn[midValueIndex], 2);
                returnArray[6] = paramToReturn[minValueIndex - 1] * paramToReturn[minValueIndex - 1];
                returnArray[7] = Math.pow(paramToReturn[minValueIndex], 2);
            }
            if (fitDouble) {//Since the faster and slower components can be either of the two, this section rearranges them for final output           
                double[] arrayOfValues = new double[2];
                arrayOfValues[0] = paramToReturn[2];
                arrayOfValues[1] = paramToReturn[4];

                int[] arrayOfIndices = new int[2];
                arrayOfIndices[0] = 2;
                arrayOfIndices[1] = 4;

                int maxValueIndex = getIndexOfMaxValue(arrayOfIndices, arrayOfValues);
                int minValueIndex = getIndexOfMinValue(arrayOfIndices, arrayOfValues);

                returnArray[2] = paramToReturn[maxValueIndex - 1] * paramToReturn[maxValueIndex - 1];
                returnArray[3] = Math.pow(paramToReturn[maxValueIndex], 2);
                returnArray[4] = paramToReturn[minValueIndex - 1] * paramToReturn[minValueIndex - 1];
                returnArray[5] = Math.pow(paramToReturn[minValueIndex], 2);
            }
        } else {
            //for the single exponential with offset fit
            //we use the built in version
            //we found it to be ~5 faster than the same type in the user function
            cf = new CurveFitter(x, y);
            double errotTol = 10;
            double[] fitparam = {
                paramsPassed[2],
                paramsPassed[3],
                paramsPassed[1],
                maxiteration,
                numRestarts,
                errotTol
            };
            cf.setInitialParameters(fitparam);
            cf.doFit(11); //exponential decay with offset: (A*exp^(-k*t))+offset
            double[] fittedParam = cf.getParams();
            double R2 = cf.getFitGoodness();
            double[] residuals = cf.getResiduals();
            double Chi2 = calculateReducedChi2(residuals, y);

            returnArray[0] = Chi2;
            returnArray[1] = fittedParam[2];
            returnArray[2] = fittedParam[0];
            returnArray[3] = fittedParam[1];
        }
        return returnArray;
    }
    /*
     x=time
     y=pixel value
     */
    
    private double[] initAzeorExponentialFit(int x, int y, int cycleNum) {
    	
    	
    	double guess_o = 0;
    	double guess_a1 = 0;
    	double guess_a2 = 0;                                
    	double guess_a3 = 0;
    	double guess_a4 = 0;                                
    	double guess_a5 = 0;
    	

    	
    	
		if(usePhasortoInitialize&&isPhasorFitDone) {
			//now testing for first cycle only
			//initialize using the data from phasor
			//later expand and compare with subsequent cycles
			IJ.log("came here");
    		guess_o = 	0;
    		guess_a1 = 	arrayChA[x][y][0];
    		guess_a2 = 	arrayChB[x][y][0];                                
    		guess_a3 = 	arrayChC[x][y][0];
    		guess_a4 = 	arrayChD[x][y][0];                                
    		guess_a5 = 	arrayChE[x][y][0];
		}
	
    	
    	if(cycleNum>0)
    	{

    		//copying from the previous cycle for initializing value for the subsequent cycle
    		guess_o = 	offsetDataG_t[x][y][cycleNum-1];
    		guess_a1 = 	a1DataG_t[x][y][cycleNum-1] ;
    		guess_a2 = 	a2DataG_t[x][y][cycleNum-1];                                
    		guess_a3 = 	a3DataG_t[x][y][cycleNum-1];
    		guess_a4 = 	a4DataG_t[x][y][cycleNum-1];                                
    		guess_a5 = 	a5DataG_t[x][y][cycleNum-1];


    	}
    	
    	double[] fitData= {0,guess_o, guess_a1, guess_a2,guess_a3,guess_a4,guess_a5};
    	return fitData; 
    	
    	
    	
    	
    }
    private double[] fitAzeroExponentialFunction(double[] x, double[] y, double[] paramsPassed, int maxIter) {
    	
//    	IJ.log("inside the fit function start");
        //this is the method fitting only the fractional contributions
        //it assumes that rate constants have been measured previously and input into the plugin gui
        double[] params = new double[0];
        if (useChA && useChB && useChC && useChD && useChE) {
            params = new double[paramsPassed.length - 1];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);//converting to the square root and then including the squares in the equation forces these to be positive
            params[2] = Math.sqrt(params[2]);
            params[3] = Math.sqrt(params[3]);
            params[4] = Math.sqrt(params[4]);
            params[5] = Math.sqrt(params[5]);
        }
        if (useChA && useChB && useChC && useChD && !useChE) {
            params = new double[paramsPassed.length - 2];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);//converting to the square root and then including the squares in the equation forces these to be positive
            params[2] = Math.sqrt(params[2]);
            params[3] = Math.sqrt(params[3]);
            params[4] = Math.sqrt(params[4]);
        }
        if (useChA && useChB && useChC && !useChD && !useChE) {
            params = new double[paramsPassed.length - 3];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);//converting to the square root and then including the squares in the equation forces these to be positive
            params[2] = Math.sqrt(params[2]);
            params[3] = Math.sqrt(params[3]);
        }
        if (useChA && useChB && !useChC && !useChD && !useChE) {
            params = new double[paramsPassed.length - 4];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);//converting to the square root and then including the squares in the equation forces these to be positive
            params[2] = Math.sqrt(params[2]);
        }
        if (useChA && !useChB && !useChC && !useChD && !useChE) {
            params = new double[paramsPassed.length - 5];
            System.arraycopy(paramsPassed, 1, params, 0, params.length);
            params[1] = Math.sqrt(params[1]);//converting to the square root and then including the squares in the equation forces these to be positive
        }
       
        //double[] paramVariations = multiplyArrayByValue(params, varParam); //OPT
        // x, y length 300
        CurveFitter cf = new CurveFitter(x, y);
        cf.setOffsetMultiplySlopeParams(-1, -1, -1);
        cf.setMaxIterations(maxIter);      
     //	should set the initial pa
       // cf.setRestarts(4);
        cf.doCustomFit(new UserFunction() {
            @Override
            public double userFunction(double[] par, double x) {
                if (useChA && useChB && useChC && useChD && useChE){
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x)) + (par[2] * par[2] * Math.exp(-chB_Kmean * x)) + (par[3] * par[3] * Math.exp(-chC_Kmean * x)) + (par[4] * par[4] * Math.exp(-chD_Kmean * x)) + (par[5] * par[5] * Math.exp(-chE_Kmean * x));                
                }
                if (useChA && useChB && useChC && useChD && !useChE){
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x)) + (par[2] * par[2] * Math.exp(-chB_Kmean * x)) + (par[3] * par[3] * Math.exp(-chC_Kmean * x)) + (par[4] * par[4] * Math.exp(-chD_Kmean * x));
                }
                if (useChA && useChB && useChC && !useChD && !useChE){
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x)) + (par[2] * par[2] * Math.exp(-chB_Kmean * x)) + (par[3] * par[3] * Math.exp(-chC_Kmean * x));
                }
                if (useChA && useChB && !useChC && !useChD && !useChE){
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x)) + (par[2] * par[2] * Math.exp(-chB_Kmean * x));
                }
                if (useChA && !useChB && !useChC && !useChD && !useChE){
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x));
                }
                else{
                    return par[0] + (par[1] * par[1] * Math.exp(-chA_Kmean * x));
                }
            }
        }, params.length, "", params, null, false);             
        double[] paramToReturn = cf.getParams();
        double Chi2ToReturn = calculateReducedChi2(cf.getResiduals(), y);
        double[] returnArray = new double[paramsPassed.length];
        Arrays.fill(returnArray, 0);
        returnArray[0] = Chi2ToReturn;
        returnArray[1] = paramToReturn[0];
        
        //IJ.log("chi2 calculated "+Double.toString(Chi2ToReturn)+" param 0 = "+Double.toString(paramToReturn[0]));
        if (useChA) {
            returnArray[2] = paramToReturn[1] * paramToReturn[1];//these are the square roots; convert back to the fractional contributions
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
        
    public double[] getTheFit(double[] fitParameters, double[] timePoints) {
        //returns values based on fit parameters
        //these are used in plotting since the residuals can be directly returned from the fits
        double[] theFit = new double[timePoints.length];
        for (int tp = 0; tp < timePoints.length; tp++) {
            if(fitTriple)
                theFit[tp] =  fitParameters[1] + (fitParameters[2] * Math.exp(-fitParameters[3] * timePoints[tp])) + (fitParameters[4] * Math.exp(-fitParameters[5] * timePoints[tp])) + (fitParameters[6] * Math.exp(-fitParameters[7] * timePoints[tp]));
            if(fitDouble)
                theFit[tp] =  fitParameters[1] + (fitParameters[2] * Math.exp(-fitParameters[3] * timePoints[tp])) + (fitParameters[4] * Math.exp(-fitParameters[5] * timePoints[tp]));
            if(fitSingle)
                theFit[tp] =  fitParameters[1] + (fitParameters[2] * Math.exp(-fitParameters[3] * timePoints[tp]));
        }
        return theFit;
    }

    public double calculateChi2(double[] residualArray, double[] theFitArray) {
        //returns Pearson's Chi square; probably less helpful than the reduced Chi2
        double chi2ToReturn = 0;
        double[] residualArray2 = multiplyTwoArrays(residualArray, residualArray);
        double[] arrayToSum = divideTwoArrays(residualArray2, theFitArray);
        for (int tp = 0; tp < arrayToSum.length; tp++) {
            if (!Double.isInfinite(arrayToSum[tp])) {
                chi2ToReturn += arrayToSum[tp];
            }
        }
        return chi2ToReturn;
    }
    
    public static double calculateReducedChi2(double[] residualArray, double[] dataArray) {
        //returns a reduced Chi2
        //pixel values are converted into photoelectrons using the camera gain
        //since we we also bin pixels, are using a camera and we are still limited by diffraction
        //pixels within the PSF are expected to show some correlated noise
        //thus covariance within the PSF is used toi estimate the variance
        //along with the number of photoelectrons when calculating the weighted reduced Chi2
        double AiryRadius = 1.22*lambda/NA;         
        double PSFArea = Math.PI*AiryRadius*AiryRadius;        
        double numPixelsInPSF = PSFArea/(pixSize*pixSize);
        double chi2ToReturn = 0;
        double weightedAverageVariance = 0;
        double sumWeightedVariance = 0;

        residualArray = multiplyArrayByValue(residualArray,binFactorImage*binFactorImage);//in case of post-processing bin averaging to get total signal
        dataArray = multiplyArrayByValue(dataArray,binFactorImage*binFactorImage);        
        
        residualArray = divideArrayByValue(residualArray,cameraGain);//convert to electrons
        dataArray = divideArrayByValue(dataArray,cameraGain);//convert to electrons; this will also be the uncorrelated variance        
        double[] coVarArray = multiplyArrayByValue(dataArray,binFactor*binFactor/numPixelsInPSF); 
        //if occuring covariance should be within a PSF. The total signal is scaled by the 
        //number of unbinned pixels (number of actual measurements) in a PSF
        //these would be the camera pixels which may have correlated noise
        //this is more of a problem with larger Rois
        //I don't think this is an issue with TCSPC FLIM imaging, which the field from which I'm trying to borrow methods
        dataArray=addTwoArrays(dataArray,coVarArray);//total variance
        
        for(int da=0;da<dataArray.length;da++){
            if(dataArray[da]>0)//if the data are negative or zero, do not use in Chi2 determination
                sumWeightedVariance += (1/dataArray[da]);
        }
        weightedAverageVariance = sumWeightedVariance/dataArray.length;
        double[] weightArray = new double[dataArray.length];
        for(int wa=0;wa<dataArray.length;wa++){
            if(dataArray[wa]>0)//if the data are negative or zero, do not use in Chi2 determination
                weightArray[wa] = (1/dataArray[wa])/weightedAverageVariance;
        }
        double[] residualArray2 = multiplyTwoArrays(residualArray, residualArray);
        //double[] arrayToSum = multiplyTwoArrays(residualArray2, weightArray);
        double[] arrayToSum = new double[residualArray2.length];
        for (int tp = 0; tp < arrayToSum.length; tp++) {
            if(dataArray[tp]>0)
                arrayToSum[tp]=residualArray2[tp]*weightArray[tp];
            if (!Double.isInfinite(arrayToSum[tp])) {               
                chi2ToReturn += arrayToSum[tp];
            }
        }
        int df;
        if(fitTriple)
            df=8;
        if(fitDouble)
            df=6;
        else
            df=4;
        return (chi2ToReturn/(residualArray.length-df))*weightedAverageVariance;
    }


    
    
    
    
//****************Fit and Data Plotting, Profiling, etc.**********************************
    
    double[] getTAxisProfile() {
        //This is used to get a background trace if needed.
        img = IJ.getImage();
        Roi roi = img.getRoi();
        if (roi == null) {
            IJ.showMessage("Phasor Plotter", "Please make an ROI on the image for background subtraction");
            return null;
        }
        int nSlices = img.getNSlices();
        int size = img.getNFrames();
        if (size == 1)//in case the stack is read as a Z stack instead of T stack
        {
            size = nSlices;
        }
        int currentchannel = img.getC() - 1;
        int currentZ = img.getZ() - 1;
        double[] values = new double[size];
        Calibration cal = img.getCalibration();
        //ROI with Area > 0
        img.setC(currentchannel + 1);
        for (int i = 1; i <= size; i++) {
            if (img.getNFrames() == 1) {
                img.setZ(i);
            } else {
                img.setT(i);
            }
            ImageProcessor ip = img.getProcessor();
            ip.setRoi(roi);
            ImageStatistics stats = ImageStatistics.getStatistics(ip, MEAN, cal);
            values[i - 1] = ((double) stats.mean - cameraOffset*binFactor*binFactor);
        }
        if (img.getNFrames() == 1) {
            img.setZ(1);
        } else {
            img.setT(1);
        }
        double[] extrema = Tools.getMinMax(values);
        if (Math.abs(extrema[1]) == Double.MAX_VALUE) {
            return null;
        } else {
            return values;
        }
    }

    public void setUpListeners() {
        //This sets up the mouse listeners for examining the fits of individual pixels after a fitting routine
        String[] imageTitles = WindowManager.getImageTitles();
        listenersRemoved = false;
        ImageWindow win = img.getWindow();
        win.addWindowListener(win);
        canvas = win.getCanvas();
        canvas.addMouseListener(this);

        for (String imageTitle : imageTitles) {
            if (imageTitle.contains("RateConstantsImage")) {
                wmkImage = WindowManager.getImage(imageTitle);
                ImageWindow win2 = wmkImage.getWindow();
                win2.addWindowListener(win2);
                canvas2 = win2.getCanvas();
                canvas2.addMouseListener(this);
            }
        }

        xAxis = timeData3;
        xLabel = "Time (sec)";
        yLabel = "Fluorescence";
    }

    void positionPlotWindow() {
        IJ.wait(500);
        if (pwin == null || img == null) {
            return;
        }
        ImageWindow iwin = img.getWindow();
        if (iwin == null) {
            return;
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension plotSize = pwin.getSize();
        Dimension imageSize = iwin.getSize();
        if (plotSize.width == 0 || imageSize.width == 0) {
            return;
        }
        Point imageLoc = iwin.getLocation();
        int w = imageLoc.x + imageSize.width + 10;
        if (w + plotSize.width > screen.width) {
            w = screen.width - plotSize.width;
        }
        pwin.setLocation(w, imageLoc.y);
        iwin.toFront();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    //Gets the pixel values through a single point at (x,y). 
        int mag = (int) img.getCanvas().getMagnification();
        Rectangle imgr = img.getCanvas().getSrcRect();
        int magRC = (int) wmkImage.getCanvas().getMagnification();
        Rectangle imgRC = wmkImage.getCanvas().getSrcRect();
        if(mag>magRC){
            //this part is dealing with changes in zoom factor in case 
            //you need to zoom into a specific region to pick a pixel to examine
            wmkImage.getCanvas().setMagnification(mag);
            //rateConstantImage.getCanvas().zoomIn(mag*img.getWidth(), mag*img.getHeight());
            wmkImage.getCanvas().setSourceRect(imgr);
        }
        if(mag<magRC){
            mag=magRC;
            img.getCanvas().setMagnification(mag);
            //img.getCanvas().zoomIn(mag*img.getWidth(), mag*img.getHeight());
            img.getCanvas().setSourceRect(imgRC);
        }
        ImageStack stack = img.getStack();
        int size = stack.getSize();
        double[] values = new double[size];
        xpoint = e.getX()/mag;
        ypoint = e.getY()/mag;
        float[] cTable = img.getCalibration().getCTable();
        if (img.isHyperStack()) {
            //the pixel values are retrieved different depending on hyperstack or not
            for (int p = 1; p <= img.getNFrames(); p++) {
                int z2 = img.getStackIndex(img.getC(), img.getZ(), p) - 1;
                values[p - 1] = (stack.getVoxel(xpoint, ypoint, z2)-(cameraOffset*binFactor*binFactor));
            }
        } else {
            for (int p = 1; p <= size; p++) {
                ImageProcessor ip = stack.getProcessor(p);
                ip.setCalibrationTable(cTable);
                values[p - 1] = (ip.getPixelValue(xpoint, ypoint)-(cameraOffset*binFactor*binFactor));
            }
        }

        img.setRoi(xpoint, ypoint, 1, 1);
        wmkImage.setRoi(xpoint, ypoint, 1, 1);
        yAxis = values;
        xAxis = timeData3;
        if (size > numCycles * imagesPerCycle) {
            double[] yAxis2 = new double[numCycles * imagesPerCycle];
            double[] xAxis2 = new double[numCycles * imagesPerCycle];
            for (int i = 0; i < numCycles * imagesPerCycle; i++) {
                yAxis2[i] = yAxis[i];
                xAxis2[i] = xAxis[i];
            }
            yAxis = yAxis2;
            xAxis = xAxis2;
        }
        updateProfile(xAxis, yAxis);

    }

    void updateProfile(double[] x, double[] y) {
        //updates the plots, fits, and residuals
        if (!isSelection()) {
            return;
        }
        checkPlotWindow();
        if (listenersRemoved || y == null || y.length == 0) {
            return;
        }
        Plot plot = new Plot("Data and fit", xLabel, yLabel);
        plot.setAxisYLog(yLog);

        plot.add("circles", x, y);
        double[] fitToAdd = new double[x.length];
        double[] yForResiduals = new double[x.length];
        if (k1DataG != null) {
            for (int c = 0; c < numCycles; c++) {
                double[] fitValues = new double[8];
                if (fitTriple) {
                    fitValues[0] = Chi2G[xpoint][ypoint][c];
                    fitValues[1] = offsetDataG[xpoint][ypoint][c];
                    fitValues[2] = a1DataG[xpoint][ypoint][c];
                    fitValues[3] = k1DataG[xpoint][ypoint][c];
                    fitValues[4] = a2DataG[xpoint][ypoint][c];
                    fitValues[5] = k2DataG[xpoint][ypoint][c];
                    fitValues[6] = a3DataG[xpoint][ypoint][c];
                    fitValues[7] = k3DataG[xpoint][ypoint][c];
                }
                if (fitDouble) {
                    fitValues[0] = Chi2G[xpoint][ypoint][c];
                    fitValues[1] = offsetDataG[xpoint][ypoint][c];
                    fitValues[2] = a1DataG[xpoint][ypoint][c];
                    fitValues[3] = k1DataG[xpoint][ypoint][c];
                    fitValues[4] = a2DataG[xpoint][ypoint][c];
                    fitValues[5] = k2DataG[xpoint][ypoint][c];
                }
                if (fitSingle) {
                    fitValues[0] = Chi2G[xpoint][ypoint][c];
                    fitValues[1] = offsetDataG[xpoint][ypoint][c];
                    fitValues[2] = a1DataG[xpoint][ypoint][c];
                    fitValues[3] = k1DataG[xpoint][ypoint][c];
                }
                
                double[] x2 = new double[imagesPerCycle];
                double[] x4Plot = new double[imagesPerCycle];
                for (int i = 0; i < x2.length; i++) {
                    x2[i] = x[(c * imagesPerCycle) + i] - x[c * imagesPerCycle];
                    x4Plot[i] = x[(c * imagesPerCycle) + i];
                }
                double[] fitPerCycle = getTheFit(fitValues, x2);
                for (int i = 0; i < fitPerCycle.length; i++) {
                    fitToAdd[(c * imagesPerCycle) + i] = fitPerCycle[i];
                    yForResiduals[(c * imagesPerCycle) + i] = y[(c * imagesPerCycle) + i];
                }
                //the following adds the fits as the fit parameters to the plots
                plot.add("line", x4Plot, fitPerCycle);
                String labelToAddChi2 = "Chi2=" + String.valueOf((double) Math.round(fitValues[0] * 1000) / 1000);
                String labelToAddC = "offset=" + String.valueOf((double) Math.round(fitValues[1] * 1000) / 1000);
                String labelToAddA1 = "A1=" + String.valueOf((double) Math.round(fitValues[2] * 1000) / 1000);
                String labelToAddK1 = "k1=" + String.valueOf((double) Math.round(fitValues[3] * 1000) / 1000);                
                plot.addLabel(c * 0.35 + 0.05, 0.1, labelToAddChi2);
                plot.addLabel(c * 0.35 + 0.05, 0.15, labelToAddC);
                plot.addLabel(c * 0.35 + 0.05, 0.2, labelToAddA1);
                plot.addLabel(c * 0.35 + 0.05, 0.25, labelToAddK1);
                if(fitDouble){
                    String labelToAddA2 = "A2=" + String.valueOf((double) Math.round(fitValues[4] * 1000) / 1000);
                    String labelToAddK2 = "k2=" + String.valueOf((double) Math.round(fitValues[5] * 1000) / 1000);
                    plot.addLabel(c * 0.35 + 0.05, 0.3, labelToAddA2);
                    plot.addLabel(c * 0.35 + 0.05, 0.35, labelToAddK2);
                    double WMK = ((fitValues[2]*fitValues[3])+(fitValues[4]*fitValues[5]))/(fitValues[2]+fitValues[4]);
                    String labelToAddWMK = "WMK=" + String.valueOf((double) Math.round(WMK * 1000) / 1000);
                    plot.addLabel(c * 0.35 + 0.05, 0.4, labelToAddWMK);
                }
                if(fitTriple){
                    String labelToAddA2 = "A2=" + String.valueOf((double) Math.round(fitValues[4] * 1000) / 1000);
                    String labelToAddK2 = "k2=" + String.valueOf((double) Math.round(fitValues[5] * 1000) / 1000);
                    plot.addLabel(c * 0.35 + 0.05, 0.3, labelToAddA2);
                    plot.addLabel(c * 0.35 + 0.05, 0.35, labelToAddK2);
                    String labelToAddA3 = "A3=" + String.valueOf((double) Math.round(fitValues[6] * 1000) / 1000);
                    String labelToAddK3 = "k3=" + String.valueOf((double) Math.round(fitValues[7] * 1000) / 1000);
                    plot.addLabel(c * 0.35 + 0.05, 0.4, labelToAddA3);
                    plot.addLabel(c * 0.35 + 0.05, 0.45, labelToAddK3);
                    double WMK = ((fitValues[2]*fitValues[3])+(fitValues[4]*fitValues[5])+(fitValues[6]*fitValues[7]))/(fitValues[2]+fitValues[4]+fitValues[6]);
                    String labelToAddWMK = "WMK=" + String.valueOf((double) Math.round(WMK * 1000) / 1000);
                    plot.addLabel(c * 0.35 + 0.05, 0.5, labelToAddWMK);
                }
            }
        }

        double ymin = ProfilePlot.getFixedMin();
        double ymax = ProfilePlot.getFixedMax();
        if (!(ymin == 0.0 && ymax == 0.0)) {
            double[] a = Tools.getMinMax(x);
            double xmin = a[0];
            double xmax = a[1];
            plot.setLimits(xmin, xmax, ymin, ymax);
        }
        Plot plotResiduals = new Plot("Residuals", xLabel, yLabel);
        if (k1DataG != null) {
            double[] resArray = subtractArrayFromArray(yForResiduals, fitToAdd);
            plotResiduals.add("circles", x, resArray);
            if (!(ymin == 0.0 && ymax == 0.0)) {
                double[] a = Tools.getMinMax(x);
                double xmin = a[0];
                double xmax = a[1];
                plotResiduals.setLimits(xmin, xmax, ymin, ymax);
            }
        }
        if (pwin == null) {
            pwin = plot.show();
        } else {
            pwin.drawPlot(plot);
        }
        if (k1DataG != null) {
            if (pwin2 == null) {
                pwin2 = plotResiduals.show();
            } else {
                pwin2.drawPlot(plotResiduals);
            }
        }
    }

    // returns false if image is closed
    boolean isSelection() {
        return img != null;
    }

    // stop listening for mouse and key events if the plot window has been closed
    void checkPlotWindow() {
        if (pwin == null) {
            return;
        }
        if (pwin.isVisible()) {
            return;
        }
        ImageWindow iwin = img.getWindow();
        if (iwin == null) {
            return;
        }
        canvas = iwin.getCanvas();
        canvas.removeMouseListener(this);
        ImageWindow iwin2 = wmkImage.getWindow();
        if (iwin2 == null) {
            return;
        }
        canvas2 = iwin2.getCanvas();
        canvas2.removeMouseListener(this);
        pwin = null;
        pwin2 = null;
        listenersRemoved = true;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }


    //initialized all the parameters before the fitting starts
    public void initAZeroFitting() {
    	//call all the init function written in the the AzeroCurve fit class to tranfer value from here
    	cfAzeroInst.setID(id);
    	cfAzeroInst.setChi2CutOff(Chi2CutOff);
    	cfAzeroInst.setChannelMean(chA_Kmean, chA_Kmean, chA_Kmean, chA_Kmean, chA_Kmean);
    	cfAzeroInst.setUseChannelValues(useChA, useChB, useChC, useChD, useChE);
    	cfAzeroInst.initCycleNums(numCycles, imagesPerCycle);
    	cfAzeroInst.setPixelThreshold(PixelThresholdCutOff);
    	cfAzeroInst.setChi2CutOff(Chi2CutOff);
    	cfAzeroInst.setHarmonicOmega(harmonic, Omega);
    	cfAzeroInst.setIteration(maxiteration);
    	cfAzeroInst.copyPhasorData(arrayChA, arrayChB, arrayChC, arrayChD, arrayChE);
    	cfAzeroInst.setPhasorBooleans(usePhasortoInitialize, isPhasorFitDone);//recheck to make sure it is done properly
    	
    	
    	
    	
    	
    }

    
    
    
    
//****************Phasor calculations*********************************

    public void run_PhasorPlot_on_Stack() throws Exception {
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
                                statusMessageArea.setText("Phasor progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
                                statusMessageArea.update(statusMessageArea.getGraphics());
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
            statusMessageArea.setText("Phasor time: " + timeToCompletion);                                
            statusMessageArea.update(statusMessageArea.getGraphics());
            if (LogPhasorTime == true) {
                IJ.log("Image " + id + " cycle " + cycle + " processing time = " + (timeToCompletion / 1000) + " sec");
            }
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


    public double getTheOffset(double[] x, double[] y) {
        //this measures a mean value for the last 10 data points in a decay
        //a dubious attempt to deal with background
        double[] last10YArray = new double[10];
        System.arraycopy(y, y.length-10, last10YArray, 0, 10);
        //CurveFitter cf = new CurveFitter(x, logYArray);
        //cf.doFit(0); //linear 
        //double[] fittedParam = cf.getParams();
        //offsetToReturn=Math.exp(fittedParam[0]);
        double offsetToReturn = getMeanOfArray(last10YArray);
        return offsetToReturn;
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

    
    public double [] calculateCorrectedGmGs(double[] timeData, double[] pixelData, double[] bkGrdData, double omega) {

        double [] G = new double [3];
        pixelData = subtractArrayFromArray(pixelData,bkGrdData);
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
    

       



    
    
//*****************Phasor Plotting, etc. ***********************************    
    
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
    //works only with rectangle ROIs
    /*public static double[] getCalibratedPixelsFromPlotWindow(Plot thePlot) {
        double[] returnArray = new double [4];
        ImagePlus plotImg = thePlot.getImagePlus();
        Roi roi = plotImg.getRoi();
        Rectangle roi2 =roi.getBounds();
        Calibration cal = plotImg.getCalibration();
            double xPos = cal.getX(roi2.getX());
            double yPos = -(cal.getY(roi2.getY()));
            double cWidth = cal.getX(roi2.getX()+roi2.getWidth())-xPos;
            double cHeight = yPos - (-(cal.getY(roi2.getY()+roi2.getHeight())));
        returnArray[0] = xPos;
        returnArray[1] = yPos;
        returnArray[2] = cWidth;
        returnArray[3] = cHeight;

    return returnArray;
    }*/
    
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
    
    
    public double[][][] filterDataFromPhasor(double[][]theBounds) {
        //usually used in conjunction with getCalibratedPixelsFromPlotWindow
        //to make images with only data subsets from the phasor plot
        double[][][] returnArray =new double[imageW][imageH][numCycles];
        for (int cyc = 0; cyc < numCycles; cyc++) {
            for (int y = 0; y < imageH; y++) {
                for (int x = 0; x < imageW; x++) {
                    //if(fitsInROI(GmData[x][y][cyc], GsData[x][y][cyc], theBounds[0], theBounds[1])){
                    if(containsMatch(GmData[x][y][cyc],theBounds[0]) && containsMatch(GsData[x][y][cyc],theBounds[1])){
                        returnArray[x][y][cyc] = AZeroData[x][y][cyc];
                    } else {
                        returnArray[x][y][cyc] = Double.NaN;
                    }
                }
            }
        }
        return returnArray;
    }

    public boolean containsMatch(double value, double[] theArray) {
        //checks for a match within 0.01
        //sometimes the data are 9 or 10 decimal places
        //nothing generally matches, so we check within 0.01 
        for (int i = 0; i < theArray.length; i++) {
            if (Math.abs(value-theArray[i])<=0.01) 
                return true;
        }
        return false;
    }
    
    public boolean fitsInROI(double valueGm, double valueGs, double[] theGmArray, double[] theGsArray) {
        double [] theGmArrayStats = getArrayStatistics(theGmArray);
        double [] theGsArrayStats = getArrayStatistics(theGsArray);
        
        for (int i = 0; i < theGmArray.length-1; i++) {
                if (valueGm >= theGmArray[i] && valueGs >= theGsArray[i] && valueGm <= theGmArray[i+1] && valueGs <= theGsArray[i+1]) 
                    return true;
            }
        return false;
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
                            statusMessageArea.setText("Unmixing progress: " + progress + " %  of cycle " + (cycleNum + 1) + " of " + numCycles + " total cycles");                                
                            statusMessageArea.update(statusMessageArea.getGraphics());
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
    statusMessageArea.setText("Unmixing time: " + timeToCompletion);                                
    statusMessageArea.update(statusMessageArea.getGraphics());
    
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



public void unMixPixelValuesAndCreateImagesUsingFit() {
 //if the fractional contributions are calculated by fits using psFRET_Fit_Azero_Exponential()
 //this will transfer those to the channel arrays and used to create unmixed images
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
    
    
    long timeToCompletion = System.currentTimeMillis() - startTime;    
    statusMessageArea.setText("Unmixing time: " + timeToCompletion);                                
    statusMessageArea.update(statusMessageArea.getGraphics());
    
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
    
    
    
    public double [] getMeanFromFilteredData(double[][]theBounds) {
        //calculates the mean phase and modulation of a distribution of points
        //on a phasor plot
        //usually used to determine reference data points for unmixing
        double[]returnArray = new double[2];
        double sumG = 0;
        double sumS = 0;
        double counter = 0;
        for (int cyc = 0; cyc < 1; cyc++) {
            for (int y = 0; y < imageH; y++) {
                for (int x = 0; x < imageW; x++) {
                    if(containsMatch(GmData[x][y][cyc],theBounds[0]) && containsMatch(GsData[x][y][cyc],theBounds[1])){
                        sumG = sumG+GmData[x][y][cyc];
                        sumS = sumS+GsData[x][y][cyc];
                        counter=counter+1;
                    }
                }
            }
        }
        returnArray[0]=Math.round(sumG/counter*1000.0)/1000.0;
        returnArray[1]=Math.round(sumS/counter*1000.0)/1000.0;
        return returnArray;
    }
        
    
//*******************Utilities*******************************

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
            if (planeCount == 0 || useManualTime) {
                GenericDialog gd2 = new GenericDialog("Problem with metadata");
                gd2.addMessage("Time information from metadata was not found");
                gd2.addMessage("Would you like to enter the time interval manually?");
                gd2.addNumericField("Time between images in seconds", 0.050, 3);
                gd2.showDialog();
                if (gd2.wasCanceled()) {
                    return null;
                }
                double userDeltaT = gd2.getNextNumber();
                for (int t = 0; t < timeStampsToReturn.length; t++) {
                    timeStampsToReturn[t] = t * userDeltaT;
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
    
    public double[] getArrayStatistics(double[] theArray) {

        double min = theArray[0];
        double max = theArray[0];
        double sum = 0;
        for (int i = 0; i < theArray.length; i++) {
            if (theArray[i] < min) {
                min = theArray[i];
            }
            if (theArray[i] > max) {
                max = theArray[i];
            }
            sum = sum + theArray[i];
        }
        double mean = sum / theArray.length;
        double[] returnArray = {min, max, sum, mean};
        return returnArray;
    }
      
    private static double[] subtractArrayFromArray(double[] array1, double[] array2) {
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

    private static double[] multiplyTwoArrays(double[] array1, double[] array2) {
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

    private static double[] divideTwoArrays(double[] array1, double[] array2) {
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

    private static double[] subtractValueFromArray(double[] array1, double theValue) {
        double[] arrayToReturn = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            arrayToReturn[i] = array1[i] - theValue;
        }
        return arrayToReturn;
    }
    
    private static double[] addValueToArray(double[] array1, double theValue) {
        double[] arrayToReturn = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            arrayToReturn[i] = array1[i] + theValue;
        }
        return arrayToReturn;
    }

    private static double[] divideArrayByValue(double[] array1, double theValue) {
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
    
    private static double[] multiplyArrayByValue(double[] array1, double theValue) {
        double[] arrayToReturn = new double[array1.length];
        for (int i = 0; i < array1.length; i++) {
            arrayToReturn[i] = array1[i]*theValue;
        }
        return arrayToReturn;
    }
    
    private static double[] addTwoArrays(double[] array1, double[] array2) {
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
    
    int getIndexOfMaxValue(int [] indexArray, double [] valueArray){
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
    
    int getIndexOfMinValue(int [] indexArray, double [] valueArray){
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
        
    int getIndexOfMiddle(int [] indexArray, double [] valueArray){
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




    private void writeReferenceDataToFile(){
        //writes the reference data from phasor plots
        //can be reloaded into the plugin for unmixing
       
        try {
            //ImageWindow iwin = img.getWindow();
            //ij.WindowManager.setCurrentWindow(iwin);
            ij.WindowManager.setTempCurrentImage(img);
            String dir0 = IJ.getDirectory("image");
            String title = img.getTitle();
            
            FileWriter fileWriter =new FileWriter(dir0+"Reference_channel_information.txt");

            BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);
            
            bufferedWriter.write("Reference channel data for "+title);
            bufferedWriter.newLine();
            bufferedWriter.write("Pixel_threshold:"+String.valueOf(threshold));
            bufferedWriter.newLine();
            bufferedWriter.write("Terminal_pixel_value:"+String.valueOf(terminalThreshold));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_t_one_half:"+String.valueOf(useTOneHalfEstimate));
            bufferedWriter.newLine();
            bufferedWriter.write("Harmonic_to_plot:"+String.valueOf(harmonic));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_median_filter:"+String.valueOf(medianFilter));
            bufferedWriter.newLine();
            bufferedWriter.write("Applications_median_filter:"+String.valueOf(applicationsMedianFilter));
            bufferedWriter.newLine();
            bufferedWriter.write("Background_subtract:"+String.valueOf(backgroundSubtract));
            bufferedWriter.newLine();
            bufferedWriter.write("Objective_NA:"+String.valueOf(NA));
            bufferedWriter.newLine();
            bufferedWriter.write("Emission_wavelength:"+String.valueOf(lambda));
            bufferedWriter.newLine();
            bufferedWriter.write("Unbinned_pixel_size:"+String.valueOf(pixSize));
            bufferedWriter.newLine();
            bufferedWriter.write("Camera_bin_factor:"+String.valueOf(binFactor));
            bufferedWriter.newLine();
            bufferedWriter.write("Image_bin_factor_post_processing:"+String.valueOf(binFactorImage));
            bufferedWriter.newLine();
            bufferedWriter.write("Number_of_cycles:"+String.valueOf(numCycles));
            bufferedWriter.newLine();
            bufferedWriter.write("Number_of_images_per_cycle:"+String.valueOf(imagesPerCycle));
            bufferedWriter.newLine();
            bufferedWriter.write("Camera_offset:"+String.valueOf(cameraOffset));
            bufferedWriter.newLine();
            bufferedWriter.write("Camera_gain:"+String.valueOf(cameraGain));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_Channel_A:"+String.valueOf(useChA));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_A_name:"+String.valueOf(chA_name));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_A_mean_G:"+String.valueOf(chA_Gmean));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_A_mean_S:"+String.valueOf(chA_Smean));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_Channel_B:"+String.valueOf(useChB));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_B_name:"+String.valueOf(chB_name));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_B_mean_G:"+String.valueOf(chB_Gmean));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_B_mean_S:"+String.valueOf(chB_Smean));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_Channel_C:"+String.valueOf(useChC));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_C_name:"+String.valueOf(chC_name));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_C_mean_G:"+String.valueOf(chC_Gmean));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_C_mean_S:"+String.valueOf(chC_Smean));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_Channel_D:"+String.valueOf(useChD));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_D_name:"+String.valueOf(chD_name));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_D_mean_G:"+String.valueOf(chD_Gmean));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_D_mean_S:"+String.valueOf(chD_Smean));
            bufferedWriter.newLine();
            bufferedWriter.write("Use_Channel_E:"+String.valueOf(useChE));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_E_name:"+String.valueOf(chE_name));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_E_mean_G:"+String.valueOf(chE_Gmean));
            bufferedWriter.newLine();
            bufferedWriter.write("Channel_E_mean_S:"+String.valueOf(chE_Smean));
            bufferedWriter.newLine();
            
            
            
            if(useChA){
                bufferedWriter.write("");
                bufferedWriter.newLine();
                bufferedWriter.write("Channel_A_phasor_plot ROI");
                for(int i=0;i<phasorPlotROIChA[0].length;i++){
                    bufferedWriter.newLine();
                    bufferedWriter.write("Channel_A_x:"+String.valueOf(phasorPlotROIChA[0][i])+",Channel_A_y:"+String.valueOf(phasorPlotROIChA[1][i]));
                }
            }
            if(useChB){
               bufferedWriter.write("");
               bufferedWriter.newLine();
                bufferedWriter.write("Channel_B_phasor_plot ROI");
                for(int i=0;i<phasorPlotROIChB[0].length;i++){
                    bufferedWriter.newLine();
                    bufferedWriter.write("Channel_B_x:"+String.valueOf(phasorPlotROIChB[0][i])+",Channel_B_y:"+String.valueOf(phasorPlotROIChB[1][i]));
                }
            }
            if(useChC){
                bufferedWriter.write("");
                bufferedWriter.newLine();
                bufferedWriter.write("Channel_C_phasor_plot ROI");
                for(int i=0;i<phasorPlotROIChC[0].length;i++){
                    bufferedWriter.newLine();
                    bufferedWriter.write("Channel_C_x:"+String.valueOf(phasorPlotROIChC[0][i])+",Channel_C_y:"+String.valueOf(phasorPlotROIChC[1][i]));
                }
            }
            if(useChD){
                bufferedWriter.write("");
                bufferedWriter.newLine();
                bufferedWriter.write("Channel_D_phasor_plot ROI");
                for(int i=0;i<phasorPlotROIChD[0].length;i++){
                    bufferedWriter.newLine();
                    bufferedWriter.write("Channel_D_x:"+String.valueOf(phasorPlotROIChD[0][i])+",Channel_D_y:"+String.valueOf(phasorPlotROIChD[1][i]));
                }
            }
            if(useChE){
                bufferedWriter.write("");
                bufferedWriter.newLine();
                bufferedWriter.write("Channel_E_phasor_plot ROI");
                for(int i=0;i<phasorPlotROIChE[0].length;i++){
                    bufferedWriter.newLine();
                    bufferedWriter.write("Channel_E_x:"+String.valueOf(phasorPlotROIChE[0][i])+",Channel_E_y:"+String.valueOf(phasorPlotROIChE[1][i]));
                }
            }
            
            bufferedWriter.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    private void readReferenceDataFromFile(){
       //reads the reference data from the text file
        try {
            //ImageWindow iwin = img.getWindow();
            //ij.WindowManager.setCurrentWindow(iwin);
            ij.WindowManager.setTempCurrentImage(img);
            String dir0 = IJ.getDirectory("image");
            File refFile = new File(dir0+"Reference_channel_information.txt");
            if (!refFile.exists()) {
                IJ.showMessage("ps Phasor Plotter", "Reference data file was not found");
                return;
            }
            
            FileReader fileReader =new FileReader(dir0+"Reference_channel_information.txt");

            BufferedReader bufferedReader =new BufferedReader(fileReader);
            int numLines = 0;
            while (bufferedReader.readLine()!=null)
                numLines++;
            bufferedReader.close();
            
            int chAROIlines = 0;
            int chBROIlines = 0;
            int chCROIlines = 0;
            int chDROIlines = 0;
            int chEROIlines = 0;
            
            FileReader fileReader2 =new FileReader(dir0+"Reference_channel_information.txt");
            BufferedReader bufferedReader2 =new BufferedReader(fileReader2);
            String [] readTextFile = new String [numLines];
            for (int i=0; i<readTextFile.length;i++){
                readTextFile[i] = bufferedReader2.readLine();
                if(readTextFile[i].contains("Channel_A_x:"))
                    chAROIlines++;
                if(readTextFile[i].contains("Channel_B_x:"))
                    chBROIlines++;
                if(readTextFile[i].contains("Channel_C_x:"))
                    chCROIlines++;
                if(readTextFile[i].contains("Channel_D_x:"))
                    chDROIlines++;
                if(readTextFile[i].contains("Channel_E_x:"))
                    chEROIlines++;
            }
            
            bufferedReader2.close();
            
            //parse the String array to get information
            
                       
            int chAROIcounter = 0;
            int chBROIcounter = 0;
            int chCROIcounter = 0;
            int chDROIcounter = 0;
            int chEROIcounter = 0;
            
            for (int i=0; i<readTextFile.length;i++){
                //parse the file for Channel A information
                if(readTextFile[i].contains("Use_Channel_A:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    useChA = Boolean.parseBoolean(thePara);
                    useChA_CB.setSelected(useChA);
                    if(useChA){
                        phasorPlotROIChA = new double [2][chAROIlines];                        
                    }
                }
                if(readTextFile[i].contains("Channel_A_name:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chA_name = thePara;
                    chA_Name_TF.setText(chA_name);
                }  
                if(readTextFile[i].contains("Channel_A_mean_G:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chA_Gmean = Double.parseDouble(thePara);
                    chA_Gmean_TF.setText(String.valueOf(chA_Gmean));
                }
                if(readTextFile[i].contains("Channel_A_mean_S:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chA_Smean = Double.parseDouble(thePara);
                    chA_Smean_TF.setText(String.valueOf(chA_Smean));
                }
                if(useChA && readTextFile[i].contains("Channel_A_x:")){
                    String theParaX = readTextFile[i].substring(readTextFile[i].indexOf(":")+1,readTextFile[i].indexOf(","));
                    String theEnd = readTextFile[i].substring(readTextFile[i].indexOf(",")+1);
                    String theParaY = theEnd.substring(theEnd.indexOf(":")+1);
                    double xPos = Double.parseDouble(theParaX);
                    double yPos = Double.parseDouble(theParaY);
                    phasorPlotROIChA[0][chAROIcounter] = xPos;
                    phasorPlotROIChA[1][chAROIcounter] = yPos;
                    chAROIcounter++;
                }
                
                //parse the file for Channel B information
                if(readTextFile[i].contains("Use_Channel_B:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    useChB = Boolean.parseBoolean(thePara);
                    useChB_CB.setSelected(useChB);
                    if(useChB){
                        phasorPlotROIChB = new double [2][chBROIlines];                        
                    }
                }
                if(readTextFile[i].contains("Channel_B_name:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chB_name = thePara;
                    chB_Name_TF.setText(chB_name);
                }  
                if(readTextFile[i].contains("Channel_B_mean_G:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chB_Gmean = Double.parseDouble(thePara);
                    chB_Gmean_TF.setText(String.valueOf(chB_Gmean));
                }
                if(readTextFile[i].contains("Channel_B_mean_S:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chB_Smean = Double.parseDouble(thePara);
                    chB_Smean_TF.setText(String.valueOf(chB_Smean));
                }
                if(useChB && readTextFile[i].contains("Channel_B_x:")){
                    String theParaX = readTextFile[i].substring(readTextFile[i].indexOf(":")+1,readTextFile[i].indexOf(","));
                    String theEnd = readTextFile[i].substring(readTextFile[i].indexOf(",")+1);
                    String theParaY = theEnd.substring(theEnd.indexOf(":")+1);
                    double xPos = Double.parseDouble(theParaX);
                    double yPos = Double.parseDouble(theParaY);
                    phasorPlotROIChB[0][chBROIcounter] = xPos;
                    phasorPlotROIChB[1][chBROIcounter] = yPos;
                    chBROIcounter++;
                }
                
                //parse the file for Channel C information
                if(readTextFile[i].contains("Use_Channel_C:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    useChC = Boolean.parseBoolean(thePara);
                    useChC_CB.setSelected(useChC);
                    if(useChC){
                        phasorPlotROIChC = new double [2][chCROIlines];                        
                    }
                }
                if(readTextFile[i].contains("Channel_C_name:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chC_name = thePara;
                    chC_Name_TF.setText(chC_name);
                }  
                if(readTextFile[i].contains("Channel_C_mean_G:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chC_Gmean = Double.parseDouble(thePara);
                    chC_Gmean_TF.setText(String.valueOf(chC_Gmean));
                }
                if(readTextFile[i].contains("Channel_C_mean_S:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chC_Smean = Double.parseDouble(thePara);
                    chC_Smean_TF.setText(String.valueOf(chC_Smean));
                }
                if(useChC && readTextFile[i].contains("Channel_C_x:")){
                    String theParaX = readTextFile[i].substring(readTextFile[i].indexOf(":")+1,readTextFile[i].indexOf(","));
                    String theEnd = readTextFile[i].substring(readTextFile[i].indexOf(",")+1);
                    String theParaY = theEnd.substring(theEnd.indexOf(":")+1);
                    double xPos = Double.parseDouble(theParaX);
                    double yPos = Double.parseDouble(theParaY);
                    phasorPlotROIChC[0][chCROIcounter] = xPos;
                    phasorPlotROIChC[1][chCROIcounter] = yPos;
                    chCROIcounter++;
                }
                
                //parse the file for Channel D information
                if(readTextFile[i].contains("Use_Channel_D:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    useChD = Boolean.parseBoolean(thePara);
                    useChD_CB.setSelected(useChD);
                    if(useChD){
                        phasorPlotROIChD = new double [2][chDROIlines];                        
                    }
                }
                if(readTextFile[i].contains("Channel_D_name:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chD_name = thePara;
                    chD_Name_TF.setText(chD_name);
                }  
                if(readTextFile[i].contains("Channel_D_mean_G:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chD_Gmean = Double.parseDouble(thePara);
                    chD_Gmean_TF.setText(String.valueOf(chD_Gmean));
                }
                if(readTextFile[i].contains("Channel_D_mean_S:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chD_Smean = Double.parseDouble(thePara);
                    chD_Smean_TF.setText(String.valueOf(chD_Smean));
                }
                if(useChD && readTextFile[i].contains("Channel_D_x:")){
                    String theParaX = readTextFile[i].substring(readTextFile[i].indexOf(":")+1,readTextFile[i].indexOf(","));
                    String theEnd = readTextFile[i].substring(readTextFile[i].indexOf(",")+1);
                    String theParaY = theEnd.substring(theEnd.indexOf(":")+1);
                    double xPos = Double.parseDouble(theParaX);
                    double yPos = Double.parseDouble(theParaY);
                    phasorPlotROIChD[0][chDROIcounter] = xPos;
                    phasorPlotROIChD[1][chDROIcounter] = yPos;
                    chDROIcounter++;
                }
                
                //parse the file for Channel E information
                if(readTextFile[i].contains("Use_Channel_E:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    useChE = Boolean.parseBoolean(thePara);
                    useChE_CB.setSelected(useChE);
                    if(useChE){
                        phasorPlotROIChE = new double [2][chEROIlines];                        
                    }
                }
                if(readTextFile[i].contains("Channel_E_name:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chE_name = thePara;
                    chE_Name_TF.setText(chE_name);
                }  
                if(readTextFile[i].contains("Channel_E_mean_G:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chE_Gmean = Double.parseDouble(thePara);
                    chE_Gmean_TF.setText(String.valueOf(chE_Gmean));
                }
                if(readTextFile[i].contains("Channel_E_mean_S:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    chE_Smean = Double.parseDouble(thePara);
                    chE_Smean_TF.setText(String.valueOf(chE_Smean));
                }
                if(useChE && readTextFile[i].contains("Channel_E_x:")){
                    String theParaX = readTextFile[i].substring(readTextFile[i].indexOf(":")+1,readTextFile[i].indexOf(","));
                    String theEnd = readTextFile[i].substring(readTextFile[i].indexOf(",")+1);
                    String theParaY = theEnd.substring(theEnd.indexOf(":")+1);
                    double xPos = Double.parseDouble(theParaX);
                    double yPos = Double.parseDouble(theParaY);
                    phasorPlotROIChE[0][chEROIcounter] = xPos;
                    phasorPlotROIChE[1][chEROIcounter] = yPos;
                    chEROIcounter++;
                }
                
                //parse the file for harmonic information
                if(readTextFile[i].contains("Harmonic_to_plot:")){
                    String thePara = readTextFile[i].substring(readTextFile[i].indexOf(":")+1);
                    harmonic = Double.parseDouble(thePara);
                    FrequencyToPlotTF.setText(String.valueOf(harmonic));
                }
            }
            
            
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
   
    
    
    
    
    
    
//*********************Image plotting, etc. ******************************************    
//this section plots the various images resulting from fits or unmixing
    
    public ImagePlus createImage(String titleOfImage, double[][][]theDataToPlot) {
        //this is just a general image plotting method
        ImagePlus imp = IJ.createImage(titleOfImage, "32-bit", imageW, imageH, numCycles);
        for (int cyc = 0; cyc < numCycles; cyc++) {
            imp.setSlice(cyc + 1);
            ImageProcessor ip = imp.getProcessor();
            FloatProcessor fip = (FloatProcessor) ip.convertToFloat();
            for (int y = 0; y < imageH; y++) {
                for (int x = 0; x < imageW; x++) {
                    if (Double.isNaN(theDataToPlot[x][y][cyc])) {
                        fip.setf(x, y, Float.NaN);
                    } else {
                        fip.setf(x, y, (float) theDataToPlot[x][y][cyc]);
                    }
                }
            }
            IJ.resetMinAndMax(imp);
        }
        imp.show();
        return imp;
    }
    

    public ImagePlus createWeightedRateConstantsImage() {
        //plots a weighted rate constant image
        ImagePlus imp = IJ.createImage("WeightedRateConstantsImage", "32-bit", imageW, imageH, numCycles);
        for (int cyc = 0; cyc < numCycles; cyc++) {
            imp.setSlice(cyc + 1);
            ImageProcessor ip = imp.getProcessor();
            FloatProcessor fip = (FloatProcessor) ip.convertToFloat();
            for (int y = 0; y < imageH; y++) {
                for (int x = 0; x < imageW; x++) {
                    if (Double.isNaN(k1DataG[x][y][cyc])) {
                        fip.setf(x, y, Float.NaN);
                    } else {
                        if(fitTriple)
                            fip.setf(x, y, (float) (((a1DataG[x][y][cyc]*k1DataG[x][y][cyc])+(a2DataG[x][y][cyc]*k2DataG[x][y][cyc])+(a3DataG[x][y][cyc]*k3DataG[x][y][cyc]))/(a1DataG[x][y][cyc]+a2DataG[x][y][cyc]+a3DataG[x][y][cyc])));
                        if(fitDouble)
                            fip.setf(x, y, (float) (((a1DataG[x][y][cyc]*k1DataG[x][y][cyc])+(a2DataG[x][y][cyc]*k2DataG[x][y][cyc]))/(a1DataG[x][y][cyc]+a2DataG[x][y][cyc])));
                        if(fitSingle)
                            fip.setf(x, y, (float) (k1DataG[x][y][cyc]));
                    }
                }
            }
            IJ.resetMinAndMax(imp);
        }
        imp.show();
        return imp;
    }
        
    private void saveUnmixedImages() {
    //saves the images unmixed by the phasor or the fractional component fitting
        String title = img.getTitle();
        IJ.selectWindow(title);
        String dir0 = IJ.getDirectory("image");
        
        String[] imageTitles = WindowManager.getImageTitles();
            for (String imageTitle : imageTitles) {
                if (imageTitle.contains(chA_name)) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+chA_name+"_"+title);
                }
                if (imageTitle.contains(chB_name)) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+chB_name+"_"+title);
                }
                if (imageTitle.contains(chC_name)) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+chC_name+"_"+title);
                }
                if (imageTitle.contains(chD_name)) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+chD_name+"_"+title);
                }
                if (imageTitle.contains(chE_name)) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+chE_name+"_"+title);
                }
                if (imageTitle.contains("Phasor Plot")) {
                    IJ.selectWindow(imageTitle);
                    IJ.saveAs("tif",dir0+"Phasor_Plot_"+title);
                }
            }
    }
    
}
