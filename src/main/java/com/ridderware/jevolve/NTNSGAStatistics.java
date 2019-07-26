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

import java.io.File;
import org.apache.logging.log4j.*;

/**
 *
 * @author Jeff Ridder
 */
public class NTNSGAStatistics extends MultiObjectiveStatistics
{
    private double last_output;

    private File paretoDir;

    private File paretoFile;

    private final static Logger logger =
        LogManager.getLogger(NTNSGAStatistics.class);

    /** Creates a new instance of NTNSGAStatistics */
    public NTNSGAStatistics()
    {
        super();
        this.last_output = -Double.MAX_VALUE;

        this.paretoDir = new java.io.File(System.getProperty("user.dir") +
            java.io.File.separator);
    }

    /**
     *  Constructor for the NTNSGAStatistics object
     *
     * @param  paretoDir directory in which to create the pareto output files.
     */
    public NTNSGAStatistics(java.io.File paretoDir)
    {
        super(paretoDir);
        this.paretoDir = paretoDir;
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
            header += "\tNum Evals";
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
            last_output + getPeriod())
        {

            for (Individual ind : pop.getIndividuals())
            {
                assert (ind instanceof EvaluationCountingGAIndividual);
                EvaluationCountingGAIndividual ec_ind =
                    (EvaluationCountingGAIndividual) ind;

                popStats += Long.toString(ind.getMyID()) + "\t";
                logger.debug("Individual ID in Population: " + ind.getMyID());
                popStats += Integer.toString(ind.getParetoRank()) + "\t";
                popStats += Integer.toString(ec_ind.getTotalNumEvaluations()) +
                    "\t";
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
            pop.population2file(new File(paretoFile.getParent() + File.separator +
                "state_" + pop.getProblem().getStepper().getCurrentGeneration() +
                ".state"));

            last_output = pop.getProblem().getStepper().getCurrentGeneration();
        }
    }
}
