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
package com.ridderware.jevolve.examples.zdt1;

import  com.ridderware.jevolve.GAIndividual;
import java.io.*;
import java.util.ArrayList;


public class TrueParetoFinder
{
    public static void main(String args[])
    {
        if (args.length != 1)
        {
            System.err.println("Expected a generation # for an argument.");
            System.exit(1);
        }
        
        File stateFile = new File(System.getProperty("user.dir")+File.separator+"state_"+args[0]+".0.state");
        ArrayList<GAIndividual<Double>> individuals = new ArrayList<GAIndividual<Double>>(100);

        try
        {
            FileInputStream inFile = new FileInputStream(stateFile);
            ObjectInputStream inStream = new ObjectInputStream(inFile);
            
            //generation of this population
            inStream.readDouble();
            
            inStream2arrayList(inStream, individuals);
            
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
        
        
        ArrayList<String> outputData = new ArrayList<String>(100);
        
        
        //Now we've got the population.
        
        String outputLine;
        for (GAIndividual<Double> ind : individuals)
        {
            evaluateFitness(ind);
            
            outputLine = "";
            
            for (Double score : ind.getFitnessArray())
            {
                outputLine+=(score + "\t");
            }
            outputData.add(outputLine);
        }
        
        File outputFile = new File("true_pareto_"+args[0]+".pareto");
        arrayList2file(outputFile, outputData);
        System.out.println("Created: "+ outputFile.getAbsolutePath());
    }
    
    
    /**
     * Method evaluateFitness
     *
     * @param    ind                 an Individual
     *
     */
    private static void evaluateFitness(GAIndividual<Double> ga_ind)
    {
        double g = 0.;
        for ( int i = 1; i < ga_ind.getGenotypeSize(); i++ )
        {
            g += ga_ind.getValue(i);
        }
        g = 1. + 9.*g/(ga_ind.getGenotypeSize()-1);
        
        ga_ind.getFitnessArray()[0] = ga_ind.getValue(0);
        ga_ind.getFitnessArray()[1] = g*(1.-Math.sqrt(ga_ind.getValue(0)/g));
        //setup to minimize
    }
    
    
    /**
     *  Method inStream2arrayList
     *
     * @param  inStream   an ObjectInputStream
     * @param  arrayList  an ArrayList
     */
    private static void inStream2arrayList(ObjectInputStream inStream, ArrayList<GAIndividual<Double>> arrayList)
    {
        int genotypeSize;
        try
        {
            int popSize = inStream.readInt();
            for (int i = 0; i < popSize; i++)
            {
                arrayList.add(new GAIndividual<Double>(2));
                arrayList.get(i).setMyID(inStream.readLong());
                
                genotypeSize = inStream.readInt();
                
                for (int j = 0; j < genotypeSize; j++)
                {
                    arrayList.get(i).getGenotype().add(inStream.readDouble());
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("IOException while reading population in from ObjectInputStream.");
            System.err.println("Check the format of the output file (and thus input stream).");
            e.printStackTrace();
        }
    }
    
    
    /**
     *  Method ArrayList2file
     *
     * @param  file_data  an ArrayList
     * @return            a boolean, true iff there was an IOException.
     */
    private static boolean arrayList2file(File file, ArrayList<String> file_data)
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
                outFile.println(file_data.get(index).trim());
            }
            
            outFile.close();
        }
        
        catch (IOException e)
        {
            System.err.println("ERROR, IOException - Could not write file: " + file);
            e.printStackTrace();
            error = true;
        }
        
        return error;
    }
}

