/* %%
 * 
 * JEvolve
 *
 * Copyright 2011 Jeff Ridder
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

import com.ridderware.jrandom.MersenneTwisterFast;

/**
 *  A continuous, float-valued gene.
 *
 * @author Jeff Ridder
 */
public class FloatGAGene extends GAGene<Float>
{
    /**
     *  Constructor for the FloatGAGene object
     */
    public FloatGAGene()
    {
    }

    /**
     *  Constructor for the FloatGAGene object
     *
     * @param  lower_bound lower value bound.
     * @param  upper_bound upper value bound.
     * @param  sigma standard deviation for Gaussian RNG.
     */
    public FloatGAGene(Float lower_bound, Float upper_bound, Float sigma)
    {
        super(lower_bound, upper_bound, sigma);
    }

    /**
     *  Returns a random uniform value between the upper and lower bound.
     *
     * @return    random uniform value
     */
    @Override
    public Float randomUniformValue()
    {
        float value = getLowerBound() + MersenneTwisterFast.getInstance().
            nextFloat() * (getUpperBound() - getLowerBound());

        return value;
    }

    /**
     * Returns a random uniform value between the lower and upper bound as a
     * deviation from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @param  multiplier specified to support generating the next value.
     * @return             random uniform value
     */
    @Override
    public Float randomUniformValue(Float value, double multiplier)
    {
        if (MersenneTwisterFast.getInstance().nextDouble() < 0.5)
        {
            value += (float)(multiplier * (getUpperBound() - value));
        }
        else
        {
            value += (float)(multiplier * (getLowerBound() - value));
        }

        return value;
    }

    /**
     *  Returns a random Gaussian value deviating from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @return random Gaussian value.
     */
    @Override
    public Float randomGaussianValue(Float value)
    {
        value += (float)(MersenneTwisterFast.getInstance().nextGaussian() * getSigma());

        value = Math.min(value, getUpperBound());

        value = Math.max(value, getLowerBound());

        return value;
    }

    /**
     * Returns a random Gaussian value between the lower and upper bound as a
     * deviation from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @param  multiplier specified to support generating the next value.
     * @return             random Gaussian value
     */
    @Override
    public Float randomGaussianValue(Float value, double multiplier)
    {
        value += (float)(MersenneTwisterFast.getInstance().nextGaussian() * getSigma() *
            multiplier);

        value = Math.min(value, getUpperBound());

        value = Math.max(value, getLowerBound());

        return value;
    }
}
