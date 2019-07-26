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

/**
 * An implementation of two-point crossover.
 *
 * @param E gene type.
 * @author Jeff Ridder
 */
public class TwoPointCrossover<E> extends Recombinator
{
    /**
     *  Implementation of two-point Crossover. TwoPointCrossover selects 2 indices.
     *  If there are N genes in the individual, the index's valid range is
     *  [0,N-1]. If you have parents(dad, mom, boy, girl): abcd, efgh, rstu, and
     *  wxyz then crossing over at indexes 0 and 2 will produce BOY=ebch and
     *  GIRL=aefd. This implementation of crossover forces some exchange of
     *  information (even if it's the same information!). parent1 = dad, parent2 =
     *  mom, child1 = boy, child2 = girl. boy = mom_head + +dad_middle + mom_tail
     *  girl = dad_head + mom_middle + dad_tail
     *
     * @param  parent1 dad
     * @param  parent2 mom
     * @param  child1 boy
     * @param  child2 girl
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

            GAIndividual<E> dad = (GAIndividual<E>) parent1;
            GAIndividual<E> mom = (GAIndividual<E>) parent2;
            GAIndividual<E> boy = (GAIndividual<E>) child1;
            GAIndividual<E> girl = (GAIndividual<E>) child2;

            //choose 2 random locations to crossover at.
            int xoverPoint1 = (int) (MersenneTwisterFast.getInstance().
                nextDouble() * (mom.getGenome().size() - 1));
            int xoverPoint2 = xoverPoint1;
            while (xoverPoint1 == xoverPoint2)
            {
                xoverPoint2 = (int) (MersenneTwisterFast.getInstance().
                    nextDouble() * (mom.getGenome().size() - 1));
            }

            if (xoverPoint2 < xoverPoint1)
            {
                int temp = xoverPoint1;
                xoverPoint1 = xoverPoint2;
                xoverPoint2 = temp;
            }

            if (!(boy.equals(null)))
            {
                boy.getGenotype().clear();
            }
            if (!(girl.equals(null)))
            {
                girl.getGenotype().clear();
            }

            ArrayList<E> dadhead = new ArrayList<E>();
            ArrayList<E> dadmiddle = new ArrayList<E>();
            ArrayList<E> dadtail = new ArrayList<E>();
            ArrayList<E> momhead = new ArrayList<E>();
            ArrayList<E> mommiddle = new ArrayList<E>();
            ArrayList<E> momtail = new ArrayList<E>();

            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                if (i <= xoverPoint1)
                {
                    momhead.add(mom.getGenotype().get(i));
                }
                else if (i <= xoverPoint2)
                {
                    mommiddle.add(mom.getGenotype().get(i));
                }
                else
                {
                    momtail.add(mom.getGenotype().get(i));
                }
            }

            for (int i = 0; i < dad.getGenotype().size(); i++)
            {
                if (i <= xoverPoint1)
                {
                    dadhead.add(dad.getGenotype().get(i));
                }
                else if (i <= xoverPoint2)
                {
                    dadmiddle.add(dad.getGenotype().get(i));
                }
                else
                {
                    dadtail.add(dad.getGenotype().get(i));
                }
            }

            boy.getGenotype().addAll(momhead);
            boy.getGenotype().addAll(dadmiddle);
            boy.getGenotype().addAll(momtail);

            girl.getGenotype().addAll(dadhead);
            girl.getGenotype().addAll(mommiddle);
            girl.getGenotype().addAll(dadtail);

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}

