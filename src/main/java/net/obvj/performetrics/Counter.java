package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.util.SystemUtils;

/**
 * An object that stores units before and after, for elapsed time maintenance.
 *
 * @author oswaldo.bapvic.jr
 */
public class Counter
{
    /**
     * The string format applied on {@code toString()} calls.
     */
    protected static final String STRING_FORMAT = "Counter [type=%s, timeUnit=%s, unitsBefore=%s, unitsAfter=%s]";

    /**
     * Enumerates all supported counter types.
     */
    public enum Type
    {
        /**
         * The elapsed time experienced by a user waiting for a task to complete.
         */
        WALL_CLOCK_TIME("Wall clock time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getWallClockTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total time spent using a CPU for the current thread.
         */
        CPU_TIME("CPU time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getCpuTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total CPU time that the current thread has executed in user mode (i.e., the time
         * spent running current thread's own code).
         */
        USER_TIME("User time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getUserTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The time spent by the OS kernel to execute system level operations on behalf of the
         * application, such as context switching, resource allocation, etc.
         */
        SYSTEM_TIME("System time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getSystemTimeNanos(), NANOSECONDS);
            }
        };

        private final String name;

        private Type(String name)
        {
            this.name = name;
        }

        /**
         * Returns the textual representation associated with this type.
         */
        @Override
        public String toString()
        {
            return name;
        }

        /**
         * Executes a particular time fetching mode that varies for each counter type.
         *
         * @param timeUnit the time unit in which the time will be returned
         * @return the amount of time at the current instant, in the specified time unit.
         */
        public abstract long getTime(TimeUnit timeUnit);
    }

    private final Type type;
    private final TimeUnit timeUnit;

    private ConversionMode conversionMode;

    private long unitsBefore = 0;
    private long unitsAfter = 0;

    private boolean unitsAfterFlag = false;

    /**
     * Builds this Counter object with a given type and default time unit.
     *
     * @param type the type to set
     */
    public Counter(Type type)
    {
        this(type, ConfigurationHolder.getConfiguration().getTimeUnit());
    }

    /**
     * Builds this Counter object with the given type and time unit.
     *
     * @param type     the type to set
     * @param timeUnit the unit to set
     */
    public Counter(Type type, TimeUnit timeUnit)
    {
        this(type, timeUnit, ConfigurationHolder.getConfiguration().getConversionMode());
    }

    /**
     * Builds this Counter object with the given type, time unit, and conversion mode.
     *
     * @param type           the type to set
     * @param timeUnit       the unit to set
     * @param conversionMode the {@link ConversionMode} to be applied
     * @since 2.0.0
     */
    public Counter(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        this.type = type;
        this.timeUnit = timeUnit;
        this.conversionMode = conversionMode;
    }

    /**
     * @return the units before
     */
    public long getUnitsBefore()
    {
        return unitsBefore;
    }

    /**
     * @param unitsBefore the units to be set
     */
    public void setUnitsBefore(long unitsBefore)
    {
        this.unitsBefore = unitsBefore;
    }

    /**
     * @return the units after
     */
    public long getUnitsAfter()
    {
        return unitsAfter;
    }

    /**
     * @param unitsAfter the units to be set
     */
    public void setUnitsAfter(long unitsAfter)
    {
        this.unitsAfter = unitsAfter;
        unitsAfterFlag = true;
    }

    /**
     * @return the type of measurement
     */
    public Type getType()
    {
        return type;
    }

    /**
     * @return the time unit
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * @return the {@link ConversionMode} used by this counter
     * @since 2.0.0
     */
    public ConversionMode getConversionMode()
    {
        return conversionMode;
    }

    /**
     * Populates the {@code unitsBefore} field with the value retrieved by the time fetching
     * mode defined by this counter's type.
     */
    public void setUnitsBefore()
    {
        setUnitsBefore(type.getTime(timeUnit));
    }

    /**
     * Populates the {@code unitsAfter} field with the value retrieved by the time fetching
     * mode defined by this counter's type.
     */
    public void setUnitsAfter()
    {
        setUnitsAfter(type.getTime(timeUnit));
    }

    /**
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if
     *         {@code unitsAfter} is greater than {@code unitsBefore}; -1 otherwise.
     */
    private static long elapsedTime(long unitsBefore, long unitsAfter)
    {
        return unitsAfter >= unitsBefore ? unitsAfter - unitsBefore : -1;
    }

    /**
     * Returns the elapsed time, in default time unit.
     *
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's default data fetch mode, if {@code unitsAfter}
     *         is not set. The value is retrieved in the default time unit.
     */
    public long elapsedTime()
    {
        long tempUnitsAfter = unitsAfterFlag ? unitsAfter : type.getTime(timeUnit);
        return elapsedTime(unitsBefore, tempUnitsAfter);
    }

    /**
     * Returns the elapsed time, in a given {@link TimeUnit}.
     *
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's default data fetch mode, if {@code unitsAfter}
     *         is not set. The value is converted to the given time unit using the default
     *         conversion mode.
     */
    public double elapsedTime(TimeUnit timeUnit)
    {
        return elapsedTime(timeUnit, this.conversionMode);
    }

    /**
     * Returns the elapsed time, in a given {@link TimeUnit}, with a custom
     * {@link ConversionMode}.
     *
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be used
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's default data fetch mode, if {@code unitsAfter}
     *         is not set. The value converted to the given time unit using the given
     *         conversion mode.
     * @since 2.0.0
     */
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return conversionMode.convert(elapsedTime(), this.timeUnit, timeUnit);
    }

    /**
     * Returns a string representation of this object.
     *
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format(STRING_FORMAT, type, timeUnit, unitsBefore, unitsAfter);
    }

}
