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

import java.io.*;
import java.util.*;
import org.apache.logging.log4j.*;

/**
 * Base class for all populations of individuals.
 *
 * @author Jeff Ridder
 */
public class Population implements Cloneable, Serializable
{
    private Individual prototype;

    private int max_pop_size;

    private boolean elitist;

    private int max_elites;

    private boolean force_evaluation;

    private ArrayList<Individual> elites = new ArrayList<Individual>();

    private ArrayList<Individual> individuals = new ArrayList<Individual>();

    private Problem problem;

    private int num_evaluated;

    private boolean done;

    private final static Logger logger = LogManager.getLogger(Population.class);

    /**
     *  Constructor for the Population object
     *
     * @param  prototype prototype individual for the population.
     */
    public Population(Individual prototype)
    {
        this.prototype = prototype;
        this.max_pop_size = 0;

        this.elitist = false;
        this.max_elites = 0;

        this.num_evaluated = 0;
        this.done = false;
        this.force_evaluation = false;

        this.problem = null;
    }

    /**
     * Sets whether or not to use elitism for this population.
     *
     * @param  elitist true if elitist, false otherwise.
     */
    public void setElitist(boolean elitist)
    {
        this.elitist = elitist;
    }

    /**
     * Sets the maximum number of elite individuals for the population.
     *
     * @param  max_elites max number of elites.
     */
    public void setMaxNumberOfElites(int max_elites)
    {
        this.max_elites = max_elites;
    }

    /**
     * Sets the maximum size of this population.
     *
     * @param  max_pop_size max number of individuals in the population.
     */
    public void setMaxPopulationSize(int max_pop_size)
    {
        this.max_pop_size = max_pop_size;
    }

    /**
     * Set whether or not to force evaluation of individuals, even if they have
     * been previously evaluated.
     *
     * @param  force_evaluation true if evaluation is to be forced, false otherwise.
     */
    public void setForceEvaluation(boolean force_evaluation)
    {
        this.force_evaluation = force_evaluation;
    }

    /**
     * Returns whether or not we are forcing the evaluation of individuals regardless
     * of whether they have been previously evaluated.
     *
     * @return true if evaluation is forced, false otherwise.
     */
    public boolean getForceEvaluation()
    {
        return this.force_evaluation;
    }

    /**
     * Sets the Problem for the population.
     *
     * @param  problem problem.
     */
    public void setProblem(Problem problem)
    {
        this.problem = problem;
    }

    /**
     * Sets the number of individuals that were evaluated in this population.
     *
     * @param  num_evaluated number of evaluated individuals.
     */
    public void setNumberEvaluated(int num_evaluated)
    {
        this.num_evaluated = num_evaluated;
    }

    /**
     *  Returns whether this population is elitist.
     *
     * @return true if elitist, false otherwise.
     */
    public boolean getElitist()
    {
        return this.elitist;
    }

    /**
     * Returns the maximum number of elites this population will maintain.
     *
     * @return max number of elites.
     */
    public int getMaxNumberOfElites()
    {
        return this.max_elites;
    }

    /**
     *  Returns the problem for the population.
     *
     * @return    problem.
     */
    public Problem getProblem()
    {
        return this.problem;
    }

    /**
     * Returns the population of individuals.
     *
     * @return an ArrayList of the individuals in the population.
     */
    public ArrayList<Individual> getIndividuals()
    {
        return individuals;
    }

    /**
     *  Returns the current population size.
     *
     * @return current population size.
     */
    public int getPopulationSize()
    {
        return this.individuals.size();
    }

    /**
     * Returns the maximum population size.
     *
     * @return max population size.
     */
    public int getMaxPopulationSize()
    {
        return this.max_pop_size;
    }

    /**
     * Returns the prototype individual for the population.
     *
     * @return prototype
     */
    public Individual getPrototype()
    {
        return this.prototype;
    }

    /**
     * Returns whether or not this population is done evolving.  Typically, a
     * population will be "done" if there exists an individual in the population
     * that has been flagged as successful.
     *
     * @return true if done, false otherwise.
     */
    public boolean getDone()
    {
        return done;
    }

    /**
     * Sets the done condition for the population.  If "done", evolution will stop because...
     * well...we're done!
     *
     * @param done true if done, false otherwise.
     */
    public void setDone(boolean done)
    {
        this.done = done;
    }

    /**
     *  Returns the specified individual.
     *
     * @param  index index of requested individual.
     * @return   individual.
     */
    public Individual getIndividual(int index)
    {
        return individuals.get(index);
    }

    /**
     * Returns the number of evaluated individuals in the population.
     *
     * @return number of evaluated individuals.
     */
    public int getNumberEvaluated()
    {
        return num_evaluated;
    }

    /**
     *  Returns the elites.
     *
     * @return an ArrayList of elite individuals.
     */
    protected ArrayList<Individual> getElites()
    {
        return this.elites;
    }

    /**
     * De-serializes the population from the specified file.
     *
     * @param  file file from which to read the population.
     */
    public void file2population(java.io.File file)
    {
        try
        {
            FileInputStream inFile = new FileInputStream(file);
            ObjectInputStream inStream = new ObjectInputStream(inFile);

            individuals.clear();

            this.getProblem().getStepper().setCurrentGeneration(inStream.readDouble());
            this.inStream2arrayList(inStream, individuals);

            if (elitist)
            {
                elites.clear();
                inStream2arrayList(inStream, elites);
            }
            inFile.close();
            inStream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  Serializes the population to the specified file.
     *
     * @param  file file to which to write the population.
     */
    public void population2file(java.io.File file)
    {
        try
        {
            FileOutputStream outFile = new FileOutputStream(file);
            ObjectOutputStream outStream = new ObjectOutputStream(outFile);

            logger.debug("Writing double generation from Population @ gen: " + this.getProblem().
                getStepper().getCurrentGeneration());
            outStream.writeDouble(this.getProblem().getStepper().
                getCurrentGeneration());
            arrayList2outStream(outStream, individuals);

            if (elitist)
            {
                arrayList2outStream(outStream, elites);
            }

            outStream.flush();
            outStream.close();
            outFile.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Finds and maintains the elite individuals.
     */
    public void processElites()
    {
        int normal_pop_size = individuals.size();

        for (Individual elite : elites)
        {
            this.addIndividual(elite);
        }

        this.sortFitness();

        this.clearElites();

        //    Find elites
        for (int i = 0; i < max_elites; i++)
        {
            Individual ind = individuals.get(i).clone();
            elites.add(ind);
        }

        int num = individuals.size() - normal_pop_size;
        for (int i = num - 1; i >= 0; i--)
        {
            individuals.remove(normal_pop_size + i);
        }

        if (elites.size() != max_elites)
        {
            logger.error("Num elites " + elites.size() + " is wrong");
        }

        if (individuals.size() != normal_pop_size)
        {
            logger.error("Population size " + individuals.size() + " is wrong");
        }
    }

    /**
     * Clears all individuals from the population.
     */
    public void clearPopulation()
    {
        individuals.clear();
    }

    /**
     * Evaluates the population using the specified evaluator.
     *
     * @param  evaluator an EvaluatorInterface object.
     */
    public void evaluate(EvaluatorInterface evaluator)
    {
        standaloneEvaluate(evaluator);
    }

    /**
     * Adds the specified individual to the population.
     * @param  ind individual to add to the population.
     */
    public void addIndividual(Individual ind)
    {
        if (!individuals.contains(ind))
        {
            individuals.add(ind);
            ind.setPopulation(this);
        }
    }

    /**
     * Inserts the specified individual in the population at the specified location.
     * @param i index position at which to insert individual.
     * @param  ind individual to add to the population.
     */
    public void addIndividual(int i, Individual ind)
    {
        if (!individuals.contains(ind))
        {
            individuals.add(i, ind);
            ind.setPopulation(this);
        }
    }

    /**
     * Removes the specified individual from the population.
     *
     * @param  ind individual to remove from the population.
     */
    public void removeIndividual(Individual ind)
    {
        individuals.remove(ind);
    }

    /**
     * Removes the individual from the population specified by index.
     *
     * @param  index index of individual to remove.
     */
    public void removeIndividual(int index)
    {
        if (index < individuals.size() && index >= 0)
        {
            individuals.remove(index);
        }
    }

    /**
     * Sorts the population by fitness.
     */
    public void sortFitness()
    {
        Collections.sort(individuals, new FitnessComparator());
    }

    /**
     * Adds the specified individual to the elites.
     * @param  elite elite individual.
     */
    public void addElite(Individual elite)
    {
        if (!elites.contains(elite))
        {
            elites.add(elite);
        }
    }

    /**
     * Adds the specified individuals to the elites.
     * @param  elites elite individuals.
     */
    public void addElites(ArrayList<Individual> elites)
    {
        this.elites.addAll(elites);
    }

    /**
     * Removes the specified individual from the elites.
     *
     * @param  elite elite to be removed.
     */
    public void removeElite(Individual elite)
    {
        elites.remove(elite);
    }

    /**
     * Clears all elites.
     */
    public void clearElites()
    {
        elites.clear();
    }

    /**
     * Clones the population.
     *
     * @return  newly cloned population.
     */
    @Override
    public Population clone()
    {
        try
        {
            Population obj = (Population) super.clone();

            obj.prototype = this.prototype.clone();

            obj.max_pop_size = this.max_pop_size;

            obj.individuals = new ArrayList<Individual>();
            obj.elites = new ArrayList<Individual>();

            obj.problem = this.problem;

            //        Don't clone elites
            for (Individual i : this.individuals)
            {
                obj.addIndividual(i.clone());
            }

            return obj;
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.toString());
        }
    }

    /**
     * Prints the population in an ASCII readable form.
     *
     * @param  dir directory in which to write the file.
     */
    public void population2readableFile(String dir)
    {
        ArrayList<String> outputFile = new ArrayList<String>(100);

        outputFile.add("Printing population at generation # " +
            this.getProblem().getStepper().getCurrentGeneration() +
            " NumIndividuals = " +
            individuals.size());
        outputFile.add("ID\tFitnesses\tGenes");
        for (Individual ind : individuals)
        {
            String temps = Long.toString(ind.getMyID()) + "\t";

            for (Double fit : ind.getFitnessArray())
            {
                temps += Double.toString(fit) + "\t";
            }

            for (Double gene : ((GAIndividual<Double>) ind).getGenotype())
            {
                temps += Double.toString(gene) + "\t";
            }

            outputFile.add(temps.trim());
        }
        arrayList2file(new File(dir + File.separator +
            this.getProblem().getStepper().getCurrentGeneration() +
            ".population"), outputFile);
    }

    /**
     * Evaluates the entire population on a single processor.
     *
     * @param  evaluator EvaluatorInterface object.
     */
    private void standaloneEvaluate(EvaluatorInterface evaluator)
    {
        num_evaluated = 0;
        done = false;
        for (Individual i : individuals)
        {
            if (!i.getEvaluated() || force_evaluation)
            {
                evaluator.evaluateConstraints(i);
                evaluator.evaluateFitness(i);

                i.setEvaluated(true);

                ++num_evaluated;
            }

            if (i.getSuccess())
            {
                done = true;
            }
        }
    }

    /**
     * Instantiates the individuals ArrayList from an input stream.
     *
     * @param  inStream   an ObjectInputStream
     * @param  arrayList  an ArrayList
     */
    private void inStream2arrayList(ObjectInputStream inStream,
        ArrayList<Individual> arrayList)
    {
        int genotypeSize;
        try
        {
            int popSize = inStream.readInt();
            for (int i = 0; i < popSize; i++)
            {
                arrayList.get(i).setMyID(inStream.readLong());
                genotypeSize = inStream.readInt();
                for (int j = 0; j < genotypeSize; j++)
                {
                    ((GAIndividual) arrayList.get(j)).getGenotype().add(inStream.readDouble());
                }
            }
        }
        catch (IOException e)
        {
            logger.error("inStream2arrayList reading saved population IOException");
            e.printStackTrace();
        }
    }

    /**
     * Writes the individuals to an output stream.
     *
     * @param  outStream  an ObjectOutputStream
     * @param  arrayList  an ArrayList
     */
    private void arrayList2outStream(ObjectOutputStream outStream,
        ArrayList<Individual> arrayList)
    {
        try
        {
            outStream.writeInt(arrayList.size());
            for (Individual ind : arrayList)
            {
                String temps = Long.toString(ind.getMyID()) + " " +
                    ind.getGenotypeSize() + " ";
                outStream.writeLong(ind.getMyID());
                outStream.writeInt(ind.getGenotypeSize());
                for (Double gene : ((GAIndividual<Double>) ind).getGenotype())
                {
                    outStream.writeDouble(gene);
                }
                logger.debug(temps);
            }
        }
        catch (IOException e)
        {
            logger.error("arrayList2outStream saving population IOException. ");
            e.printStackTrace();
        }
    }

    /**
     * Writes an array list to a file.
     *
     * @param  file file to write to.
     * @param  file_data ArrayList of strings to write.
     * @return  true if there was an IOException.
     */
    private boolean arrayList2file(File file, ArrayList<String> file_data)
    {
        boolean error = false;

        try
        {
            FileWriter fw = new FileWriter(file);
            PrintWriter outFile = new PrintWriter(fw);

            file_data.trimToSize();

            int size = file_data.size();
            for (int index = 0; index < size; index++)
            {
                outFile.println(file_data.get(index));
            }

            outFile.close();
        }
        catch (IOException e)
        {
            System.err.println("ERROR, IOException - Could not write file: " +
                file);
            e.printStackTrace();
            error = true;
        }

        return error;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeObject(prototype);
        out.writeInt(max_pop_size);
        out.writeBoolean(elitist);
        out.writeInt(max_elites);
        out.writeBoolean(force_evaluation);
        out.writeObject(elites);
        out.writeObject(individuals);
        out.writeInt(num_evaluated);
        out.writeBoolean(done);
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        prototype = (Individual) in.readObject();
        max_pop_size = in.readInt();
        elitist = in.readBoolean();
        max_elites = in.readInt();
        force_evaluation = in.readBoolean();
        elites = (ArrayList<Individual>) in.readObject();
        individuals = (ArrayList<Individual>) in.readObject();
        num_evaluated = in.readInt();
        done = in.readBoolean();
    }

    /**
     * A comparator for supporting sorting of the population based on fitness.
     */
    protected class FitnessComparator implements Comparator<Individual>
    {
        private int index;

        /**
         *  Constructor for the FitnessComparator object
         */
        public FitnessComparator()
        {
            this.index = 0;
        }

        /**
         *  Constructor for the FitnessComparator object
         *
         * @param  index index of fitness value to use for comparison.
         */
        public FitnessComparator(int index)
        {
            this.index = index;
        }

        /**
         * Compares the two individuals and returns the result needed by the sort.
         *
         * @param  o1 first individual
         * @param  o2 second individual
         * @return -1 if o1 < o2, 0 if o1 == o2, 1 if o1 > o2
         */
        public int compare(Individual o1, Individual o2)
        {
            if (o1.getFitness(index) < o2.getFitness(index))
            {
                return -1;
            }
            else if (o1.getFitness(index) == o2.getFitness(index))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    /**
     * A comparator for supporting sorting of individuals based on crowding distance.
     */
    protected class CrowdingComparator implements Comparator<Individual>
    {
        /**
         * Compares the two individuals and returns the result.
         *
         * @param  o1 first individual
         * @param  o2 second individual
         * @return -1 if o1 > o2, 0 if o1 == o2, 1 if o1 < o2
         */
        public int compare(Individual o1, Individual o2)
        {
            if (o1.getCrowdingDistance() > o2.getCrowdingDistance())
            {
                return -1;
            }
            else if (o1.getCrowdingDistance() == o2.getCrowdingDistance())
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }
}

