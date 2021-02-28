package net.obvj.performetrics.util;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
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
 * @implSpec This class is immutable and thread-safe.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Duration implements Comparable<Duration>
{
    private static final String MSG_DURATION_MUST_NOT_BE_NULL = "The other duration must not be null";
    private static final String MSG_DURATION_TO_ADD_MUST_NOT_BE_NULL = "The duration to add must not be null";
    private static final String MSG_SOURCE_TIME_UNIT_MUST_NOT_BE_NULL = "The source TimeUnit must not be null";
    private static final String MSG_TARGET_TIME_UNIT_MUST_NOT_BE_NULL = "The target TimeUnit must not be null";

    public static final Duration ZERO = new Duration(java.time.Duration.ZERO);

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;

    private java.time.Duration internalDuration;

    /**
     * Constructs an instance of {@code Duration} from the given {@link java.time.Duration}.
     *
     * @param internalDuration the internal {@link java.time.Duration}; must not be null
     */
    private Duration(java.time.Duration internalDuration)
    {
        this.internalDuration = internalDuration;
    }

    /**
     * Obtains a {@code Duration} representing an amount in the specified time unit.
     * <p>
     * For example, calling {@code Duration.of(65, SECONDS)} produces an object in which the
     * getters behave according to the example below:
     *
     * <pre>
     * duration.getHours()       //returns: 0
     * duration.getMinutes()     //returns: 1
     * duration.getSeconds()     //returns: 5
     * duration.getNanoseconds() //returns: 0
     * </pre>
     *
     * @param amount   the amount of the duration, measured in terms of the time unit argument
     * @param timeUnit the unit that the amount argument is measured in, not null
     * @return a {@code Duration}, not null
     *
     * @throws NullPointerException if the specified time unit is null
     */
    public static Duration of(long amount, TimeUnit timeUnit)
    {
        Objects.requireNonNull(timeUnit, MSG_SOURCE_TIME_UNIT_MUST_NOT_BE_NULL);
        ChronoUnit chronoUnit = TimeUnitConverter.toChronoUnit(timeUnit);
        java.time.Duration internalDuration = java.time.Duration.of(amount, chronoUnit);
        return new Duration(internalDuration);
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
        return internalDuration.getSeconds() / SECONDS_PER_HOUR;
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
        return (int) ((internalDuration.getSeconds() % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
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
        return (int) (internalDuration.getSeconds() % SECONDS_PER_MINUTE);
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
        return internalDuration.getNano();
    }

    /**
     * Checks if this duration is zero length.
     *
     * @return true if this duration has a total length equal to zero
     * @since 2.2.2
     */
    public boolean isZero()
    {
        return internalDuration.isZero();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (!(object instanceof Duration))
        {
            return false;
        }
        Duration other = (Duration) object;
        return internalDuration.equals(other.internalDuration);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(internalDuration);
    }

    /**
     * Returns a string representation of this {@code Duration} in default format.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString()
    {
        return DurationFormatter.format(this);
    }

    /**
     * Returns a string representation of this {@code Duration} with a specific format.
     * <p>
     * <b>Note:</b> This is equivalent to calling:
     * {@code DurationFormatter.format(duration, format)}
     *
     * @param format the {@link DurationFormat} to be applied
     * @return a string representation of this object in the specified format
     */
    public String toString(DurationFormat format)
    {
        return toString(format, true);
    }

    /**
     * Returns a string representation of this {@code Duration} with a specific format.
     *
     * @param format      the {@link DurationFormat} to be applied
     * @param printLegend a flag indicating whether or not to include a legend in the
     *                    generated string
     * @return a string representation of this object in the specified format
     */
    public String toString(DurationFormat format, boolean printLegend)
    {
        return DurationFormatter.format(this, format, printLegend);
    }

    /**
     * Converts this duration to the total length in a given time unit, with default scale.
     * <p>
     * <b>Note:</b> The number of decimal places applied is determined by
     * {@code ConfigurationHolder.getConfiguration().getScale()} and may be changed by calling
     * {@code Performetrics.setScale(int)}.
     * </p>
     *
     * @param timeUnit the target time unit, not null
     *
     * @return the total length of the duration in the specified time unit, with default scale
     *
     * @throws NullPointerException if the specified time unit is null
     */
    public double toTimeUnit(TimeUnit timeUnit)
    {
        return toTimeUnit(timeUnit, -1);
    }

    /**
     * Converts this duration to the total length in a given time unit, with a custom scale.
     *
     * @param timeUnit the target time unit, not null
     * @param scale    a positive number indicates the number of decimal places to maintain;
     *                 if negative, the default scale will be applied
     *
     * @return the total length of the duration in the specified time unit, with a custom
     *         scale, not null
     *
     * @throws NullPointerException if the specified time unit is null
     */
    public double toTimeUnit(TimeUnit timeUnit, int scale)
    {
        Objects.requireNonNull(timeUnit, MSG_TARGET_TIME_UNIT_MUST_NOT_BE_NULL);

        BigDecimal targetSeconds = internalDuration.getSeconds() > 0
                ? BigDecimal.valueOf(timeUnit.convert(internalDuration.getSeconds(), TimeUnit.SECONDS))
                : BigDecimal.ZERO;

        BigDecimal targetNanoseconds = internalDuration.getNano() > 0
                ? convertNanosecondsPart(timeUnit, scale)
                : BigDecimal.ZERO;

        return targetSeconds.add(targetNanoseconds).doubleValue();
    }

    private BigDecimal convertNanosecondsPart(TimeUnit timeUnit, int scale)
    {
        int nanoseconds = internalDuration.getNano();
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
     *
     * @throws NullPointerException if the specified duration is null
     * @throws ArithmeticException  if numeric overflow occurs
     */
    public Duration plus(Duration duration)
    {
        Objects.requireNonNull(duration, MSG_DURATION_TO_ADD_MUST_NOT_BE_NULL);
        return new Duration(internalDuration.plus(duration.internalDuration));
    }

    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount   the amount to add, measured in terms of the timeUnit argument
     * @param timeUnit the unit that the amount to add is measured in, not null
     *
     * @return a {@code Duration} based on this duration with the specified duration added,
     *         not null
     *
     * @throws NullPointerException if the specified time unit is null
     * @throws ArithmeticException  if numeric overflow occurs
     *
     * @since 2.2.2
     */
    public Duration plus(long amount, TimeUnit timeUnit)
    {
        return plus(Duration.of(amount, timeUnit));
    }

    /**
     * Returns the sum of two durations.
     * <p>
     * This instances are immutable and unaffected by this method call.
     *
     * @param duration1 the first duration to add, not null
     * @param duration2 the second duration to add, not null
     * @return a {@code Duration} resulting by adding two durations, not null
     *
     * @throws NullPointerException if the specified duration is null
     * @throws ArithmeticException  if numeric overflow occurs
     */
    public static Duration sum(Duration duration1, Duration duration2)
    {
        Objects.requireNonNull(duration1, MSG_DURATION_TO_ADD_MUST_NOT_BE_NULL);
        return duration1.plus(duration2);
    }

    /**
     * Returns a copy of this duration divided by the specified value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor the value to divide the duration by, positive or negative, not zero
     * @return a Duration based on this duration divided by the specified divisor, not null
     *
     * @throws ArithmeticException if the divisor is zero or if numeric overflow occurs
     *
     * @since 2.2.0
     */
    public Duration dividedBy(long divisor)
    {
        return new Duration(internalDuration.dividedBy(divisor));
    }

    /**
     * Compares this duration to the specified {@code Duration} based on the total length of
     * the durations, as defined by {@link Comparable}.
     *
     * @param otherDuration the other duration to compare to; not null
     * @return the comparator value, negative if less, positive if greater
     *
     * @since 2.2.3
     */
    @Override
    public int compareTo(Duration otherDuration)
    {
        Objects.requireNonNull(otherDuration, MSG_DURATION_MUST_NOT_BE_NULL);
        return internalDuration.compareTo(otherDuration.internalDuration);
    }

    /**
     * @return the internal {@code java.time.Duration} object
     * @since 2.2.0
     */
    protected java.time.Duration getInternalDuration()
    {
        return internalDuration;
    }

}
