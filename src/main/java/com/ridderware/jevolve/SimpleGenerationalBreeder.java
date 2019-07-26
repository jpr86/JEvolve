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
 *  Simple generational breeding. Produces a child population from the parent
 *  population through a basic sequence of selection, crossover, and mutation.
 *
 * @author Jeff Ridder
 */
public class SimpleGenerationalBreeder extends Breeder
{
    private final static Logger logger =
        LogManager.getLogger(SimpleGenerationalBreeder.class);

    /**
     *  Constructor for the SimpleGenerationalBreeder object
     */
    public SimpleGenerationalBreeder()
    {
        super();
    }

    /**
     *  Constructor for the SimpleGenerationalBreeder object
     *
     * @param  selector Selector object.
     * @param  recombinator Recombinator object.
     * @param  mutator Mutator object.
     */
    public SimpleGenerationalBreeder(Selector selector,
        Recombinator recombinator, Mutator mutator)
    {
        super(selector, recombinator, mutator);
    }

    /**
     * Breeds the next generation.
     *
     * @param  parent_pop parent population.
     * @param  child_pop child population.
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

        for (int i = 0; i < parent_pop.getPopulationSize() - 1; i += 2)
        {
            mom = getSelector().select(parent_pop);
            dad = getSelector().select(parent_pop);

            boy = child_pop.getIndividual(i);
            girl = child_pop.getIndividual(i + 1);

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);
            getMutator().mutate(girl);

            boy.setPopulation(child_pop);
            girl.setPopulation(child_pop);
        }

        if (parent_pop.getPopulationSize() % 2 != 0)
        {
            mom = getSelector().select(parent_pop);
            dad = getSelector().select(parent_pop);

            boy = child_pop.getIndividual(parent_pop.getPopulationSize() - 1);
            girl = null;

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);

            boy.setPopulation(child_pop);
        }
    }
}

