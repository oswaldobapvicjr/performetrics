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

/**
 * A utility class for handling statistical flags.
 *
 * <p>
 * Each flag represents a specific statistical operation and can be combined using bitwise
 * OR operations. This class provides constants for the flags and a utility method to
 * check if a specific flag is enabled.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * int flags = StatFlags.AVERAGE | StatFlags.MIN; // Enable AVERAGE and MIN flags
 * boolean isAverageEnabled = StatFlags.isEnabled(StatFlags.AVERAGE, flags); // true
 * boolean isMaxEnabled = StatFlags.isEnabled(StatFlags.MAX, flags); // false
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.6.0
 */
public class StatFlags
{
    /**
     * Flag representing the calculation of the average. Binary: {@code 0b001}.
     */
    public static final int AVERAGE = 0b001;
    /**
     * Flag representing the calculation of the minimum. Binary: {@code 0b010}.
     */
    public static final int MIN = 0b010;
    /**
     * Flag representing the calculation of the maximum. Binary: {@code 0b100}.
     */
    public static final int MAX = 0b100;
    /**
     * Flag enabling all calculations: average, minimum, and maximum. Binary: {@code 0b111}.
     */
    public static final int ALL = 0b111;

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws IllegalStateException if instantiation is attempted.
     */
    private StatFlags()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Checks whether a specific flag is enabled in the given combination of flags.
     * <p>
     * Usage example:
     *
     * <pre>
     * int flags = StatFlags.AVERAGE | StatFlags.MIN;
     * boolean isAverageEnabled = StatFlags.isEnabled(StatFlags.AVERAGE, flags); // true
     * boolean isMaxEnabled = StatFlags.isEnabled(StatFlags.MAX, flags); // false
     * </pre>
     *
     * @param flag  the flag to check, such as {@link StatFlags#AVERAGE},
     *              {@link StatFlags#MIN}, or {@link StatFlags#MAX}.
     * @param flags the combined flags to check against, typically created using bitwise OR
     *              operations.
     * @return {@code true} if the specified flag is enabled; {@code false} otherwise.
     */
    public static boolean isEnabled(int flag, int flags)
    {
        return (flags & flag) != 0;
    }
}
