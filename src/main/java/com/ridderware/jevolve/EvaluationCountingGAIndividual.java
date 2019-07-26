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

/**
 * A GAIndividual with attributes for keeping track of the number of times it has been
 * evaluated.  We expect this to be useful for noisy problems where the number of
 * evaluations assigned to an individual is some function of the number of times it
 * has already been evaluated relative to the rest of the population.
 *
 * @param E gene type.
 * @author Jeff Ridder
 */
public class EvaluationCountingGAIndividual<E> extends GAIndividual<E>
{
    private int total_num_evaluations;

    private int assigned_evaluations;

    /** Creates a new instance of EvaluationCountingGAIndividual */
    public EvaluationCountingGAIndividual()
    {
        super();

        this.total_num_evaluations = 0;
        this.assigned_evaluations = 0;
    }

    /**
     * Constructor for the GAIndividual object
     *
     * @param numObjectives number of objectives
     */
    public EvaluationCountingGAIndividual(int numObjectives)
    {
        super(numObjectives);

        this.total_num_evaluations = 0;
        this.assigned_evaluations = 0;
    }

    public int getAssignedEvaluations()
    {
        return assigned_evaluations;
    }

    public int getTotalNumEvaluations()
    {
        return total_num_evaluations;
    }

    public void setAssignedEvaluations(int assigned_evaluations)
    {
        this.assigned_evaluations = assigned_evaluations;
    }

    public void addTotalNumEvaluations(int num_evaluations)
    {
        this.total_num_evaluations += num_evaluations;
    }

    public void resetTotalNumEvaluations()
    {
        this.total_num_evaluations = 0;
    }

    public void accumulateFitness(double fitness, int num_evals)
    {
        double f = (this.getFitness() * this.total_num_evaluations + fitness *
            num_evals) / (this.total_num_evaluations + num_evals);
        this.setFitness(f);
        this.total_num_evaluations += num_evals;
    }

    public void accumulateFitness(double[] fitness, int num_evals)
    {
        for (int i = 0; i < this.getNumObjectives(); i++)
        {
            double f = (this.getFitness(i) * this.total_num_evaluations +
                fitness[i] * num_evals) / (this.total_num_evaluations +
                num_evals);
            this.setFitness(i, f);
        }
        this.total_num_evaluations += num_evals;
    }

    /**
     * Clones the individual.  This is very useful during breeding.
     *
     * @return a clone of the individual.
     */
    @Override
    public EvaluationCountingGAIndividual<E> clone()
    {
        EvaluationCountingGAIndividual<E> obj =
            (EvaluationCountingGAIndividual<E>) super.clone();

        obj.assigned_evaluations = this.assigned_evaluations;
        obj.total_num_evaluations = this.total_num_evaluations;

        return obj;
    }

    /**
     * Deep copies the individual.  Deep copies ensure that the individual
     * has its own copies of "deep" attributes, and not just sharing with somebody else.
     *
     * @param  obj individual to be deep copied.
     */
    @Override
    public void deepCopy(Individual obj)
    {
        super.deepCopy(obj);

        EvaluationCountingGAIndividual<E> ga_obj =
            (EvaluationCountingGAIndividual<E>) obj;

        this.assigned_evaluations = ga_obj.assigned_evaluations;

        this.total_num_evaluations = ga_obj.total_num_evaluations;
    }
}
