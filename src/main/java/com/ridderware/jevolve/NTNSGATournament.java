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

/**
 *
 * @author Jeff Ridder
 */
public class NTNSGATournament extends Selector
{

    /** Creates a new instance of NTNSGATournament */
    public NTNSGATournament()
    {
    }

    /**
     *  A variant of Deb's multi-objective tournament selection (NSGA-II), modified
     *  for NT-NSGA.
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

        //  Asserts because this only works for evaluation-counting
        assert (cand1 instanceof EvaluationCountingGAIndividual);
        assert (cand2 instanceof EvaluationCountingGAIndividual);

        EvaluationCountingGAIndividual c1 =
            (EvaluationCountingGAIndividual) cand1;
        EvaluationCountingGAIndividual c2 =
            (EvaluationCountingGAIndividual) cand2;
//        if ( MersenneTwisterFast.getInstance().nextDouble() < 0.5 )
//        {
//            if ( c1.getTotalNumEvaluations() >= c2.getTotalNumEvaluations() )
//            {
//                return c1;
//            }
//            else
//            {
//                return c2;
//            }
//        }
//        else
        {
            double adjusted1 = c1.getParetoRank();
            double adjusted2 = c2.getParetoRank();
            if (adjusted1 < adjusted2)
            {
                return c1;
            }
            else if (adjusted2 < adjusted1)
            {
                return c2;
            }
            else
            {
                if (c1.getCrowdingDistance() > c2.getCrowdingDistance())
                {
                    return c1;
                }
                else
                {
                    return c2;
                }
            }
        }
    }
}
