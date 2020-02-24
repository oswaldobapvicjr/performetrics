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
     * <p>
     * Converts the given duration to a different time unit, with double-precision;
     * </p>
     *
     * <p>
     * For example, to convert 10 minutes to milliseconds, use:
     * {@code TimeUnitConverter.convert(10, TimeUnit.MINUTES, TimeUnit.MILLISECONDS)}
     * </p>
     *
     * <p>
     * <b>Note:</b> The number of decimal places applied is determined by calling
     * {@code ConfigurationHolder.getConfiguration().getScale()}
     * </p>
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceTimeUnit the unit of the sourceDuration argument
     * @param targetTimeUnit the target time unit
     * @return the converted duration, as double.
     */
    public static double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
    {
        return convert(sourceDuration, sourceTimeUnit, targetTimeUnit,
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
     * {@code TimeUnitConverter.convert(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2)}
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
     *
     * <pre>
     * convert(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2)  = 0.99
     * convert(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 0)  = 1
     * </pre>
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceTimeUnit the unit of the sourceDuration argument
     * @param targetTimeUnit the target time unit
     * @param targetTimeUnit the target time unit
     * @param decimalPlaces  the number of decimal places to which the number will be rounded
     * @return the converted duration, as double.
     */
    public static double convert(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit,
            int decimalPlaces)
    {
        if (sourceTimeUnit == targetTimeUnit)
        {
            return sourceDuration;
        }
        return round(rawConvertion(sourceDuration, sourceTimeUnit, targetTimeUnit), decimalPlaces);
    }

    /**
     * Converts the given duration and time unit into another time unit, as double, with no
     * rounding.
     *
     * @param sourceDuration the time duration in the given sourceUnit
     * @param sourceTimeUnit the unit of the sourceDuration argument
     * @param targetTimeUnit the target time unit
     * @return the converted duration, as double.
     */
    private static double rawConvertion(long sourceDuration, TimeUnit sourceTimeUnit, TimeUnit targetTimeUnit)
    {
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
     *
     * <pre>
     * round(22.859, 2)  = 22.86
     * round(22.859, 0)  = 23
     * round(22.859, -1) = 20
     * </pre>
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
