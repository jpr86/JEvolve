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

import com.ridderware.jevolve.EvaluatorInterface;

/**
 * Marks evaluators as supporting fixed-budget evaluations, multiple
 * fitness samples per evaluation, and various levels of noise. 
 * 
 * @author Jason
 */
public interface INumEvaluationsAndNoiseSettableEvaluator extends 
        EvaluatorInterface, Cloneable
{
    /**
     * Sets the total number of evaluations that can be used before 
     * evolution is completed (fixed evaluation budget). 
     * @param totalNumEvals the total # of evaluations that may be used
     * @see #getTotalNumEvaluationsRemaining()
     */
    public void setTotalNumEvaluations(int totalNumEvals);
    
    /**
     * Returns the total number of evaluations remaining before 
     * evolution is completed (fixed number of evaluations). 
     * @return total # evaluations remaining
     * @see #setTotalNumEvaluations(int)
     */
    public int getTotalNumEvaluationsRemaining();
    
    /**
     * Gets the number of fitness samples being used per evaluation. 
     * The actual fitness(es) assigned to an individual are based on the 
     * sampled fitness values.
     * @see #setNumSamplesPerEval(int)
     * @return the number of samples per evaluation
     */
    public int getNumSamplesPerEval();

    /**
     * Sets the number of fitness samples to use per evaluation. In other words,
     * the number of times each individual will be sampled per evaluation.
     * @see #getNumSamplesPerEval()
     * @param numEvaluations the number of samples to use to calculate fitness
     */
    public void setNumSamplesPerEval(int numEvaluations);

    /**
     * Noise account for both sigma (standard deviation) as well as any 
     * required scaling factors to take into account fitness
     * values that are not contained solely between 0 and 1. For example,
     * if fitness values are expected between 0 and 100 a scaling factor of 50
     * may be appropriate which would make the appropriate noise level 
     * {@code 50 * sigma}.
     * @see #getNoiseLevels
     * @param noiseLevels the standard deviation times the fitness scaling 
     * factor where each index represents it's respective fitness's noise level
     */
    public void setNoiseLevels(double[] noiseLevels);

    /**
     * Returns the noise level which accounts for both sigma and the fitness 
     * noiseLevels on a fitness objective by fitness objective basis
     * @see #setNoiseLevels(double[])
     * @return the current noise level for all fitness objectives
     */
    public double[] getNoiseLevels();
}
