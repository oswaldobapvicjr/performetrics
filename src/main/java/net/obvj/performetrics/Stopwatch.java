package net.obvj.performetrics;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.PerformetricsUtils;

public class Stopwatch
{
    private Map<Type, Counter> counters;

    public Stopwatch()
    {
        reset();
    }

    public void reset()
    {
        counters = new EnumMap<>(Type.class);
        counters.put(Type.WALL_CLOCK_TIME, new Counter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECOND));
        counters.put(Type.CPU_TIME, new Counter(Type.CPU_TIME, TimeUnit.NANOSECOND));
        counters.put(Type.USER_TIME, new Counter(Type.USER_TIME, TimeUnit.NANOSECOND));
        counters.put(Type.SYSTEM_TIME, new Counter(Type.SYSTEM_TIME, TimeUnit.NANOSECOND));
    }

    public void start()
    {
        counters.get(Type.WALL_CLOCK_TIME).setUnitsBefore(PerformetricsUtils.getWallClockTimeMillis());
        counters.get(Type.CPU_TIME).setUnitsBefore(PerformetricsUtils.getCpuTimeNanos());
        counters.get(Type.USER_TIME).setUnitsBefore(PerformetricsUtils.getUserTimeNanos());
        counters.get(Type.SYSTEM_TIME).setUnitsBefore(PerformetricsUtils.getSystemTimeNanos());
    }

    public void stop()
    {
        counters.get(Type.WALL_CLOCK_TIME).setUnitsAfter(PerformetricsUtils.getWallClockTimeMillis());
        counters.get(Type.CPU_TIME).setUnitsAfter(PerformetricsUtils.getCpuTimeNanos());
        counters.get(Type.USER_TIME).setUnitsAfter(PerformetricsUtils.getUserTimeNanos());
        counters.get(Type.SYSTEM_TIME).setUnitsAfter(PerformetricsUtils.getSystemTimeNanos());
    }

    public Collection<Counter> getAllCounters()
    {
        return counters.values();
    }

    public Counter getCounter(Type type)
    {
        return counters.get(type);
    }

    public void printStatistics()
    {
        String rowFormat = "\n%-15s | %20s | %-11s";
        StringBuilder builder = new StringBuilder();
        String header = String.format(rowFormat, "Counter", "Elapsed time", "Time unit");
        String separator = String.format(rowFormat, "", "", "").replace(" ", "-");
        builder.append(header);
        builder.append(separator);
        for (Counter counter : counters.values())
        {
            builder.append(
                    String.format(rowFormat, counter.getType(), counter.getElapsedTime(), counter.getTimeUnit()));
        }
        System.out.println(builder.toString());
    }

    @Override
    public String toString()
    {
        return String.format("Stopwatch [counters=%s]", counters);
    }

}
