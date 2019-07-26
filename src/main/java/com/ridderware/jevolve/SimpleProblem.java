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

/**
 *  A simple problem is one that uses a single population, evaluator,
 *  initializer, and breeder. This implements simple generational
 *  evolution.
 *
 * @author Jeff Ridder
 */
public class SimpleProblem extends Problem
{
    /**
     *  Constructor for the SimpleProblem object
     */
    public SimpleProblem()
    {
        super();
    }

    /**
     *  Initializes all populations contained by the problem.
     */
    public void initialize()
    {
        if (!getPopulations().isEmpty() && !getInitializers().isEmpty())
        {
            getInitializer(0).initialize(getPopulation(0));

            //	Now create the shadow population
            addPopulation(getPopulation(0).clone());
        }
    }

    /**
     *  Breeds all populations contained by the problem.
     */
    public void breed()
    {
        getBreeder(0).breedNextGeneration(getPopulation(0), getPopulation(1));

        getPopulations().add(0, getPopulations().remove(1));
        getPopulation(0).clearElites();
        getPopulation(0).addElites(getPopulation(1).getElites());
    }

    /**
     * Replaces individuals in the population.  This implementation enforces elitism.
     */
    public void replaceIndividuals()
    {
        if (getPopulation(0).getElitist())
        {
            getPopulation(0).processElites();
        }
    }

    /**
     * Preevaluates all populations contained by the problem.
     */
    public void preevaluate()
    {
        getEvaluator(0).preevaluate(getPopulation(0));
    }

    /**
     * Evaluates all populations contained by the problem.
     */
    public void evaluate()
    {
        getPopulation(0).evaluate(getEvaluator(0));

        if (getPopulation(0).getDone())
        {
            setDone(true);
        }
    }

    /**
     * Postevaluates all populations contained by the problem.
     */
    public void postevaluate()
    {
        getEvaluator(0).postevaluate(getPopulation(0));
    }

    /**
     * Writes statistics for the population.
     */
    public void writeStatistics()
    {
        if (getStatistics() != null)
        {
            getStatistics().outputGenerationalStats(getPopulation(0));
        }
    }
}

