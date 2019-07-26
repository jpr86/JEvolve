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
 * Interface declaring methods for evaluators.  Evaluators are problem specific
 * and evaluate individuals for constraint error and fitness.  Populations may need to
 * be pre- and/or post-evaluated in some cases, and so we also include methods for
 * this that are called immediately before (pre) and after (post)
 * evaluation of individuals.
 *
 * @author Jeff Ridder
 */
public interface EvaluatorInterface
{
    /**
     * Called by the problem to pre-evaluate a population, conditioning the
     * individuals for constraint and fitness evaluation.
     *
     * @param  pop population to be pre-evaluated.
     */
    public void preevaluate(Population pop);

    /**
     * Called to evaluate the fitness of the specified individual.  The individual
     * fitness should be set via a setFitness call prior to exiting.
     *
     * @param  ind  individual to be evaluated for fitness.
     */
    public void evaluateFitness(Individual ind);

    /**
     * Called by the problem to post-evaluate a population, providing a "last
     * chance" to manipulate fitness and constraint scores prior to breeding.
     *
     * @param  pop population to be post-evaluated.
     */
    public void postevaluate(Population pop);

    /**
     * Called to evaluate the constraints error of the specified individual.  The
     * individual error should be set via a setConstraintError call prior to exiting.
     *
     * @param  ind individual to be evaluated for constraint error.
     */
    public void evaluateConstraints(Individual ind);
}

