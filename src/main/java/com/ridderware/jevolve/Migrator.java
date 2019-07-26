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

import java.util.ArrayList;

/**
 * Abstract base class for migrators.  Migrators are responsible for migrating
 * individuals between populations for multi-population evolution.
 *
 * @author Jeff Ridder
 */
public abstract class Migrator
{
    /**
     * Migrates individuals between populations.
     *
     * @param  pops populations to be considered during migration.
     */
    public abstract void migrateIndividuals(ArrayList<Population> pops);
}

