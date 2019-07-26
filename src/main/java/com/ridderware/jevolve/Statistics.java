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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.logging.log4j.*;

/**
 * A class for collecting and writing the statistics of evolution.
 *
 * @author Jeff Ridder
 */
public class Statistics
{
    private File file;

    private Individual best_ever;

    private final static Logger logger = LogManager.getLogger(Statistics.class);

    /**
     *  Constructor for the Statistics object
     */
    public Statistics()
    {
        this.file = null;
        this.best_ever = null;
    }

    /**
     *  Constructor for the Statistics object
     *
     * @param  file file to write to.
     */
    public Statistics(File file)
    {
        this.file = file;
        this.best_ever = null;
    }

    /**
     *  Constructor for the Statistics object
     *
     * @param  filename Name of file to write to.
     */
    public Statistics(String filename)
    {
        this.file = new File(filename);
        this.best_ever = null;
    }

    /**
     * Returns the statistics file.
     * @return a File object.
     */
    protected File getFile()
    {
        return file;
    }

    /**
     * Returns the sum of all fitness scores for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return the accumulated fitness.
     */
    public static double getSigmaFitness(ArrayList<Individual> individuals)
    {
        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getSigmaFitness) called with a 0-sized population");
        }

        double sigmaFitness = individuals.get(0).getFitness();
        for (int i = 1; i < individuals.size(); i++)
        {
            sigmaFitness += individuals.get(i).getFitness();
        }
        return sigmaFitness;
    }

    /**
     * returns the sum of all adjusted fitness scores for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return accumulated adjusted fitness.
     */
    public static double getSigmaAdjustedFitness(ArrayList<Individual> individuals)
    {

        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getSigmaAdjustedFitness) called with a 0-sized population");
        }

        double sigmaAdjustedFitness = individuals.get(0).getAdjustedFitness();
        for (int i = 1; i < individuals.size(); i++)
        {
            sigmaAdjustedFitness += individuals.get(i).getAdjustedFitness();
        }

        return sigmaAdjustedFitness;
    }

    /**
     * Returns the index of the individual with the largest fitness.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return  the index of the individual with the highest fitness.
     */
    public static int getMaxFitnessIndex(ArrayList<Individual> individuals)
    {

        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getMaxFitnessIndex) called with a 0-sized population");
        }

        int maxFitInd = 0;
        double maxFitness = individuals.get(0).getFitness();

        for (int i = 1; i < individuals.size(); i++)
        {
            if (individuals.get(i).getFitness() > maxFitness)
            {
                maxFitness = individuals.get(i).getFitness();
                maxFitInd = i;
            }
        }
        return maxFitInd;
    }

    /**
     * Returns the best ever individual.
     *
     * @return  best ever individual.
     */
    public Individual getBestEver()
    {
        return best_ever;
    }

    /**
     * Resets the best ever individual to null, forcing a re-discovery of the
     * best individual the next time stats are computed.  This is useful for
     * dynamic fitness landscapes in which the best individual for an earlier
     * landscape may no longer be valid.
     */
    public void resetBestEver()
    {
        best_ever = null;
    }

    /**
     * Returns the index of the individual with the smallest fitness.
     *
     * @param  individuals an ArrayList of Individuals representing a population.
     * @return index of the individual with the lowest fitness.
     */
    public static int getMinFitnessIndex(ArrayList<Individual> individuals)
    {

        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getMinFitnessIndex) called with a 0-sized population");
        }

        int minFitInd = 0;
        double minFitness = individuals.get(0).getFitness();

        for (int i = 1; i < individuals.size(); i++)
        {
            if (individuals.get(i).getFitness() < minFitness)
            {
                minFitness = individuals.get(i).getFitness();
                minFitInd = i;
            }
        }

        return minFitInd;
    }

    /**
     *  Returns the average fitness for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return the average fitness of the population.
     */
    public static double getAvgFitness(ArrayList<Individual> individuals)
    {
        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getAvgFitness) called with a 0-sized population");
        }

        return (getSigmaFitness(individuals) / individuals.size());
    }

    /**
     * Returns the average adjusted fitness for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return  the average adjusted fitness of the population.
     */
    public static double getAvgAdjustedFitness(ArrayList<Individual> individuals)
    {
        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getAvgAdjustedFitness) called with a 0-sized population");
        }

        return (getSigmaAdjustedFitness(individuals) / individuals.size());
    }

    /**
     * Returns the standard deviation of fitness for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return the standard deviation of the fitness of the population.
     */
    public static double getFitnessStndDev(ArrayList<Individual> individuals)
    {
        if (individuals.isEmpty())
        {
            logger.warn("Error. Statistics (getAvgFitnessStndDev) called with a 0-sized population");
        }

        double numerator = 0.;
        double avgFitness = getAvgFitness(individuals);

        for (Individual ind : individuals)
        {
            numerator += Math.pow((ind.getFitness() - avgFitness), 2.);
        }

        double variance = numerator / (individuals.size() - 1.);

        return (Math.sqrt(variance));
    }

    /**
     * Returns the standard deviation of the adjusted fitness for the individuals.
     *
     * @param  individuals  an ArrayList of Individuals representing a population.
     * @return the standard deviation of the adjusted fitness of the population.
     */
    public static double getAdjFitnessStndDev(ArrayList<Individual> individuals)
    {

        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (getAdjFitnessStndDev) called with a 0-sized population");
        }

        double numerator = 0.;
        double avgAdjFitness = getAvgAdjustedFitness(individuals);

        for (Individual ind : individuals)
        {
            numerator += Math.pow((ind.getFitness() - avgAdjFitness), 2.);
        }

        double variance = numerator / (individuals.size() - 1.);

        return Math.sqrt(variance);
    }

    /**
     * Prints a header.
     *
     * @param  append  true will append to existing 'filename'. False will overwrite.
     */
    public void printHeader(boolean append)
    {
        if (file != null)
        {
            String header = "\"Generation #\"";
            header += "\t\"Minimum Fitness Score\"";
            header += "\t\"Average Fitness Score\"";
            header += "\t\"Maximum Fitness Score\"";
            header += "\t\"Average Fitness Score Standard Deviation\"";

            string2file(append, header, file);
        }
    }

    /**
     *  Outputs the generational statistics for the population.
     *
     * @param  pop population for which statistics are to be generated and written.
     */
    public void outputGenerationalStats(Population pop)
    {
        ArrayList<Individual> individuals = pop.getIndividuals();

        assert (pop != null) : "Population is null";
        assert (pop.getProblem() != null) : "Population's Problem is null";
        assert (pop.getProblem().getStepper() != null) :
            "Population's Problem's Stepper is null";

        double generation = pop.getProblem().getStepper().getCurrentGeneration();

        if (file == null)
        {
            return;
        }

        if (individuals.isEmpty())
        {
            logger.error("Error. Statistics (outputGenerationalStats) called with a 0-sized population");
        }

        int minFitInd = getMinFitnessIndex(individuals);

        if (best_ever == null || individuals.get(minFitInd).getFitness() <
            best_ever.getFitness())
        {
            best_ever = individuals.get(minFitInd).clone();
        }

        //generation #
        String line = "  " + generation;
        //minimum fitness score
        line += "\t\t\t" +
            (individuals.get(minFitInd)).getFitness();
        //average fitness
        line += "\t\t\t" + getAvgFitness(individuals);
        //maximum fitness score
        line += "\t\t\t" + individuals.get(getMaxFitnessIndex(individuals)).
            getFitness();
        //average fitness standard deviation
        line += "\t\t\t" + getFitnessStndDev(individuals);

        string2file(true, line, file);
    }

    /**
     * Writes a string to a file.
     *
     * @param  append  true appends data, false overwrites.
     * @param  line    line to write to file.
     * @param  file file to write to.
     * @return a boolean : returns true if there was a IOException.
     */
    protected boolean string2file(boolean append, String line, File file)
    {
        if (file == null)
        {
            return true;
        }

        boolean error = false;
        line.trim();

        try
        {
            FileWriter fw = new FileWriter(file, append);
            PrintWriter outFile = new PrintWriter(fw);

            outFile.println(line);
            outFile.close();
        }
        catch (IOException e)
        {
            logger.error("ERROR - Could not write to file: " +
                file.getAbsolutePath());
            logger.error("IO Exception: " + e);
            error = true;
        }

        return error;
    }
}

