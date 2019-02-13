package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

/**
 * An object containing units before and units after for a particular unit type
 *
 * @author oswaldo.bapvic.jr
 */
public class Counter
{
    /**
     * An enumeration of all types of measurement supported
     */
    public enum Type
    {
        /**
         * The elapsed time experienced by a user waiting for a task to complete
         */
        WALL_CLOCK_TIME,
        /**
         * The total time spent using a CPU for the current thread
         */
        CPU_TIME,
        /**
         * The total CPU time that the current thread has executed in user mode
         */
        USER_TIME,
        /**
         * The time spent by the kernel to execute system level operations on behalf of the
         * application
         */
        SYSTEM_TIME;
    }

    /**
     * The default time unit to be stored (nanoseconds) if no specific time unit informed
     */
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.NANOSECONDS;

    private final Type type;
    private final TimeUnit timeUnit;

    private long unitsBefore = 0;
    private long unitsAfter = 0;

    /**
     * Builds this Counter object with default time unit of nanoseconds.
     * <p>
     * This is equivalent to: {@code new Counter(type, TimeUnit.NANOSECONDS}}
     *
     * @param type the type to set
     */
    public Counter(Type type)
    {
        this.type = type;
        this.timeUnit = DEFAULT_UNIT;
    }

    /**
     * Builds this Counter object with the given type and time unit.
     *
     * @param type the type to set
     * @param timeUnit the unit to set
     */
    public Counter(Type type, TimeUnit timeUnit)
    {
        this.type = type;
        this.timeUnit = timeUnit;
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
     * @return the difference between units before and units after
     */
    public long elapsedTime()
    {
        return unitsAfter >= unitsBefore ? unitsAfter - unitsBefore : -1;
    }

    @Override
    public String toString()
    {
        return String.format("Counter [type=%s, timeUnit=%s, unitsBefore=%s, unitsAfter=%s]", type, timeUnit,
                unitsBefore, unitsAfter);
    }

}
