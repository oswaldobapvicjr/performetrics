package net.obvj.performetrics.strategy;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.util.TimeUnitConverter;

/**
 * Defines supported conversion strategies.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public enum ConversionStrategy
{
    /**
     * A fast conversion strategy uses Java-standard {@link TimeUnit} class to convert a given
     * duration to a different time unit. In this strategy, conversions from finer to coarser
     * granularities truncate, so lose precision.
     * <p>
     * For example, converting 999 milliseconds to seconds results in 0.
     */
    FAST
    {
        @Override
        public double convert(long sourceDuration, TimeUnit sourceUnit, TimeUnit targetUnit)
        {
            return targetUnit.convert(sourceDuration, sourceUnit);
        }
    },

    /**
     * This strategy implements a more robust conversion logic that avoids truncation from
     * finer to coarser granularities.
     * <p>
     * For example, converting 999 milliseconds to seconds results in 0.999.
     */
    DOUBLE_PRECISION
    {
        @Override
        public double convert(long sourceDuration, TimeUnit sourceUnit, TimeUnit targetUnit)
        {
            return TimeUnitConverter.convert(sourceDuration, sourceUnit, targetUnit);
        }
    };

    /**
     * Converts the given duration and time unit into another time unit.
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * {@code TimeUnitConverter.convert(10L, TimeUnit.MINUTES, TimeUnit.MILLISECONDS)}
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceUnit     the unit of the sourceDuration argument
     * @param targetUnit     the target time unit
     * @return the converted duration, as double.
     */
    public abstract double convert(long sourceDuration, TimeUnit sourceUnit, TimeUnit targetUnit);

}
