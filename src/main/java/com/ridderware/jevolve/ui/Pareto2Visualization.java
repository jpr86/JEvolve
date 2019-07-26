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
import com.ridderware.jevolve.MultiObjectivePopulation;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import javax.swing.JInternalFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author  jhanduber
 */
public class Pareto2Visualization <INDIVIDUAL>
        extends JInternalFrame
        implements Observer, ChartMouseListener
{
    public enum ParetoMemory
    {
        /**
         *
         */
        DISPLAY_CURRENT_FRONT,
        /**
         * CURRENTLY NOT WORKING
         */
        DISPLAY_GLOBAL_FRONT,
    }
    
    private final int maxNumParetoRanksToShow;
    private final double generationalDrawingFrequency;
    private final String[] fitnessDescriptions;
    private final ParetoMemory paretoMemory;
    
    private final DefaultXYDataset paretoFront = new DefaultXYDataset();
    private final Object populationUpdateSyncRoot = new Object();
    
    private final JFreeChart paretoChart;
    private final ChartPanel chartPanel;
    
    private MultiObjectivePopulation currentPopulation;
    private MultiObjectivePopulation currentlyDrawnPopulation;
    private int lastDrawnPopulationGeneration = -1;
    
    private Map<Integer, List<Double>> lastDrawnRank2XValues = new HashMap<Integer, List<Double>>();
    private Map<Integer, List<Double>> lastDrawnRank2YValues = new HashMap<Integer, List<Double>>();
    private final IVizSolutions[] visualizers;
    
    private final List<Set<Individual>> drawnIndividuals = new ArrayList<Set<Individual>>(2);
    
    /**
     * Creates new form Pareto2Visualization
     */
    public Pareto2Visualization(
            int maxNumParetoRanksToShow,
            double generationalDrawingFrequency,
            Observable[] moPopSources,
            ParetoMemory paretoMemory,
            String title,
            IVizSolutions[] visualizers,
            String ... fitnessDescriptions)
    {
        super(title, true, true, true, true);
        assert(fitnessDescriptions.length > 1);
        this.maxNumParetoRanksToShow = maxNumParetoRanksToShow;
        this.visualizers = visualizers;
        this.generationalDrawingFrequency = generationalDrawingFrequency;
        this.paretoMemory = paretoMemory;
        this.fitnessDescriptions = fitnessDescriptions;
        switch (paretoMemory)
        {
            case DISPLAY_CURRENT_FRONT:
//                globalParetoPop = null;
                break;
            case DISPLAY_GLOBAL_FRONT:
//                globalParetoPop = new MultiObjectivePopulation(null);
                throw new IllegalArgumentException("DISPLAY_GLOBAL_FRONT Not currently supported");
            default:
                throw new IllegalArgumentException("Unhandled: "+ paretoMemory);
        }
        
        initComponents();
        
        xaxisComboBox.setModel(new javax.swing.DefaultComboBoxModel(fitnessDescriptions));
        yaxisComboBox.setModel(new javax.swing.DefaultComboBoxModel(fitnessDescriptions));
        
        xaxisComboBox.setSelectedIndex(0);
        yaxisComboBox.setSelectedIndex(1);
        
        for(Observable moPopSource : moPopSources)
        {
            moPopSource.addObserver(this);
        }
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        paretoChart = createChartPanel(title,
                fitnessDescriptions[0],
                fitnessDescriptions[1]);
        
        chartPanel = new ChartPanel(paretoChart);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.addChartMouseListener(this);
//        ((XYPlot)paretoChart.getPlot()).getRenderer().setToolTipGenerator(
//                new );
        
        paretoPanel.add(chartPanel, gridBagConstraints);
        pack();
    }
    
    public void update(Observable o, Object arg)
    {
        synchronized(populationUpdateSyncRoot)
        {
            //without this clone, concurrent modification exceptions are thrown
            currentPopulation = ((MultiObjectivePopulation) arg).clone();
            if (EventQueue.isDispatchThread())
            {
                handleUpdate(currentPopulation);
            }
            else
            {
                final MultiObjectivePopulation fPop = currentPopulation;
                try
                {
                    EventQueue.invokeAndWait(new Runnable()
                    {
                        public void run()
                        {
                            handleUpdate(fPop);
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
    }
    
    private void handleUpdate(MultiObjectivePopulation population)
    {
        int curGen = (int) currentPopulation.getProblem().getStepper().getCurrentGeneration();
        if (curGen % generationalDrawingFrequency == 0)
        {
            this.setTitle("Generation "+ curGen +" Pareto Front");
            
            switch (paretoMemory)
            {
                case DISPLAY_CURRENT_FRONT:
                    setFront(currentPopulation);
                    break;
                case DISPLAY_GLOBAL_FRONT:
//                    addToFront(currentPopulation);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled: "+ paretoMemory);
            }
            lastDrawnPopulationGeneration = curGen;
            currentlyDrawnPopulation = currentPopulation;
        }
        else
        {
//            addToFront(currentPopulation);
        }
    }
    
    private void setFront(MultiObjectivePopulation population)
    {
        // (1) which x, y points to add
        population.sortParetoRank();
        // (2) add them
        final Map<Integer, List<Double>> rank2XValues = new HashMap<Integer, List<Double>>();
        final Map<Integer, List<Double>> rank2YValues = new HashMap<Integer, List<Double>>();
        
        final int selectedXIndex = xaxisComboBox.getSelectedIndex();
        final int selectedYIndex = yaxisComboBox.getSelectedIndex();
        
        final Set<Individual> drawnGuys = new HashSet<Individual>();
        
        for (Individual individual : population.getIndividuals())
        {
            final int pRank = individual.getParetoRank();
            if (pRank < maxNumParetoRanksToShow)
            {
                drawnGuys.add(individual);
                
                if (!rank2XValues.containsKey(pRank))
                {rank2XValues.put(pRank, new ArrayList<Double>());}
                
                if (!rank2YValues.containsKey(pRank))
                {rank2YValues.put(pRank, new ArrayList<Double>());}
                
                rank2XValues.get(pRank).add(individual.getFitness(selectedXIndex));
                rank2YValues.get(pRank).add(individual.getFitness(selectedYIndex));
            }
        }
        
        switch(drawnIndividuals.size())
        {
            case 0:
            case 1:
                drawnIndividuals.add(drawnGuys);
                break;
            case 2:
                drawnIndividuals.set(0, drawnIndividuals.get(1));
                drawnIndividuals.set(1, drawnGuys);
                break;
            default:
                throw new IllegalArgumentException();
        }
        
        // (3) redraw?
        // - clear all series data
        for (int i=0; i < paretoFront.getSeriesCount(); i++)
        { paretoFront.removeSeries(paretoFront.getSeriesKey(i)); }
//        System.out.println("Cleared series");
        
        final int curGen = (int) population.getProblem().getStepper().getCurrentGeneration();
        for (int i=0; i < maxNumParetoRanksToShow; i++)
        {
            addSeriesToFront("Gen "+curGen+" Rank "+ i, rank2XValues.get(i), rank2YValues.get(i));
        }
        
        if (lastDrawnRank2XValues != null)
        {
            for (int i=0; i < maxNumParetoRanksToShow; i++)
            {
                int t = i * -1;
                if (lastDrawnRank2XValues.containsKey(t))
                {
                    addSeriesToFront(
                            "Gen "+ lastDrawnPopulationGeneration + " Rank "+ t,
                            lastDrawnRank2XValues.get(t),
                            lastDrawnRank2YValues.get(t));
                }
            }
        }
        lastDrawnRank2XValues = rank2XValues;
        lastDrawnRank2YValues = rank2YValues;
    }
    
    private void addSeriesToFront(String name, Collection<Double> xValues,
            Collection<Double> yValues)
    {
//        System.out.println("Added series: "+ name+ " with "+ xValues.size()+ " values");
        double[][] theData = new double[2][];
        theData[0] = convert(xValues);//x-values
        theData[1] = convert(yValues);//y-values
        paretoFront.addSeries(name, theData);
    }
    
    private void addToFront(MultiObjectivePopulation population)
    {}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        paretoPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        xaxisComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        yaxisComboBox = new javax.swing.JComboBox();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        paretoPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(paretoPanel, gridBagConstraints);

        jLabel1.setText("X-Axis:");
        jPanel2.add(jLabel1);

        xaxisComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                xaxisComboBoxActionPerformed(evt);
            }
        });

        jPanel2.add(xaxisComboBox);

        jLabel2.setText("Y-Axis:");
        jPanel2.add(jLabel2);

        yaxisComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                yaxisComboBoxActionPerformed(evt);
            }
        });

        jPanel2.add(yaxisComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        getContentPane().add(jPanel2, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void yaxisComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_yaxisComboBoxActionPerformed
    {//GEN-HEADEREND:event_yaxisComboBoxActionPerformed
        
        //plot.getDomainAxis().setLabel()
        
    }//GEN-LAST:event_yaxisComboBoxActionPerformed
    
    private void xaxisComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_xaxisComboBoxActionPerformed
    {//GEN-HEADEREND:event_xaxisComboBoxActionPerformed
        
        //plot.getDomainAxis().setLabel()
        
    }//GEN-LAST:event_xaxisComboBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel paretoPanel;
    private javax.swing.JComboBox xaxisComboBox;
    private javax.swing.JComboBox yaxisComboBox;
    // End of variables declaration//GEN-END:variables
    
    public void chartMouseClicked(ChartMouseEvent event)
    {
        synchronized(populationUpdateSyncRoot)
        {
            XYPlot plot = (XYPlot) paretoChart.getPlot();
            ValueAxis xaxis = plot.getDomainAxis();
            ValueAxis yaxis = plot.getRangeAxis();
            ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
            Rectangle2D dataArea = info.getPlotInfo().getDataArea();
            
            double tX = xaxis.java2DToValue(event.getTrigger().getPoint().getX(),
                    dataArea, plot.getDomainAxisEdge());
            
            double tY = yaxis.java2DToValue(event.getTrigger().getPoint().getY(),
                    dataArea, plot.getRangeAxisEdge());
//            System.out.println("Did you clikc on "+ tX+","+ tY);
            
            Point2D clickAt = new Point2D.Double(tX, tY);
//            System.out.println("Raw Click @ :"+ event.getTrigger().getPoint());
//            System.out.println("Translated Click @: "+ clickAt);
            double minDistance = Double.MAX_VALUE;
            int bestSeries = -1, bestItem = -1;
//            System.out.println("Number series: "+ paretoFront.getSeriesCount());
            //the mouse click wasn't directly on an entity, so locate the nearest one
            final int xIndex = xaxisComboBox.getSelectedIndex();
            final int yIndex = yaxisComboBox.getSelectedIndex();
            Individual selectedDude = null;
            for(Set<Individual> drawnSet : this.drawnIndividuals)
            {
//                System.out.println("Set: "+ drawnSet+" size: "+ drawnSet.size());
                for(Individual drawnGuy : drawnSet)
                {
                    final double x = drawnGuy.getFitness(xIndex);
                    final double y = drawnGuy.getFitness(yIndex);
                    final double distanceSq = clickAt.distanceSq(x,y);
                    if (distanceSq < minDistance)
                    {
//                        System.out.println("Distance Squared: "+ distanceSq +" x,y: "+ x+","+y);
                        minDistance = distanceSq;
                        selectedDude = drawnGuy;
                    }
                    else
                    {
//                        System.out.println("Loser: "+ distanceSq +" x,y: "+ x+","+y);
                    }
                }
            }
            
            if (selectedDude != null)
            {
//                System.out.println("Passing to "+ this.visualizers.length+" visualizers");
                for(IVizSolutions viz : this.visualizers)
                {
                    JInternalFrame frame = viz.visualize(selectedDude);
                    getDesktopPane().add(frame);
                    frame.setVisible(true);
                }
            }
            else
            {
                System.out.println("You didn't click on jack, did you? " +
                        chartPanel.getChartRenderingInfo().getEntityCollection().getEntityCount());
            }
        }
    }
    
    private static boolean isLegendEntity(ChartEntity entity)
    {
        return (entity instanceof LegendItemEntity);
    }
    
    public void chartMouseMoved(ChartMouseEvent event)
    {
    }
    
    private JFreeChart createChart(XYDataset dataset, String title, String xAxisLabel, String yAxisLabel)
    {
        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        XYPlot plot = (XYPlot) chart.getPlot();
        DefaultDrawingSupplier ds = new DefaultDrawingSupplier(
                createDefaultPaintArray(),
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        
        plot.setDrawingSupplier(ds);
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setDotWidth(2);
        renderer.setDotHeight(2);
        plot.setRenderer(renderer);
//        plot.getRangeAxis().setRange(0,15);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
//        domainAxis.setRange(-10, 0);
        domainAxis.setAutoRangeIncludesZero(false);
        return chart;
    }
    
    /**
     */
    private JFreeChart createChartPanel(String title, String xAxisLabel, String yAxisLabel)
    {
        JFreeChart chart = createChart(paretoFront,title,xAxisLabel,yAxisLabel);
        return chart;
    }
    
    /**
     * Convenience method to return an array of <code>Paint</code> objects that
     * represent the pre-defined colors in the <code>Color<code> and
     * <code>ChartColor</code> objects.
     *
     * @return An array of objects with the <code>Paint</code> interface.
     */
    public Paint[] createDefaultPaintArray()
    {
        List<Paint> list = new ArrayList<Paint>();
        int paintIndex = 0;
        Paint[] paintSequence = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
        for (int i=0; i < maxNumParetoRanksToShow*2; i++)
        {
            if (paintIndex == 2)
            {
                paintIndex++;
            }
            else if (paintIndex == paintSequence.length)
            {
                paintIndex = 0;
            }
            list.add(paintSequence[paintIndex++]);
        }
        return list.toArray(new Paint[0]);
    }
    
    private static double[] convert(Collection<Double> list)
    {
        double[] d = new double[list.size()];
        int i = 0;
        for(Double value : list)
        {
            d[i++] = value;
        }
        return d;
    }
}
