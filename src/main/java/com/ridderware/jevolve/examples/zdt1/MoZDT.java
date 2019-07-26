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
package com.ridderware.jevolve.examples.zdt1;

import com.ridderware.jevolve.EvaluationCountingGAIndividual;
import com.ridderware.jevolve.EvaluatorInterface;
import com.ridderware.jevolve.GAIndividual;
import com.ridderware.jevolve.Individual;
import com.ridderware.jevolve.Population;
import com.ridderware.jrandom.MersenneTwisterFast;


/**
 *  TBD Description of the Class
 */
public class MoZDT implements EvaluatorInterface
{
    private double noise_sigma = 0.;
    
    /**
     *  TBD
     *
     * @param  ind
     */
    public void evaluateConstraints(Individual ind)
    {
    }
    
    /**
     *  TBD
     *
     * @param  pop
     */
    public void preevaluate(Population pop)
    {
    }
    
    /**
     *  TBD
     *
     * @param  pop
     */
    public void postevaluate(Population pop)
    {
    }
    
    public void setNoiseSigma(double sigma)
    {
        this.noise_sigma = sigma;
    }
    
    /**
     *  TBD
     *
     * @param  ind
     */
    public void evaluateFitness(Individual ind)
    {
        GAIndividual<Double> ga_ind = (GAIndividual<Double>) ind;
        EvaluationCountingGAIndividual ec_ind = null;
        
        int num_evals = 10;
        if ( ga_ind instanceof EvaluationCountingGAIndividual )
        {
            ec_ind = (EvaluationCountingGAIndividual)ga_ind;
            num_evals = ec_ind.getAssignedEvaluations();
        }
        
        double g = 0.;
        for ( int i = 1; i < ga_ind.getGenotypeSize(); i++ )
        {
            g += ga_ind.getValue(i);
        }
        g = 1. + 9.*g/(ga_ind.getGenotypeSize()-1);
        
        double f[] = new double[2];
        for ( int i = 0; i < num_evals; i++ )
        {
            f[0] += MersenneTwisterFast.getInstance().nextGaussian() *
                    noise_sigma + ga_ind.getValue(0);
            f[1] += MersenneTwisterFast.getInstance().nextGaussian() *
                    noise_sigma + g*(1.-Math.sqrt(ga_ind.getValue(0)/g));
        }
        
        f[0] /= num_evals;
        f[1] /= num_evals;
        
        if ( !(ga_ind instanceof EvaluationCountingGAIndividual) )
        {
            ga_ind.setFitness(0, f[0]);
            ga_ind.setFitness(1, f[1]);
        }
        else
        {
            ec_ind.accumulateFitness(f, num_evals);
        }
    }
}

