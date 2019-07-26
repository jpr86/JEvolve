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
package com.ridderware.jevolve.examples.zdts;

import com.ridderware.jevolve.GAIndividual;
import com.ridderware.jevolve.Individual;
import com.ridderware.jevolve.Population;
import java.util.List;

/**
 * Provides a default implementation of 
 * {@code INumEvaluationsAndNoiseSettableEvaluator}, which allows subclasses
 * to only have to implement {@code getNoiselessFitness(List)}.
 * @author Jason
 */
public abstract class AbstractNoisyNumEvaluationProblem implements 
        INumEvaluationsAndNoiseSettableEvaluator, Cloneable
{

    private double[] noiseLevels = null;
    private int numSamplesPerEval = 1,  numEvaluationsRemaining = 1;

    
    protected abstract double[] getNoiselessFitness(List<Double> ga_ind);

    public final int getNumSamplesPerEval()
    {
        return this.numSamplesPerEval;
    }

    public final void setNumSamplesPerEval(int numEvaluations)
    {
        this.numSamplesPerEval = numEvaluations;
    }

    public final void setNoiseLevels(double[] noiseLevels)
    {
        this.noiseLevels = noiseLevels;
    }

    public final double[] getNoiseLevels()
    {
        return noiseLevels;
    }

    public final void setTotalNumEvaluations(int totalNumEvals)
    {
        numEvaluationsRemaining = totalNumEvals;
    }

    public final int getTotalNumEvaluationsRemaining()
    {
        return numEvaluationsRemaining;
    }

    /**
     *  TBD
     *
     * @param  ind
     */
    public final void evaluateConstraints(Individual ind)
    {
    }

    /**
     *  TBD
     *
     * @param  pop
     */
    public final void preevaluate(Population pop)
    {
    }

    /**
     *  TBD
     *
     * @param  pop
     */
    public final void postevaluate(Population pop)
    {
//        if (numEvaluationsRemaining <= 0)
//        {
//            pop.setDone(true);
//        }
    }

    /**
     *  TBD
     *
     * @param  ind
     */
    public final void evaluateFitness(Individual ind)
    {
        assert (ind.getFitnessArray().length == 2);
        GAIndividual<Double> ga_ind = (GAIndividual<Double>) ind;

        double[] noiselessFitness = getNoiselessFitness(ga_ind.getGenotype());

        if (ga_ind instanceof ILikeEvaluations)
        {
            NoisyEvaluationUtils.DoNoisyEvaluations(
                    numSamplesPerEval,
                    noiseLevels,
                    noiselessFitness,
                    ((ILikeEvaluations) ga_ind));
        }
        else
        {
            NoisyEvaluationUtils.DoNoisyEvaluations(
                    numSamplesPerEval,
                    noiseLevels,
                    noiselessFitness,
                    ga_ind);
        }

        numEvaluationsRemaining -= numSamplesPerEval;
        if (numEvaluationsRemaining <= 0)
        {
            ind.setSuccess(true);
        }
//        System.out.println("Evals remaining: "+ numEvaluationsRemaining);
    }

    @Override
    public Object clone()
    {
        try
        {
            AbstractNoisyNumEvaluationProblem supr = (AbstractNoisyNumEvaluationProblem) super.clone();
            supr.noiseLevels = this.noiseLevels.clone();
            supr.numEvaluationsRemaining = this.numEvaluationsRemaining;
            supr.numSamplesPerEval = this.numSamplesPerEval;
            return supr;
        }
        catch (CloneNotSupportedException ex)
        {
            return null;
        }
    }
}
