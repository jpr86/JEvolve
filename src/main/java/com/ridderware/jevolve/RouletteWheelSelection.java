/* %%
 * 
 * JEvolve
 *
 * Copyright 2006 Jeff Ridder
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
package com.ridderware.jevolve;

import com.ridderware.jrandom.MersenneTwisterFast;
import org.apache.logging.log4j.*;

/**
 * A basic roulette wheel selection operator using adjusted fitness.
 *
 * @author Jeff Ridder
 */
public class RouletteWheelSelection extends Selector
{
    private final static Logger logger =
        LogManager.getLogger(RouletteWheelSelection.class);

    private double population_adjusted_fitness;


    /**
     *  Constructor for the TournamentSelection object
     */
    public RouletteWheelSelection()
    {
        this.population_adjusted_fitness = Double.MAX_VALUE;
    }

    /**
     * Sets the cumulative adjusted fitness for the population.  Necessary for determining selection of individuals.
     * @param popFitness
     */
    public void setPopAdjustedFitness(double popFitness)
    {
        this.population_adjusted_fitness = popFitness;
    }


    /**
     *  This implementation of tournament selection goes through the randomly
     *  chosen individuals in the tournament. If the currentWinner (starting with
     *  the first person defaulted as the current Winner) has a lower fitness than
     *  the currentCompetitor and the currentCompetitor gets lucky (by rolling
     *  dice < kParam), then the currentCompetitor becomes the current winner. If
     *  k = 1.0 this has the effect of returning the highest fitness individual
     *  from the tournament pool.
     *
     * @param  pop population from which to select.
     * @return  selected individual
     */
    public Individual select(Population pop)
    {
        pop.sortFitness();

        double prob = MersenneTwisterFast.getInstance().nextDouble();

        double sumAdj = 0.0;
        int i = 0;
        Individual ind = null;
        do
        {
            ind = pop.getIndividual(i);
            sumAdj += ind.getAdjustedFitness()/population_adjusted_fitness;
            i++;
        } while(sumAdj < prob);

        return ind;
    }
}
