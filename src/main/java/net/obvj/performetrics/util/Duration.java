package net.obvj.performetrics.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A time-based amount, such as '01:59:59.987654321 (H:M:S.ns)'.
 * </p>
 * <p>
 * This class models a quantity or amount of time in terms of:
 * </p>
 * <ul>
 * <li><b>hours</b>, from 0 to the maximum value that can be held in a {@code long}</li>
 * <li><b>minutes within the hour</b>, from 0 to 59</li>
 * <li><b>seconds within the minute</b>, from 0 to 59</li>
 * <li><b>nanoseconds within the second</b>, from 0 to 999,999,999</li>
 * </ul>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Duration
{

    /**
     * Enumerates different time format styles, each one with particular behaviors.
     *
     * @author oswaldo.bapvic.jr
     * @since 2.0.0
     */
    public enum FormatStyle
    {
        /**
         * Formats a time duration in the following format: {@code H:M:s:ns}. For example:
         * '01:59:59.987654321'.
         */
        FULL
        {
            @Override
            public String toString(Duration duration)
            {
                return String.format(H_M_S_NS_FORMAT, duration.hours, duration.minutes, duration.seconds,
                        duration.nanoseconds);
            }
        },

        /**
         * Formats a time duration in either of the following formats: {@code H:M:s:ns},
         * {@code M:S:ns}, or {@code S.ns}. The algorithm always chooses the shortest possible
         * format that can represent a time duration.
         */
        SHORT
        {
            @Override
            public String toString(Duration duration)
            {
                if (duration.hours > 0)
                {
                    return FormatStyle.FULL.toString(duration);
                }
                else if (duration.minutes > 0)
                {
                    return String.format(M_S_NS_FORMAT, duration.minutes, duration.seconds, duration.nanoseconds);
                }
                return String.format(S_NS_FORMAT, duration.seconds, duration.nanoseconds);
            }
        };

        private static final String H_M_S_NS_FORMAT = "%02d:%02d:%02d.%09d";
        private static final String M_S_NS_FORMAT = "%02d:%02d.%09d";
        private static final String S_NS_FORMAT = "%02d.%09d";

        /**
         * Formats a time duration.
         *
         * @param duration the {@link Duration} to be formatted
         * @return a formatted time duration
         */
        public abstract String toString(Duration duration);
    }

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;

    private static final FormatStyle DEFAULT_FORMAT_STYLE = FormatStyle.SHORT;

    private final long hours;
    private final int minutes;
    private final int seconds;
    private final int nanoseconds;

    /**
     * Constructs an instance of {@code Duration} with all mandatory fields.
     *
     * @param hours       the number of hours to set
     * @param minutes     the minutes within the hour
     * @param seconds     the seconds within the minute
     * @param nanoseconds the nanoseconds within the second
     */
    private Duration(long hours, int minutes, int seconds, int nanoseconds)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.nanoseconds = nanoseconds;
    }

    /**
     * Obtains a {@code Duration} representing an amount in the specified time unit.
     * <p>
     * For example, calling {@code Duration.of(65, SECONDS)} produces an object with:
     * {@code [hours = 0, minutes = 1, seconds = 5, nanoseconds = 0]}
     *
     * @param amount   the amount of the duration, measured in term of the timeUnit argument
     * @param timeUnit the unit that the amount argument is measured in; cannot be null
     * @return a {@code Duration}, not null.
     * @throws NullPointerException if the specified timeUnit is null
     */
    public static Duration of(long amount, TimeUnit timeUnit)
    {
        java.time.Duration duration = java.time.Duration.of(amount, timeUnit.toChronoUnit());
        long effectiveTotalSeconds = duration.getSeconds();
        long hours = effectiveTotalSeconds / SECONDS_PER_HOUR;
        int minutes = (int) ((effectiveTotalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        int seconds = (int) (effectiveTotalSeconds % SECONDS_PER_MINUTE);
        return new Duration(hours, minutes, seconds, duration.getNano());
    }

    /**
     * Returns the number of hours in this duration.
     * <p>
     * The hours field is a value from 0 to the maximum value that can be held in a
     * {@code long}.
     *
     * @return the minutes within the hours part of the length of the duration, from 0 to 59
     */
    public long getHours()
    {
        return hours;
    }

    /**
     * Returns the number of minutes within the hour in this duration.
     * <p>
     * The minutes field is a value from 0 to 59, which is an adjustment to the length in
     * hours, retrieved by calling {@code getHours()}.
     *
     * @return the minutes within the hours part of the length of the duration, from 0 to 59
     */
    public int getMinutes()
    {
        return minutes;
    }

    /**
     * Returns the number of seconds within the minute in this duration.
     * <p>
     * The seconds field is a value from 0 to 59, which is an adjustment to the length in
     * minutes, and hours, each part separately retrieved with their respective getter
     * methods.
     *
     * @return the seconds within the minutes part of the length of the duration, from 0 to 59
     */
    public int getSeconds()
    {
        return seconds;
    }

    /**
     * Returns the number of nanoseconds within the second in this duration.
     * <p>
     * The nanoseconds field is a value from 0 to 999,999,999, which is an adjustment to the
     * length in seconds, minutes, and hours, each part separately retrieved with their
     * respective getter methods.
     *
     * @return the nanoseconds within the seconds part of the length of the duration, from 0
     *         to 999,999,999
     */
    public int getNanoseconds()
    {
        return nanoseconds;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (!(object instanceof Duration)) return false;
        Duration other = (Duration) object;
        return hours == other.hours && minutes == other.minutes && nanoseconds == other.nanoseconds
                && seconds == other.seconds;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hours, minutes, nanoseconds, seconds);
    }

    /**
     * Returns a string representation of this {@code Duration} in default format style.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString()
    {
        return toString(DEFAULT_FORMAT_STYLE);
    }

    /**
     * Returns a string representation of this{@code Duration} with custom format style.
     *
     * @param style the {@link FormatStyle} to be applied
     * @return a string representation of this object in the given style
     */
    public String toString(FormatStyle style)
    {
        return style.toString(this);
    }

}
