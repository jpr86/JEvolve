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
package com.ridderware.jevolve.examples.dejong;

import com.ridderware.jevolve.GAIndividual;
import com.ridderware.jevolve.Individual;
import com.ridderware.jevolve.Population;
import com.ridderware.jevolve.EvaluatorInterface;
import com.ridderware.jevolve.RouletteWheelSelection;
import com.ridderware.jevolve.Selector;
import com.ridderware.jrandom.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.Observable;

/**
 * An evaluator for the one-max problem.
 *
 * @author Jeff Ridder
 */
public class DeJongEval extends Observable implements EvaluatorInterface
{
    private double[][] a =
    {
        {
            -32.0, -16.0, 0.0, 16.0, 32.0
        },
        {
            -32.0, -16.0, 0.0, 16.0, 32.0
        }
    };

    private int f;

    private Selector selector;

    public DeJongEval(int f, Selector s)
    {
        this.f = f;
        this.selector = s;
    }

    /**
     * Method to evaluate constraints.  Empty.
     *
     * @param  ind individual whose constraints won't be evaluated.
     */
    public void evaluateConstraints(Individual ind)
    {
    }

    /**
     * Preevaluates the population.  Empty.
     *
     * @param  pop population that won't be preevaluated.
     */
    public void preevaluate(Population pop)
    {
    }

    /**
     * Postevaluates the population.  Empty.
     *
     * @param  pop population that won't be postevaluated.
     */
    public void postevaluate(Population pop)
    {
        //  Updates graph
        super.setChanged();
        super.notifyObservers(pop);

        if (selector instanceof RouletteWheelSelection)
        {
            //  We need to compute population adjusted fitness.
            double sumAdjFit = 0.0;
            for (int i = 0; i < pop.getPopulationSize(); i++)
            {
                sumAdjFit += pop.getIndividual(i).getAdjustedFitness();
            }

            ((RouletteWheelSelection) selector).setPopAdjustedFitness(sumAdjFit);
        }
    }

    /**
     *  Evaluates fitness.
     *
     * @param  ind individual to evaluate.
     */
    public void evaluateFitness(Individual ind)
    {
        GAIndividual<Integer> ga_ind = (GAIndividual<Integer>) ind;

        double fitness = Double.MAX_VALUE;

        switch (f)
        {
            case 1:
                fitness = f1(ga_ind.getGenotype());
                break;
            case 2:
                fitness = f2(ga_ind.getGenotype());
                break;
            case 3:
                fitness = f3(ga_ind.getGenotype());
                break;
            case 4:
                fitness = f4(ga_ind.getGenotype());
                break;
            case 5:
                fitness = f5(ga_ind.getGenotype());
                break;
            default:
        }
//
//        if (fitness == 0.0)
//        {
//            ga_ind.setSuccess(true);
//        }


        ga_ind.setFitness(fitness);
    }

    /* Decode the bit string */
    private int decode(ArrayList<Integer> c, int j, int k)
    {
        int sum, x, n;

        sum = 0;
        n = 1;
        for (x = k - j; x >= 0; x--)
        {
            if (c.get(x + j) == 1)
            {
                sum = sum + n;
            }
            n = n * 2;
        }
        return (sum);
    }


    /* 3 variables, 10 bits/variable. */
    private double f1(ArrayList<Integer> c)
    {
        int x;
        double sum;

//        double solution = 78.6;

        sum = 0.0;
        for (x = 0; x <= 2; x++)
        {
            sum = sum + Math.pow(((double) (decode(c, (x * 10), (x * 10) +
                9) -
                512) / 100.0), 2.0);
        }

        return (sum);
    }

    /* 2 variables, 12 bits/variable. */
    private double f2(ArrayList<Integer> c)
    {
        double x1, x2;

//        double solution = 3905.93;

        x1 = (double) (decode(c, 0, 11) - 2048) / 1000.0;
        x2 = (double) (decode(c, 12, 23) - 2048) / 1000.0;

        return (((100.0 * Math.pow(Math.pow(x1, 2.0) - x2, 2.0)) +
            Math.pow(1.0 - x1, 2.0)));
    }

    /* 5 variables, 10 bits/variable. */
    private double f3(ArrayList<Integer> c)
    {
        int x;
        int sum;
        double temp;

//        double solution = 25.0;

        sum = 0;
        for (x = 0; x <= 4; x++)
        {
            temp = (double) (decode(c, (x * 10), (x * 10) + 9) - 512) /
                100.0;
            if (temp > 0.0)
            {
                sum = sum + (int) temp;
            }
            else
            {
                sum = sum + (int) temp - 1;
            }
        }

        return ((double) sum);
    }

    /* 30 variables, 8 bits/variable. ssrand() needs to be defined to be
    a (0,1) gaussian random variable */
    private double f4(ArrayList<Integer> c)
    {
        int x;
        double sum;
        double temp, temp2, temp4;

//        double solution = 1248.2;

        sum = 0.0;
        for (x = 0; x <= 29; x++)
        {
            temp =
                ((double) (decode(c, (x * 8), (x * 8) + 7) - 128) / 100.0);
            temp2 = temp * temp;
            temp4 = temp2 * temp2;
            sum = sum + ((double) x * temp4) + MersenneTwisterFast.getInstance().
                nextGaussian();
        }

        return (sum);
    }

    /* 2 variables, 17 bits/variable. */
    private double f5_j(ArrayList<Integer> c, int j)
    {
        double save, temp, temp2, temp6;

        temp = ((double) (decode(c, 0,
            16) - 65536) / 1000.0) - a[0][j % 5];
        temp2 = temp * temp;
        temp6 = temp2 * temp2 * temp2;
        save = temp6;

        /* The following error was pointed out to me by Andrew Czarn on
        Dec 11, 2000. Apologies for the typo! */
        /*temp = ((double)(decode(i, 18, 34) - 65536) / 1000.0) - f5_a[1][j % 5];*/
        temp = ((double) (decode(c, 17, 33) - 65536) / 1000.0) - a[1][j / 5];
        temp2 = temp * temp;
        temp6 = temp2 * temp2 * temp2;

        return (save + temp6 + (double) (j + 1));
    }

    private double f5(ArrayList<Integer> c)
    {
        int x;
        double sum;

//        double solution = 500.0;

        sum = 0.0;
        for (x = 0; x <= 24; x++)
        {
            sum = sum + (1.0 / f5_j(c, x));
        }

        return ((1.0 / (sum + .002)));
    }
}

