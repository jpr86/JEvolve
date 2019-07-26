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

import org.apache.logging.log4j.*;

/**
 *  A breeder for a steady-state population.  The population is never fully replaced, but the lower
 *  worst individuals are replaced by reproduction amongst those that survive into the next generation.
 *  After each population evaluation, a specified percentage of the population is replaced by children bred
 *  from the most remainder of the population.
 *
 * @author Jeff Ridder
 */
public class SteadyStateBreeder extends Breeder
{
    private double percent_replacement;

    private final static Logger logger =
        LogManager.getLogger(SteadyStateBreeder.class);

    /**
     *  Constructor for the SteadyStateBreeder object
     * @param percent_replacement percent replacement of population per generation.
     */
    public SteadyStateBreeder(double percent_replacement)
    {
        super();

        this.percent_replacement = percent_replacement;
    }

    /**
     *  Constructor for the SteadyStateBreeder object
     *
     * @param  selector Selector object.
     * @param  recombinator Recombinator object.
     * @param  mutator Mutator object.
     * @param percent_replacement percent replace of population per generation
     */
    public SteadyStateBreeder(Selector selector, Recombinator recombinator,
        Mutator mutator, double percent_replacement)
    {
        super(selector, recombinator, mutator);

        this.percent_replacement = percent_replacement;
    }

    /**
     * Breeds the next generation of individuals using the selection, recombination,
     * and mutation operators.  For steady state, the next generation
     * is bred directly into the parent population and the child population is ignored.
     *
     * @param  parent_pop parent population.
     * @param  child_pop child population, which is ignored for this breeder.
     */
    public void breedNextGeneration(Population parent_pop, Population child_pop)
    {
        Individual mom;
        Individual dad;
        Individual boy;
        Individual girl;

        if (getSelector() == null || getRecombinator() == null ||
            getMutator() == null)
        {
            logger.error("Selector, Recombinator, or Mutator not set");
        }

        Population pop = parent_pop;

        //  Fitness sort may already have been done (e.g., in the Problem), but let's not
        //  take any chances.  Do it here.
        pop.sortFitness();

        Population best_part = pop.clone();

        //  This needs to be an even number
        int num_replaced = (int) (percent_replacement / 2 / 100. *
            pop.getPopulationSize()) * 2;

        int pop_size = pop.getPopulationSize();

        for (int i = pop_size - 1; i >= pop_size - num_replaced; i--)
        {
            best_part.removeIndividual(i);
        }

        for (int i = best_part.getPopulationSize(); i <
            pop.getMaxPopulationSize() - 1; i += 2)
        {
            mom = getSelector().select(best_part);
            dad = getSelector().select(best_part);

            boy = pop.getIndividual(i);
            girl = pop.getIndividual(i + 1);

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);
            getMutator().mutate(girl);

            boy.setPopulation(parent_pop);
            girl.setPopulation(parent_pop);
        }

        if (pop.getPopulationSize() % 2 != 0)
        {
            mom = getSelector().select(best_part);
            dad = getSelector().select(best_part);

            boy = pop.getIndividual(pop.getPopulationSize() - 1);
            girl = null;

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);
            boy.setPopulation(parent_pop);
        }

        logger.debug("Pop size after breeding is " + pop.getPopulationSize());
    }
}

