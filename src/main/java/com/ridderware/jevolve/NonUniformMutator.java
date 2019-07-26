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

import com.ridderware.jrandom.MersenneTwisterFast;

/**
 * Michalewicz' nonuniform mutator.
 *
 * @param E gene type
 * @author Jeff Ridder
 */
public class NonUniformMutator<E> extends Mutator
{
    private double exponent = 1.;

    /**
     *  Constructor for the NonUniformMutator object
     *
     * @param  exponent nonuniform mutation exponent.
     */
    public NonUniformMutator(double exponent)
    {
        this.exponent = exponent;
    }

    /**
     *  Return the nonuniform mutation exponent.
     *
     * @return    nonuniform mutation exponent.
     */
    public double getExponent()
    {
        return this.exponent;
    }

    /**
     *  Mutates the individual.
     *
     * @param  ind individual to be mutated.
     */
    public void mutate(Individual ind)
    {
        if (ind.getProbMutation() > 0)
        {
            GAIndividual<E> ga_ind = (GAIndividual<E>) ind;

            for (int i = 0; i < ga_ind.getGenotype().size(); i++)
            {
                if (MersenneTwisterFast.getInstance().nextDouble() <=
                    ind.getProbMutation())
                {
                    ga_ind.setEvaluated(false);

                    double nonuniform = 1.;

                    if (ga_ind.getPopulation() != null)
                    {
                        double cur_gen = ga_ind.getPopulation().getProblem().
                            getStepper().getCurrentGeneration();
                        double max_gen = ga_ind.getPopulation().getProblem().
                            getStepper().getMaxGenerations();

                        nonuniform = MersenneTwisterFast.getInstance().
                            nextDouble() * Math.pow(1. - cur_gen / max_gen,
                            exponent);

                    }

                    ga_ind.setValue(i, ga_ind.getGenome().get(i).
                        randomUniformValue(ga_ind.getValue(i), nonuniform));

                }
            }
        }
    }
}

