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

import java.io.Serializable;
import org.apache.logging.log4j.*;

/**
 * Abstract base class for all individuals in com.ridderware.jevolve.  Provides some basics common
 * to all individuals.
 *
 * @author Jeff Ridder
 */
public abstract class Individual implements Cloneable, Serializable
{
    /**
     * Enumeration of dominance conditions for purposes of Pareto ranking.
     */
    public enum Dominance
    {
        /** Dominating the comparison individual. */
        DOMINATING,
        /** Dominated by the comparison individual. */
        DOMINATED,
        /** Incomparable to the comparison individual. */
        INCOMPARABLE
    }

    private static long last_id = 0;

    private double[] fitness;

    private double crowding_distance;

    private int pareto_rank;

    private double adjusted_fitness;

    private double constraint_error;

    private boolean success;

    private boolean evaluated;

    private double prob_recombination;

    private double prob_mutation;

    private Object user_data;

    private Population pop;

    private long id = ++last_id;

    private final static Logger logger = LogManager.getLogger(Individual.class);

    /**
     *  Constructor for the Individual object
     */
    public Individual()
    {
        fitness = new double[1];
        fitness[0] = Double.MAX_VALUE;
        this.adjusted_fitness = 0.;
        this.success = false;
        this.evaluated = false;
        this.prob_recombination = 1.;
        this.prob_mutation = 0.;
        this.user_data = null;
        this.constraint_error = 0.;

        this.crowding_distance = 0.;
        this.pareto_rank = -1;
        this.pop = null;
    }

    /**
     *  Constructor for the Individual object
     *
     * @param numObjectives number of objectives.
     */
    public Individual(int numObjectives)
    {
        fitness = new double[numObjectives];
        for (int i = 0; i < numObjectives; i++)
        {
            fitness[i] = Double.MAX_VALUE;
        }

        this.adjusted_fitness = 0.;
        this.success = false;
        this.evaluated = false;
        this.prob_recombination = 1.;
        this.prob_mutation = 0.;
        this.user_data = null;
        this.constraint_error = 0.;

        this.crowding_distance = 0.;
        this.pareto_rank = -1;
        this.pop = null;
    }

    /**
     *  Sets the population that this individual belongs to.
     *
     * @param  pop population of the individual.
     */
    public void setPopulation(Population pop)
    {
        this.pop = pop;
    }

    /**
     *  Sets the pareto rank of multi-objective individuals (lower is better).
     *
     * @param  pareto_rank pareto rank.
     */
    public void setParetoRank(int pareto_rank)
    {
        this.pareto_rank = pareto_rank;
    }

    /**
     * Sets the crowding distance of the individual.  For NSGA, crowding distance
     * is determined phenotypically in fitness space.
     *
     * @param  crowding_distance crowding distance.
     */
    public void setCrowdingDistance(double crowding_distance)
    {
        this.crowding_distance = crowding_distance;
    }

    /**
     *  Sets the fitness for single objective individuals.
     *
     * @param  fitness fitness.
     */
    public void setFitness(double fitness)
    {
        this.fitness[0] = fitness;
        this.adjusted_fitness = 1. / (1. + fitness);
    }

    /**
     *  Sets a fitness for multi-objective individuals.
     *
     * @param  index objective index.
     * @param  fitness fitness value.
     */
    public void setFitness(int index, double fitness)
    {
        this.fitness[index] = fitness;
    }

    /**
     *  Sets user data (if any).
     *
     * @param  user_data user defined data.
     */
    public void setUserData(Object user_data)
    {
        this.user_data = user_data;
    }

    /**
     *  Sets whether this individual has been evaluated.
     *
     * @param  evaluated true if evaluated, false if not.
     */
    public void setEvaluated(boolean evaluated)
    {
        this.evaluated = evaluated;
    }

    /**
     * Sets whether this individual has achieved success.  This is set by
     * the evaluator and should be used for those problems for which a success
     * condition is known.
     *
     * @param  success true if successful, false otherwise.
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     *  Sets the probability of recombination for the individual.
     *
     * @param  prob_recombination probability of recombination.
     */
    public void setProbRecombination(double prob_recombination)
    {
        this.prob_recombination = prob_recombination;
    }

    /**
     *  Sets the probability of mutation for the individual.
     *
     * @param  prob_mutation probability of mutation.
     */
    public void setProbMutation(double prob_mutation)
    {
        this.prob_mutation = prob_mutation;
    }

    /**
     * Sets the constraint error for the individual.  Constraint error <= 0
     * is equivalent to no error.  Constraint error > 0 is the magnitude of the error.
     *
     * @param  constraint_error constraint error.
     */
    public void setConstraintError(double constraint_error)
    {
        this.constraint_error = constraint_error;
    }

    /**
     *  Sets the ID of the individual.  Whenever we load a saved individual, we will
     *  set the individual's ID to that which was saved so as to avoid confusion.
     *
     * @param  id unique individual ID.
     */
    public void setMyID(long id)
    {
        this.id = id;
    }

    /**
     * Returns the genotype size for the individual.  This is abstract since,
     * in this base class, we know that there will be a genotype, but not the form.
     * e.g., the genotype could be a tree, a vector, etc.
     *
     * @return size of the genotype.
     */
    public abstract int getGenotypeSize();

    /**
     * Returns the population this individual belongs to.
     *
     * @return population.
     */
    public Population getPopulation()
    {
        return this.pop;
    }

    /**
     *  Returns the crownding distance.
     *
     * @return crowding distance.
     */
    public double getCrowdingDistance()
    {
        return this.crowding_distance;
    }

    /**
     *  Returns the unique ID.
     *
     * @return ID
     */
    public long getMyID()
    {
        return id;
    }

    /**
     *  Returns the number of objectives for this individual.
     *
     * @return number of objectives.
     */
    public int getNumObjectives()
    {
        return fitness.length;
    }

    /**
     * Returns the fitness for single-objective individuals.
     *
     * @return    fitness
     */
    public double getFitness()
    {
        return fitness[0];
    }

    /**
     * Returns the fitness array.
     *
     * @return the fitness array.
     */
    public double[] getFitnessArray()
    {
        return fitness;
    }

    /**
     * Returns the fitness value at the specified index for multi-objective individuals.
     *
     * @param  index index of the fitness value.
     * @return  fitness value.
     */
    public double getFitness(int index)
    {
        return fitness[index];
    }

    /**
     * Returns the Pareto rank of multi-objective individuals.
     *
     * @return Pareto rank.
     */
    public int getParetoRank()
    {
        return this.pareto_rank;
    }

    /**
     * Returns the constraint error.
     *
     * @return    constraint error
     */
    public double getConstraintError()
    {
        return this.constraint_error;
    }

    /**
     * Returns the user data.
     *
     * @return  user-specified data.
     */
    public Object getUserData()
    {
        return this.user_data;
    }

    /**
     *  Returns whether this individual has been evaluated.
     *
     * @return true if evaluated, false if not.
     */
    public boolean getEvaluated()
    {
        return this.evaluated;
    }

    /**
     * Returns whether this individual achieved success during its last evaluation.
     *
     * @return true if successful, false if not.
     */
    public boolean getSuccess()
    {
        return this.success;
    }

    /**
     * Returns the probability of recombination.
     *
     * @return probability of recombination.
     */
    public double getProbRecombination()
    {
        return this.prob_recombination;
    }

    /**
     * Returns the probability of mutation.
     * @return  probability of mutation.
     */
    public double getProbMutation()
    {
        return this.prob_mutation;
    }

    /**
     * Returns the adjusted fitness of single-objective individuals.
     *
     * @return adjusted fitness.
     */
    public double getAdjustedFitness()
    {
        return this.adjusted_fitness;
    }

    /**
     * Adds the specified distance to the crowding distance of this individual.
     * @param  distance distance to add to the crowding distance.
     */
    public void addCrowdingDistance(double distance)
    {
        this.crowding_distance += distance;
    }

    /**
     * Abstract method to initialize the individual prior to evolution.
     */
    public abstract void initialize();

    /**
     *  Genotype distance calculation to support estimates of crowding (e.g. for
     *  deterministic crowding).  Abstract.
     *
     * @param  ind individual to measure distance to.
     * @return genotype distance.
     */
    public abstract double genotypeDistance(Individual ind);

    /**
     * Computes and returns the distance to the specified individual in terms
     * of fitness.
     *
     * @param  ind individual to measure distance to.
     * @return fitness distance.
     */
    public double fitnessDistance(Individual ind)
    {
        double distance = 0.;
        for (int i = 0; i < this.fitness.length; i++)
        {
            distance += Math.abs(ind.getFitness(i) - this.getFitness(i));
        }

        return distance;
    }

    /**
     * Compares this with the specified individual for Pareto dominance.
     *
     * @param  ind individual to be compared with.
     * @return  Dominance condition.
     */
    public Dominance checkDominance(Individual ind)
    {
        int nfunc = fitness.length;

        int m = 0;
        int n = 0;

        while (m < nfunc && this.fitness[m] <= ind.fitness[m] + 1.e-10)
        {
            if (ind.fitness[m] < this.fitness[m] + 1.e-10)
            {
                n++;
            }

            m++;
        }

        if (m == nfunc)
        {
            if (n == nfunc)
            {
                return Dominance.INCOMPARABLE;
            }
            else
            {
                return Dominance.DOMINATING;
            }
        }

        m = 0;
        n = 0;
        while (m < nfunc && ind.fitness[m] <= this.fitness[m] + 1.e-10)
        {
            if (this.fitness[m] < ind.fitness[m] + 1.e-10)
            {
                n++;
            }

            m++;
        }

        if (m == nfunc)
        {
            if (n != nfunc)
            {
                return Dominance.DOMINATED;
            }
            else
            {
                return Dominance.INCOMPARABLE;
            }
        }

        return Dominance.INCOMPARABLE;
    }

    /**
     * Compares this with the specified individual for Pareto dominance, preferring
     * feasible individuals to infeasible.  That is, if both individuals are infeasible,
     * then the one with lower constraint error is dominating.  If one is feasible and the
     * other is not, then the feasible individual is dominating.  If both are feasible,
     * then Pareto comparison applies.
     *
     * @param  ind individual to be compared with.
     * @return  Dominance condition.
     */
    public Dominance checkConstrainedDominance(Individual ind)
    {
        if (this.getConstraintError() <= 0. && ind.getConstraintError() > 0.)
        {
            return Dominance.DOMINATING;
        }
        else if (ind.getConstraintError() <= 0 && this.getConstraintError() > 0.)
        {
            //	pCand2 is feasible, pCand1 is not
            return Dominance.DOMINATED;
        }
        else if (this.getConstraintError() <= 0. && ind.getConstraintError() <=
            0.)
        {
            //	Both are feasible, choose the one with the lower rank
            if (this.getNumObjectives() > 1)
            {
                return this.checkDominance(ind);
            }
            else
            {
                if (this.getFitness() < ind.getFitness())
                {
                    return Dominance.DOMINATING;
                }
                else if (ind.getFitness() < this.getFitness())
                {
                    return Dominance.DOMINATED;
                }
                else
                {
                    return Dominance.INCOMPARABLE;
                }
            }
        }
        else
        {
            //	Neither is feasible, choose the one with the least error
            if (this.getConstraintError() < ind.getConstraintError())
            {
                return Dominance.DOMINATING;
            }
            else
            {
                return Dominance.DOMINATED;
            }
        }
    }

    /**
     * Clones the individual.  This is very useful during breeding.
     *
     * @return a clone of the individual.
     */
    @Override
    public Individual clone()
    {
        try
        {
            Individual obj = (Individual) super.clone();

            obj.fitness = new double[this.fitness.length];
            for (int i = 0; i < fitness.length; i++)
            {
                obj.fitness[i] = this.fitness[i];
            }

            obj.adjusted_fitness = this.adjusted_fitness;
            obj.constraint_error = this.constraint_error;
            obj.success = this.success;
            obj.evaluated = this.evaluated;
            obj.prob_recombination = this.prob_recombination;
            obj.prob_mutation = this.prob_mutation;
            obj.user_data = this.user_data;
            obj.pareto_rank = this.pareto_rank;
            obj.crowding_distance = this.crowding_distance;
            obj.pop = this.pop;
            obj.id = ++last_id;
            logger.debug("Cloning, id, lastId" + obj.id + "," + last_id);

            return obj;
        }
        catch (CloneNotSupportedException e)
        {
            logger.error("Clone not supported exception.");
            throw new InternalError(e.toString());
        }
    }

    /**
     * Deep copies the individual.  Deep copies ensure that the individual
     * has its own copies of "deep" attributes, and not just sharing with somebody else.
     *
     * @param  obj individual to be deep copied.
     */
    public void deepCopy(Individual obj)
    {
        for (int i = 0; i < fitness.length; i++)
        {
            this.fitness[i] = obj.fitness[i];
        }

        this.adjusted_fitness = obj.adjusted_fitness;
        this.constraint_error = obj.constraint_error;
        this.success = obj.success;
        this.evaluated = obj.evaluated;
        this.prob_recombination = obj.prob_recombination;
        this.prob_mutation = obj.prob_mutation;
        this.user_data = obj.user_data;
        this.pareto_rank = obj.pareto_rank;
        this.crowding_distance = obj.crowding_distance;
        this.pop = obj.pop;
        this.id = ++last_id;
        logger.debug("Deep-Copying: id, last_id" + this.id + "," + last_id);
    }
}
