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

import java.util.Collections;
import org.apache.logging.log4j.*;

/**
 * A breeder supporting deterministic crowding replacement.  The selection operator
 * is ignored here and can be null.  Instead, this breeder shuffles the parent
 * population in order to determine mates for breeding (consistent with
 * deterministic crowding).  The actual replacement takes place in the
 * DeterministicCrowdingProblem class.
 *
 * @author Jeff Ridder
 */
public class DeterministicCrowdingBreeder extends Breeder
{
    private final static Logger logger =
        LogManager.getLogger(DeterministicCrowdingBreeder.class);

    /**
     *  Creates a new instance of DeterministicCrowdingBreeder
     */
    public DeterministicCrowdingBreeder()
    {
        super();
    }

    /**
     * Creates a new instance of DeterministicCrowdingBreeder
     *
     * @param  selector selection operator object
     * @param  recombinator recombination operator object
     * @param  mutator mutation operator object
     */
    public DeterministicCrowdingBreeder(Selector selector,
        Recombinator recombinator, Mutator mutator)
    {
        super(selector, recombinator, mutator);
    }

    /**
     * Breeds the next generation using deterministic crowding for replacement.
     *
     * @param  parent_pop parent population
     * @param  child_pop child population
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

        Collections.shuffle(parent_pop.getIndividuals());

        for (int i = 0; i < parent_pop.getPopulationSize() - 1; i += 2)
        {
            mom = parent_pop.getIndividual(i);
            dad = parent_pop.getIndividual(i + 1);

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
            mom = parent_pop.getIndividual(parent_pop.getPopulationSize() - 1);
            dad = getSelector().select(parent_pop);

            boy = child_pop.getIndividual(parent_pop.getPopulationSize() - 1);
            girl = null;

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);
            boy.setPopulation(child_pop);
        }
    }
}

