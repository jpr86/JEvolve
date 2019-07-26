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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.*;

/**
 * A multi-threaded problem that implements Dynamic NSGA.
 * See Ridder & HandUber from GECCO '05.
 * @author Jason HandUber
 */
public class MTDynamicNSGAProblem extends Problem
{
    private final static Logger logger =
        LogManager.getLogger(MTDynamicNSGAProblem.class);

    private BlockingQueue<EvaluatorInterface> evaluatorsQ;

    /**
     * The Thread Pool for doing multi-threaded evaluations
     */
    private ExecutorService executor;

    /**
     *  Constructor for the DynamicNSGAProblem object
     *
     * @param evaluators list of evaluators for multi-threaded evaluation.
     */
    public MTDynamicNSGAProblem(List<EvaluatorInterface> evaluators)
    {
        super();

        executor = Executors.newFixedThreadPool(evaluators.size());
        evaluatorsQ = new ArrayBlockingQueue<EvaluatorInterface>(
            evaluators.size(), false, evaluators);

        int recNumEvals = Runtime.getRuntime().availableProcessors() + 1;
        if (evaluators.size() < recNumEvals)
        {
            logger.warn("Found " + evaluators.size() +
                " Evaluators, Recommend: " +
                recNumEvals);
        }

        for (EvaluatorInterface eval : evaluators)
        {
            super.addEvaluator(eval);
        }
    }

    /**
     *  Constructor for the DynamicNSGAProblem object
     */
    public MTDynamicNSGAProblem()
    {
        super();
    }

    /**
     * Sets the evaluator pool for the multi-threaded problem.
     * @param evaluators list of evaluators for multi-threaded evaluation.
     */
    public void setEvaluators(List<EvaluatorInterface> evaluators)
    {
        executor = Executors.newFixedThreadPool(evaluators.size());
        evaluatorsQ = new ArrayBlockingQueue<EvaluatorInterface>(
            evaluators.size(), false, evaluators);

        int recNumEvals = Runtime.getRuntime().availableProcessors() + 1;
        if (evaluators.size() < recNumEvals)
        {
            logger.warn("Found " + evaluators.size() +
                " Evaluators, Recommend: " +
                recNumEvals);
        }

        for (EvaluatorInterface eval : evaluators)
        {
            super.addEvaluator(eval);
        }
    }

    /**
     *  Initializes all populations contained by the problem.
     */
    public void initialize()
    {
        if (!getPopulations().isEmpty())
        {
            getInitializer(0).initialize(getPopulation(0));
        }
    }

    /**
     *  Breeds all populations contained by the problem.
     */
    public void breed()
    {
        if (!getBreeders().isEmpty())
        {
            getBreeder(0).breedNextGeneration(getPopulation(0), null);
        }
    }

    /**
     *  Performs replacement of individuals.  This method does nothing for this problem.
     */
    public void replaceIndividuals()
    {
    }

    /**
     *  Preevaluates all populations contained by the problem.
     */
    public void preevaluate()
    {
        for (EvaluatorInterface evaluator : getEvaluators())
        {
            evaluator.preevaluate(getPopulation(0));
        }
    }

    private final List<Future> futures = new ArrayList<Future>();

    /**
     *  Evaluates all populations contained by the problem.
     */
    public void evaluate()
    {
        //split the population up into as many subpopulations as possible

        final AtomicBoolean done = new AtomicBoolean(false);
        MultiObjectivePopulation papa =
            (MultiObjectivePopulation) getPopulation(0);
        MultiObjectivePopulation[] chillen =
            new MultiObjectivePopulation[papa.getMaxPopulationSize()];
        int i = 0;
        for (Individual ind : papa.getIndividuals())
        {
            chillen[i] = new MultiObjectivePopulation(ind);
            chillen[i].setMaxPopulationSize(1);
            chillen[i].setElitist(papa.getElitist());
            chillen[i].setForceEvaluation(papa.getForceEvaluation());
            chillen[i].setMaxNumberOfElites(1);
            chillen[i].setNumberEvaluated(0);
            chillen[i].setProblem(papa.getProblem());
            chillen[i].addIndividual(ind);
            i++;
        }

        for (final Population child : chillen)
        {
            final EvaluatorInterface evaluator;

            try
            {
                evaluator = evaluatorsQ.take();
            }
            catch (InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }

            futures.add(executor.submit(new Runnable()
            {
                public void run()
                {
                    child.evaluate(evaluator);
                    if (child.getDone())
                    {
                        done.set(true);
                    }
                    try
                    {
                        evaluatorsQ.put(evaluator);
                    }
                    catch (InterruptedException ex)
                    {
                        logger.error("Should Never Happen", ex);
                        throw new RuntimeException(ex);
                    }

                }
            }));
        }

        //make certain we block! don't leave this method with un-postevaluated
        //individuals
        for (Future future : futures)
        {
            try
            {
                future.get();
            }
            catch (ExecutionException ex)
            {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        futures.clear();

        if (!papa.getDone() && done.get())
        {
            papa.setDone(true);
        }
    }

    /**
     *  Postevaluates all populations contained by the problem.
     */
    public void postevaluate()
    {
        //split the population up into as many subpopulations as possible
        MultiObjectivePopulation papa =
            (MultiObjectivePopulation) getPopulation(0);
        MultiObjectivePopulation[] chillen =
            new MultiObjectivePopulation[papa.getMaxPopulationSize()];
        int i = 0;
        for (Individual ind : papa.getIndividuals())
        {
            chillen[i] = new MultiObjectivePopulation(ind);
            chillen[i].setMaxPopulationSize(1);
            chillen[i].setElitist(papa.getElitist());
            chillen[i].setForceEvaluation(papa.getForceEvaluation());
            chillen[i].setMaxNumberOfElites(1);
            chillen[i].setNumberEvaluated(0);
            chillen[i].setProblem(papa.getProblem());
            chillen[i].addIndividual(ind);
            i++;
        }

        for (final Population child : chillen)
        {
            final EvaluatorInterface evaluator;

            try
            {
                evaluator = evaluatorsQ.take();
            }
            catch (InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }

            futures.add(executor.submit(new Runnable()
            {
                public void run()
                {
                    evaluator.postevaluate(child);
                    try
                    {
                        evaluatorsQ.put(evaluator);
                    }
                    catch (InterruptedException ex)
                    {
                        logger.error("Should Never Happen", ex);
                        throw new RuntimeException(ex);
                    }
                }
            }));
        }

        //make certain we block! don't leave this method with un-postevaluated
        //individuals
        for (Future future : futures)
        {
            try
            {
                future.get();
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        futures.clear();

        //papa's individuals have been updated by multi-threaded evaluators
        papa.sortParetoRank();

        if (getStepper().getCurrentGeneration() == getStepper().
            getMaxGenerations())
        {
            executor.shutdownNow();
        }
    }

    /**
     * Writes the evolution statistics.
     */
    public void writeStatistics()
    {
        if (getStatistics() != null)
        {
            getStatistics().outputGenerationalStats(getPopulation(0));
        }
    }
}
