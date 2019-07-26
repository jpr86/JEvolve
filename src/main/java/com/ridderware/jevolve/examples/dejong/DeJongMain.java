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
package com.ridderware.jevolve.examples.dejong;

import com.ridderware.jevolve.*;
import com.ridderware.jevolve.ui.UIFactory;
import com.ridderware.jevolve.ui.UIFactoryOptions;

/**
 * A main for the classical one-max problem.
 *
 * @author Jeff Ridder
 */
public class DeJongMain
{
    /**
     *  The main function.
     *
     * @param  args command-line arguments
     */
    public static void main(String args[])
    {
        int func = 1;
        if (args.length > 0)
        {
            func = Integer.parseInt(args[0]);
        }

        long tstart = System.currentTimeMillis();
        GAIndividual<Integer> ind = new GAIndividual<Integer>();


        IntegerGAGene gene = new IntegerGAGene(0, 1, 1);

        switch (func)
        {
            case 1:
                //  30 bits
                for (int i = 0; i < 30; i++)
                {
                    ind.getGenome().add(gene);
                }
                break;
            case 2:
                //  24 bits
                for (int i = 0; i < 24; i++)
                {
                    ind.getGenome().add(gene);
                }
                break;
            case 3:
                //  50 bits
                for (int i = 0; i < 50; i++)
                {
                    ind.getGenome().add(gene);
                }
                break;
            case 4:
                //  240 bits
                for (int i = 0; i < 240; i++)
                {
                    ind.getGenome().add(gene);
                }
                break;
            case 5:
                //  34 bits
                for (int i = 0; i < 34; i++)
                {
                    ind.getGenome().add(gene);
                }
                break;
            default:

        }




        SimpleProblem prob = new SimpleProblem();

        Selector s = new RouletteWheelSelection();
        prob.addBreeder(new SimpleGenerationalBreeder(s,
            new SinglePointCrossover(), new FlipMutator()));
        prob.addInitializer(new SimpleInitializer());
        DeJongEval eval = new DeJongEval(func, s);
        prob.addEvaluator(eval);
        prob.setStatistics(new Statistics("dejongstats.txt"));

        Population pop = new Population(ind);

        ind.setProbMutation(0.01);
        ind.setProbRecombination(1.0);
        pop.setMaxPopulationSize(50);

        prob.addPopulation(pop);

        Stepper stepper = new Stepper();
        stepper.addProblem(prob);
        stepper.setMaxGenerations(1000);

        UIFactoryOptions options = new UIFactoryOptions(eval, "Fitness");
        options.setDisplayConstraintError(false);
        options.setGenerationalDrawingFrequency(1);
        UIFactory.ShowUI(options);

        stepper.evolve();

        UIFactory.CloseUI();

//        long tend = System.currentTimeMillis();
//
//        double deltaT = (tend - tstart) / (double) 1000;
//
//        System.out.println(tstart);
//        System.out.println(tend);
//        System.out.println(deltaT);
    }
}

