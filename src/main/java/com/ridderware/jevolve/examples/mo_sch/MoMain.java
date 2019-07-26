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

package com.ridderware.jevolve.examples.mo_sch;

import com.ridderware.jevolve.*;

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
        
        SimpleProblem prob = new SimpleProblem();
        prob.addBreeder(new SimpleGenerationalBreeder(new MultiObjectiveTournament(), new SinglePointCrossover<Double>(), new GaussianMutator<Double>()));
        prob.addInitializer(new SimpleInitializer());
        prob.addEvaluator(new MoSch());
        
        MultiObjectivePopulation pop = new MultiObjectivePopulation(ind);
        pop.setMaxPopulationSize(100);
        pop.setElitist(true);
        pop.setMaxNumberOfElites(100);
        
        MultiObjectiveStatistics stats = new MultiObjectiveStatistics();
        stats.setPeriod(100);
        prob.setStatistics(stats);
        
        prob.addPopulation(pop);
        
        Stepper stepper = new Stepper();
        stepper.addProblem(prob);
        stepper.setMaxGenerations(1000);
        
//        for ( int i = 0; i < 100; i++ )
//        {
//            System.out.println(i);
            stepper.evolve();    
//        }
    } 
}

