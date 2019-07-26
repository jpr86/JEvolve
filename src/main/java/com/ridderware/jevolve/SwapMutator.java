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

import com.ridderware.jrandom.MersenneTwisterFast;

/**
 * An implementation of swap mutation.  Swap mutation swaps the values of two randomly selected
 * values in the genotype.  Note that this may only be applied to genomes in which all genes are
 * the same type and have the same range of values.
 *
 * @param E gene type.
 * @author Jeff Ridder
 */
public class SwapMutator<E> extends Mutator
{
    public void mutate(Individual ind)
    {
        if (MersenneTwisterFast.getInstance().nextDouble() <=
            ind.getProbMutation() &&
            ind.getGenotypeSize() > 1)
        {
            GAIndividual<E> ga_ind = (GAIndividual<E>) ind;

            //  Choose two randome indices to swap
            int p1 = (int) (MersenneTwisterFast.getInstance().nextDouble() *
                ga_ind.getGenotypeSize());
            int p2 = p1;
            while (p1 == p2)
            {
                p2 = (int) (MersenneTwisterFast.getInstance().nextDouble() *
                    ga_ind.getGenotypeSize());
            }

            E p1_val = ga_ind.getGenotype().get(p1);
            E p2_val = ga_ind.getGenotype().get(p2);
            ga_ind.setValue(p1, p2_val);
            ga_ind.setValue(p2, p1_val);

            ga_ind.setEvaluated(false);
        }
    }
}
