package net.obvj.performetrics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    /**
     * This is a utility class, not meant to be instantiated.
     */
    private TimeUnitConverter()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts the given duration and time unit into another time unit, as double.
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * {@code TimeUnitConverter.convert(10, TimeUnit.MINUTES, TimeUnit.MILLISECONDS)}
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceUnit     the unit of the sourceDuration argument
     * @param targetUnit     the target time unit
     * @return the converted duration, as double.
     */
    public static double convert(long sourceDuration, TimeUnit sourceUnit, TimeUnit targetUnit)
    {
        if (sourceUnit == targetUnit)
        {
            return sourceDuration;
        }
        return round(rawConvertion(sourceDuration, sourceUnit, targetUnit),
                ConfigurationHolder.getConfiguration().getScale());
    }

    /**
     * Converts the given duration and time unit into another time unit, as double, with no
     * rounding.
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceUnit     the unit of the sourceDuration argument
     * @param targetUnit     the target time unit
     * @return the converted duration, as double.
     */
    private static double rawConvertion(long sourceDuration, TimeUnit sourceUnit, TimeUnit targetUnit)
    {
        if (sourceUnit.ordinal() < targetUnit.ordinal())
        {
            // source time unit granularity is finer
            return (double) sourceDuration / sourceUnit.convert(1, targetUnit);
        }
        else
        {
            return targetUnit.convert(sourceDuration, sourceUnit);
        }
    }

    /**
     * Rounds the given amount with a given number of decimal places.
     *
     * @param amount        the source amount to be rounded
     * @param decimalPlaces the number of decimal places to which the source amount will be
     *                      rounded. Zero rounds to the nearest integer; negative amounts
     *                      round to the left of the decimal point.
     * @return a double value, rounded with the given number of decimal places
     */
    protected static double round(double amount, int decimalPlaces)
    {
        return BigDecimal.valueOf(amount).setScale(decimalPlaces, RoundingMode.HALF_EVEN).doubleValue();
    }
}
