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
 *  A problem that implements deterministic crowding during the
 *  replacement step.  This is designed to be used with the
 *  DeterministicCrowdingBreeder class.
 *
 * @author Jeff Ridder
 */
public class DeterministicCrowdingProblem extends Problem
{
    /**
     * Creates a new instance of DeterministicCrowdingProblem.
     */
    public DeterministicCrowdingProblem()
    {
        super();
    }

    /**
     *  Method called by the stepper to initialize all pops contained by the
     *  problem.
     */
    public void initialize()
    {
        if (!getPopulations().isEmpty())
        {
            getInitializer(0).initialize(getPopulation(0));

            //	Now create the shadow population
            addPopulation(getPopulation(0).clone());
        }
    }

    /**
     * Method called by the stepper to breed all pops contained by the problem.
     */
    public void breed()
    {
        getBreeder(0).breedNextGeneration(getPopulation(0), getPopulation(1));

        getPopulations().add(0, getPopulations().remove(1));
    }

    /**
     * Method called by the stepper to replace individuals in the population.
     * This class implements deterministic crowding.
     */
    public void replaceIndividuals()
    {
        Individual mom;
        Individual dad;
        Individual boy;
        Individual girl;

        Population parent_pop = getPopulation(1);
        Population child_pop = getPopulation(0);
        if (getStepper().getCurrentGeneration() > 0.)
        {
            for (int i = 0; i < child_pop.getPopulationSize() - 1; i += 2)
            {
                mom = parent_pop.getIndividual(i);
                dad = parent_pop.getIndividual(i + 1);

                boy = child_pop.getIndividual(i);
                girl = child_pop.getIndividual(i + 1);

                if (mom.genotypeDistance(boy) + dad.genotypeDistance(girl) <=
                    mom.genotypeDistance(girl) + dad.genotypeDistance(boy))
                {
                    if (mom.getFitness() <= boy.getFitness())
                    {
                        boy.deepCopy(mom);
                    }

                    if (dad.getFitness() <= girl.getFitness())
                    {
                        girl.deepCopy(dad);
                    }
                }
                else
                {
                    if (dad.getFitness() <= boy.getFitness())
                    {
                        boy.deepCopy(dad);
                    }

                    if (mom.getFitness() <= girl.getFitness())
                    {
                        girl.deepCopy(mom);
                    }
                }
            }
        }
    }

    /**
     *  Method called by the stepper to pre-evaluate all pops contained by the
     *  problem.
     */
    public void preevaluate()
    {
        getEvaluator(0).preevaluate(getPopulation(0));
    }

    /**
     *  Method called by the stepper to evaluate the population.
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
     *  Method called by the stepper to post-evaluate all pops.
     */
    public void postevaluate()
    {
        getEvaluator(0).postevaluate(getPopulation(0));
    }

    /**
     *  Writes the statistics for the population.
     */
    public void writeStatistics()
    {
        if (getStatistics() != null)
        {
            getStatistics().outputGenerationalStats(getPopulation(0));
        }
    }
}

