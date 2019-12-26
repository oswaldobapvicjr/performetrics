package net.obvj.performetrics;

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.PerformetricsUtils;
import net.obvj.performetrics.util.printer.PrintUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PerformetricsUtils.class, PrintUtils.class })
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
        given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_BEFORE);
        given(PerformetricsUtils.getCpuTimeNanos()).willReturn(CPU_TIME_BEFORE);
        given(PerformetricsUtils.getUserTimeNanos()).willReturn(USER_TIME_BEFORE);
        given(PerformetricsUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_BEFORE);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter()
    {
        given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_AFTER);
        given(PerformetricsUtils.getCpuTimeNanos()).willReturn(CPU_TIME_AFTER);
        given(PerformetricsUtils.getUserTimeNanos()).willReturn(USER_TIME_AFTER);
        given(PerformetricsUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_AFTER);
    }

    /**
     * Checks that all units-before are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsBefore(Stopwatch stopwatch)
    {
        assertThat(stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsBefore(), is(WALL_CLOCK_TIME_BEFORE));
        assertThat(stopwatch.getCounter(CPU_TIME).getUnitsBefore(), is(CPU_TIME_BEFORE));
        assertThat(stopwatch.getCounter(USER_TIME).getUnitsBefore(), is(USER_TIME_BEFORE));
        assertThat(stopwatch.getCounter(SYSTEM_TIME).getUnitsBefore(), is(SYSTEM_TIME_BEFORE));
    }

    /**
     * Checks that all units-after are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsAfter(Stopwatch stopwatch)
    {
        assertThat(stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsAfter(), is(WALL_CLOCK_TIME_AFTER));
        assertThat(stopwatch.getCounter(CPU_TIME).getUnitsAfter(), is(CPU_TIME_AFTER));
        assertThat(stopwatch.getCounter(USER_TIME).getUnitsAfter(), is(USER_TIME_AFTER));
        assertThat(stopwatch.getCounter(SYSTEM_TIME).getUnitsAfter(), is(SYSTEM_TIME_AFTER));
    }

    /**
     * Checks that all units-before are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */
    private void assertAllUnitsBeforeEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getAllCounters())
            assertThat("For the counter of type: " + c.getType(), c.getUnitsBefore(), is(0L));
    }

    /**
     * Checks that all units-after are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */

    private void assertAllUnitsAfterEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getAllCounters())
            assertThat("For the counter of type:" + c.getType(), c.getUnitsAfter(), is(0L));
    }

    /**
     * Checks that all of the known counters are available by default in a stopwatch
     */
    @Test
    public void constructor_withNoArguments_assignsAllAvailableTypes()
    {
        Stopwatch sw = new Stopwatch();
        assertEquals(Type.values().length, sw.getAllCounters().size());
        assertNotNull("Wall-clock-time counter should not be null", sw.getCounter(WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter should not be null", sw.getCounter(CPU_TIME));
        assertNotNull("User-time counter should not be null", sw.getCounter(USER_TIME));
        assertNotNull("System-time counter should not be null", sw.getCounter(SYSTEM_TIME));
    }

    /**
     * Checks that only the counter passed to the constructor will be maintained (one type)
     */
    @Test
    public void constructor_withOneArgument_assignsCorrectCounter()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        assertEquals(1, sw.getAllCounters().size());
        assertNotNull("System-time counter not set", sw.getCounter(SYSTEM_TIME));
    }

    /**
     * Checks that only the counters passed to the constructor will be maintained (two types)
     */
    @Test
    public void constructor_withTwoArguments_assignsCorrectCounters()
    {
        Stopwatch sw = new Stopwatch(CPU_TIME, USER_TIME);
        assertEquals(2, sw.getAllCounters().size());
        assertNotNull("CPU-time counter not set", sw.getCounter(CPU_TIME));
        assertNotNull("User-time counter not set", sw.getCounter(USER_TIME));
    }

    /**
     * Checks that all units are equal to zero when a new stopwatch is created
     */
    @Test
    public void getAllCounters_withStopwatchUnstarted_returnsAllUnitsEqualToZero()
    {
        Stopwatch sw = new Stopwatch();
        assertAllUnitsBeforeEqualZero(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

    /**
     * Checks that all units-before are duly set upon creation of an started stopwatch with
     * default types. All units-after shall still be equal to zero.
     */
    @Test
    public void createdStarted_withNoArguments_assignsAllAvailableTypesWithAllUnitsBeforeSetAndUnitsAfterUnset()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        assertEquals(Type.values().length, sw.getAllCounters().size());
        assertAllUnitsBefore(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

    /**
     * Checks that units-before is duly set upon creation of an started stopwatch with a
     * specific counter type. The units-after shall still be equal to zero.
     */
    @Test
    public void createdStarted_withOneType_assignsAllAvailableTypesWithAllUnitsBeforeSetAndUnitsAfterUnset()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted(WALL_CLOCK_TIME);
        assertEquals(1, sw.getAllCounters().size());
        assertEquals(WALL_CLOCK_TIME_BEFORE, sw.getCounter(WALL_CLOCK_TIME).getUnitsBefore());
        assertEquals(0, sw.getCounter(WALL_CLOCK_TIME).getUnitsAfter());
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch with default
     * counters
     */
    @Test
    public void stop_withAllAvailableTypes_updatesAllUnitsAfterAccordingly()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        Stopwatch sw = new Stopwatch();
        assertEquals(Type.values().length, sw.getAllCounters().size());
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();
        sw.stop();
        assertAllUnitsBefore(sw);
        assertAllUnitsAfter(sw);
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch with
     * specific counters
     */
    @Test
    public void stop_withTwoTypes_updatesAllUnitsAfterAccordingly()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        Stopwatch sw = new Stopwatch(WALL_CLOCK_TIME, CPU_TIME);
        assertEquals(2, sw.getAllCounters().size());
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();
        sw.stop();
        assertEquals(WALL_CLOCK_TIME_BEFORE, sw.getCounter(WALL_CLOCK_TIME).getUnitsBefore());
        assertEquals(WALL_CLOCK_TIME_AFTER, sw.getCounter(WALL_CLOCK_TIME).getUnitsAfter());
        assertEquals(CPU_TIME_BEFORE, sw.getCounter(CPU_TIME).getUnitsBefore());
        assertEquals(CPU_TIME_AFTER, sw.getCounter(CPU_TIME).getUnitsAfter());
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used stopwatch
     * created with default counters
     */
    @Test
    public void reset_withAllAvailableTypes_setsAllUnitsToZeroAccordingly()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        setupExpectsAfter();
        sw.stop();
        sw.reset();
        assertEquals(Type.values().length, sw.getAllCounters().size());
        assertAllUnitsBeforeEqualZero(sw);
        assertAllUnitsAfterEqualZero(sw);
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used stopwatch
     * created with specific counters
     */
    @Test
    public void reset_withTwoTypes_setsAllUnitsToZeroAccordingly()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted(USER_TIME, SYSTEM_TIME);
        setupExpectsAfter();
        sw.stop();
        sw.reset();
        assertEquals(2, sw.getAllCounters().size());
        assertEquals(0, sw.getCounter(USER_TIME).getUnitsBefore());
        assertEquals(0, sw.getCounter(USER_TIME).getUnitsAfter());
        assertEquals(0, sw.getCounter(SYSTEM_TIME).getUnitsBefore());
        assertEquals(0, sw.getCounter(SYSTEM_TIME).getUnitsAfter());
    }

    /**
     * Tests that the method that prints stopwatch data calls the PrintUtils class
     */
    @Test
    public void printStatistics_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        PowerMockito.mockStatic(PrintUtils.class);
        Stopwatch sw = new Stopwatch();
        sw.printStatistics(System.out);
        PowerMockito.verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printStopwatch(sw, System.out);
    }

    /**
     * Tests that the method that prints stopwatch data in custom time unit calls the correct
     * PrintUtils method
     */
    @Test
    public void printStatistics_withPrintWriterAndTimeUnitArguments_callsCorrectPrintUtilMethod()
    {
        PowerMockito.mockStatic(PrintUtils.class);
        Stopwatch sw = new Stopwatch();
        sw.printStatistics(System.out, TimeUnit.SECONDS);
        PowerMockito.verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printStopwatch(sw, System.out, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCounter_invalidType_throwsException()
    {
        Stopwatch sw = new Stopwatch(Type.CPU_TIME, Type.SYSTEM_TIME);
        sw.getCounter(USER_TIME);
    }

}
