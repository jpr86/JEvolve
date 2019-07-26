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
package com.ridderware.jevolve.examples.zdts;

import java.util.List;

/**
 * The fourth of the five benchmark problems in the ZDT (Zitler, Deb, and 
 * Thiele) Test Suite. This is a multimodal landscape. Typically, 10 real-
 * valued parameters are used.
 */
public class ZDT4 extends AbstractNoisyNumEvaluationProblem
{
    @Override
    protected double[] getNoiselessFitness(final List<Double> ga_ind)
    {
        double[] noiseLessFitness = new double[2];
        noiseLessFitness[0] = ga_ind.get(0);
        double g = evalG(ga_ind);
        double h = evalH(noiseLessFitness[0], g);
        noiseLessFitness[1] = h * g;
        return noiseLessFitness;
    }

    /**
     * Returns the value of the ZDT4 function G.
     * @param decisionVariables The decision variables of the solution to
     * evaluate.
     */
    private final static double evalG(List<Double> decisionVariables)
    {
        double g = 0.0;
        for (int var = 1; var < decisionVariables.size(); var++)
        {
            g += decisionVariables.get(var) * decisionVariables.get(var) +
                    -10.0 * Math.cos(4.0 * Math.PI * decisionVariables.get(var));
        }

        double constante = 1.0 + 10.0 * (decisionVariables.size() - 1);
        return g + constante;
    } // evalG

    /**
     * Returns the value of the ZDT4 function H.
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     * @return h
     */
    public double evalH(double f, double g)
    {
        return 1.0 - Math.sqrt(f / g);
    } // evalH
}

//
//    public final static void DoNoisyEvaluations(final int numEvals,
//            final double noise_sigma,
//            final double[] noiselessFitnesses,
//            final NoisyFitnessGAIndividual<Double> ga_ind)
//    {
//        ga_ind.setNoiselessFitness(noiselessFitnesses);
//        final int numObjectives = ga_ind.getNumObjectives();
//        for (int e = 0; e < numEvals; e++)
//        {
//            double[] mySampleOfFit = new double[numObjectives];
//            System.arraycopy(noiselessFitnesses, 0, mySampleOfFit, 0, numObjectives);
//            for (int f = 0; f < numObjectives; f++)
//            {
//                final double ran = MersenneTwisterFast.getInstance().nextGaussian();
//                mySampleOfFit[f] += (ran * noise_sigma);
//                assert (!Double.isInfinite(mySampleOfFit[f]) && !Double.isNaN(mySampleOfFit[f]));
////                System.out.println(f+"] "+ ran + " --> " + mySampleOfFit[f]);
//            }
////            System.out.println();
//
//            ga_ind.addEvaluation(mySampleOfFit);
//        }
//        ga_ind.markLastEval();

//        if (ga_ind instanceof EvaluationCountingGAIndividual)
//        {
//            EvaluationCountingGAIndividual ecGAInd = (EvaluationCountingGAIndividual) ga_ind;
//            double[] fit = new double[ga_ind.getNumObjectives()];
//            for (int f = 0; f < fit.length; f++)
//            {
//                fit[f] = fitMeans[f].getResult();
//                assert (!Double.isInfinite(fit[f]) && !Double.isNaN(fit[f]));
//            }
//            ecGAInd.accumulateFitness(fit, numEvals);
//        }
//        else if (ga_ind instanceof NoisyFitnessGAIndividual)
//        {
//            NoisyFitnessGAIndividual ngaInd = (NoisyFitnessGAIndividual) ga_ind;
//            double[] fit = new double[ga_ind.getNumObjectives()];
//            for (int f = 0; f < fit.length; f++)
//            {
//                fit[f] = fitMeans[f].getResult();
//                assert (!Double.isInfinite(fit[f]) && !Double.isNaN(fit[f]));
//            }
//            ngaInd.addEvaluation(fit);
//        }
//        else
//        {
//            for (int f = 0; f < ga_ind.getNumObjectives(); f++)
//            {
//                final double meanFit = fitMeans[f].getResult();
//                assert (!Double.isInfinite(meanFit) && !Double.isNaN(meanFit));
//                ga_ind.setFitness(f, meanFit);
//            }
//        }
//    }

