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

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;

/**
 * General options that should be applicable to all jevolve problems. Specific
 * subclasses may unleash further options.
 * @author jhanduber
 */
public class UIFactoryOptions<INDIVIDUAL>
{
    //add max # pareto ranks to show
    
    private boolean displayConstraintError = true;
    private boolean exitJVMonClose = true;
    
    private double generationalDrawingFrequency = 4;
    private double paretoDrawingFrequency = 25;
    
    private double minGraphBounds = -10.;
    private double maxGraphBounds = 10.;
    
    private final String[] fitnessDescriptions;
    
    private final Collection<Observable> populationSources =
            new HashSet<Observable>();
    
    private final Collection<IVizSolutions> individualVisualizers =
            new HashSet<IVizSolutions>();
    
    /**
     * The most common way to build a UIFactoryOptions instance. Use this
     * in combination with setters/adders for more advanced options and
     * to override default settings.
     *
     * @param populationSource the Observable that will call its' observers
     * everytime the population may need to be displayed. Just because the
     * {@code populationSource} notifies its Observers does not guarantee
     * that the new population will be rendered.
     *
     * @param fitnessDescriptions Short descriptions of each fitness metric.
     * The order of these metrics must
     * correspond to the order each Individual uses. For instance, if
     * indvididual.getFitness(0) returns a cost metric, then the 0<sup>th</sup>
     * index of the fitnessDescriptions array should be something like "cost".
     */
    public UIFactoryOptions(Observable populationSource, String ... fitnessDescriptions)
    {
        this.fitnessDescriptions = fitnessDescriptions;
        populationSources.add(populationSource);
    }
    
    /**
     * @param populationSource the Observable that will call its' observers
     * everytime the population may need to be displayed. Just because the
     * {@code populationSource} notifies its Observers does not guarantee
     * that the new population will be rendered.
     */
    public void addPopulationSource(Observable populationSource)
    {
        this.populationSources.add(populationSource);
    }
    
    public void addIndividualVisualizer(IVizSolutions viz)
    {
        this.individualVisualizers.add(viz);
    }
    
    public boolean getDisplayConstraintError()
    {
        return displayConstraintError;
    }
    
    /*
     * @param displayConstraintError Whether or not to have a window display a graph showing constraint error mean
     * and sigma per generation
     */
    public void setDisplayConstraintError(boolean displayConstraintError)
    {
        this.displayConstraintError = displayConstraintError;
    }
    
    public boolean getExitJVMonClose()
    {
        return exitJVMonClose;
    }
    
    public void setExitJVMonClose(boolean exitJVMonClose)
    {
        this.exitJVMonClose = exitJVMonClose;
    }
    
    public double getMinGraphBounds()
    {
        return minGraphBounds;
    }
    
    public void setMinGraphBounds(double defaultMinGraphBounds)
    {
        this.minGraphBounds = defaultMinGraphBounds;
    }
    
    public double getMaxGraphBounds()
    {
        return maxGraphBounds;
    }
    
    public void setMaxGraphBounds(double defaultMaxGraphBounds)
    {
        this.maxGraphBounds = defaultMaxGraphBounds;
    }
    
    public String[] getFitnessDescriptions()
    {
        return fitnessDescriptions;
    }
    
    public IVizSolutions[] getVisualizationConsumers()
    {
        return individualVisualizers.toArray(new IVizSolutions[0]);
    }
    
    public Observable[] getPopulationSources()
    {
        return populationSources.toArray(new Observable[0]);
    }
    
    public double getGenerationalDrawingFrequency()
    {
        return generationalDrawingFrequency;
    }
    
    public void setGenerationalDrawingFrequency(double generationalDrawingFrequency)
    {
        this.generationalDrawingFrequency = generationalDrawingFrequency;
    }
    
    public double getParetoDrawingFrequency()
    {
        return paretoDrawingFrequency;
    }
    
    public void setParetoDrawingFrequency(double paretoDrawingFrequency)
    {
        this.paretoDrawingFrequency = paretoDrawingFrequency;
    }
}
