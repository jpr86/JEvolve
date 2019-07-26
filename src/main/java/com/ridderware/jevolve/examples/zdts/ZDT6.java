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
 * The sixth of the five benchmark problems in the ZDT (Zitler, Deb, and 
 * Thiele) Test Suite. This is a non-uniform landscape. Typically, 10 real-
 * valued parameters are used.
 */
public class ZDT6 extends AbstractNoisyNumEvaluationProblem
{
    @Override
    protected double[] getNoiselessFitness(final List<Double> ga_ind)
    {
        final double x1 = ga_ind.get(0);
        final double[] noiseLessFitness = new double[2];
        noiseLessFitness[0] = 1.0 - Math.exp((-4.0) * x1) * Math.pow(Math.sin(6.0 * Math.PI * x1), 6.0);
        final double g = evalG(ga_ind);
        final double h = evalH(noiseLessFitness[0], g);
        noiseLessFitness[1] = h * g;
        return noiseLessFitness;
    }

    /**
     * Returns the value of the ZDT6 function G.
     * @param decisionVariables The decision variables of the solution to
     * evaluate.
     */
    private static final double evalG(List<Double> decisionVariables)
    {
        double g = 0.0;
        for (int var = 1; var < decisionVariables.size(); var++)
        {
            g += decisionVariables.get(var);
        }
        g = g / (decisionVariables.size() - 1);
        g = java.lang.Math.pow(g, 0.25);
        g = 9.0 * g;
        g = 1.0 + g;
        return g;
    } // evalG

    /**
     * Returns the value of the ZDT6 function H.
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     */
    private static final double evalH(double f, double g)
    {
        return 1.0 - Math.pow((f / g), 2.0);
    } // evalH
} //ZDT6

