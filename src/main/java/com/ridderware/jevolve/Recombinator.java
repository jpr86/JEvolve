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
 * Abstract base class for recombination operators.
 *
 * @author Jeff Ridder
 */
public abstract class Recombinator
{
    /**
     * Performs recombination of parents to produce children.
     *
     * @param  parent1 dad.
     * @param  parent2 mom.
     * @param  child1 son.
     * @param  child2 daughter.
     */
    public abstract void recombine(Individual parent1, Individual parent2,
        Individual child1, Individual child2);
}
