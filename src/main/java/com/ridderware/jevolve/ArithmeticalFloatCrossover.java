/* %%
 * 
 * JEvolve
 *
 * Copyright 2011 Jeff Ridder
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
import org.apache.logging.log4j.*;

/**
 *  Michalewicz' Arithmetical Crossover for floats.
 *
 * @author Jeff Ridder
 */
public
    class ArithmeticalFloatCrossover extends Recombinator
{
    private final static
        Logger logger =
        LogManager.getLogger(ArithmeticalFloatCrossover.class);

    /**
     * Performs recombination of parents to produce children using arithmetical
     * crossover.
     *
     * @param  parent1 dad.
     * @param  parent2 mom.
     * @param  child1 son.
     * @param  child2 daughter.
     */
    @Override
    public
        void recombine(Individual parent1, Individual parent2,
        Individual child1, Individual child2)
    {
        if (MersenneTwisterFast.getInstance().nextDouble()
            > parent2.getProbRecombination())
        {
            if (child1 != null)
            {
                child1.deepCopy(parent1);
            }
            if (child2 != null)
            {
                child2.deepCopy(parent2);
            }
        }
        else
        {
            GAIndividual<Float> dad = (GAIndividual<Float>) parent1;
            GAIndividual<Float> mom = (GAIndividual<Float>) parent2;
            GAIndividual<Float> boy = (GAIndividual<Float>) child1;
            GAIndividual<Float> girl = (GAIndividual<Float>) child2;

            if (dad == null || mom == null)
            {
                System.err.println("Parent individuals not configured for Floats.");
                System.exit(1);
            }

            boy.setEvaluated(false);
            girl.setEvaluated(false);

            if (!(boy == null))
            {
                boy.getGenotype().clear();
            }
            if (!(girl == null))
            {
                girl.getGenotype().clear();
            }

            float a = MersenneTwisterFast.getInstance().nextFloat();

            for (int i = 0; i < mom.getGenotype().size(); i++)
            {
                Float momValue = (Float) mom.getValue(i);
                Float dadValue = (Float) dad.getValue(i);

                Float c1;

                Float c2;

                c1 = a * momValue + (1.F - a) * dadValue;
                c2 = a * dadValue + (1.F - a) * momValue;

                boy.getGenotype().add(c1);
                girl.getGenotype().add(c2);
            }
        }
    }
}
