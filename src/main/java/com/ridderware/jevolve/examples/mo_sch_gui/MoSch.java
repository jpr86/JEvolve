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

import com.ridderware.jevolve.GAIndividual;
import com.ridderware.jevolve.Individual;
import com.ridderware.jevolve.Population;
import com.ridderware.jevolve.EvaluatorInterface;
import java.util.Observable;
import org.apache.logging.log4j.*;

/**
 * A multi-objective evaluator.
 */
public class MoSch
        extends Observable
        implements EvaluatorInterface
{
    private final static Logger logger = LogManager.getLogger(MoSch.class);
    
    /**
     * Method to evaluate constraints.  Empty.
     *
     * @param  ind individual whose constraints won't be evaluated.
     */
    public void evaluateConstraints(Individual ind)
    {
        if (Math.random() < 0.1)
        {
            ind.setConstraintError(Math.random());
        }
    }
    
    /**
     * Preevaluates the population.  Empty.
     *
     * @param  pop population that won't be preevaluated.
     */
    public void preevaluate(Population pop)
    {
    }
    
    /**
     * Postevaluates the population.  Empty.
     *
     * @param  pop population that won't be postevaluated.
     */
    public void postevaluate(Population pop)
    {
        super.setChanged();
        super.notifyObservers(pop);
    }
    
    /**
     *  Evaluates fitness.
     *
     * @param  ind individual to evaluate.
     */
    public void evaluateFitness(Individual ind)
    {
//        System.out.println("Evaluating fitness of "+ ind.getMyID()+" on thread "+ Thread.currentThread().getName());
        GAIndividual<Double> ga_ind = (GAIndividual<Double>) ind;
        
        ga_ind.setFitness(0, Math.pow(ga_ind.getValue(0), 2.));
        ga_ind.setFitness(1, Math.pow(ga_ind.getValue(0) - 2., 2.));
//        System.out.println("Done Evaluating fitness of "+ ind.getMyID()+" on thread "+ Thread.currentThread().getName());
    }
}

