package net.obvj.performetrics;

/**
 * An object containing units before and units after for a particular unit type
 *
 * @author oswaldo.bapvic.jr
 */
public class Counter
{
    public enum Type
    {
        WALL_CLOCK_TIME, CPU_TIME, USER_TIME, SYSTEM_TIME;
    }

    public static final TimeUnit DEFAULT_UNIT = TimeUnit.NANOSECOND;

    private final Type type;
    private final TimeUnit timeUnit;

    private long unitsBefore = 0;
    private long unitsAfter = 0;

    /**
     * Builds this Counter object with default time unit of nanoseconds.
     * <p>
     * This is equivalent to: {@code new Counter(type, TimeUnit.NANOSECOND}}
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

    public long getElapsedTime()
    {
        return unitsAfter >= unitsBefore ? unitsAfter - unitsBefore : -1;
    }

    public long getUnitsBefore()
    {
        return unitsBefore;
    }

    public void setUnitsBefore(long unitsBefore)
    {
        this.unitsBefore = unitsBefore;
    }

    public long getUnitsAfter()
    {
        return unitsAfter;
    }

    public void setUnitsAfter(long unitsAfter)
    {
        this.unitsAfter = unitsAfter;
    }

    public Type getType()
    {
        return type;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    @Override
    public String toString()
    {
        return String.format("Counter [type=%s, timeUnit=%s, unitsBefore=%s, unitsAfter=%s]", type, timeUnit,
                getUnitsBefore(), getUnitsAfter());
    }

}
