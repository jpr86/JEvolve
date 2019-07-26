/* %%
 * 
 * JEvolve
 *
 * Copyright 2007 Jeff Ridder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ridderware.jevolve.ui;

import com.ridderware.jevolve.Individual;
import com.ridderware.jevolve.Population;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 * A InternalFrame Evolution Visualization tool that displays statistics
 * based on a single metric. For example, if you are optimizing based on cost and
 * effectiveness, this window can look at either cost or effectiveness (or
 * constraint error), but not a combination thereof.
 * @author Jason HandUber
 */
public class GenerationalVisualization
        extends JInternalFrame
        implements Observer
{

    private final YIntervalSeries meanSeries = new YIntervalSeries("Mean");
    private final XYSeries minSeries = new XYSeries("Min");
    private final XYSeries maxSeries = new XYSeries("Max");
    //any graph value above or below is chopped off and graphed at that value
    private final double minGraphBound;
    private final double maxGraphBound;
//    private final DefaultStatisticalCategoryDataset dataset =
//                                                    new DefaultStatisticalCategoryDataset();
    private final int fitnessIndex;
    private final double generationalDrawingFrequency;
    private final String fitnessDescription;

    private JFreeChart chart;

    /**
     * Creates new form GenerationalVisualization that will visualize basic stats
     * relevant to the prescribed fitness index.
     * @param populationSources the Observable that will give the viz the population to visualize
     * @param title A description title for this window
     * @param xAxisTitle The x-axis title
     * @param yAxisTitle The y-axis title
     * @param fitnessIndex The fitness index that will be considered by this Window, or -1 if this window
     * should consider constraint error.
     * @param generationalDrawingFrequency How often the GUI should update the graph with new data (for example, if 5, then
     * the GUI will update stats every 5 generations).
     * @param minGraphBound any value below this will be cutoff and graphed at this bound.
     * @param maxGraphBound any value above this will be cutoff and graphed at this bound.
     */
    public GenerationalVisualization(Observable[] populationSources,
            String title,
            String xAxisTitle, String yAxisTitle, int fitnessIndex,
            double generationalDrawingFrequency, double minGraphBound,
            double maxGraphBound)
    {
        super(title, true, true, true, true);

        initComponents();

        this.minGraphBound = minGraphBound;
        this.maxGraphBound = maxGraphBound;
        this.fitnessIndex = fitnessIndex;
        this.generationalDrawingFrequency = generationalDrawingFrequency;
        this.fitnessDescription = title;

        for (Observable populationSource : populationSources)
        {
            populationSource.addObserver(this);
        }

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        chart = createGenerationalLineChart(meanSeries, minSeries,
                                                       maxSeries, title,
                                                       xAxisTitle, yAxisTitle);
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, gridBagConstraints);
        pack();
    }

    /**
     * Allows the UI to redraw the given population (arg)
     * @param o the observable (not referenced, can be nil)
     * @param arg The Population to visualize
     */
    public void update(Observable o, Object arg)
    {
        final Population pop = (Population) arg;
        if (EventQueue.isDispatchThread())
        {
            handleUpdate(pop);
        }
        else
        {
            try
            {
                EventQueue.invokeAndWait(new Runnable()
                {

                    public void run()
                    {
                        handleUpdate(pop);
                    }
                });
            }
            catch (InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }
            catch (InvocationTargetException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    public void handleClose()
    {
        File file = new File(System.getProperty("user.dir")+File.separator+"fitness.png");
        try
        {
            ChartUtilities.saveChartAsPNG(file, chart, 1280, 1024);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GenerationalVisualization.class.getName()).
                log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Updates the graphic (if required per the {@code generationalDrawingFrequency}
     * with the new means, sigmas, best, and worst fitness values.
     * @param pop The new Population
     */
    public void handleUpdate(Population pop)
    {
        final double gen = pop.getProblem().getStepper().getCurrentGeneration();
        final Mean mean = new Mean();
        final StandardDeviation sigma = new StandardDeviation();
        //means & standard devs
        if (fitnessIndex >= 0)
        {
            for (Individual individual : pop.getIndividuals())
            {
                if (individual.getConstraintError() == 0)
                {
                    mean.increment(individual.getFitness(fitnessIndex));
                    sigma.increment(individual.getFitness(fitnessIndex));
                }
            }
        }
        else if (fitnessIndex == -1)
        {
            for (Individual individual : pop.getIndividuals())
            {
                mean.increment(individual.getConstraintError());
                sigma.increment(individual.getConstraintError());
            }
        }
        else
        {
            throw new IllegalArgumentException("Unhandled fitness index: " +
                    fitnessIndex + " population fitness array length: " +
                    pop.getIndividual(0).getFitnessArray().length);
        }

        if (gen % generationalDrawingFrequency == 0)
        {

            double leMean =
                   bound(mean.getResult(), minGraphBound, maxGraphBound);
            double leSigma = bound(sigma.getResult(), minGraphBound,
                                   maxGraphBound);

            meanSeries.add(gen, leMean, leMean-leSigma, leMean+leSigma);

            //best & worst
            if (fitnessIndex != -1)
            {
                double best = Double.POSITIVE_INFINITY, worst = Double.NEGATIVE_INFINITY;
                for (Individual individual : pop.getIndividuals())
                {
                    if (individual.getConstraintError() == 0)
                    {
                        best = Math.min(best,
                                        individual.getFitness(fitnessIndex));
                        worst = Math.max(worst, individual.getFitness(
                                fitnessIndex));
                    }
                }
                double boundBest = bound(best, minGraphBound, maxGraphBound);
                double boundWorst = bound(worst, minGraphBound, maxGraphBound);
                minSeries.add(gen, boundBest);
                maxSeries.add(gen, boundWorst);
//                System.out.println("Best Fitness: "+ best +" Bound --> "+ boundBest);
//                System.out.println("Best Fitness: "+ worst +" Bound --> "+ boundWorst);
            }
            else
            {
                double best = Double.POSITIVE_INFINITY, worst =
                                                        Double.NEGATIVE_INFINITY;
                for (Individual individual : pop.getIndividuals())
                {
                    best = Math.min(best, individual.getConstraintError());
                    worst = Math.max(worst, individual.getConstraintError());
                }
                double boundBest = bound(best, minGraphBound, maxGraphBound);
                double boundWorst = bound(worst, minGraphBound, maxGraphBound);
                minSeries.add(gen, boundBest);
                maxSeries.add(gen, boundWorst);
//                System.out.println("Best Fitness: "+ best +" Bound --> "+ boundBest);
//                System.out.println("Best Fitness: "+ worst +" Bound --> "+ boundWorst);
            }
//            System.out.println("Fitness Index: " + fitnessIndex+ " Gen: "+ gen+" Worst: "+ worst +" Best: "+ best +" Avg: "+ mean.getResult()+" Stnd Dev: "+ sigma.getResult());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createGenerationalLineChart(
            YIntervalSeries meanSeries, XYSeries minSeries,
            XYSeries maxSeries, String title, String xAxisTitle,
            String yAxisTitle)
    {
        final YIntervalSeriesCollection meanSigmaDataset =
                                        new YIntervalSeriesCollection();
        meanSigmaDataset.addSeries(meanSeries);
        final XYSeriesCollection minMaxDataset =
                                 new XYSeriesCollection();
        minMaxDataset.addSeries(minSeries);
        minMaxDataset.addSeries(maxSeries);

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                title, // chart title
                xAxisTitle, // domain axis label
                yAxisTitle, // range axis label
                meanSigmaDataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );

        // customise the plot
        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setDataset(1, minMaxDataset);

        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true,
                                                                         true);
        plot.setRenderer(1, lineRenderer);

        // customise the range axis...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        // customise the renderer...
        XYErrorRenderer meanSigmaRenderer = new XYErrorRenderer();
        meanSigmaRenderer.setBaseLinesVisible(true);
        meanSigmaRenderer.setUseFillPaint(true);
        meanSigmaRenderer.setBaseFillPaint(Color.white);
        plot.setRenderer(0, meanSigmaRenderer);

        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }

    private static double bound(double value, double min, double max)
    {
        return Math.max(Math.min(value, max), min);
    }
}
