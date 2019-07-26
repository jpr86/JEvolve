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

/**
 * An implementation of uniform crossover.
 *
 * @param E gene type.
 * @author Jeff Ridder
 */
public class UniformCrossover<E> extends Recombinator
{
    /**
     * Performs recombination using uniform crossover.
     *
     * @param  parent1 dad
     * @param  parent2 mom
     * @param  child1 boy
     * @param  child2 girl
     */
    public void recombine(Individual parent1, Individual parent2,
        Individual child1, Individual child2)
    {
        if (MersenneTwisterFast.getInstance().nextDouble() >
            parent2.getProbRecombination())
        {
            if (child1 != null)
            {
                child1.deepCopy(parent1);
            }
            if (child2 != null)
            {
                child2.deepCopy(parent2);
            }
        }
        else
        {
            GAIndividual<E> dad = (GAIndividual<E>) parent1;
            GAIndividual<E> mom = (GAIndividual<E>) parent2;
            GAIndividual<E> boy = (GAIndividual<E>) child1;
            GAIndividual<E> girl = (GAIndividual<E>) child2;

            if (!(boy.equals(null)))
            {
                boy.getGenotype().clear();
            }
            if (!(girl.equals(null)))
            {
                girl.getGenotype().clear();
            }

            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                if (MersenneTwisterFast.getInstance().nextDouble() < 0.5)
                {
                    boy.getGenotype().add(mom.getValue(i));
                    girl.getGenotype().add(dad.getValue(i));
                }
                else
                {
                    boy.getGenotype().add(dad.getValue(i));
                    girl.getGenotype().add(mom.getValue(i));
                }
            }

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}

