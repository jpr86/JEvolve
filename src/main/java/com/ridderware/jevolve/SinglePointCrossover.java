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
import java.util.ArrayList;
import org.apache.logging.log4j.*;

/**
 *  Implementation of single point crossover. SinglePointCrossover selects an
 *  index.  If there are N genes in the individual, the index's valid range is
 *  [0,N-1].  If you have parents: DAD and MOM and children: BOY and GIRL then
 *  crossing over at index 0 will produce BOY=(MOM[0])+(DAD[1=>N]) and
 *  GIRL=(DAD[0])+(MOM[1=>N]). Reversely, crossing over at N-1 will yield BOY =
 *  (MOM[0=>N-1]) + (DAD[N]) and GIRL = (DAD[0=>N-1]) + (MOM[N]). This
 *  implementation of crossover forces some exchange of information (even if
 *  it's the same information!). parent1 = dad, parent2 = mom, child1 = boy,
 *  child2 = girl. boy = mom_head + dad_tail girl = dad_head + mom_tail
 *
 * @param E gene type
 * @author Jason HandUber
 */
public class SinglePointCrossover<E> extends Recombinator
{
    private final static Logger logger =
        LogManager.getLogger(SinglePointCrossover.class);

    /**
     * Performs recombination of the parents to create the children.
     * @param  parent1 mom
     * @param  parent2 dad
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

            int gsize = dad.getGenotypeSize();

            if (mom.getGenotypeSize() != gsize)
            {
                logger.error("Before crossover, mom genotype is size " +
                    mom.getGenotypeSize());
            }

            //choose a random location to crossover at. one location insures equal sized children.
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

            ArrayList<E> momhead = new ArrayList<E>();
            ArrayList<E> momtail = new ArrayList<E>();
            ArrayList<E> dadhead = new ArrayList<E>();
            ArrayList<E> dadtail = new ArrayList<E>();

            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                if (i <= xoverPoint)
                {
                    momhead.add(mom.getGenotype().get(i));
                }
                else
                {
                    momtail.add(mom.getGenotype().get(i));
                }
            }

            for (int i = 0; i < dad.getGenotype().size(); i++)
            {
                if (i <= xoverPoint)
                {
                    dadhead.add(dad.getGenotype().get(i));
                }
                else
                {
                    dadtail.add(dad.getGenotype().get(i));
                }
            }

            if (momhead.size() + momtail.size() != gsize)
            {
                logger.error("Two parts of mom are: " + momhead.size() + " and " +
                    momtail.size());
            }

            if (dadhead.size() + dadtail.size() != gsize)
            {
                logger.error("Two parts of dad are: " + dadhead.size() + " and " +
                    dadtail.size());
            }

            boy.getGenotype().addAll(momhead);
            boy.getGenotype().addAll(dadtail);

            girl.getGenotype().addAll(dadhead);
            girl.getGenotype().addAll(momtail);

            if (boy.getGenotypeSize() != gsize)
            {
                logger.error("Boy genotype is size " + boy.getGenotypeSize() +
                    " formed out of momhead of size " + momhead.size() +
                    " and dadtail of size " + dadtail.size());
            }

            if (girl.getGenotypeSize() != gsize)
            {
                logger.error("Girl genotype is size " + girl.getGenotypeSize() +
                    " formed out of dadhead of size " + dadhead.size() +
                    " and momtail of size " + momtail.size());
            }

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}

