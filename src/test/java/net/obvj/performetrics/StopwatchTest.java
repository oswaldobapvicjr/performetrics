package net.obvj.performetrics;

import static net.obvj.performetrics.Counter.Type.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.util.PerformetricsUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PerformetricsUtils.class)
public class StopwatchTest
{
    private static final long WALL_CLOCK_TIME_BEFORE = 2000000000l;
    private static final long CPU_TIME_BEFORE = 1200000000l;
    private static final long USER_TIME_BEFORE = 1200000001l;
    private static final long SYSTEM_TIME_BEFORE = 1200000002l;

    private static final long WALL_CLOCK_TIME_AFTER = 3000000000l;
    private static final long CPU_TIME_AFTER = 1200000300l;
    private static final long USER_TIME_AFTER = 1200000201l;
    private static final long SYSTEM_TIME_AFTER = 1200000102l;

    /**
     * Setup the expects on PerformetricUtils mock with "_BEFORE" constant values
     */
    private void setupExpectsBefore()
    {
        BDDMockito.given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_BEFORE);
        BDDMockito.given(PerformetricsUtils.getCpuTimeNanos()).willReturn(CPU_TIME_BEFORE);
        BDDMockito.given(PerformetricsUtils.getUserTimeNanos()).willReturn(USER_TIME_BEFORE);
        BDDMockito.given(PerformetricsUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_BEFORE);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter()
    {
        BDDMockito.given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_AFTER);
        BDDMockito.given(PerformetricsUtils.getCpuTimeNanos()).willReturn(CPU_TIME_AFTER);
        BDDMockito.given(PerformetricsUtils.getUserTimeNanos()).willReturn(USER_TIME_AFTER);
        BDDMockito.given(PerformetricsUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_AFTER);
    }

    /**
     * Checks that all units-before are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsBefore(Stopwatch stopwatch)
    {
        assertEquals(WALL_CLOCK_TIME_BEFORE, stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsBefore());
        assertEquals(CPU_TIME_BEFORE, stopwatch.getCounter(CPU_TIME).getUnitsBefore());
        assertEquals(USER_TIME_BEFORE, stopwatch.getCounter(USER_TIME).getUnitsBefore());
        assertEquals(SYSTEM_TIME_BEFORE, stopwatch.getCounter(SYSTEM_TIME).getUnitsBefore());
    }

    /**
     * Checks that all units-after are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsAfter(Stopwatch stopwatch)
    {
        assertEquals(WALL_CLOCK_TIME_AFTER, stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsAfter());
        assertEquals(CPU_TIME_AFTER, stopwatch.getCounter(CPU_TIME).getUnitsAfter());
        assertEquals(USER_TIME_AFTER, stopwatch.getCounter(USER_TIME).getUnitsAfter());
        assertEquals(SYSTEM_TIME_AFTER, stopwatch.getCounter(SYSTEM_TIME).getUnitsAfter());
    }

    /**
     * Checks that all units-before are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */
    private void assertAllUnitsBeforeEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getAllCounters())
            assertEquals("Units-before is not zero for " + c.getType(), 0, c.getUnitsBefore());
    }

    /**
     * Checks that all units-after are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */

    private void assertAllUnitsAfterEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getAllCounters())
            assertEquals("Units-after is not zero for " + c.getType(), 0, c.getUnitsAfter());
    }

    /**
     * Checks that all of the known counters are available by default in a stopwatch
     */
    @Test
    public void testAvailableCounters()
    {
        Stopwatch sw = new Stopwatch();
        assertNotNull("Wall-clock-time counter not set", sw.getCounter(WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter not set", sw.getCounter(CPU_TIME));
        assertNotNull("User-time counter not set", sw.getCounter(USER_TIME));
        assertNotNull("System-time counter not set", sw.getCounter(SYSTEM_TIME));
    }

    /**
     * Checks that all units are equal to zero when a new stopwatch is created.
     */
    @Test
    public void testAllUnitsWhenCreated()
    {
        Stopwatch sw = new Stopwatch();
        assertAllUnitsBeforeEqualZero(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

    /**
     * Checks that all units-before are duly set upon creation of an started stopwatch. All
     * units-after shall still be equal to zero.
     */
    @Test
    public void testAllUnitsWhenCreatedStarted()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        assertAllUnitsBefore(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch.
     */
    @Test
    public void testAllUnitsWhenStopped()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        Stopwatch sw = new Stopwatch();
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();
        sw.stop();
        assertAllUnitsBefore(sw);
        assertAllUnitsAfter(sw);
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used stopwatch.
     */
    @Test
    public void testAllUnitsWhenStoppedAndReset()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        setupExpectsAfter();
        sw.stop();
        sw.reset();
        assertAllUnitsBeforeEqualZero(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

}
