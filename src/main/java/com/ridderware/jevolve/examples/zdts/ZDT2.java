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
 * The second of the five benchmark problems in the ZDT 
 * (Zitler, Deb, and Thiele) Test Suite. This is a non-convex landscape 
 * counterpart to ZDT-1. Typically, 30 real-valued parameters are used.
 */
public class ZDT2 extends AbstractNoisyNumEvaluationProblem
{
    @Override
    protected double[] getNoiselessFitness(final List<Double> genotype)
    {
        final double noiseLessFitness[] = new double[2];
        noiseLessFitness[0] = genotype.get(0);
        final double g = evalG(genotype);
        final double h = evalH(noiseLessFitness[0], g);
        noiseLessFitness[1] = h * g;
        return noiseLessFitness;
    }

    /**
     * Returns the value of the ZDT2 function G.
     * @param decisionVariables The decision variables of the solution to
     * evaluate.
     */
    private final static double evalG(List<Double> decisionVariables)
    {
        double g = 0.0;
        for (int i = 1; i < decisionVariables.size(); i++)
        {
            g += decisionVariables.get(i);
        }
        double constante = (9.0 / (decisionVariables.size() - 1));
        g = constante * g;
        g = g + 1.0;
        assert (!Double.isInfinite(g) && !Double.isNaN(g)) : "G = " + g;
        return g;
    } //evalG

    /**
     * Returns the value of the ZDT2 function H.
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     */
    private final static double evalH(double f, double g)
    {
//        double h = 0.0;
        final double fDIVg = f / g;
//        h = 1.0 - (fDIVg*fDIVg);
//        return h;
        return 1.0 - (fDIVg * fDIVg);
    } // evalH
}
