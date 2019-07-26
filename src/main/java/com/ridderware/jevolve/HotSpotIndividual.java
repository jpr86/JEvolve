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
 * The HotSpotIndividual works with HotSpotChunkCrossover.  It keeps track of
 * the frequency of occurrence of crossover points in an attempt to locate those
 * that lead to improvement -- hot spots.
 *
 * @param E gene type
 * @author Jason HandUber
 * @author Jeff Ridder
 */
public class HotSpotIndividual<E> extends GAIndividual
{
    private final static Logger logger =
        LogManager.getLogger(HotSpotIndividual.class);

    private int maxNumChunks = 4;

    private int initialValue = 1;

    private int[] hotSpots;

    /**
     *  Creates a new instance of HotSpotIndividual.
     */
    public HotSpotIndividual()
    {
        super();
        hotSpots = new int[getGenotypeSize()];
    }

    /**
     *  Creates a new instance of HotSpotIndividual.
     *
     * @param  initialValue  an int to initialize the hot spots array.
     * @param  maxNumChunks maximum number of chunks to keep track of.  We blow the rest.
     */
    public HotSpotIndividual(int initialValue, int maxNumChunks)
    {
        super();
        hotSpots = new int[getGenotypeSize()];
        this.maxNumChunks = maxNumChunks;
        this.initialValue = initialValue;
    }

    /**
     * Sets the hot spots.
     *
     * @param  hotSpots  an integer array.
     */
    public void setHotSpots(int[] hotSpots)
    {
        this.hotSpots = hotSpots.clone();
    }

    /**
     *  Returns the hot spots.
     *
     * @return integer array.
     */
    public int[] getHotSpots()
    {
        return hotSpots;
    }

    /**
     *  Returns the maximum number of chunks.
     *
     * @return max number of chunks.
     */
    public int getMaxNumChunks()
    {
        return maxNumChunks;
    }

    /**
     * Returns the total hot spot points accumulated so far.
     *
     * @return  total hot spot points.
     */
    public int getTotalHotSpotPoints()
    {
        int hotSpotPoints = 0;
        for (int i = 0; i < getGenotypeSize(); i++)
        {
            hotSpotPoints += hotSpots[i];
        }
        return hotSpotPoints;
    }

    /**
     *  Returns the hot spot index for a specified point value.
     *
     * @param  pointValue  point value.
     * @return  hot spot index.
     */
    public int getHotSpotIndexFromPoints(int pointValue)
    {
        int currentIndex = 0;
        int currentIndexPointValue = hotSpots[0];
        while (currentIndexPointValue <= pointValue)
        {
            currentIndexPointValue += hotSpots[++currentIndex];
        }
        return currentIndex;
    }

    /**
     *  Initialize all elements in the hot spot array to the initial value. (equal
     *  probability of xover location).
     */
    @Override
    public void initialize()
    {
        super.initialize();

        hotSpots = new int[getGenotypeSize()];
        for (int i = 0; i < getGenotypeSize(); i++)
        {
            hotSpots[i] = initialValue;
        }
    }

    /**
     * Called by the hot spot crossover operator to add crossover events at the
     * specified spots.
     *
     * @param  location1 location of first event.
     * @param  location2 location of second event.
     */
    public void addCrossoverEvent(int location1, int location2)
    {
        hotSpots[location1]++;
        hotSpots[location2]++;
    }

    /**
     *  Divides all values by the input parameter, rounding to
     *  the nearest int and making certain no value is less than the default
     *  initial value.
     *
     * @param  softResetFactor reset factor
     */
    public void softReset(int softResetFactor)
    {
        for (int i = 0; i < getGenotypeSize(); i++)
        {
            if (initialValue >= 0)
            {
                hotSpots[i] = Math.max(initialValue, (Math.round(hotSpots[i] /
                    softResetFactor)));
            }
            else
            {
                hotSpots[i] = Math.min(initialValue, (Math.round(hotSpots[i] /
                    softResetFactor)));
            }
        }
    }

    /**
     *  Resets this individual's hotspot array to contain
     *  its initial value at all indices.
     */
    public void hardReset()
    {
        for (Integer i : hotSpots)
        {
            i = initialValue;
        }
    }

    /**
     * Clones the individual.  This is very useful during breeding.
     *
     * @return a clone of the individual.
     */
    @Override
    public HotSpotIndividual<E> clone()
    {
        HotSpotIndividual<E> obj = (HotSpotIndividual<E>) super.clone();

        obj.maxNumChunks = maxNumChunks;
        obj.initialValue = initialValue;

        obj.hotSpots = hotSpots.clone();
        //array of ints

        return obj;
    }

    /**
     * Deep copies the individual.  Deep copies ensure that the individual
     * has its own copies of "deep" attributes, and not just sharing with somebody else.
     *
     * @param  obj individual to be deep copied.
     */
    public void deepCopy(HotSpotIndividual<E> obj)
    {
        super.deepCopy(obj);

        this.maxNumChunks = obj.maxNumChunks;
        this.initialValue = obj.initialValue;

        this.hotSpots = obj.hotSpots;
    //array of ints
    }
}

