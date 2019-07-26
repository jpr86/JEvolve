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

import java.io.Serializable;

/**
 *  Abstract base class for all genes.
 *
 * @param E gene type
 * @author Jeff Ridder
 */
public abstract class GAGene<E> implements Serializable
{
    /**
     * Standard deviation used for Gaussian random number generation.
     */
    private E sigma;

    /**
     * Lower bound for the gene.
     */
    private E lower_bound;

    /**
     * Upper bound for the gene.
     */
    private E upper_bound;

    /**
     *  Constructor for the GAGene object
     */
    public GAGene()
    {
    }

    /**
     *  Constructor for the GAGene object
     *
     * @param  lower_bound lower bound.
     * @param  upper_bound upper bound.
     */
    public GAGene(E lower_bound, E upper_bound)
    {
        this.lower_bound = lower_bound;
        this.upper_bound = upper_bound;
    }

    /**
     *  Constructor for the GAGene object
     *
     * @param  lower_bound lower bound.
     * @param  upper_bound upper bound.
     * @param  sigma standard deviation used for Gaussian random number generation.
     */
    public GAGene(E lower_bound, E upper_bound, E sigma)
    {
        this.lower_bound = lower_bound;
        this.upper_bound = upper_bound;
        this.sigma = sigma;
    }

    /**
     *  Sets the standard deviation.
     *
     * @param  sigma standard deviation.
     */
    public void setSigma(E sigma)
    {
        this.sigma = sigma;
    }

    /**
     *  Returns the standard deviation.
     *
     * @return    standard deviation.
     */
    public E getSigma()
    {
        return sigma;
    }

    /**
     *  Returns the lower bound for the gene.
     *
     * @return    lower bound
     */
    public E getLowerBound()
    {
        return lower_bound;
    }

    /**
     *  Returns the upper bound for the gene.
     *
     * @return    upper bound
     */
    public E getUpperBound()
    {
        return upper_bound;
    }

    /**
     *  Returns a random uniform value between the upper and lower bound.
     *
     * @return    random uniform value
     */
    public abstract E randomUniformValue();

    /**
     * Returns a random uniform value between the lower and upper bound as a
     * deviation from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @param  multiplier specified to support generating the next value.
     * @return             random uniform value
     */
    public abstract E randomUniformValue(E value, double multiplier);

    /**
     *  Returns a random Gaussian value deviating from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @return random Gaussian value.
     */
    public abstract E randomGaussianValue(E value);

    /**
     * Returns a random Gaussian value between the lower and upper bound as a
     * deviation from the specified value.
     *
     * @param  value value from which the new random value deviates.
     * @param  multiplier specified to support generating the next value.
     * @return             random Gaussian value
     */
    public abstract E randomGaussianValue(E value, double multiplier);

    /**
     * Returns a String containing the attributes of the gene.
     *
     * @return attribute String.
     */
    @Override
    public String toString()
    {
        return "LowerBound[" + this.lower_bound + "] " +
            "UpperBound[" + this.upper_bound + "] " +
            "Sigma[" + this.sigma + "]";
    }
}
