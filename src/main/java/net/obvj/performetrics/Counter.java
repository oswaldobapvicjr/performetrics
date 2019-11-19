package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * An object containing units before and units after for a particular unit type
 *
 * @author oswaldo.bapvic.jr
 */
public class Counter
{
    protected static final String STRING_FORMAT = "Counter [type=%s, timeUnit=%s, unitsBefore=%s, unitsAfter=%s]";

    /**
     * An enumeration of all types of measurement supported
     */
    public enum Type
    {
        /**
         * The elapsed time experienced by a user waiting for a task to complete
         */
        WALL_CLOCK_TIME("Wall clock time")
        {
            @Override
            public long defaultDataFetchStrategy(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(PerformetricsUtils.getWallClockTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total time spent using a CPU for the current thread
         */
        CPU_TIME("CPU time")
        {
            @Override
            public long defaultDataFetchStrategy(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(PerformetricsUtils.getCpuTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total CPU time that the current thread has executed in user mode
         */
        USER_TIME("User time")
        {
            @Override
            public long defaultDataFetchStrategy(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(PerformetricsUtils.getUserTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The time spent by the kernel to execute system level operations on behalf of the
         * application
         */
        SYSTEM_TIME("System time")
        {
            @Override
            public long defaultDataFetchStrategy(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(PerformetricsUtils.getSystemTimeNanos(), NANOSECONDS);
            }
        };

        private final String name;

        private Type(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }

        /**
         * Executes default data fetch strategy for the specific type.
         * 
         * @param targetTimeUnit the time unit in which the data will be returned
         * @return the default data strategy for this type, in the specified time unit
         */
        public abstract long defaultDataFetchStrategy(TimeUnit targetTimeUnit);
    }

    /**
     * The default time unit to be stored (nanoseconds) if no specific time unit informed
     */
    public static final TimeUnit DEFAULT_UNIT = NANOSECONDS;

    private final Type type;
    private final TimeUnit defaultTimeUnit;

    private long unitsBefore = 0;
    private long unitsAfter = 0;

    private boolean unitsAfterFlag = false;

    /**
     * Builds this Counter object with default time unit of nanoseconds.
     * <p>
     * This is equivalent to: {@code new Counter(type, TimeUnit.NANOSECONDS}}
     *
     * @param type the type to set
     */
    public Counter(Type type)
    {
        this(type, DEFAULT_UNIT);
    }

    /**
     * Builds this Counter object with the given type and time unit.
     *
     * @param type     the type to set
     * @param timeUnit the unit to set
     */
    public Counter(Type type, TimeUnit timeUnit)
    {
        this.type = type;
        this.defaultTimeUnit = timeUnit;
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
     * @return the default time unit
     */
    public TimeUnit getDefaultTimeUnit()
    {
        return defaultTimeUnit;
    }

    /**
     * @return set the units before with this counter's default data fetch strategy
     */
    public void before()
    {
        unitsBefore = type.defaultDataFetchStrategy(defaultTimeUnit);
    }

    /**
     * @return set the units after with this counter's default data fetch strategy
     */
    public void after()
    {
        setUnitsAfter(type.defaultDataFetchStrategy(defaultTimeUnit));
    }

    /**
     * @return the difference between units before and units after
     */
    private static long elapsedTime(long unitsBefore, long unitsAfter)
    {
        return unitsAfter >= unitsBefore ? unitsAfter - unitsBefore : -1;
    }

    /**
     * @return the difference between units before and units after, if both units are set; or
     *         the difference between units before and the current units, retrieved by the
     *         counter's default data fetch strategy, if the units after ate not set.
     */
    public long elapsedTime()
    {
        long tempUnitsAfter = unitsAfterFlag ? unitsAfter : type.defaultDataFetchStrategy(defaultTimeUnit);
        return elapsedTime(unitsBefore, tempUnitsAfter);
    }

    /**
     * Returns the elapsed time, in a given {@link TimeUnit}.
     * 
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the difference between units before and units after, in the given time unit
     */
    public long elapsedTime(TimeUnit timeUnit)
    {
        return timeUnit.convert(elapsedTime(), this.defaultTimeUnit);
    }

    @Override
    public String toString()
    {
        return String.format(STRING_FORMAT, type, defaultTimeUnit, unitsBefore, unitsAfter);
    }

}
