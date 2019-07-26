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

import org.apache.logging.log4j.*;

/**
 * An initializer that produces only feasible individuals subject to the contraints
 * specified in the Evaluator.
 *
 * @author Jeff Ridder
 */
public class ConstrainedInitializer extends Initializer
{
    private final static Logger logger =
        LogManager.getLogger(ConstrainedInitializer.class);

    private EvaluatorInterface evaluator;

    /**
     * Sets the evaluator containing the constraints.
     *
     * @param  evaluator EvaluatorInterface object.
     */
    public void setEvaluator(EvaluatorInterface evaluator)
    {
        this.evaluator = evaluator;
    }

    /**
     *  Returns the evaluator.
     *
     * @return evaluator.
     */
    public EvaluatorInterface getEvaluator()
    {
        return this.evaluator;
    }

    /**
     *  Initializes the population, creating only feasible individuals.
     *
     * @param  pop population to be initialized.
     */
    public void initialize(Population pop)
    {
        if (pop != null)
        {
            pop.clearPopulation();

            if (pop.getPrototype() != null)
            {
                for (int i = 0; i < pop.getMaxPopulationSize(); i++)
                {
                    Individual ind = pop.getPrototype().clone();

                    do
                    {
                        ind.initialize();
                        evaluator.evaluateConstraints(ind);
                    }
                    while (ind.getConstraintError() > 0.);

                    logger.debug("!!!!!!!!!!!! VALID INDIVIDUAL FOUND  !!!!!!!!!!!!!");

                    pop.addIndividual(ind);
                }
            }
        }
    }
}

