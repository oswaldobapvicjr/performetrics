package net.obvj.performetrics;

import static net.obvj.performetrics.Counter.Type.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class StopwatchTest
{

    @Test
    public void testAvailableCounters()
    {
        Stopwatch sw = new Stopwatch();
        assertNotNull("Wall-clock-time counter not set", sw.getCounter(WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter not set", sw.getCounter(CPU_TIME));
        assertNotNull("User-time counter not set", sw.getCounter(USER_TIME));
        assertNotNull("System-time counter not set", sw.getCounter(SYSTEM_TIME));
    }

}
