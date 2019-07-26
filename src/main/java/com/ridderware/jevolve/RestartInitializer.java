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

/**
 * A restart initializer.  It simply does nothing since the population was instantiated
 * by serializing from a restart file.
 *
 * @author Jeff Ridder
 */
public class RestartInitializer extends Initializer
{
    /**
     * Initializes the population by restarting from a saved population.
     *
     * @param  pop population to be initialized.
     */
    public void initialize(Population pop)
    {
    //  Do nothing since we're restarting.
    }
}

