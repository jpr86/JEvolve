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
import com.ridderware.jrandom.MersenneTwisterFast;

/**
 * Some utilities to convert noiseless fitness evaluations to noisy ones.
 * This of course assumes that noiseless fitness values can be obtained.
 * 
 * @author Jason
 */
public class NoisyEvaluationUtils
{

    /**
     * Evaluates {@code ga_ind} using the {@code ga_ind.addEvaluation} 
     * method. This call is made {@code numEvals} times at the given
     * {@code noiseLevels} and {@code noiselessFitnesses}.
     * @param numEvals the number of evaluations to perform
     * @param noiseLevels the noise level associated with each fitness term
     * @param noiselessFitnesses the true fitness of the individual, to which
     * noise will be added
     * @param ga_ind the individual on which to call {@code addEvaluation}
     */
    public final static void DoNoisyEvaluations(
            final int numEvals,
            final double[] noiseLevels,
            final double[] noiselessFitnesses,
            final ILikeEvaluations<Double> ga_ind)
    {
//        ga_ind.setNoiselessFitness(noiselessFitnesses);
        final int numObjectives = ga_ind.getNumObjectives();
        for (int e = 0; e < numEvals; e++)
        {
            final double[] mySampleOfFit = new double[numObjectives];
            System.arraycopy(noiselessFitnesses, 0, mySampleOfFit, 0, numObjectives);
            for (int f = 0; f < numObjectives; f++)
            {
                final double ran = MersenneTwisterFast.getInstance().nextGaussian();
                mySampleOfFit[f] += (ran * noiseLevels[f]);
                assert (!Double.isInfinite(mySampleOfFit[f]) && !Double.isNaN(mySampleOfFit[f]));
            }

            Double[] mySampleOfFitWrapper = new Double[mySampleOfFit.length];
            for(int i=0; i < mySampleOfFit.length; i++)
            {mySampleOfFitWrapper[i] = mySampleOfFit[i];}
            
            ga_ind.addEvaluation(mySampleOfFitWrapper);
        }
//        ga_ind.markLastEval();
    }

    /**
     * Evaluates {@code ga_ind} using the {@code ga_ind.setFitness} where
     * fitness is determined by the average fitness of that individual over
     * {@code numEvals} samples with {@code noiseLevel}.
     * @param numEvals the number of evaluations over which to average fitness
     * @param noiseLevels the noise level associated with each fitness term
     * @param noiselessFitnesses the true fitness of the individual, to which
     * noise will be added
     * @param ga_ind the individual on which to call {@code setFitness}
     */
    public final static void DoNoisyEvaluations(
            final int numEvals,
            final double[] noiseLevels,
            final double[] noiselessFitnesses,
            final GAIndividual<Double> ga_ind)
    {
        assert(noiseLevels.length == noiselessFitnesses.length);
        assert(noiseLevels.length == ga_ind.getGenotypeSize());
        final int numObjectives = ga_ind.getNumObjectives();
        final double[] mySampleOfFit = new double[numObjectives];
        System.arraycopy(noiselessFitnesses, 0, mySampleOfFit, 0, numObjectives);
        for (int e = 0; e < numEvals; e++)
        {
            for (int f = 0; f < numObjectives; f++)
            {
                final double ran = MersenneTwisterFast.getInstance().nextGaussian();
                mySampleOfFit[f] += (ran * noiseLevels[f]);
                assert (!Double.isInfinite(mySampleOfFit[f]) && !Double.isNaN(mySampleOfFit[f]));
            }
        }
        
        for (int f = 0; f < numObjectives; f++)
        {
            ga_ind.setFitness(f, mySampleOfFit[f]/numEvals);
        }
    }
}
