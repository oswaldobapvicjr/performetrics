package net.obvj.performetrics;

import static org.junit.Assert.*;

import org.junit.Test;

import net.obvj.performetrics.Counter.Type;

/**
 * Test methods for the {@Counter} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class CounterTest
{
    private static final long ONE_SECOND = 1;
    private static final long ONE_SECOND_IN_MILLIS = 1000;
    private static final long ONE_SECOND_IN_NANOS = ONE_SECOND_IN_MILLIS * 1000000;

    @Test
    public void testDefaultTimeUnit()
    {
        Counter counter = new Counter(Type.SYSTEM_TIME);
        assertEquals(TimeUnit.NANOSECOND, counter.getTimeUnit());
    }

    @Test
    public void testCounterElapsedTimeInSeconds()
    {
        Counter counter = new Counter(Type.SYSTEM_TIME, TimeUnit.SECOND);
        counter.setUnitsBefore(0);
        counter.setUnitsAfter(ONE_SECOND);
        assertEquals(ONE_SECOND, counter.getElapsedTime());
    }

}
