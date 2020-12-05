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
    private static final String MSG_DURATION_TO_ADD_MUST_NOT_BE_NULL = "The Duration to add must not be null";

    public static final Duration ZERO = new Duration(java.time.Duration.ZERO);

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;

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
     * @param amount   the amount of the duration, measured in terms of the timeUnit argument
     * @param timeUnit the unit that the amount argument is measured in; cannot be null
     * @return a {@code Duration}, not null
     *
     * @throws NullPointerException if the specified timeUnit is null
     */
    public static Duration of(long amount, TimeUnit timeUnit)
    {
        Objects.requireNonNull(timeUnit, "The TimeUnit must not be null");
        ChronoUnit chronoUnit = toChronoUnit(timeUnit);
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
        return scale >= 0
                ? BigDecimal.valueOf(TimeUnitConverter.convertAndRound(internalDuration.getNano(), TimeUnit.NANOSECONDS, timeUnit, scale))
                : BigDecimal.valueOf(TimeUnitConverter.convertAndRound(internalDuration.getNano(), TimeUnit.NANOSECONDS, timeUnit));
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
