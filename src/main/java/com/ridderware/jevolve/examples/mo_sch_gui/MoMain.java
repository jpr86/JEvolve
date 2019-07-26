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
package com.ridderware.jevolve.examples.mo_sch_gui;

import com.ridderware.jevolve.*;
import com.ridderware.jevolve.ui.UIFactory;

/**
 *  A main for multi-objective problems.  This example implements Deb's NSGA-II algorithm.
 *
 * @author Jeff Ridder
 */
public class MoMain
{
    /**
     *  The main function.
     *
     * @param  args command-line arguments
     */
    public static void main(String args[])
    {
        GAIndividual<Double> ind = new GAIndividual<Double>(2);
        DoubleGAGene gene = new DoubleGAGene(-1000., 1000., 1.);
        
        ind.getGenome().add(gene);
        ind.setProbMutation(0.01);
        ind.setProbRecombination(0.8);
        
        MoSch sch = new MoSch();
        DynamicNSGAProblem prob = new DynamicNSGAProblem();
        prob.addEvaluator(sch);
        prob.addBreeder(new DynamicNSGABreeder(new MultiObjectiveTournament(),
                new SinglePointCrossover(), new GaussianMutator()));
        prob.addInitializer(new SimpleInitializer());
        
        MultiObjectivePopulation pop = new MultiObjectivePopulation(ind/*, evaluators*/);
        pop.setMaxPopulationSize(5000);
        pop.setElitist(false);
        pop.setForceEvaluation(true);
        
        UIFactory.ShowUI(sch, "Term 1", "Term 2");
        
        prob.addPopulation(pop);
        
        Stepper stepper = new Stepper();
        stepper.addProblem(prob);
        stepper.setMaxGenerations(500);
        stepper.setScreenOutput(true);
        
        for ( int i = 0; i < 1; i++ )
        {
            long startTime = System.nanoTime();
            stepper.evolve();
            long endTime = System.nanoTime();
            long delta = /*TimeUnit.NANOSECONDS.toSeconds*/(endTime-startTime);
            System.out.println(i+" took "+ delta +" nano-seconds");
        }
        System.out.println("Done");
    }
}

