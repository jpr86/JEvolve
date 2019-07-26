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

/**
 * A temporary interface that I think some of this can replace some of the 
 * old {@code setFitness} functionality. It is very similar to the 
 * {@code EvaluationCountingIndividual} and it lets an individual know 
 * that it was evaluated and allows the individual to do what it pleases 
 * with that information. Typically I see that {@code getFitness} would 
 * return a mean value in a noisy problem environment. However, abstracting
 * away the concept of domination all together may be appropriate as well.
 * @param F the type that stores fitness values, usually {@code Double}
 * @author Jason
 */
public interface ILikeEvaluations<F>
{
    /**
     * Lets the individual know that he has been evaluated and 
     * received a score represented by {@code fitness}.
     * @param fitness the fitness score received by the individual, one index
     * per objective
     */
    void addEvaluation(F[] fitness);
    
    /**
     * <p>
     * I don't think I like this method as it wouldn't be necessary if 
     * individuals were immutable. TODO: think about this one.
     * <p>
     * Clears the individual's fitness data. This happens when the individual
     * is modified in some way and old fitness values are no longer applicable
     * (e.g. modification).
     */
    void clearEvaluationData();
    
    /**
     * Gets the number of fitness objectives
     * @return the number of fitness objectives
     */
    int getNumObjectives();
}
