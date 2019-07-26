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
import java.util.HashSet;
import org.apache.logging.log4j.*;

/**
 * The ChunkCrossover operator crosses chunks of information over from
 *  individual to individual attempting to cross logical blocks. The
 *  ChunkCrossover operator attemps to extend unifrom crossover's lack of
 *  positional bias. The operator is presented here in limited form in that it
 *  only has the chance to look at its immediate neighbors to avoid "chunking"
 *  or to "chunk". In reality, this should be extended so that it will look at
 *  N of its neighbors where N is +-N and is determined by a random gaussian
 *  number. UPDATE WITH ABOVE SUGGESTION PRIOR TO SERIOUS USE. I expect the
 *  operator to perform well initially in the GA run and poorer later where a
 *  more precise crossover operator would be required to more finely tune an
 *  indvidual.
 *
 * @param E gene type.
 * @author Jason HandUber
 */
public class ChunkCrossover<E> extends Recombinator
{
    private final static Logger logger =
        LogManager.getLogger(ChunkCrossover.class);

    private HashSet<Integer> getIndexes(int max, int current_index,
        HashSet<Integer> goodIndexes, HashSet<Integer> badIndexes)
    {
        if ((current_index <= 0) || (goodIndexes.size() + badIndexes.size() ==
            max))
        {
            logger.debug("Done chunking, good Indexes, bad Indexes: ");
            logger.debug(goodIndexes.toString());
            logger.debug(badIndexes.toString());
            return goodIndexes;
        //exit out, we're done chunking
        }
        else if ((MersenneTwisterFast.getInstance().nextDouble()) < 0.5)
        {
            //collect a chunk
            if (!((badIndexes.contains(current_index)) ||
                (badIndexes.contains(current_index - 1)) ||
                (badIndexes.contains(current_index + 1))))
            {
                if (current_index <= max)
                {
                    goodIndexes.add(current_index);
                }
            }
            logger.debug("good indexes was denied index: " + current_index);
            int nextIndex = (current_index + (int) (MersenneTwisterFast.getInstance().
                nextGaussian() * 1.8));
            logger.debug("Chunking, current index = " + current_index +
                ", next index = " + nextIndex);
            getIndexes(max, nextIndex, goodIndexes, badIndexes);
        }
        else if ((MersenneTwisterFast.getInstance().nextDouble()) < 0.5)
        {
            //avoid a chunk
            if (!((goodIndexes.contains(current_index)) ||
                (goodIndexes.contains(current_index - 1)) ||
                (goodIndexes.contains(current_index + 1))))
            {
                if (current_index <= max)
                {
                    badIndexes.add(current_index);
                }
            }

            int nextIndex = (current_index + (int) (MersenneTwisterFast.getInstance().
                nextGaussian() * 1.8));
            // logger.debug("DeChunking, current index = %d, next index = %d\n",current_index, nextIndex);
            getIndexes(max, current_index, goodIndexes, badIndexes);
        }
        else
        {
            //we start with a high number, so we want a slow force pushing us the lower indexes so that the whole string may be seen by the chunker.
            getIndexes(max, --current_index, goodIndexes, badIndexes);
        }
        return goodIndexes;
    //unreachable
    }

    /**
     * Performs chunk crossover between two parents to produce two chickens.
     *
     * @param parent1 dad
     * @param parent2 mom
     * @param child1 son
     * @param child2 daughter
     */
    public void recombine(Individual parent1, Individual parent2,
        Individual child1, Individual child2)
    {
        //if we aren't going to xover and the children don't exist, create them directly from their parents.
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
            //if we are going to xover

            GAIndividual<E> dad = (GAIndividual<E>) parent1;
            GAIndividual<E> mom = (GAIndividual<E>) parent2;
            GAIndividual<E> boy = (GAIndividual<E>) child1;
            GAIndividual<E> girl = (GAIndividual<E>) child2;

            //choose a random location to crossover at.
            int xoverPoint = (int) (MersenneTwisterFast.getInstance().nextDouble() * (mom.getGenome().
                size() - 1));

            if (!(boy.equals(null)))
            {
                boy.getGenotype().clear();
            }
            if (!(girl.equals(null)))
            {
                girl.getGenotype().clear();
            }

            //collect indexes to crossover.
            HashSet<Integer> goodIndexes = new HashSet<Integer>();
            HashSet<Integer> badIndexes = new HashSet<Integer>();
            goodIndexes = getIndexes(mom.getGenome().size() - 1, mom.getGenome().
                size() - 1, goodIndexes, badIndexes);

            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                if (goodIndexes.contains(i))
                {
                    girl.getGenotype().add(dad.getGenotype().get(i));
                    boy.getGenotype().add(mom.getGenotype().get(i));
                }
                else
                {
                    girl.getGenotype().add(mom.getGenotype().get(i));
                    boy.getGenotype().add(dad.getGenotype().get(i));
                }
            }

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}

