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
 *  A breeder that implements Dynamic NSGA.  Dynamic NSGA differs from NSGA-II in
 *  that it does not use elitism, and instead uses Steady State evolution with
 *  50% replacement.  That is, after each population evaluation, the least fit half
 *  of the population is replaced by children bred from the most fit half of the
 *  population.
 *
 * @author Jeff Ridder
 */
public class DynamicNSGABreeder extends Breeder
{
    private final static Logger logger =
        LogManager.getLogger(DynamicNSGABreeder.class);

    /**
     *  Constructor for the DynamicNSGABreeder object
     */
    public DynamicNSGABreeder()
    {
        super();
    }

    /**
     *  Constructor for the DynamicNSGABreeder object
     *
     * @param  selector Selector object.
     * @param  recombinator Recombinator object.
     * @param  mutator Mutator object.
     */
    public DynamicNSGABreeder(Selector selector, Recombinator recombinator,
        Mutator mutator)
    {
        super(selector, recombinator, mutator);
    }

    /**
     * Breeds the next generation of individuals using the selection, recombination,
     * and mutation operators.  Since Dynamic NSGA is steady state, the next generation
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

        MultiObjectivePopulation pop = (MultiObjectivePopulation) parent_pop;

        //  Pareto sort may already have been done (e.g., in the Problem), but let's not
        //  take any chances.  Do it here.
        pop.sortParetoRank();

        MultiObjectivePopulation best_half = pop.clone();

        for (int i = pop.getPopulationSize() - 1; i >=
            pop.getMaxPopulationSize() / 2; i--)
        {
            best_half.removeIndividual(i);
        }

        for (int i = pop.getMaxPopulationSize() / 2; i <
            pop.getMaxPopulationSize() - 1; i += 2)
        {
            mom = getSelector().select(best_half);
            dad = getSelector().select(best_half);

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
            mom = getSelector().select(best_half);
            dad = getSelector().select(best_half);

            boy = pop.getIndividual(pop.getPopulationSize() - 1);
            girl = null;

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);

            boy.setPopulation(parent_pop);
        }

        logger.debug("Pop size after breeding is " + pop.getPopulationSize());
    }
}

