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
package com.ridderware.jevolve.examples.onemax;

import com.ridderware.jevolve.*;

/**
 * A main for the classical one-max problem.
 *
 * @author Jeff Ridder
 */
public class OneMaxMain
{
    /**
     *  The main function.
     *
     * @param  args command-line arguments
     */
    public static void main(String args[])
    {
        long tstart = System.currentTimeMillis();
        GAIndividual<Integer> ind = new GAIndividual<Integer>();
        IntegerGAGene gene = new IntegerGAGene(0, 1, 100);
        
        for (int i = 0; i < 100; i++)
        {
            ind.getGenome().add(gene);
        }
        
        if (ind == null)
        {
            System.err.println("Holy Moly What's Going On?");
            System.err.println("--> My Individual is null");
        }
        
        ind.setProbMutation(0.01);
        ind.setProbRecombination(1.0);
        
        SimpleProblem prob = new SimpleProblem();
        
        prob.addBreeder(new SimpleGenerationalBreeder(new TournamentSelection(),
                new SinglePointCrossover(), new FlipMutator()));
        prob.addInitializer(new SimpleInitializer());
        prob.addEvaluator(new OneMax());
        prob.setStatistics(new Statistics("onemaxstats.txt"));
        
        Population pop = new Population(ind);
        
        pop.setMaxPopulationSize(100);
        
        prob.addPopulation(pop);
        
        Stepper stepper = new Stepper();
        stepper.addProblem(prob);
        stepper.setMaxGenerations(1000);
        
        stepper.evolve();

        long tend = System.currentTimeMillis();

        double deltaT = (tend-tstart)/(double)1000;

        System.out.println(tstart);
        System.out.println(tend);
        System.out.println(deltaT);
    }
}

