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

import org.apache.logging.log4j.*;

/**
 * A problem that implements Dynamic NSGA.  See Ridder & HandUber from GECCO '05.
 * @author Jeff Ridder
 */
public class DynamicNSGAProblem extends Problem
{
    private final static Logger logger =
        LogManager.getLogger(DynamicNSGAProblem.class);

    /**
     *  Constructor for the DynamicNSGAProblem object
     */
    public DynamicNSGAProblem()
    {
        super();
    }

    /**
     *  Initializes all populations contained by the problem.
     */
    public void initialize()
    {
        if (!getPopulations().isEmpty())
        {
            getInitializer(0).initialize(getPopulation(0));
        }
    }

    /**
     *  Breeds all populations contained by the problem.
     */
    public void breed()
    {
        if (!getBreeders().isEmpty())
        {
            getBreeder(0).breedNextGeneration(getPopulation(0), null);
        }
    }

    /**
     *  Performs replacement of individuals.  This method does nothing for this problem.
     */
    public void replaceIndividuals()
    {
    }

    /**
     *  Preevaluates all populations contained by the problem.
     */
    public void preevaluate()
    {
        if (!getEvaluators().isEmpty())
        {
            getEvaluator(0).preevaluate(getPopulation(0));
        }
    }

    /**
     *  Evaluates all populations contained by the problem.
     */
    public void evaluate()
    {
        if (!getEvaluators().isEmpty())
        {
            getPopulation(0).setForceEvaluation(true);
            getPopulation(0).evaluate(getEvaluator(0));
        }
    }

    /**
     *  Postevaluates all populations contained by the problem.
     */
    public void postevaluate()
    {
        if (!getEvaluators().isEmpty())
        {
            getEvaluator(0).postevaluate(getPopulation(0));
        }

        MultiObjectivePopulation pop =
            (MultiObjectivePopulation) getPopulation(0);
        pop.sortParetoRank();
    }

    /**
     * Writes the evolution statistics.
     */
    public void writeStatistics()
    {
        if (getStatistics() != null)
        {
            getStatistics().outputGenerationalStats(getPopulation(0));
        }
    }
}

