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
import java.util.HashSet;
import org.apache.logging.log4j.*;

/**
 * Cascading tournament selection operator.  Performs multiple rounds of 
 * 2-player tournaments, where the likelihood of the winner of each round 
 * proceeding to the next is determined by a parameter, k.  The tournament continues
 * until only one player is left standing.
 *
 * @author Jason HandUber
 */
public class CascadingTournamentSelection extends Selector
{
    private final static Logger logger =
        LogManager.getLogger(CascadingTournamentSelection.class);

    private int tournamentSize = 2;

    private double kParam = .75;

    /**
     *  Creates an instance of CascadingTournamentSelection.
     */
    public CascadingTournamentSelection()
    {
    }

    /**
     *  Creates an instance of CascadingTournamentSelection.
     *
     * @param tournamentSize size of the tournament.
     * @param kParam parameter between 0 and 1.
     */
    public CascadingTournamentSelection(int tournamentSize, double kParam)
    {
        this.tournamentSize = tournamentSize;
        this.kParam = kParam;
    }

    /**
     *  Sets the size of the tournament.
     *
     * @param  tournamentSize tournament size.
     */
    public void setTournamentSize(int tournamentSize)
    {
        this.tournamentSize = tournamentSize;
    }

    /**
     * Sets the k parameter (between 0 and 1).  This parameter is the
     * probability that the winner of any round of the tournament actually 
     * proceeds to the next round.
     *
     * @param  kParam  probability of winner proceeding.
     */
    public void setkParam(double kParam)
    {
        this.kParam = kParam;
    }

    /**
     * Returns the tournament size.
     *
     * @return     tournament size.
     */
    public int getTournamentSize()
    {
        return tournamentSize;
    }

    /**
     * Returns the k parameter.
     *
     * @return  k.
     */
    public double getkParam()
    {
        return kParam;
    }

    /**
     *  Performs cascading tournament selection.
     *
     * @param  pop population from which to select.
     * @return   selected individual.
     */
    public Individual select(Population pop)
    {

        HashSet<Integer> selectedIndexes = new HashSet<Integer>();

        if (tournamentSize > pop.getPopulationSize())
        {
            logger.info("\nNote: Tournament Size is Greater than Population Size. \nSetting Tournament Size to Population Size\n");
            tournamentSize = pop.getPopulationSize();
        }

        while (selectedIndexes.size() < tournamentSize)
        {
            selectedIndexes.add(MersenneTwisterFast.getInstance().nextInt(pop.getPopulationSize()));
        }

        ArrayList<Integer> indexes = new ArrayList<Integer>();

        int bob;

        int sue;

        int winner;

        int loser;
        while (selectedIndexes.size() != 1)
        {
            indexes.clear();
            indexes.addAll(selectedIndexes);

            int i = 0;
            while (1 < indexes.size() - i)
            {
                bob = indexes.get(i);
                sue = indexes.get(i + 1);

                if (pop.getIndividual(bob).getFitness() < pop.getIndividual(sue).
                    getFitness())
                {
                    winner = bob;
                    loser = sue;
                }
                else
                {
                    winner = sue;
                    loser = bob;
                }

                //toss the dice, if < k, remove loser
                if (MersenneTwisterFast.getInstance().nextDouble() < kParam)
                {
                    selectedIndexes.remove(loser);
                }
                else
                {
                    selectedIndexes.remove(winner);
                }
                i += 2;
            }
        }

        if (selectedIndexes.size() != 1)
        {
            logger.error("Expected 1 individual returning from CascadingTournamentSelection, found: " +
                selectedIndexes.size());
            System.exit(1);
        }

        int finalWinner = -1;
        for (Integer i : selectedIndexes)
        {
            finalWinner = i;
        }

        return pop.getIndividual(finalWinner);
    }
}

