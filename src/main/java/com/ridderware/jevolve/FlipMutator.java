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
 * A basic flip mutator.  Flip mutation checks each gene for mutation (using
 * the individual's probability of mutation), and for mutated genes uses a 
 * uniform distribution to randomly select a new value without regard to the 
 * current value of the gene.
 *
 * @param E gene type
 * @author Jeff Ridder
 */
public class FlipMutator<E> extends Mutator
{
    /**
     * Mutates the individual using flip mutation.
     *
     * @param  ind individual to be mutated.
     */
    public void mutate(Individual ind)
    {
        GAIndividual<E> ga_ind = (GAIndividual<E>) ind;

        for (int i = 0; i < ga_ind.getGenome().size(); i++)
        {
            if (MersenneTwisterFast.getInstance().nextDouble() <=
                ind.getProbMutation())
            {
                ga_ind.setEvaluated(false);
                ga_ind.setValue(i,
                    ga_ind.getGenome().get(i).randomUniformValue());
            }
        }
    }
}

