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

package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.util.TimeUnitConverter;

/**
 * Defines supported conversion modes.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public enum ConversionMode
{
    /**
     * A fast conversion mode uses Java-standard {@link TimeUnit} class to convert a given
     * duration to a different time unit. In this mode, conversions from finer to coarser
     * granularities truncate, so lose precision.
     * <p>
     * For example, converting 999 milliseconds to seconds results in 0.
     */
    FAST
    {
        @Override
        public double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
        {
            return targetTimeUnit.convert(sourceDuration, sourceTimeUnit);
        }
    },

    /**
     * This mode implements a more robust conversion logic that avoids truncation from finer
     * to coarser granularities.
     * <p>
     * For example, converting 999 milliseconds to seconds results in 0.999.
     */
    DOUBLE_PRECISION
    {
        @Override
        public double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
        {
            return TimeUnitConverter.convertAndRound(sourceDuration, sourceTimeUnit, targetTimeUnit);
        }
    };

    /**
     * Converts the given duration and time unit into another time unit.
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * </p>
     *
     * <pre>
     * TimeUnitConverter.convert(10L, TimeUnit.MINUTES, TimeUnit.MILLISECONDS);
     * </pre>
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceTimeUnit the unit of the sourceDuration argument, not null
     * @param targetTimeUnit the target time unit, not null
     *
     * @return the converted duration, as double
     *
     * @throws NullPointerException if any of the specified time units is null
     */
    public abstract double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit);

}
