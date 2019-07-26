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

/**
 *  A breeder is responsible for producing the next generation of individuals
 *  from a parent population. The breeder performs all aspects of replacement,
 *  selection, recombination, and mutation. Typically, a parent population will
 *  be used for input, with the resulting children for the next generation as
 *  output (generational evolution). However, steady state evolution would use
 *  only a single population, replacing some of its individuals with newly
 *  produced children and ignoring the child_pop parameter.
 *
 * @author Jeff Ridder
 */
public abstract class Breeder
{
    private Selector selector;

    private Recombinator recombinator;

    private Mutator mutator;

    /**
     * Creates a new instance of Breeder.
     */
    public Breeder()
    {
        this.selector = null;
        this.recombinator = null;
        this.mutator = null;
    }

    /**
     * Creates a new instance of Breeder.
     *
     * @param  selector selection operator object.
     * @param  recombinator recombination operator object.
     * @param  mutator mutation operator object.
     */
    public Breeder(Selector selector, Recombinator recombinator,
        Mutator mutator)
    {
        this.selector = selector;
        this.recombinator = recombinator;
        this.mutator = mutator;
    }

    /**
     * Sets the selector attribute of the Breeder object
     *
     * @param  selector selection operator.
     */
    public void setSelector(Selector selector)
    {
        this.selector = selector;
    }

    /**
     * Sets the recombinator attribute of the Breeder object
     *
     * @param  recombinator recombination operator.
     */
    public void setRecombinator(Recombinator recombinator)
    {
        this.recombinator = recombinator;
    }

    /**
     * Sets the mutator attribute of the Breeder object
     *
     * @param  mutator mutation operator.
     */
    public void setMutator(Mutator mutator)
    {
        this.mutator = mutator;
    }

    /**
     * Gets the selector attribute of the Breeder object
     *
     * @return  selection operator.
     */
    public Selector getSelector()
    {
        return this.selector;
    }

    /**
     * Gets the recombinator attribute of the Breeder object
     *
     * @return    recombination operator.
     */
    public Recombinator getRecombinator()
    {
        return this.recombinator;
    }

    /**
     * Gets the mutator attribute of the Breeder object
     *
     * @return    mutation operator.
     */
    public Mutator getMutator()
    {
        return this.mutator;
    }

    /**
     * Breeds the next generation of individuals using the selection, recombination,
     * and mutation operators.
     *
     * @param  parent_pop parent population.
     * @param  child_pop child population.
     */
    public abstract void breedNextGeneration(Population parent_pop,
        Population child_pop);
}

