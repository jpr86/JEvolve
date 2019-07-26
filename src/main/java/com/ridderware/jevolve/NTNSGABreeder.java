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

import org.apache.logging.log4j.*;

/**
 *  A breeder that implements Noise Tolerant NSGA.  NT NSGA differs from Dynamic NSGA in
 *  that it seeks to assign a fixed pool of evaluations to individuals to maximize efficiency and
 *  to improve accuracy over time.  This breeder requires the use of an EvaluationCountingGAIndividual,
 *  and resets the total number of evaluations of bred children.
 *
 *  @author Jeff Ridder
 */
public class NTNSGABreeder extends Breeder
{
    private final static Logger logger =
        LogManager.getLogger(NTNSGABreeder.class);

    /**
     *  Constructor for the NTNSGABreeder object
     */
    public NTNSGABreeder()
    {
        super();
    }

    /**
     *  Constructor for the NTNSGABreeder object
     *
     * @param  selector Selector object.
     * @param  recombinator Recombinator object.
     * @param  mutator Mutator object.
     */
    public NTNSGABreeder(Selector selector, Recombinator recombinator,
        Mutator mutator)
    {
        super(selector, recombinator, mutator);
    }

    /**
     * Breeds the next generation of individuals using the selection, recombination,
     * and mutation operators.  Since Dynamic NSGA is steady state, the next generation
     * is bred directly into the parent population and the child population is ignored.
     *
     * @param  parent_pop parent population.
     * @param  child_pop child population, which is ignored for this breeder.
     */
    public void breedNextGeneration(Population parent_pop, Population child_pop)
    {
        Individual mom;
        Individual dad;
        Individual boy;
        Individual girl;

        if (getSelector() == null || getRecombinator() == null ||
            getMutator() == null)
        {
            logger.error("Selector, Recombinator, or Mutator not set");
        }

        MultiObjectivePopulation pop = (MultiObjectivePopulation) parent_pop;

        //  Pareto sort may already have been done (e.g., in the Problem), but let's not
        //  take any chances.  Do it here.
        pop.sortParetoRank();

        MultiObjectivePopulation best_half = pop.clone();

        for (int i = pop.getPopulationSize() - 1; i >=
            pop.getMaxPopulationSize() / 2; i--)
        {
            best_half.removeIndividual(i);
            pop.removeIndividual(i);
        }

        for (int i = pop.getMaxPopulationSize() - best_half.getPopulationSize(); i <
            pop.getMaxPopulationSize() - 1; i += 2)
        {
            mom = getSelector().select(best_half);
            dad = getSelector().select(best_half);

            boy = dad.clone();
            girl = mom.clone();

            getRecombinator().recombine(dad, mom, boy, girl);

            getMutator().mutate(boy);
            getMutator().mutate(girl);

            boy.setPopulation(parent_pop);
            girl.setPopulation(parent_pop);

            assert (boy instanceof EvaluationCountingGAIndividual);
            assert (girl instanceof EvaluationCountingGAIndividual);

            ((EvaluationCountingGAIndividual) boy).resetTotalNumEvaluations();

            ((EvaluationCountingGAIndividual) girl).resetTotalNumEvaluations();
            for (int j = 0; j < boy.getNumObjectives(); j++)
            {
                boy.setFitness(j, 0.);
                girl.setFitness(j, 0.);
            }

            pop.addIndividual(boy);
            pop.addIndividual(girl);
        }

        if ((pop.getPopulationSize() % 2 != 0 && best_half.getPopulationSize() %
            2 == 0) ||
            (pop.getPopulationSize() % 2 == 0 && best_half.getPopulationSize() %
            2 != 0))
        {
            mom = getSelector().select(best_half);
            dad = getSelector().select(best_half);

            boy = dad.clone();
            girl = null;

            getRecombinator().recombine(dad, mom, boy, girl);

            getMutator().mutate(boy);

            boy.setPopulation(parent_pop);

            assert (boy instanceof EvaluationCountingGAIndividual);

            ((EvaluationCountingGAIndividual) boy).resetTotalNumEvaluations();
            for (int j = 0; j < boy.getNumObjectives(); j++)
            {
                boy.setFitness(j, 0.);
            }
            pop.addIndividual(boy);
        }

        logger.debug("Pop size after breeding is " + pop.getPopulationSize());
    }
}
