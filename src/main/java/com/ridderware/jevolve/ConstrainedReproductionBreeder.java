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
 * A breeder that produces only feasible children.  This is guaranteed by checking
 * the feasibility of each newly produced individual using the contraints specified
 * in the evaluator.  Non-feasible individuals are rejected.
 *
 * @author Jeff Ridder
 */
public class ConstrainedReproductionBreeder extends Breeder
{
    private final static Logger logger =
        LogManager.getLogger(ConstrainedReproductionBreeder.class);

    private EvaluatorInterface evaluator;

    /**
     *  Creates a new instance of ConstainedReproductionBreeder.
     */
    public ConstrainedReproductionBreeder()
    {
        super();
    }

    /**
     *  Creates a new instance of ConstainedReproductionBreeder.
     *
     * @param  selector selection operator object.
     * @param  recombinator recombination operator object.
     * @param  mutator mutation operator object.
     * @param  evaluator evaluator.
     */
    public ConstrainedReproductionBreeder(Selector selector,
        Recombinator recombinator, Mutator mutator, EvaluatorInterface evaluator)
    {
        super(selector, recombinator, mutator);
        this.evaluator = evaluator;
    }

    /**
     * Sets the evaluator.
     *
     * @param  evaluator evaluator.
     */
    public void setEvaluator(EvaluatorInterface evaluator)
    {
        this.evaluator = evaluator;
    }

    /**
     * Returns the evaluator.
     *
     * @return    evaluator.
     */
    public EvaluatorInterface getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * Breeds the next generation, guaranteeing the feasibility of the next
     * generation.
     *
     * @param  parent_pop parent population
     * @param  child_pop child population
     */
    public void breedNextGeneration(Population parent_pop, Population child_pop)
    {
        Individual mom;
        Individual dad;
        Individual boy;
        Individual girl;

        if (this.getSelector() == null || this.getRecombinator() == null ||
            this.getMutator() == null)
        {
            logger.error("Selector, Recombinator, or Mutator not set");
        }

        int numChildren = 0;
        while (numChildren != parent_pop.getPopulationSize())
        {
            mom = getSelector().select(parent_pop);
            dad = getSelector().select(parent_pop);

            boy = child_pop.getIndividual(numChildren);

            if (numChildren != parent_pop.getPopulationSize() - 1)
            {
                girl = child_pop.getIndividual(numChildren + 1);
            }
            else
            {
                girl = null;
            }

            getRecombinator().recombine(mom, dad, boy, girl);

            getMutator().mutate(boy);
            boy.setPopulation(child_pop);
            if (girl != null)
            {
                getMutator().mutate(girl);
            }

            //    Validate the boy
            evaluator.evaluateConstraints(boy);
            if (boy.getConstraintError() <= 0)
            {
                numChildren++;
            }

            //    Validate the girl
            if (girl != null)
            {
                evaluator.evaluateConstraints(girl);
                if (girl.getConstraintError() <= 0)
                {
                    numChildren++;
                }
                girl.setPopulation(child_pop);
            }

        }
    }
}

