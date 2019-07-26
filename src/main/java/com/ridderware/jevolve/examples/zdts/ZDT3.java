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
 * The third of the five benchmark problems in the ZDT (Zitler, Deb, and Thiele) 
 * Test Suite. This is a discontinous pareto front. Typically, 30 real-
 * valued parameters are used.
 */
public class ZDT3 extends AbstractNoisyNumEvaluationProblem
{
    @Override
    protected double[] getNoiselessFitness(final List<Double> ga_ind)
    {
        final double noiseLessFitness[] = new double[2];
        noiseLessFitness[0] = ga_ind.get(0);
        final double g = evalG(ga_ind);
        final double h = evalH(noiseLessFitness[0], g);
        noiseLessFitness[1] = h * g;
        return noiseLessFitness;
    }

    /**
     * Returns the value of the ZDT2 function G.
     * @param decisionVariables The decision variables of the solution to
     * evaluate.
     */
    private final static double evalG(List<Double> genome)
    {
        double g = 0.0;
        for (int i = 1; i < genome.size(); i++)
        {
            g += genome.get(i);
        }
        double constante = (9.0 / (genome.size() - 1));
        g = constante * g;
        g = g + 1.0;
        return g;
    } //evalG

    /**
     * Returns the value of the ZDT3 function H.
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     */
    private final static double evalH(double f, double g)
    {
        final double h = 1.0 - java.lang.Math.sqrt(f / g) - (f / g) * Math.sin(10.0 * Math.PI * f);
        assert (!Double.isNaN(h)) : "f=" + f + " g=" + g;
        return h;
    } //evalH
}
