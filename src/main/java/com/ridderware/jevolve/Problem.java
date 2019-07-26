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

import java.util.ArrayList;

/**
 *  A Problem contains all of the objects necessary to evaluate and manipulate
 *  individuals of the same species (i.e., same genome) toward a solution.
 *
 * @author Jeff Ridder
 */
public abstract class Problem
{
    private ArrayList<Population> pops = new ArrayList<Population>();

    private ArrayList<Initializer> initializers = new ArrayList<Initializer>();

    private ArrayList<Breeder> breeders = new ArrayList<Breeder>();

    private Migrator migrator;

    private ArrayList<EvaluatorInterface> evaluators =
        new ArrayList<EvaluatorInterface>();

    private Statistics stats;

    private Stepper stepper;

    private boolean done;

    /**
     *  Constructor for the Problem object
     */
    public Problem()
    {
        this.migrator = null;
        this.stats = null;
        this.stepper = null;
        this.done = false;
    }

    /**
     *  Sets the migrator.
     *
     * @param  migrator Migrator object.
     */
    public void setMigrator(Migrator migrator)
    {
        this.migrator = migrator;
    }

    /**
     *  Sets the statistics.
     *
     * @param  stats Statistics object.
     */
    public void setStatistics(Statistics stats)
    {
        this.stats = stats;
    }

    /**
     *  Sets the stepper.
     *
     * @param  stepper Stepper object
     */
    public void setStepper(Stepper stepper)
    {
        this.stepper = stepper;
    }

    /**
     *  Sets done.
     *
     * @param  done true if done, false otherwise.
     */
    public void setDone(boolean done)
    {
        this.done = done;
    }

    /**
     *  Returns the number of breeders.
     *
     * @return number of breeders.
     */
    public int getNumBreeders()
    {
        return breeders.size();
    }

    /**
     *  Returns the specified breeder.
     *
     * @param  i index of breeder to retrieve.
     * @return  retrieved breeder.
     */
    public Breeder getBreeder(int i)
    {
        return breeders.get(i);
    }

    /**
     * Returns the ArrayList of breeders.
     *
     * @return breeders.
     */
    protected ArrayList<Breeder> getBreeders()
    {
        return this.breeders;
    }

    /**
     * Returns the number of initializers.
     *
     * @return   number of initializers.
     */
    public int getNumInitializers()
    {
        return initializers.size();
    }

    /**
     * Returns the specified initializer
     *
     * @param  i index of the initializer to retrieve.
     * @return  retrieved initializer.
     */
    public Initializer getInitializer(int i)
    {
        return initializers.get(i);
    }

    /**
     * Returns the ArrayList of initializers.
     *
     * @return initializers.
     */
    protected ArrayList<Initializer> getInitializers()
    {
        return this.initializers;
    }

    /**
     *  Returns done.
     *
     * @return true if done, false otherwise.
     */
    public boolean getDone()
    {
        return done;
    }

    /**
     * Returns the number of populations.
     *
     * @return  number of populations.
     */
    public int getNumPopulations()
    {
        return pops.size();
    }

    /**
     * Returns the specified population.
     *
     * @param  i index of population to retrieve.
     * @return  retrieved population.
     */
    public Population getPopulation(int i)
    {
        return pops.get(i);
    }

    /**
     * Returns the ArrayList of populations.
     *
     * @return populations.
     */
    protected ArrayList<Population> getPopulations()
    {
        return this.pops;
    }

    /**
     *  Returns the migrator.
     *
     * @return Migrator object.
     */
    public Migrator getMigrator()
    {
        return this.migrator;
    }

    /**
     * Returns the number of evaluators.
     *
     * @return  number of evaluators.
     */
    public int getNumEvaluators()
    {
        return this.evaluators.size();
    }

    /**
     * Returns the specified evaluator.
     *
     * @param  i index of evaluator to retrieve.
     * @return retrieved evaluator.
     */
    public EvaluatorInterface getEvaluator(int i)
    {
        return evaluators.get(i);
    }

    /**
     * Returns the ArrayList of evaluators.
     *
     * @return evaluators.
     */
    protected ArrayList<EvaluatorInterface> getEvaluators()
    {
        return this.evaluators;
    }

    /**
     *  Returns the statistics object.
     *
     * @return Statistics object.
     */
    public Statistics getStatistics()
    {
        return this.stats;
    }

    /**
     * Returns the stepper.
     *
     * @return Stepper object.
     */
    public Stepper getStepper()
    {
        return this.stepper;
    }

    /**
     * Adds a breeder.
     *
     * @param  breeder Breeder object.
     */
    public void addBreeder(Breeder breeder)
    {
        if (!breeders.contains(breeder))
        {
            breeders.add(breeder);
        }
    }

    /**
     * Removes a breeder.
     *
     * @param  breeder breeder to be removed.
     */
    public void removeBreeder(Breeder breeder)
    {
        breeders.remove(breeder);
    }

    /**
     * Adds an initializer.
     *
     * @param  initializer initializer to add.
     */
    public void addInitializer(Initializer initializer)
    {
        if (!initializers.contains(initializer))
        {
            initializers.add(initializer);
        }
    }

    /**
     * Removes an initializer
     *
     * @param  initializer initializer to remove.
     */
    public void removeInitializer(Initializer initializer)
    {
        initializers.remove(initializer);
    }

    /**
     * Adds a population.
     *
     * @param  pop population to add.
     */
    public void addPopulation(Population pop)
    {
        if (!pops.contains(pop))
        {
            pops.add(pop);
            pop.setProblem(this);
        }
    }

    /**
     * Removes a population.
     *
     * @param  pop population to remove.
     */
    public void removePopulation(Population pop)
    {
        pops.remove(pop);
    }

    /**
     * Adds an evaluator.
     *
     * @param  evaluator evaluator to add.
     */
    public void addEvaluator(EvaluatorInterface evaluator)
    {
        if (!evaluators.contains(evaluator))
        {
            evaluators.add(evaluator);
        }
    }

    /**
     * Removes an evaluator.
     *
     * @param  evaluator evaluator to remove.
     */
    public void removeEvaluator(EvaluatorInterface evaluator)
    {
        evaluators.remove(evaluator);
    }

    /**
     *  Abstract method to initialize all populations contained by the
     *  problem.
     */
    public abstract void initialize();

    /**
     *  Abstract method to breed all populations contained by the problem.
     */
    public abstract void breed();

    /**
     * Migrates individuals between populations.
     */
    public void migrate()
    {
        if (migrator != null)
        {
            migrator.migrateIndividuals(pops);
        }
    }

    /**
     * Abstract method to replace individuals in the populations.
     */
    public abstract void replaceIndividuals();

    /**
     * Abstract method to write the statistics for the populations.
     */
    public abstract void writeStatistics();

    /**
     *  Abstract method to preevaluate all populations.
     */
    public abstract void preevaluate();

    /**
     * Abstract method evaluate all populations.
     */
    public abstract void evaluate();

    /**
     * Abstract method to postevaluate all populations.
     */
    public abstract void postevaluate();
}

