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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Generic class for genetic algorithm individuals.  This class extends the
 * base Individual class by adding attributes for genomes and genotypes.
 *
 * @param <E> gene type
 * @author Jeff Ridder
 */
public class GAIndividual<E> extends Individual
{
    private ArrayList<GAGene<E>> genome = new ArrayList<GAGene<E>>();

    private ArrayList<E> genotype = new ArrayList<E>();

    private final static Logger logger = LogManager.getLogger(GAIndividual.class);

    /**
     * Constructor for the GAIndividual object
     */
    public GAIndividual()
    {
        super();
    }

    /**
     * Constructor for the GAIndividual object
     *
     * @param numObjectives number of objectives
     */
    public GAIndividual(int numObjectives)
    {
        super(numObjectives);
    }

    /**
     * Sets an element in the genotype at i to the specified value.
     *
     * @param  i array index of genotype.
     * @param  value value to set.
     */
    public void setValue(int i, E value)
    {
        genotype.set(i, value);
    }

    /**
     * Returns the entire genome.
     *
     * @return ArrayList of GAGene objects.
     */
    public ArrayList<GAGene<E>> getGenome()
    {
        return genome;
    }

    /**
     * Returns the entire genotype.
     *
     * @return  ArrayList of genotype values.
     */
    public ArrayList<E> getGenotype()
    {
        return genotype;
    }

    /**
     * Returns the size of the genotype.
     *
     * @return  number of values in the genotype.
     */
    public int getGenotypeSize()
    {
        return genotype.size();
    }

    /**
     * Returns the genotype value at the specified index.
     *
     * @param  i index of genotype value to retrieve.
     * @return  genotype value.
     */
    public E getValue(int i)
    {
        return genotype.get(i);
    }

    /**
     * Returns the genotype as a string of space delimited values.
     *
     * @return  string containing the genotype.
     */
    public String getGenotypeString()
    {
        String genotypeS = "";
        DecimalFormat fmt;

        if (genotype.size() == 0)
        {
            logger.debug("Cannot return genotype string, genotype doesn't exist.");
            System.exit(1);
        //for debug
        }
        else
        {
            if (genotype.get(0) instanceof Integer)
            {
                fmt = new DecimalFormat("0");
            }
            else
            {
                fmt = new DecimalFormat("0000.0000");
            }
            for (int i = 0; i < genotype.size(); i++)
            {
                genotypeS += fmt.format(genotype.get(i)) + " ";
            }
        }

        return genotypeS;
    }

    /**
     * Clones the individual.  This is very useful during breeding.
     *
     * @return a clone of the individual.
     */
    @Override
    public GAIndividual<E> clone()
    {
        GAIndividual<E> obj = (GAIndividual<E>) super.clone();

        obj.genome = this.genome;

        obj.genotype = (ArrayList<E>) this.genotype.clone();

        return obj;
    }

    /**
     * Deep copies the individual.  Deep copies ensure that the individual
     * has its own copies of "deep" attributes, and not just sharing with somebody else.
     *
     * @param  obj individual to be deep copied.
     */
    @Override
    public void deepCopy(Individual obj)
    {
        super.deepCopy(obj);

        GAIndividual<E> ga_obj = (GAIndividual<E>) obj;

        this.genome = ga_obj.genome;

        this.genotype = (ArrayList<E>) ga_obj.genotype.clone();
    }

    /**
     * Initializes the individual with a genotype of values generated using
     * a uniform distribution.
     */
    public void initialize()
    {
        genotype.clear();

        for (GAGene<E> g : genome)
        {
            genotype.add(g.randomUniformValue());
        }
    }

//    /**
//     * Writes the genotype to the specified file.  Use this for persistence.
//     *
//     * @param  file File object.
//     * @return true if there was an error in writing, false otherwise.
//     */
//    public boolean genotype2file(File file)
//    {
//        boolean error = false;
//        String genotypeS = "";
//        
//        for (int i = 0; i < genotype.size(); i++)
//        {
//            genotypeS += genotype.get(i) + " ";
//        }
//        genotypeS.trim();
//        
//        try
//        {
//            FileWriter fw = new FileWriter(file, false);
//            PrintWriter outFile = new PrintWriter(fw);
//            
//            outFile.println(genotypeS);
//            outFile.close();
//        }
//        catch (IOException e)
//        {
//            logger.error("ERROR - Could not write to file: " + file.getAbsolutePath());
//            logger.error("IO Exception: " + e);
//            error = true;
//        }
//        
//        return error;
//    }
    /**
     * Measures the distance between individuals.  This can be used as a measure
     * of "crowding", such as that to support Deterministic Crowding.  In this
     * implementation, the distance is simply the number of genotype values that
     * are different between this and the input individuals.
     *
     * @param  ind individual to which my distance is to be measured.
     * @return  distance.
     */
    public double genotypeDistance(Individual ind)
    {
        GAIndividual<E> src = (GAIndividual<E>) ind;

        double distance = 0.;
        for (int i = 0; i < src.getGenotype().size(); i++)
        {
            if (this.genotype.get(i) != src.getGenotype().get(i))
            {
                distance += 1.;
            }
        }

        return distance;
    }
}


