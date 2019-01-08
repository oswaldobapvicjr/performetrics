package net.obvj.performetrics;

/**
 * An object containing units before and units after for a particular unit type, with
 * precision of nanoseconds.
 *
 * @author oswaldo.bapvic.jr
 */
public class Counter
{
    private long unitsBefore = 0;
    private long unitsAfter = 0;
    private UnitType unitType;

    /**
     * Builds this Counter object.
     *
     * @param unitType the unit type to set
     */
    public Counter(UnitType unitType)
    {
        this.unitType = unitType;
    }

    public long getElapsedTimeNanos()
    {
        return unitsAfter >= unitsBefore ? (unitsAfter - unitsBefore) : -1;
    }

    public long getElapsedTimeMillis()
    {
        long elapsedTimeNanos = getElapsedTimeNanos();
        return elapsedTimeNanos > 0 ? (getElapsedTimeNanos() / 1000000) % 1000000 : -1;
    }

    public double getElapsedTimeSeconds()
    {
        long elapsedTimeMilis = getElapsedTimeMillis();
        return elapsedTimeMilis > 0 ? elapsedTimeMilis / 1000 : -1;
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

    public UnitType getUnitType()
    {
        return unitType;
    }

    public void setUnitType(UnitType unitType)
    {
        this.unitType = unitType;
    }

    @Override
    public String toString()
    {
        return "Counter [unitsBefore=" + unitsBefore + ", unitsAfter=" + unitsAfter + ", unitType=" + unitType + "]";
    }

}
