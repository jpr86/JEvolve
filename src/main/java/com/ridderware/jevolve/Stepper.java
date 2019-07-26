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
import org.apache.logging.log4j.*;

/**
 * An incrementer for evolutionary computation.  Stepper advances the evolution by
 * executing the fundamental functions of evolutionary computation at each step.
 *
 * @author Jeff Ridder
 */
public class Stepper
{
    private ArrayList<Problem> problems = new ArrayList<Problem>();

    private double max_generations;

    private double current_generation;

    private boolean screen;

    private final static Logger logger = LogManager.getLogger(Stepper.class);

    private boolean done = false;

    /**
     *  Constructor for the Stepper object
     */
    public Stepper()
    {
        this.max_generations = 1.;
        this.current_generation = 0.;
        this.screen = false;
    }

    /**
     * Sets whether or not to print the progress of the evolution to the screen.
     * @param screen true if you want screen output, false otherwise.
     */
    public void setScreenOutput(boolean screen)
    {
        this.screen = screen;
    }

    /**
     *  Sets the max number of generations to execute.
     *
     * @param  max_generations max generations.
     */
    public void setMaxGenerations(int max_generations)
    {
        this.max_generations = max_generations;
    }

    /**
     * Sets the current generation.
     *
     * @param  current_generation  current generation.
     */
    public void setCurrentGeneration(double current_generation)
    {
        this.current_generation = current_generation;
    }

    /**
     * Returns the max generations to execute.
     *
     * @return max generations.
     */
    public double getMaxGenerations()
    {
        return this.max_generations;
    }

    /**
     * Returns the current generation.
     *
     * @return current generation.
     */
    public double getCurrentGeneration()
    {
        return this.current_generation;
    }

    /**
     * Returns an ArrayList of problems for the stepper.
     *
     * @return ArrayList of problems.
     */
    public ArrayList<Problem> getProblems()
    {
        return this.problems;
    }

    /**
     * Adds a problem to the stepper.
     *
     * @param  problem problem to add.
     */
    public void addProblem(Problem problem)
    {
        if (!problems.contains(problem))
        {
            problems.add(problem);
            problem.setStepper(this);
        }
    }

    /**
     * Removes a problem from the stepper.
     *
     * @param  problem problem to remove.
     */
    public void removeProblem(Problem problem)
    {
        problems.remove(problem);
        problem.setStepper(null);
    }

    /**
     * Initializes all problems in the stepper.
     */
    public void initialize()
    {
        for (Problem problem : problems)
        {
            problem.initialize();
        }
    }

    /**
     * Executes the evolution.  This is the call for starting the evolution.
     */
    public void evolve()
    {
        this.initialize();
//        current_generation = 0.;
        for (; current_generation <= this.max_generations; current_generation +=
                1.)
        {
            if (screen)
            {
                logger.info("Generation: " + current_generation);
            }

            this.step();

            if (done)
            {
                break;
            }
        }
    }

    /**
     * Executes an evolution step.
     */
    public void step()
    {
        done = false;
        for (Problem problem : problems)
        {
            problem.preevaluate();
        }
        for (Problem problem : problems)
        {
            problem.evaluate();
        }
        for (Problem problem : problems)
        {
            problem.postevaluate();

            if (problem.getDone())
            {
                done = true;
            }
        }

        for (Problem problem : problems)
        {
            problem.replaceIndividuals();
        }

        for (Problem problem : problems)
        {
            problem.writeStatistics();
        }

        if (done)
        {
            return;
        }

        for (Problem problem : problems)
        {
            problem.migrate();
        }

        for (Problem problem : problems)
        {
            problem.breed();
        }
    }
}

