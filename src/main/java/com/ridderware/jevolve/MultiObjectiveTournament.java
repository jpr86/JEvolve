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
import org.apache.logging.log4j.*;

/**
 * Deb's multi-objective tournament selection from NSGA-II.  The tournament winner
 * will be the individual with the lower Pareto rank (i.e., better) or, if equal,
 * the individual with the greater crowding distance (to increase diversity).
 *
 * @author Jeff Ridder
 */
public class MultiObjectiveTournament extends Selector
{
    private final static Logger logger =
        LogManager.getLogger(MultiObjectiveTournament.class);

    /**
     *  Constructor for the MultiObjectiveTournament object
     */
    public MultiObjectiveTournament()
    {
    }

    /**
     *  Deb's multi-objective tournament selection (NSGA-II).
     *
     * @param  pop population from which to select
     * @return   selected individual
     */
    public Individual select(Population pop)
    {
        Individual cand1 = pop.getIndividual(MersenneTwisterFast.getInstance().
            nextInt(pop.getIndividuals().size()));

        Individual cand2 = null;
        if (pop.getIndividuals().size() <= 1)
        {
            cand2 = cand1;
        }
        else
        {

            do
            {
                cand2 = pop.getIndividual(MersenneTwisterFast.getInstance().
                    nextInt(pop.getIndividuals().size()));
            }
            while (cand2 == cand1);
        }

        if (cand1.getParetoRank() < cand2.getParetoRank())
        {
            return cand1;
        }
        else if (cand2.getParetoRank() < cand1.getParetoRank())
        {
            return cand2;
        }
        else
        {
            if (cand1.getCrowdingDistance() > cand2.getCrowdingDistance())
            {
                return cand1;
            }
            else
            {
                return cand2;
            }
        }
    }
}
