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
