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

/**
 * A simple initializer.  It simply calls the initialize method of each newly 
 * created individual, which is a clone of the prototype for the population.
 *
 * @author Jeff Ridder
 */
public class SimpleInitializer extends Initializer
{
    private final static Logger logger =
        LogManager.getLogger(SimpleInitializer.class);

    /**
     * Initializes the population.
     *
     * @param  pop population to be initialized.
     */
    public void initialize(Population pop)
    {
        if (pop != null)
        {
            pop.clearPopulation();

            if (pop.getPrototype() != null)
            {
                for (int i = 0; i < pop.getMaxPopulationSize(); i++)
                {
                    logger.debug("Simple Initializer calling clone");
                    Individual ind = pop.getPrototype().clone();

                    ind.initialize();

                    pop.addIndividual(ind);
                }
            }
        }
    }
}

