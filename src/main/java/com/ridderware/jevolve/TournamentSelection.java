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
 * A basic tournament selection operator.
 *
 * @author Jeff Ridder
 */
public class TournamentSelection extends Selector
{
    private final static Logger logger =
        LogManager.getLogger(TournamentSelection.class);

    private int tournamentSize = 2;

    private double kParam = 1.;

    /**
     *  Constructor for the TournamentSelection object
     */
    public TournamentSelection()
    {
    }

    /**
     *  Constructor for the TournamentSelection object
     *
     * @param  tournamentSize  an int, for classic tournament selection, use 2.
     * @param  kParam          a double, for classic tournament selection use 1.0.
     */
    public TournamentSelection(int tournamentSize, double kParam)
    {
        this.tournamentSize = tournamentSize;
        this.kParam = kParam;
    }

    /**
     * Sets the tournament size.
     *
     * @param  tournamentSize tournament size.
     */
    public void setTournamentSize(int tournamentSize)
    {
        this.tournamentSize = tournamentSize;
    }

    /**
     * Sets the probability that the individual with the best
     * fitness will be selected as the winner of the tournament.
     *
     * @param  kParam probability of best individual winning.
     */
    public void setkParam(double kParam)
    {
        this.kParam = kParam;
    }

    /**
     * Returns the tournament size.
     *
     * @return  tournament size.
     */
    public int getTournamentSize()
    {
        return tournamentSize;
    }

    /**
     * Returns the probability that the best individual will be selected as the 
     * winner of the tournament.
     *
     * @return  probability of the best individual winning.
     */
    public double getkParam()
    {
        return kParam;
    }

    /**
     *  This implementation of tournament selection goes through the randomly
     *  chosen individuals in the tournament. If the currentWinner (starting with
     *  the first person defaulted as the current Winner) has a lower fitness than
     *  the currentCompetitor and the currentCompetitor gets lucky (by rolling
     *  dice < kParam), then the currentCompetitor becomes the current winner. If
     *  k = 1.0 this has the effect of returning the highest fitness individual
     *  from the tournament pool.
     *
     * @param  pop population from which to select.
     * @return  selected individual
     */
    public Individual select(Population pop)
    {
        if (tournamentSize > pop.getPopulationSize())
        {
            tournamentSize = pop.getPopulationSize();
            logger.info("\nNote: Tournament Size is Greater than Population Size. \n\t==> Setting Tournament Size to Population Size\n");
        }

        HashSet<Integer> selectedIndexes = new HashSet<Integer>();

        int selectMe = -1;
        while (selectedIndexes.size() < tournamentSize)
        {
            selectMe =
                MersenneTwisterFast.getInstance().nextInt(pop.getPopulationSize());
            selectedIndexes.add(selectMe);
        }

        int currentWinner = selectMe;
        for (Integer person : selectedIndexes)
        {
            if ((pop.getIndividual(person).getFitness() < pop.getIndividual(currentWinner).
                getFitness()) &&
                (MersenneTwisterFast.getInstance().nextDouble() < kParam))
            {
                currentWinner = person;
            }
        }

        return pop.getIndividual(currentWinner);
    }
}
