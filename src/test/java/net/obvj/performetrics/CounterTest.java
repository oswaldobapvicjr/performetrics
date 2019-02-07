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
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertEquals(1, counter.getElapsedTime());
        assertEquals(TimeUnit.SECOND, counter.getTimeUnit());
    }

    @Test
    public void testCounterElapsedTimeInMilliseconds()
    {
        Counter counter = new Counter(Type.SYSTEM_TIME, TimeUnit.MILLISECOND);
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertEquals(500, counter.getElapsedTime());
        assertEquals(TimeUnit.MILLISECOND, counter.getTimeUnit());
    }

    @Test
    public void testCounterElapsedTimeInNanoseconds()
    {
        Counter counter = new Counter(Type.SYSTEM_TIME, TimeUnit.NANOSECOND);
        counter.setUnitsBefore(1000000000);
        counter.setUnitsAfter(6000000000l); // 5 seconds after
        assertEquals(5, TimeUnit.SECOND.fromNanoseconds(counter.getElapsedTime()));
        assertEquals(TimeUnit.NANOSECOND, counter.getTimeUnit());
    }

}
