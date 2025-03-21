/*
 * Copyright 2025 obvj.net
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

import java.util.Objects;

/**
 * A class that represents statistical metrics for durations, such as average, minimum,
 * and maximum.
 *
 * <p>
 * This class is immutable and provides a convenient way to encapsulate the calculated
 * duration statistics. If no values are provided, default values of {@link Duration#ZERO}
 * are used.
 *
 * <p>
 * An empty object is available as {@link DurationStats#EMPTY}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.6.0
 */
public final class DurationStats
{

    /**
     * A constant representing an empty {@code DurationStats} instance with all metrics set to
     * {@link Duration#ZERO}.
     */
    public static final DurationStats EMPTY = new DurationStats(null, null, null);

    private final Duration average;
    private final Duration min;
    private final Duration max;

    /**
     * Constructs a new {@code DurationStats} object with the specified average, minimum, and
     * maximum durations.
     *
     * <p>
     * If any of the parameters are {@code null}, they are replaced with
     * {@link Duration#ZERO}.
     *
     * @param average the average duration, or {@code null} to default to
     *                {@link Duration#ZERO}.
     * @param min     the minimum duration, or {@code null} to default to
     *                {@link Duration#ZERO}.
     * @param max     the maximum duration, or {@code null} to default to
     *                {@link Duration#ZERO}.
     */
    public DurationStats(Duration average, Duration min, Duration max)
    {
        this.average = Objects.requireNonNullElse(average, Duration.ZERO);
        this.min = Objects.requireNonNullElse(min, Duration.ZERO);
        this.max = Objects.requireNonNullElse(max, Duration.ZERO);
    }

    /**
     * Returns the average duration.
     *
     * @return the average duration, never {@code null}; defaults to {@link Duration#ZERO} if
     *         not set.
     */
    public Duration average()
    {
        return average;
    }

    /**
     * Returns the minimum duration.
     *
     * @return the minimum duration, never {@code null}; defaults to {@link Duration#ZERO} if
     *         not set.
     */
    public Duration min()
    {
        return min;
    }

    /**
     * Returns the maximum duration.
     *
     * @return the maximum duration, never {@code null}; defaults to {@link Duration#ZERO} if
     *         not set.
     */
    public Duration max()
    {
        return max;
    }

}
