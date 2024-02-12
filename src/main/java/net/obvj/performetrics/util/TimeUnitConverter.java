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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * A utility class for {@link TimeUnit} conversion.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class TimeUnitConverter
{
    private static final String MSG_SOURCE_TIME_UNIT_MUST_NOT_BE_NULL = "The source TimeUnit must not be null";
    private static final String MSG_TARGET_TIME_UNIT_MUST_NOT_BE_NULL = "The target TimeUnit must not be null";

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private TimeUnitConverter()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * <p>
     * Converts the given duration to a different time unit, with double-precision.
     * </p>
     *
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * </p>
     *
     * <blockquote>
     *
     * <pre>
     * TimeUnitConverter.convertAndRound(10, TimeUnit.MINUTES, TimeUnit.MILLISECONDS);
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * <b>Note:</b> The number of decimal places applied is determined by calling
     * {@code ConfigurationHolder.getConfiguration().getScale()}
     * </p>
     *
     * @param sourceDuration the time duration to be converted
     * @param sourceTimeUnit the unit of the sourceDuration argument, not null
     * @param targetTimeUnit the target time unit, not null
     *
     * @return the converted duration, as double.
     *
     * @throws NullPointerException if any of the specified time units is null
     */
    public static double convertAndRound(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
    {
        return convertAndRound(sourceDuration, sourceTimeUnit, targetTimeUnit,
                ConfigurationHolder.getConfiguration().getScale());
    }

    /**
     * <p>
     * Converts the given duration to a different time unit, with double-precision and a
     * custom number of decimal places.
     * </p>
     * <p>
     * For example, to convert 999 milliseconds to seconds, with a precision of 2 decimal
     * places, use:
     * </p>
     *
     * <blockquote>
     *
     * <pre>
     * TimeUnitConverter.convertAndRound(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2);
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * Remarks:
     * </p>
     *
     * <ul>
     * <li>If {@code decimalPlaces} is greater than zero, the number is rounded to the
     * specified number of decimal places</li>
     * <li>If {@code decimalPlaces} is zero, the number is rounded to the nearest integer</li>
     * <li>If {@code decimalPlaces} is less than zero, the number is rounded to the left of
     * the decimal point
     * </ul>
     *
     * <p>
     * Examples:
     * </p>
     *
     * <blockquote>
     *
     * <pre>
     * convertAndRound(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2)  = 0.99
     * convertAndRound(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 0)  = 1
     * </pre>
     *
     * </blockquote>
     *
     * @param sourceDuration the time duration to be converted
     * @param sourceTimeUnit the unit of the sourceDuration argument, not null
     * @param targetTimeUnit the target time unit, not null
     * @param decimalPlaces  the number of decimal places to which the number will be rounded
     *
     * @return the converted duration, as double.
     *
     * @throws NullPointerException if any of the specified time units is null
     */
    public static double convertAndRound(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit,
            int decimalPlaces)
    {
        return round(convert(sourceDuration, sourceTimeUnit, targetTimeUnit), decimalPlaces);
    }

    /**
     * <p>
     * Converts the given duration and time unit into another time unit, as double, with no
     * rounding.
     * </p>
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * </p>
     *
     * <blockquote>
     *
     * <pre>
     * TimeUnitConverter.convert(10, TimeUnit.MINUTES, TimeUnit.MILLISECONDS);
     * </pre>
     *
     * </blockquote>
     *
     * @param sourceDuration the time duration to be converted, not null
     * @param sourceTimeUnit the unit of the sourceDuration argument, not null
     * @param targetTimeUnit the target time unit
     *
     * @return the converted duration, as double
     *
     * @throws NullPointerException if any of the specified time units is null
     */
    public static double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
    {
        Objects.requireNonNull(sourceTimeUnit, MSG_SOURCE_TIME_UNIT_MUST_NOT_BE_NULL);
        Objects.requireNonNull(targetTimeUnit, MSG_TARGET_TIME_UNIT_MUST_NOT_BE_NULL);

        if (sourceTimeUnit == targetTimeUnit)
        {
            return sourceDuration;
        }
        if (sourceTimeUnit.ordinal() < targetTimeUnit.ordinal())
        {
            // source time unit granularity is finer
            return (double) sourceDuration / sourceTimeUnit.convert(1, targetTimeUnit);
        }
        else
        {
            return targetTimeUnit.convert(sourceDuration, sourceTimeUnit);
        }
    }

    /**
     * <p>
     * Rounds a number to a specified number of decimal places.
     * </p>
     *
     * <p>
     * Remarks:
     * </p>
     *
     * <ul>
     * <li>If {@code decimalPlaces} is greater than zero, the number is rounded to the
     * specified number of decimal places</li>
     * <li>If {@code decimalPlaces} is zero, the number is rounded to the nearest integer</li>
     * <li>If {@code decimalPlaces} is less than zero, the number is rounded to the left of
     * the decimal point
     * </ul>
     *
     * <p>
     * Examples:
     * </p>
     * <blockquote>
     *
     * <pre>
     * round(22.859, 2)  = 22.86
     * round(22.859, 0)  = 23
     * round(22.859, -1) = 20
     * </pre>
     *
     * </blockquote>
     *
     * @param number        the number to be rounded
     * @param decimalPlaces the number of decimal places to which the number will be rounded
     * @return a double value, rounded with the given number of decimal places
     */
    protected static double round(double number, int decimalPlaces)
    {
        return BigDecimal.valueOf(number).setScale(decimalPlaces, RoundingMode.HALF_EVEN).doubleValue();
    }

}
