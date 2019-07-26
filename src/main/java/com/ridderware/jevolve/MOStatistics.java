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

import org.apache.logging.log4j.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Class for computing and recording statistics for multi-objective evolution.  This
 * class replaces MultiObjectiveStatistics due to its improved checkpointing.  That is,
 * it creates state files by serializing the entire Population object.  
 *
 * @author Jeff Ridder
 */
public class MOStatistics extends Statistics
{
    private double period;

    private double last_output;

    private final static Logger logger = LogManager.getLogger(MOStatistics.class);

    private java.io.File paretoDir;

    private java.io.File paretoFile;

    /**
     *  Constructor for the MOStatistics object
     */
    public MOStatistics()
    {
        this.period = 1.;
        this.last_output = -Double.MAX_VALUE;

        this.paretoDir = new java.io.File(System.getProperty("user.dir") +
            java.io.File.separator);
    }

    /**
     *  Constructor for the MOStatistics object
     * 
     * 
     * 
     * @param paretoDir directory in which to create the pareto output files.
     */
    public MOStatistics(java.io.File paretoDir)
    {
        this.paretoDir = paretoDir;

        this.period = 1.;

        //	Set it to a large double to force it to write the initial.
        this.last_output = -Double.MAX_VALUE;

        if (!paretoDir.isDirectory())
        {
            logger.warn("Multi-Objective Stats arg2 should be a directory.");
            logger.warn("Assuming directory: " + paretoDir.getParent());
            paretoDir = paretoDir.getParentFile();
            logger.warn("Pareto Dir set to: " + paretoDir.getAbsolutePath());
        }
    }

    /**
     * Used to set restart runs to the generation that this run is starting from.
     * @param last_output the generation of the restart file.
     */
    public void setLastOutput(double last_output)
    {
        this.last_output = last_output;
    }

    /**
     * Sets the number of generations between writes of Pareto output files.
     *
     * @param  period number of generations
     */
    public void setPeriod(double period)
    {
        this.period = period;
    }

    /**
     *  Returns the period.
     *
     * @return  period
     */
    public double getPeriod()
    {
        return this.period;
    }

    /**
     * Prints a header over each column in Pareto files.
     *
     * @param  append  true will append to current 'filename'. False will
     *      overwrite.
     */
    @Override
    public void printHeader(boolean append)
    {
        if (getFile() != null)
        {
            String header = "Individual ID #";
            header += "\tPareto Rank";
            header += "\tConstraint Error";
            header += "\tObjective 1 Fitness Score";
            header += "\tObjective 2 Fitness Score";
            header += "\tObjective 3 Fitness Score...";

            string2file(append, header, getFile());
        }
    }

    /**
     *  Outputs Pareto stats. Called on a periodic basis, using the period
     *  set with setPeriod.
     *
     * @param  pop  a Population to output
     */
    @Override
    public void outputGenerationalStats(Population pop)
    {
        String popStats = "";

        if (pop.getProblem().getStepper().getCurrentGeneration() >=
            last_output + period)
        {

            for (Individual ind : pop.getIndividuals())
            {
                popStats += Long.toString(ind.getMyID()) + "\t";
                logger.debug("Individual ID in Population: " + ind.getMyID());
                popStats += Integer.toString(ind.getParetoRank()) + "\t";
                popStats += Double.toString(ind.getConstraintError()) + "\t";
                for (Double fitness : ind.getFitnessArray())
                {
                    popStats += Double.toString(fitness) + "\t";
                }
                popStats += "\n";
            }
            paretoFile = new java.io.File(paretoDir.getAbsolutePath() +
                java.io.File.separator + "pareto_" +
                Double.toString(pop.getProblem().getStepper().
                getCurrentGeneration()) + ".pareto");
            logger.debug("Outputting pareto-file to: " +
                paretoFile.getAbsolutePath());
            string2file(false, popStats, paretoFile);

            try
            {
                FileOutputStream outFile = new FileOutputStream(new File(paretoFile.getParent() + File.separator + "main_" + pop.getProblem().
                    getStepper().getCurrentGeneration() + ".state"));
                ObjectOutputStream outStream = new ObjectOutputStream(outFile);

                outStream.writeObject(pop);

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

            last_output = pop.getProblem().getStepper().getCurrentGeneration();
        }
    }
}

