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
package com.ridderware.jevolve.examples.zdt1;

import com.ridderware.jevolve.DoubleGAGene;
import com.ridderware.jevolve.EvaluationCountingGAIndividual;
import com.ridderware.jevolve.GaussianMutator;
import com.ridderware.jevolve.MultiObjectivePopulation;
import com.ridderware.jevolve.NTNSGABreeder;
import com.ridderware.jevolve.NTNSGAProblem;
import com.ridderware.jevolve.NTNSGAStatistics;
import com.ridderware.jevolve.NTNSGATournament;
import com.ridderware.jevolve.SimpleInitializer;
import com.ridderware.jevolve.SinglePointCrossover;
import com.ridderware.jevolve.Stepper;


/**
 *  TBD Description of the Class
 */
public class MoMain
{
    
    /**
     *  TBD
     *
     * @param  args
     */
    public static void main(String args[])
    {
//        GAIndividual<Double> ind = new GAIndividual<Double>(2);
        EvaluationCountingGAIndividual<Double> ind = new EvaluationCountingGAIndividual<Double>(2);
        ind.setAssignedEvaluations(10);
        
        DoubleGAGene gene = new DoubleGAGene(0., 1., .025);
        
        for ( int i = 0; i < 30; i++ )
        {
            ind.getGenome().add(gene);
        }
        
        ind.setProbMutation(0.01);
        ind.setProbRecombination(0.8);
        
        MoZDT evaluator = new MoZDT();
        evaluator.setNoiseSigma(0.2);
        
        
        ///////////////
        //  NSGA-II:
        ///////////////
        
//        SimpleProblem prob = new SimpleProblem();
//        prob.addBreeder(new SimpleGenerationalBreeder(new MultiObjectiveTournament(), new SinglePointCrossover<Double>(), new GaussianMutator<Double>()));
//        prob.addInitializer(new SimpleInitializer());
        
//        MultiObjectivePopulation pop = new MultiObjectivePopulation(ind);
//        pop.setForceEvaluation(true);
//        pop.setMaxPopulationSize(100);
        
//        pop.setElitist(true);
//        pop.setMaxNumberOfElites(100);
        
//        MultiObjectiveStatistics stats = new MultiObjectiveStatistics();
//        stats.setPeriod(20);
//        prob.setStatistics(stats);
        
        ///////////////
        //  DNSGA:
        ///////////////
        
//        DynamicNSGAProblem prob = new DynamicNSGAProblem();
//        prob.addBreeder(new DynamicNSGABreeder(new MultiObjectiveTournament(), new SinglePointCrossover<Double>(), new GaussianMutator<Double>()));
//        prob.addInitializer(new SimpleInitializer());
//
//
//        MultiObjectivePopulation pop = new MultiObjectivePopulation(ind);
//        pop.setForceEvaluation(true);
//        pop.setMaxPopulationSize(100);
//        pop.setElitist(false);
//        pop.setForceEvaluation(true);
//
//        MultiObjectiveStatistics stats = new MultiObjectiveStatistics();
//        stats.setPeriod(20);
//        prob.setStatistics(stats);
        
        ///////////////
        //  NT-NSGA:
        ///////////////
        
        NTNSGAProblem prob = new NTNSGAProblem();
        prob.addBreeder(new NTNSGABreeder(new NTNSGATournament(), new SinglePointCrossover<Double>(), new GaussianMutator<Double>()));
        prob.addInitializer(new SimpleInitializer());
        prob.setNewIndividualEvals(10);
        prob.setTotalEvalsPerGeneration(1000);
        
        MultiObjectivePopulation pop = new MultiObjectivePopulation(ind);
        pop.setForceEvaluation(true);
        pop.setMaxPopulationSize(100);
        pop.setElitist(false);
        pop.setForceEvaluation(true);
        
        NTNSGAStatistics stats = new NTNSGAStatistics();
        stats.setPeriod(20);
        prob.setStatistics(stats);
        
        /////////////////////////////////
        
        prob.addEvaluator(evaluator);
        
        prob.addPopulation(pop);
        
        
        Stepper stepper = new Stepper();
        stepper.addProblem(prob);
        stepper.setMaxGenerations(1000);
        
        stepper.evolve();
    }
}

