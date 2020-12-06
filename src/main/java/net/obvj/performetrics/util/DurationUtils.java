package net.obvj.performetrics.util;

import java.util.Collection;
import java.util.Comparator;

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
        throw new UnsupportedOperationException("Instantiation not allowed");
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
        if (durations == null || durations.isEmpty())
        {
            return Duration.ZERO;
        }
        Duration sum = Duration.ZERO;
        // The count is important as we skip possible null elements inside the collection
        int count = 0;
        for (Duration element : durations)
        {
            if (element != null)
            {
                sum = sum.plus(element);
                count++;
            }
        }
        return count == 0 ? Duration.ZERO : sum.dividedBy(count);
    }

    /**
     * Computes and returns the lowest/minimum Duration of the given collection of Durations.
     *
     * @param durations the collection which the minimum element is to be calculated
     * @return the lowest Duration in the collection; or {@code Duration.ZERO}, if the
     *         collection is either null or empty
     */
    public static Duration min(Collection<Duration> durations)
    {
        if (durations == null || durations.isEmpty())
        {
            return Duration.ZERO;
        }
        // We compare the seconds with the fractional nanoseconds after the decimal point
        return durations.stream().min(Comparator.comparingDouble(Duration::toSeconds)).orElse(Duration.ZERO);
    }

    /**
     * Computes and returns the highest/maximum Duration of the given collection of Durations.
     *
     * @param durations the collection which the maximum element is to be calculated
     * @return the highest Duration in the collection; or {@code Duration.ZERO}, if the
     *         collection is either null or empty
     */
    public static Duration max(Collection<Duration> durations)
    {
        if (durations == null || durations.isEmpty())
        {
            return Duration.ZERO;
        }
        // We compare the seconds with the fractional nanoseconds after the decimal point
        return durations.stream().max(Comparator.comparingDouble(Duration::toSeconds)).orElse(Duration.ZERO);
    }

}
