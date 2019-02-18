package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.*;
import static net.obvj.performetrics.Counter.Type.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
        Counter counter = new Counter(SYSTEM_TIME);
        assertEquals(NANOSECONDS, counter.getTimeUnit());
    }

    @Test
    public void testGetters()
    {
        Counter counter = new Counter(CPU_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertEquals(CPU_TIME, counter.getType());
        assertEquals(MILLISECONDS, counter.getTimeUnit());
        assertEquals(5, counter.getUnitsBefore());
        assertEquals(10, counter.getUnitsAfter());
    }

    @Test
    public void testToString()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertEquals(String.format(Counter.STRING_FORMAT, WALL_CLOCK_TIME, MILLISECONDS, 5, 10), counter.toString());
    }

    @Test
    public void testCounterElapsedTimeInSeconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertEquals(1, counter.elapsedTime());
        assertEquals(SECONDS, counter.getTimeUnit());
    }

    @Test
    public void testCounterElapsedTimeInMilliseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertEquals(500, counter.elapsedTime());
        assertEquals(MILLISECONDS, counter.getTimeUnit());
    }

    @Test
    public void testCounterElapsedTimeInNanoseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, NANOSECONDS);
        counter.setUnitsBefore(1000000000);
        counter.setUnitsAfter(6000000000l); // 5 seconds after
        assertEquals(5, counter.getTimeUnit().toSeconds(counter.elapsedTime()));
        assertEquals(NANOSECONDS, counter.getTimeUnit());
    }

}
