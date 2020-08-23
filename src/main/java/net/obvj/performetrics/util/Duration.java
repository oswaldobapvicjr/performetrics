package net.obvj.performetrics.util;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
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
         * {@code 1:59:59.987654321}
         */
        FULL
        {
            @Override
            public String format(final Duration duration, boolean printLegend)
            {
                return String.format(HOURS_FORMAT, duration.hours, duration.minutes, duration.seconds,
                        duration.nanoseconds) + legend(printLegend, HOURS_LEGEND);
            }
        },

        /**
         * Formats a time duration in any of the following formats: {@code H:M:s:ns},
         * {@code M:S:ns}, or {@code S.ns}, always choosing the shortest possible format.
         * <p>
         * Examples:
         * <ul>
         * <li>{@code 0.001000000 second(s)}</li>
         * <li>{@code 3.200000000 second(s)}</li>
         * <li>{@code 15:00.005890000 minute(s)}</li>
         * </ul>
         */
        SHORT
        {
            @Override
            public String format(final Duration duration, boolean printLegend)
            {
                if (duration.hours > 0)
                {
                    return FormatStyle.FULL.format(duration, printLegend);
                }
                if (duration.minutes > 0)
                {
                    return String.format(MINUTES_FORMAT, duration.minutes, duration.seconds, duration.nanoseconds)
                            + legend(printLegend, MINUTES_LEGEND);
                }
                return String.format(SECONDS_FORMAT, duration.seconds, duration.nanoseconds)
                        + legend(printLegend, SECONDS_LEGEND);
            }

        },

        /**
         * Formats a time duration in any of the following formats: {@code H:M:s:ns},
         * {@code M:S:ns}, or {@code S.ns}, suppressing trailing zeros from the nanosecond part.
         * <p>
         * Examples:
         * <ul>
         * <li>{@code 0.001 second(s)}</li>
         * <li>{@code 3.2 second(s)}</li>
         * <li>{@code 15:00.00589 minute(s)}</li>
         * </ul>
         */
        SHORTER
        {
            @Override
            public String format(final Duration duration, boolean printLegend)
            {
                String format = removeTrailingZeros(SHORT.format(duration, false));

                if (!printLegend)
                {
                    return format;
                }
                if (duration.hours > 0)
                {
                    return format + legend(true, HOURS_LEGEND);
                }
                if (duration.minutes > 0)
                {
                    return format + legend(true, MINUTES_LEGEND);
                }
                return format + legend(true, SECONDS_LEGEND);
            }

        },

        /**
         * Formats a time duration using ISO-8601 seconds based representation, such as
         * {@code PT8H6M12.345S}.
         * <p>
         * Examples:
         * <ul>
         * <li>{@code PT0.001S}</li>
         * <li>{@code PT3.2S}</li>
         * <li>{@code PT15M0.00589S}</li>
         * </ul>
         */
        ISO_8601
        {
            @Override
            public String format(final Duration duration, boolean printLegend)
            {
                if (ZERO.equals(duration))
                {
                    return "PT0S";
                }
                StringBuilder builder = new StringBuilder();
                builder.append("PT");
                if (duration.hours > 0)
                {
                    builder.append(duration.hours).append('H');
                }
                if (duration.minutes > 0)
                {
                    builder.append(duration.minutes).append('M');
                }
                if (duration.seconds > 0 || duration.nanoseconds > 0)
                {
                    builder.append(duration.seconds);
                    if (duration.nanoseconds > 0)
                    {
                        String nanos = removeTrailingZeros(String.format(NANOSECONDS_FORMAT, duration.nanoseconds));
                        builder.append(".").append(nanos);
                    }
                    builder.append('S');
                }
                return builder.toString();
            }

        };

        private static final String HOURS_FORMAT = "%d:%02d:%02d.%09d";
        private static final String MINUTES_FORMAT = "%d:%02d.%09d";
        private static final String SECONDS_FORMAT = "%d.%09d";
        private static final String NANOSECONDS_FORMAT = "%09d";

        private static final String HOURS_LEGEND = "hour(s)";
        private static final String MINUTES_LEGEND = "minute(s)";
        private static final String SECONDS_LEGEND = "second(s)";

        /**
         * Formats a time duration.
         *
         * @param duration    the {@link Duration} to be formatted
         * @param printLegend a flag indicating whether or not to include the legend in the
         *                    generated string
         * @return a formatted time duration
         */
        public abstract String format(final Duration duration, boolean printLegend);

        /**
         * Returns the {@code legend}, prepended with a white-space, if the
         * {@code printLegendFlag} argument is {@code true}; or an empty string, otherwise.
         *
         * @param printLegendFlag the flag to be evaluated
         * @param legend          the string to be used as legend
         * @return the legend string
         */
        static String legend(boolean printLegendFlag, final String legend)
        {
            return printLegendFlag ? " " + legend : "";
        }

        /**
         * Removes trailing zeros from the specified string. For example:
         *
         * <pre>
         * removeTrailingZeros("9.009000000) //returns: "9.009"
         * removeTrailingZeros("9.000000009) //returns: "9.000000009"
         * removeTrailingZeros("9.000000000) //returns: "9"
         * </pre>
         *
         * @param string the string whose trailing zeros are to be removed
         * @return a string without trailing zeros
         */
        static String removeTrailingZeros(final String string)
        {
            StringBuilder builder = new StringBuilder(string);
            while (builder.charAt(builder.length() - 1) == '0')
            {
                builder.setLength(builder.length() - 1);
            }
            if (builder.charAt(builder.length() - 1) == '.')
            {
                builder.setLength(builder.length() - 1);
            }
            return builder.toString();
        }
    }

    public static final Duration ZERO = new Duration(0, 0, 0, 0);

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;

    private static final FormatStyle DEFAULT_FORMAT_STYLE = FormatStyle.SHORTER;

    private static final EnumMap<TimeUnit, ChronoUnit> chronoUnitsByTimeUnit = new EnumMap<>(TimeUnit.class);

    static
    {
        chronoUnitsByTimeUnit.put(TimeUnit.NANOSECONDS, ChronoUnit.NANOS);
        chronoUnitsByTimeUnit.put(TimeUnit.MICROSECONDS, ChronoUnit.MICROS);
        chronoUnitsByTimeUnit.put(TimeUnit.MILLISECONDS, ChronoUnit.MILLIS);
        chronoUnitsByTimeUnit.put(TimeUnit.SECONDS, ChronoUnit.SECONDS);
        chronoUnitsByTimeUnit.put(TimeUnit.MINUTES, ChronoUnit.MINUTES);
        chronoUnitsByTimeUnit.put(TimeUnit.HOURS, ChronoUnit.HOURS);
        chronoUnitsByTimeUnit.put(TimeUnit.DAYS, ChronoUnit.DAYS);
    }

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
     * @param amount   the amount of the duration, measured in term of the timeUnit argument;
     *                 it must be a positive integer
     * @param timeUnit the unit that the amount argument is measured in; cannot be null
     * @return a {@code Duration}, not null.
     * @throws IllegalArgumentException if the specified amount is negative
     * @throws NullPointerException     if the specified timeUnit is null
     */
    public static Duration of(long amount, TimeUnit timeUnit)
    {
        if (amount < 0)
        {
            throw new IllegalArgumentException("The amount must be a positive integer");
        }

        java.time.Duration duration = java.time.Duration.of(amount, toChronoUnit(timeUnit));
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
     * @return the number of hours in this duration
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
     * <p>
     * <b>Note:</b> This is equivalent to calling: {@code toString(style, true)}
     *
     * @param style the {@link FormatStyle} to be applied
     * @return a string representation of this object in the given style
     */
    public String toString(FormatStyle style)
    {
        return toString(style, true);
    }

    /**
     * Returns a string representation of this{@code Duration} with custom format style.
     *
     * @param style       the {@link FormatStyle} to be applied
     * @param printLegend a flag indicating whether or not to include the legend in the
     *                    generated string
     * @return a string representation of this object in the given style
     */
    public String toString(FormatStyle style, boolean printLegend)
    {
        return style.format(this, printLegend);
    }

    /**
     * Converts this duration to the total length in a given time unit, with default scale.
     * <p>
     * <b>Note:</b> The number of decimal places applied is determined by
     * {@code ConfigurationHolder.getConfiguration().getScale()} and may be changed by calling
     * {@code Performetrics.setScale(int)}.
     * </p>
     *
     * @param timeUnit the target time unit
     *
     * @return the total length of the duration in the specified time unit, with default scale
     */
    public double toTimeUnit(TimeUnit timeUnit)
    {
        return toTimeUnit(timeUnit, -1);
    }

    /**
     * Converts this duration to the total length in a given time unit, with a custom scale.
     *
     * @param timeUnit the target time unit
     * @param scale    a positive number indicates the number of decimal places to maintain;
     *                 if negative, the default scale will be applied
     *
     * @return the total length of the duration in the specified time unit, with a custom
     *         scale, not null
     */
    public double toTimeUnit(TimeUnit timeUnit, int scale)
    {
        BigDecimal targetHours = hours > 0
                ? BigDecimal.valueOf(timeUnit.convert(hours, TimeUnit.HOURS))
                : BigDecimal.ZERO;

        BigDecimal targetMinutes = minutes > 0
                ? BigDecimal.valueOf(timeUnit.convert(minutes, TimeUnit.MINUTES))
                : BigDecimal.ZERO;

        BigDecimal targetSeconds = seconds > 0
                ? BigDecimal.valueOf(timeUnit.convert(seconds, TimeUnit.SECONDS))
                : BigDecimal.ZERO;

        BigDecimal targetNanoseconds = nanoseconds > 0
                ? convertNanosecondsPart(timeUnit, scale)
                : BigDecimal.ZERO;

        return targetHours.add(targetMinutes).add(targetSeconds).add(targetNanoseconds).doubleValue();
    }

    private BigDecimal convertNanosecondsPart(TimeUnit timeUnit, int scale)
    {
        return scale >= 0
                ? BigDecimal.valueOf(TimeUnitConverter.convertAndRound(nanoseconds, TimeUnit.NANOSECONDS, timeUnit, scale))
                : BigDecimal.valueOf(TimeUnitConverter.convertAndRound(nanoseconds, TimeUnit.NANOSECONDS, timeUnit));
    }

    /**
     * Converts this duration to the total length in seconds and fractional nanoseconds, with
     * a fixed scale of 9, so nanoseconds precision is preserved.
     * <p>
     * <b>Note:</b> This is equivalent to calling: {@code toTimeUnit(TimeUnit.SECONDS, 9)}
     *
     * @return the total length of the duration in seconds, with a scale of 9, not null
     */
    public double toSeconds()
    {
        return toTimeUnit(TimeUnit.SECONDS, 9);
    }

    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration the duration to add, not null
     * @return a {@code Duration} based on this duration with the specified duration added,
     *         not null
     * @throws NullPointerException if the specified duration is null
     * @throws ArithmeticException  if numeric overflow occurs
     */
    public Duration plus(Duration duration)
    {
        Objects.requireNonNull(duration, "The duration must not be null");
        return new Duration(hours + duration.getHours(), minutes + duration.getMinutes(),
                seconds + duration.getSeconds(), nanoseconds + duration.getNanoseconds());
    }

    /**
     * Returns the sum of two durations.
     * <p>
     * This instances are immutable and unaffected by this method call.
     *
     * @param duration1 the first duration to add, not null
     * @param duration2 the second duration to add, not null
     * @return a {@code Duration} resulting by adding two durations, not null
     * @throws NullPointerException if the specified duration is null
     * @throws ArithmeticException  if numeric overflow occurs
     */

    public static Duration sum(Duration duration1, Duration duration2)
    {
        return duration1.plus(duration2);
    }

    /**
     * Converts a given {@code TimeUnit} to the equivalent {@code ChronoUnit}.
     *
     * @param timeUnit the {@code TimeUnit} to be converted
     * @return the converted equivalent {@code ChronoUnit}
     */
    private static ChronoUnit toChronoUnit(TimeUnit timeUnit)
    {
        return chronoUnitsByTimeUnit.get(timeUnit);
    }

}
