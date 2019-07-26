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
 * The first of the five benchmark problems in the ZDT (Zitler, Deb, and Thiele) 
 * Test Suite. This is a convex landscape. Typically, 30 real-valued parameters 
 * are used.
 */
public class ZDT1 extends AbstractNoisyNumEvaluationProblem
{

    @Override
    protected double[] getNoiselessFitness(final List<Double> ga_ind)
    {
        double g = 0.;
        for (int i = 1; i < ga_ind.size(); i++)
        {
            g += ga_ind.get(i);
        }
        g = 1. + 9. * g / (ga_ind.size() - 1);

        double noiseLessFitness[] = new double[2];
        noiseLessFitness[0] = ga_ind.get(0);
        noiseLessFitness[1] = g * (1. - Math.sqrt(ga_ind.get(0) / g));
        return noiseLessFitness;
    }
}

