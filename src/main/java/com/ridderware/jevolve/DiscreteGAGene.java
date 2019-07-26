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

import java.util.ArrayList;

/**
 *  An abstract class for discretely valued genes.
 *
 * @param E gene type.
 * @author Jeff Ridder
 */
public abstract class DiscreteGAGene<E> extends GAGene<E>
{
    private ArrayList<E> alleles = new ArrayList<E>();

    /**
     *  Constructor for the DiscreteGAGene object
     */
    public DiscreteGAGene()
    {
    }

    /**
     *  Constructor for the DiscreteGAGene object
     *
     * @param  lower_bound lower bound
     * @param  upper_bound upper bound
     */
    public DiscreteGAGene(E lower_bound, E upper_bound)
    {
        super(lower_bound, upper_bound);
    }

    /**
     *  Returns the allele value at the specified index.
     *
     * @param  index array index.
     * @return  allele value.
     */
    public E getAllele(int index)
    {
        return alleles.get(index);
    }

    /**
     *  Returns the number of alleles in the gene.
     *
     * @return    number of alleles.
     */
    public int getNumAlleles()
    {
        return alleles.size();
    }

    /**
     * Adds an allele value to the genome.
     *
     * @param  allele allele value.
     */
    public void addAllele(E allele)
    {
        alleles.add(allele);
    }
}
