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

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.*;

/**
 * A problem that implements Noise Tolerant NSGA.  This differs from Dynamic NSGA in two key respects.
 * First, NT-NSGA combines the scores of genotypic duplicates and then eliminates all but one.  Second,
 * NT-NSGA works with a fixed pool of evaluations and allocates them to members of the population based on the
 * number of times they've been evaluated previously.  NT-NSGA gradually refines the accuracy of fitness evaluations
 * for surviving individuals by accumulating the approximated fitness scores from multiple generations.  This should
 * improve both the efficiency and accuracy of the algorithm.
 *
 * @author Jeff Ridder
 */
public class NTNSGAProblem extends Problem
{
    private final static Logger logger =
        LogManager.getLogger(NTNSGAProblem.class);

    private int new_individual_evals;

    private int total_evals_per_generation;

    /** Creates a new instance of NTNSGAProblem */
    public NTNSGAProblem()
    {
        super();
        this.total_evals_per_generation = 100;
        this.new_individual_evals = 1;
    }

    public int getNewIndividualEvals()
    {
        return new_individual_evals;
    }

    public int getTotalEvalsPerGeneration()
    {
        return total_evals_per_generation;
    }

    public void setNewIndividualEvals(int new_individual_evals)
    {
        this.new_individual_evals = new_individual_evals;
    }

    public void setTotalEvalsPerGeneration(int total_evals_per_generation)
    {
        this.total_evals_per_generation = total_evals_per_generation;
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

        //  Now set the evals per individual
        int evals_assigned = 0;

        double[] adjusted_evals =
            new double[getPopulation(0).getPopulationSize()];

        double sum_adjusted_evals = 0.;

        for (int i = 0; i < getPopulation(0).getPopulationSize(); i++)
        {
            Individual ind = getPopulation(0).getIndividual(i);
            assert (ind instanceof EvaluationCountingGAIndividual);

            EvaluationCountingGAIndividual ec_ind =
                (EvaluationCountingGAIndividual) ind;

            if (ec_ind.getTotalNumEvaluations() == 0)
            {
                ec_ind.setAssignedEvaluations(this.new_individual_evals);
                evals_assigned += this.new_individual_evals;
                adjusted_evals[i] = 0.;
            }
            else
            {
                adjusted_evals[i] = 1. / (1. +
                    Math.sqrt((double) ec_ind.getTotalNumEvaluations()));
                sum_adjusted_evals += adjusted_evals[i];
            }
        }

        //  Now go through again and assign the remaining evals based on adjusted_evals.
        int evals_to_assign = this.total_evals_per_generation - evals_assigned;
        for (int i = 0; i < getPopulation(0).getPopulationSize(); i++)
        {
            Individual ind = getPopulation(0).getIndividual(i);
            assert (ind instanceof EvaluationCountingGAIndividual);
            EvaluationCountingGAIndividual ec_ind =
                (EvaluationCountingGAIndividual) ind;

            if (adjusted_evals[i] > 0. && ec_ind.getTotalNumEvaluations() > 0)
            {
                int num_evals = (int) Math.round(evals_to_assign *
                    adjusted_evals[i] / sum_adjusted_evals);
                ec_ind.setAssignedEvaluations(num_evals);
            }
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

        //	First, remove and average all genotypic duplicates.
        List<Individual> removals = new ArrayList<Individual>();
        List<Individual> duplicates = new ArrayList<Individual>();
        for (Individual test_ind : getPopulation(0).getIndividuals())
        {
            if (!removals.contains(test_ind))
            {
                assert (test_ind instanceof EvaluationCountingGAIndividual);
                EvaluationCountingGAIndividual ind =
                    (EvaluationCountingGAIndividual) test_ind;

                duplicates.clear();
                for (Individual dup_ind : getPopulation(0).getIndividuals())
                {
                    if (dup_ind != test_ind &&
                        test_ind.genotypeDistance(dup_ind) == 0.)
                    {
                        assert (dup_ind instanceof EvaluationCountingGAIndividual);
                        EvaluationCountingGAIndividual dup =
                            (EvaluationCountingGAIndividual) dup_ind;

                        duplicates.add(dup_ind);

                        int num_objectives = test_ind.getNumObjectives();
                        double fitness[] = new double[num_objectives];
                        for (int i = 0; i < num_objectives; i++)
                        {
                            fitness[i] = dup_ind.getFitness(i);
                        }
                        ind.accumulateFitness(fitness,
                            dup.getTotalNumEvaluations());
                    }
                }

                removals.addAll(duplicates);
            }
        }

        logger.debug("Removing " + removals.size() + " individuals");

        for (Individual rem : removals)
        {
            getPopulation(0).removeIndividual(rem);
        }

        logger.debug("Population size is now " + getPopulation(0).
            getPopulationSize());

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
