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
 * Constrained tournament selection operator.  This operator prefers feasible
 * individuals over infeasible.  As it steps through the randomly selected tournament
 * pool, if the currentWinner (starting with the first individual defaulted as
 * the current Winner) has a lower fitness than the currentCompetitor and the
 * currentCompetitor gets lucky (by rolling dice < kParam), then the
 * currentCompetitor becomes the current winner.  If k = 1.0 this has the effect
 * of returning the highest fitness feasible individual from the tournament pool.
 *
 * @author Jeff Ridder
 */
public class ConstrainedTournamentSelection extends Selector
{
    private final static Logger logger =
        LogManager.getLogger(ConstrainedTournamentSelection.class);

    private int tournamentSize = 2;

    private double kParam = 1.;

    /**
     * Creates a new instance of ConstrainedTournamentSelection.
     */
    public ConstrainedTournamentSelection()
    {
    }

    /**
     * Creates a new instance of ConstrainedTournamentSelection.
     *
     * @param  tournamentSize  an int, for classic tournament selection, use 2.
     * @param  kParam          a double, for classic tournament selection use 1.0.
     */
    public ConstrainedTournamentSelection(int tournamentSize, double kParam)
    {
        this.tournamentSize = tournamentSize;
        this.kParam = kParam;
    }

    /**
     * Sets the size of the tournament.
     *
     * @param  tournamentSize  an int
     */
    public void setTournamentSize(int tournamentSize)
    {
        this.tournamentSize = tournamentSize;
    }

    /**
     * Sets the k-parameter.
     *
     * @param  kParam  a double between 0 and 1.
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
     * Returns the k-parameter.
     *
     * @return  k-parameter.
     */
    public double getkParam()
    {
        return kParam;
    }

    /**
     * Performs constrained tournament selection.
     *
     * @param  pop population from which to select an individual.
     * @return  selected individual.
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
            double perror = pop.getIndividual(person).getConstraintError();
            double pfitness = pop.getIndividual(person).getFitness();
            double werror =
                pop.getIndividual(currentWinner).getConstraintError();
            double wfitness = pop.getIndividual(currentWinner).getFitness();

            if (perror < werror &&
                MersenneTwisterFast.getInstance().nextDouble() < kParam)
            {
                currentWinner = person;
            }
            else if (perror == werror)
            {
                if (pfitness < wfitness && MersenneTwisterFast.getInstance().
                    nextDouble() < kParam)
                {
                    currentWinner = person;
                }
            }
        }

        return pop.getIndividual(currentWinner);
    }
}
