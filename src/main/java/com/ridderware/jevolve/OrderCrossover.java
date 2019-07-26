/* %%
 * 
 * JEvolve
 *
 * Copyright 2007 Jeff Ridder
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
 * An implementation of the order crossover (OX) operator of Davis.  Davis, L.,
 * "Applying Adaptive Algorithms to Epistatic Domains," Proceedings of the International
 * Joint Conference on Parallel Problem Solving from Nature (PPSN), Lecture Notes
 * in Computer Science, Vol. 866, Springer-Verlag, 1994.  Order Crossover seeks to preserve
 * the relative ordering of the genotype and is often used for Traveling Salesman Problems.
 * It also ensures feasible children in that the same values cannot be repeated in child
 * genotypes.  Since order crossover reorders genotypes without regard to the genome,
 * it is not a general crossover operator, and should only be applied to individuals with
 * homogeneous genomes (i.e., each gene is the same).
 *
 * @param <E>
 * @author Jeff Ridder
 */
public class OrderCrossover<E> extends Recombinator
{
    private final static Logger logger =
        LogManager.getLogger(OrderCrossover.class);

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
        if (MersenneTwisterFast.getInstance().nextDouble() >
            parent2.getProbRecombination() ||
            parent1.getGenotypeSize() <= 1)
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

            //choose 2 random locations to crossover at.
            int xoverPoint1 = (int) (MersenneTwisterFast.getInstance().
                nextDouble() * (mom.getGenotypeSize() - 1));
            int xoverPoint2 = xoverPoint1;
            while (xoverPoint1 == xoverPoint2)
            {
                xoverPoint2 = (int) (MersenneTwisterFast.getInstance().
                    nextDouble() * (mom.getGenotypeSize() - 1));
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

            ArrayList<E> boyhead = new ArrayList<E>();
            ArrayList<E> boymiddle = new ArrayList<E>();
            ArrayList<E> boytail = new ArrayList<E>();
            ArrayList<E> girlhead = new ArrayList<E>();
            ArrayList<E> girlmiddle = new ArrayList<E>();
            ArrayList<E> girltail = new ArrayList<E>();

            //  See Michaelewicz "Evolution Programs" P. 217 for an explanation of this:
            ArrayList<E> p2_sequence = new ArrayList<E>();
            p2_sequence.addAll(momtail);
            p2_sequence.addAll(momhead);
            p2_sequence.addAll(mommiddle);

            boymiddle.addAll(dadmiddle);

            //  Now remove the middle of dad.
            for (E val : dadmiddle)
            {
                p2_sequence.remove(val);
            }

            assert (p2_sequence.size() + boymiddle.size() == gsize);

            //  Now fill in from second cut point
            for (int i = 0; i < dadtail.size(); i++)
            {
                //  Pop the first item on the list and add to the tail
                boytail.add(p2_sequence.remove(0));
            }

            //  What's left is the head
            boyhead.addAll(p2_sequence);

            assert (boyhead.size() + boymiddle.size() + boytail.size() == gsize);

            //  Repeat for the boy
            ArrayList<E> p1_sequence = new ArrayList<E>();
            p1_sequence.addAll(dadtail);
            p1_sequence.addAll(dadhead);
            p1_sequence.addAll(dadmiddle);

            girlmiddle.addAll(mommiddle);

            //  Now remove the middle of mom from the sequence.
            for (E val : mommiddle)
            {
                p1_sequence.remove(val);
            }

            assert (p1_sequence.size() + girlmiddle.size() == gsize);

            //  Now fill in from second cut point
            for (int i = 0; i < dadtail.size(); i++)
            {
                //  Pop the first item on the list and add to the tail
                girltail.add(p1_sequence.remove(0));
            }

            //  What's left is the head
            girlhead.addAll(p1_sequence);

            assert (girlhead.size() + girlmiddle.size() + girltail.size() ==
                gsize);

            boy.getGenotype().addAll(boyhead);
            boy.getGenotype().addAll(boymiddle);
            boy.getGenotype().addAll(boytail);

            girl.getGenotype().addAll(girlhead);
            girl.getGenotype().addAll(girlmiddle);
            girl.getGenotype().addAll(girltail);

            boy.setEvaluated(false);
            girl.setEvaluated(false);
        }
    }
}
