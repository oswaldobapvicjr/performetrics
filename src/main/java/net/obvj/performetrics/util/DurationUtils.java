/*
 * Copyright 2021 obvj.net
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

package net.obvj.performetrics.util;

import java.util.Collection;

import net.obvj.performetrics.util.DurationStats.Flag;

/**
 * Common methods for working with durations.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public class DurationUtils
{

    private DurationUtils()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Calculates statistical metrics (sum, average, minimum, and/or maximum) for a collection of
     * durations based on the specified flags.
     * <p>
     * The flags are represented as bitwise values, and their combination determines the
     * calculations performed.
     * <p>
     * Examples of usage:
     *
     * <pre>
     * // Example 1: Calculate the average duration
     * DurationStats stats = calculateStats(durations, Flag.BASIC);
     * Duration average = stats.average();
     *
     * // Example 2: Calculate the minimum and maximum durations
     * DurationStats stats = calculateStats(durations, Flag.MIN | Flag.MAX);
     * Duration min = stats.min();
     * Duration max = stats.max();
     *
     * // Example 3: Calculate all statistics
     * DurationStats stats = calculateStats(durations, Flag.ALL);
     * </pre>
     *
     * <p>
     * <b>Note:</b> If the input collection is empty or null, {@link DurationStats#EMPTY} is returned.
     *
     * @param durations the collection of {@link Duration} objects to analyze (null is
     *                  allowed).
     * @param flags     an integer representing the enabled flags using bitwise values.
     *                  Supported flags are:
     *                  <ul>
     *                  <li>{@link Flag#BASIC} - Calculate sum, count and average duration.</li>
     *                  <li>{@link Flag#MIN} - Calculate the minimum duration.</li>
     *                  <li>{@link Flag#MAX} - Calculate the maximum duration.</li>
     *                  <li>{@link Flag#ALL} - Calculates all statistics.</li>
     *                  </ul>
     * @return a {@link DurationStats} object containing the calculated metrics.
     * @since 2.6.0
     */
    public static DurationStats analyzeDurations(Collection<Duration> durations, int flags)
    {
        return new DurationStats(flags).accept(durations);
    }

    /**
     * Computes and returns the mean Duration of the given collection of Durations.
     *
     * @param durations the collection which mean Duration is to be calculated; can be null
     * @return the average of Durations in the collection; or {@code Duration.ZERO}, if the
     *         collection is either null or empty
     */
    public static Duration average(Collection<Duration> durations)
    {
        return analyzeDurations(durations, Flag.BASIC).average();
    }

    /**
     * Returns the lowest/minimum Duration of a given collection.
     *
     * @param durations the collection which the minimum element is to be calculated
     * @return the lowest Duration in the collection; or {@code Duration.ZERO}, if the
     *         collection is either null or empty
     */
    public static Duration min(Collection<Duration> durations)
    {
        return analyzeDurations(durations, Flag.MIN).min();
    }

    /**
     * Returns the highest/maximum Duration of a given collection.
     *
     * @param durations the collection which the maximum element is to be calculated
     * @return the highest Duration in the collection; or {@code Duration.ZERO}, if the
     *         collection is either null or empty
     */
    public static Duration max(Collection<Duration> durations)
    {
        return analyzeDurations(durations, Flag.MAX).max();
    }

}
