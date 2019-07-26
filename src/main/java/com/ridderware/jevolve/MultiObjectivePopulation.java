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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.logging.log4j.*;

/**
 * A population for multi-objective evolution.  This population adds methods common
 * for multi-objective evolution, such as identification and maintenance of elites
 * and Pareto sorting.
 *
 * @author Jeff Ridder
 */
public class MultiObjectivePopulation extends Population
{
    private final static Logger logger =
        LogManager.getLogger(MultiObjectivePopulation.class);

    /**
     *  Constructor for the MultiObjectivePopulation object
     *
     * @param  prototype the prototype individual for the population.
     */
    public MultiObjectivePopulation(Individual prototype)
    {
        super(prototype);
    }

    /**
     * Method to identify and maintain an elite population.  Elite individuals
     * are identified based on Pareto rank.
     */
    @Override
    public void processElites()
    {
        if (getPopulationSize() != getMaxPopulationSize())
        {
            logger.debug("Pop size is " + getPopulationSize() +
                " in processElites.");
        }

        for (Individual elite : getElites())
        {
            this.addIndividual(elite);
        }

        this.sortParetoRank();

        if (getPopulationSize() == 0)
        {
            logger.debug("Population is empty after sorting by Pareto rank in processElites.");
        }

        this.clearElites();

        //    Find elites
        for (int i = 0; i < getMaxNumberOfElites(); i++)
        {
            Individual ind = getIndividual(i).clone();
            addElite(getIndividual(i));
            removeIndividual(i);
            addIndividual(i, ind);
        }

        int num = getPopulationSize() - getMaxPopulationSize();
        for (int i = num - 1; i >= 0; i--)
        {
            removeIndividual(getMaxPopulationSize() + i);
        }

        if (getElites().size() != getMaxNumberOfElites())
        {
            logger.error("Num elites " + getElites().size() + " is wrong");
        }

        if (getPopulationSize() != getMaxPopulationSize())
        {
            logger.error("Population size " + getPopulationSize() + " is wrong");
        }
    }

    /**
     * Method to sort the population based on Pareto rank and crowding distance.  This
     * is Deb's sorting algorithm from NSGA-II.  It assigns the pareto rank to each individual and
     * sorts them in increasing order of Pareto rank (lower is better and at the beginning).  Within
     * a rank, individuals are ordered with a preference for greater crowding distance.
     */
    public void sortParetoRank()
    {
        //	First, reset rank of each individual
        for (Individual ind : getIndividuals())
        {
            ind.setParetoRank(-1);
        }

        //	Mark number unranked
        int num_unranked = getPopulationSize();

        //	Mark the rank we are currently assigning.
        int cur_rank = 0;

        //	A temporary array.
        ArrayList<Individual> temp_list = new ArrayList<Individual>();

        while (num_unranked > 0)
        {
            //	Container for the current front
            ArrayList<Individual> list_front = new ArrayList<Individual>();

            for (Individual ind : getIndividuals())
            {
                if (ind.getParetoRank() == -1)
                {
                    //	If individual's rank has not been
                    //	assigned, then set it to current rank
                    //	and try it out.
                    ind.setParetoRank(cur_rank);
                    list_front.add(ind);

                    //	Now iterate over all candidates,
                    //	checking the new guy for dominance
                    boolean b_break = false;
                    for (Iterator<Individual> f = list_front.iterator(); f.hasNext();)
                    {
                        Individual tf = f.next();

                        if (tf != ind)
                        {
                            switch (ind.checkConstrainedDominance(tf))
                            {
                                case DOMINATING:
                                {
                                    tf.setParetoRank(-1);
                                    f.remove();
                                    break;
                                }
                                case DOMINATED:
                                {
                                    ind.setParetoRank(-1);
                                    list_front.remove(ind);
                                    b_break = true;
                                    break;
                                }
                                case INCOMPARABLE:
                                {
                                    break;
                                }

                            }

                            if (b_break)
                            {
                                break;
                            }
                        }
                    }
                }
            }

            if (list_front.isEmpty())
            {
                break;
            }

            //	Reset crowding distance
            for (Individual ind : list_front)
            {
                ind.setCrowdingDistance(0.);
            }

            //	Now compute crowding distance
            for (int k = 0; k < list_front.get(0).getNumObjectives(); k++)
            {
                Collections.sort(list_front, new FitnessComparator(k));

                list_front.get(0).addCrowdingDistance(Double.MAX_VALUE);
                list_front.get(list_front.size() - 1).addCrowdingDistance(Double.MAX_VALUE);

                for (int m = 1; m < list_front.size() - 1; m++)
                {
                    list_front.get(m).addCrowdingDistance(list_front.get(m + 1).
                        getFitness(k) - list_front.get(m - 1).getFitness(k));
                }
            }

            //	Sort it by crowding distance (high to low)
            Collections.sort(list_front, new CrowdingComparator());

            //	Move the individuals to temp
            temp_list.addAll(list_front);

            //	Remove them from individuals
            for (Iterator<Individual> it = getIndividuals().iterator(); it.hasNext();)
            {
                if (list_front.contains(it.next()))
                {
                    it.remove();
                }
            }

            num_unranked -= list_front.size();

            list_front.clear();

            cur_rank++;

        }

        getIndividuals().addAll(temp_list);

        temp_list.clear();
    }

    /**
     * Clones the population.
     *
     * @return  MultiObjectivePopulation that is a clone of this.
     */
    @Override
    public MultiObjectivePopulation clone()
    {
        MultiObjectivePopulation obj = (MultiObjectivePopulation) super.clone();

        return obj;
    }
}

