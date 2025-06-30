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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * <p>
 * A class that calculates and represents statistical metrics for durations, such as sum,
 * count, average, minimum, and maximum.
 * </p>
 *
 * <p>
 * Use one of the constructors to create a new object. The default (empty) constructor
 * provides all flags. Alternatively, use the constructor {@code DurationStats(int)} to
 * customize the new instance behavior by setting up different flags. For example:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * new DurationStats(Flag.BASIC); // calculate sum and average
 * new DurationStats(Flag.BASIC | Flag.MAX); // calculate sum, average, and max
 * new DurationStats(Flag.ALL); // calculate all metrics
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * Then, add durations using the methods: {@code accept(Duration)} or
 * {@code accept(Collection<Duration>)}. Since objects produced by this class are
 * immutable, a new {@code DurationStats} instance will be produced after each call.
 * </p>
 *
 * <p>
 * Use of the getter methods {@code sum()}, {@code average()}, {@code min()}, or
 * {@code max()} at any moment to obtain the computed values (depending on the flags
 * specified at construction time.
 * </p>
 *
 * Example:
 *
 * <blockquote>
 *
 * <pre>
 * Duration sum = new DurationStats(Flag.BASIC).accept(durationsList).sum();
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * <b>Implementation notice:</b> This class is immutable and thread-safe.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.6.0
 */
public final class DurationStats
{

    /**
     * A class that enumerates and handles statistical flags.
     *
     * <p>
     * Each flag represents one or more statistical calculations and can be combined using
     * bitwise OR operations. This class provides constants for the flags and a utility method
     * to check if a specific flag is enabled.
     *
     * <p>
     * Usage example:
     *
     * <pre>
     * int flags = Flag.BASIC | Flag.MIN; // Enables BASIC and MIN flags
     * boolean isBasicEnabled = Flag.isEnabled(Flag.BASIC, flags); // true
     * boolean isMaxEnabled = Flag.isEnabled(Flag.MAX, flags); // false
     * </pre>
     *
     * @author oswaldo.bapvic.jr
     * @since 2.7.0
     */
    public static class Flag
    {
        /**
         * A flag for calculating sum, count and average. Binary: {@code 0b001}.
         */
        public static final int BASIC = 0b001;
        /**
         * A flag for calculating the minimum. Binary: {@code 0b010}.
         */
        public static final int MIN = 0b010;
        /**
         * A flag for calculating the maximum. Binary: {@code 0b100}.
         */
        public static final int MAX = 0b100;
        /**
         * A flag for calculating the minimum and the maximum. Binary: {@code 0b010}.
         */
        public static final int MIN_MAX = 0b110;
        /**
         * Flag enabling all calculations: average, minimum, and maximum. Binary: {@code 0b111}.
         */
        public static final int ALL = 0b111;

        /**
         * Private constructor to prevent instantiation of this utility class.
         *
         * @throws IllegalStateException if instantiation is attempted.
         */
        private Flag()
        {
            throw new IllegalStateException("Instantiation not allowed");
        }

        /**
         * Checks whether a specific flag is enabled in the given combination of flags.
         * <p>
         * Usage example:
         *
         * <pre>
         * int flags = Flag.BASIC | Flag.MIN;
         * boolean isBasicEnabled = Flag.isEnabled(Flag.BASIC, flags); // true
         * boolean isMaxEnabled = Flag.isEnabled(Flag.MAX, flags); // false
         * </pre>
         *
         * @param flag  the flag to check, such as {@link Flag#BASIC}, {@link Flag#MIN}, or
         *              {@link Flag#MAX}.
         * @param flags the combined flags to check against, typically created using bitwise OR
         *              operations.
         * @return {@code true} if the specified flag is enabled; {@code false} otherwise.
         */
        public static boolean isEnabled(int flag, int flags)
        {
            return (flags & flag) != 0;
        }
    }

    private final int flags;

    private final int count;
    private final Duration sum;
    private final Duration min;
    private final Duration max;

    /**
     * Constructs a new {@code DurationStats} with all flags enabled.
     *
     * @since 2.7.0
     */
    public DurationStats()
    {
        this(Flag.ALL);
    }

    /**
     * Constructs a new {@code DurationStats} object based on the specified flags.
     * <p>
     * The flags are represented as bitwise values, and their combination determines the
     * calculations performed.
     * <p>
     * Examples of usage:
     *
     * <pre>
     * // Example 1: Calculate the average duration
     * new DurationStats(Flag.BASIC);
     *
     * // Example 2: Calculate the minimum and maximum durations
     * new DurationStats(Flag.MIN | Flag.MAX);
     *
     * // Example 3: Calculate all statistics
     * new DurationStats(Flag.ALL);
     * </pre>
     *
     * @param flags an integer representing the enabled flags using bitwise values
     * @see Flag
     * @since 2.7.0
     */
    public DurationStats(int flags)
    {
        this(flags, 0, Duration.ZERO, null, null);
    }

    private DurationStats(int flags, int count, Duration sum, Duration min, Duration max)
    {
        this.flags = flags;
        this.count = count;
        this.sum = sum;
        this.min = min;
        this.max = max;
    }

    /**
     * Records a new {@link Duration}.
     *
     * @param duration a new duration to be computed
     * @return a new {@link DurationStats} with the new duration computed
     * @since 2.7.0
     */
    public DurationStats accept(Duration duration)
    {
        return accept(Collections.singletonList(duration));
    }

    /**
     * Records new durations.
     *
     * @param durations a collection of durations to be computed
     * @return a new {@link DurationStats} with the new durations computed
     * @since 2.7.0
     */
    public DurationStats accept(Collection<Duration> durations)
    {
        if (isEmpty(durations))
        {
            return this;
        }

        int lCount = this.count;
        Duration lSum = this.sum;
        Duration lMin = this.min;
        Duration lMax = this.max;

        for (Duration duration : durations)
        {
            if (duration == null) continue; // Skip null values
            if (Flag.isEnabled(Flag.BASIC, flags))
            {
                lSum = lSum.plus(duration);
                lCount++;
            }
            if (Flag.isEnabled(Flag.MIN, flags))
            {
                lMin = lMin == null || duration.compareTo(lMin) < 0 ? duration : lMin;
            }
            if (Flag.isEnabled(Flag.MAX, flags))
            {
                lMax = lMax == null || duration.compareTo(lMax) > 0 ? duration : lMax;
            }
        }

        return new DurationStats(flags, lCount, lSum, lMin, lMax);
    }

    private static boolean isEmpty(Collection<?> collection)
    {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns the average duration.
     *
     * @return the average duration, never {@code null}
     */
    public Duration average()
    {
        return Flag.isEnabled(Flag.BASIC, flags) && count > 0 ? sum.dividedBy(count)
                : Duration.ZERO;
    }

    /**
     * Returns the minimum duration.
     *
     * @return the minimum duration, never {@code null}
     */
    public Duration min()
    {
        return Objects.requireNonNullElse(min, Duration.ZERO);
    }

    /**
     * Returns the maximum duration.
     *
     * @return the maximum duration, never {@code null}
     */
    public Duration max()
    {
        return Objects.requireNonNullElse(max, Duration.ZERO);
    }

    /**
     * Returns the sum of durations.
     *
     * @return the sum of durations, never {@code null}
     * @since 2.7.0
     */
    public Duration sum()
    {
        return sum;
    }

}
