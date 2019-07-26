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
import java.util.Set;
import org.apache.logging.log4j.*;

/**
 * HotSpotChunkCrossover works with HotSpotIndividuals which attempt to find and
 * keep track of the most advantageous locations to crossover, and to keep "chunks"
 * of the genome that work well together intact.  The crossover point is biased by
 * this preference to keep good chunks and the bad?  Well...we blow those chunks (sorry).
 *
 * @param E gene type
 * @author Jason HandUber
 */
public class HotSpotChunkCrossover<E> extends Recombinator
{
    private final static Logger logger =
        LogManager.getLogger(HotSpotChunkCrossover.class);

    /**
     * Performs recombination of parents to produce children.
     *
     * @param  parent1 dad.
     * @param  parent2 mom.
     * @param  child1 son.
     * @param  child2 daughter.
     */
    public void recombine(Individual parent1, Individual parent2,
        Individual child1, Individual child2)
    {
        //if we aren't going to xover, and the children don't exist, create them directly from their parents.
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
            //we are going to xover, children will NOT be exact copies of their parents

            if (!(parent1 instanceof HotSpotIndividual) ||
                !(parent2 instanceof HotSpotIndividual) ||
                !(child1 instanceof HotSpotIndividual) ||
                !(child2 instanceof HotSpotIndividual))
            {
                logger.error("Individuals passed to HotSpotChunkCrossover must be HotSpotIndividuals.  Exiting!");
                System.exit(2);
            }

            HotSpotIndividual<E> dad = (HotSpotIndividual<E>) parent1;
            HotSpotIndividual<E> mom = (HotSpotIndividual<E>) parent2;
            HotSpotIndividual<E> boy = (HotSpotIndividual<E>) child1;
            HotSpotIndividual<E> girl = (HotSpotIndividual<E>) child2;

            int maxNumChunks = Math.round((dad.getMaxNumChunks() +
                mom.getMaxNumChunks()) / 2);

            int numChunks = 0;
            Set<Integer> xoverLocations = new HashSet<Integer>();

            // While we don't have too many chunks and there is enough space to get more chunks
            while (numChunks < maxNumChunks && xoverLocations.size() <
                Math.round(dad.getGenotypeSize() / 3))
            {
                logger.debug("Looping to get chunks to xover. currently have: " +
                    numChunks + " Max is: " + maxNumChunks);
                logger.debug("Num XoverLocations: " + xoverLocations.size() +
                    " should be < " + Math.round(dad.getGenotypeSize() / 3));
                logger.debug("Dad genotype size: " + dad.getGenotypeSize());

                int xoverPoint1 = dad.getHotSpotIndexFromPoints(MersenneTwisterFast.getInstance().
                    nextInt(dad.getTotalHotSpotPoints()));
                int xoverPoint2 = xoverPoint1;

                while (xoverPoint1 == xoverPoint2)
                {
                    xoverPoint2 = dad.getHotSpotIndexFromPoints(MersenneTwisterFast.getInstance().
                        nextInt(dad.getTotalHotSpotPoints()));
                }

                if (xoverPoint2 < xoverPoint1)
                {
                    int temp = xoverPoint1;
                    xoverPoint1 = xoverPoint2;
                    xoverPoint2 = temp;
                }

                logger.debug("Selected xoverPoint1 & 2: " + xoverPoint1 + "," +
                    xoverPoint2);

                boolean okay2add = true;
                for (int i = xoverPoint1; i < xoverPoint2; i++)
                {
                    if (xoverLocations.contains(i))
                    {
                        okay2add = false;
                        logger.debug("Cannot cross at xoverPoint1 or 2, sections already being crossed");
                    }
                    if (!okay2add)
                    {
                        break;
                    }
                }

                if (okay2add)
                {
                    numChunks++;
                    dad.addCrossoverEvent(xoverPoint1, xoverPoint2);
                    mom.addCrossoverEvent(xoverPoint1, xoverPoint2);
                    logger.debug("HotSpotChunkCrossover added genes from locus " +
                        xoverPoint1 + " to locus " + xoverPoint2);
                    for (; xoverPoint1 < xoverPoint2; xoverPoint1++)
                    {
                        xoverLocations.add(xoverPoint1);
                    }
                }
            }
            logger.debug("Done chunking, we will cross :" +
                xoverLocations.size() + " genes");

            boy.getGenotype().clear();
            girl.getGenotype().clear();
            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                if (xoverLocations.contains(i))
                {
                    boy.getGenotype().add(mom.getGenotype().get(i));
                    girl.getGenotype().add(dad.getGenotype().get(i));
                }
                else
                {
                    boy.getGenotype().add(dad.getGenotype().get(i));
                    girl.getGenotype().add(mom.getGenotype().get(i));
                }
            }

            logger.debug("Done crossing genes, setting up boy & girl hotspots");

            //move to junit test later.
            if (boy.getGenotype().size() != dad.getGenotype().size() ||
                boy.getGenotype().size() != mom.getGenotype().size() ||
                boy.getGenotype().size() != girl.getGenotype().size())
            {
                logger.error("Mismatching genotype sizes");
                System.exit(1);
            }

            boy.setHotSpots(dad.getHotSpots());
            girl.setHotSpots(mom.getHotSpots());

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}
