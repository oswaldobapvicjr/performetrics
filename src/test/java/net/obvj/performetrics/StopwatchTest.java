package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.printer.PrintUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemUtils.class, PrintUtils.class })
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

    @Before
    public void setup()
    {
        mockStatic(SystemUtils.class);
        mockStatic(PrintUtils.class);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_BEFORE" constant values
     */
    private void setupExpectsBefore()
    {
        given(SystemUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_BEFORE);
        given(SystemUtils.getCpuTimeNanos()).willReturn(CPU_TIME_BEFORE);
        given(SystemUtils.getUserTimeNanos()).willReturn(USER_TIME_BEFORE);
        given(SystemUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_BEFORE);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter()
    {
        given(SystemUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_AFTER);
        given(SystemUtils.getCpuTimeNanos()).willReturn(CPU_TIME_AFTER);
        given(SystemUtils.getUserTimeNanos()).willReturn(USER_TIME_AFTER);
        given(SystemUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_AFTER);
    }

    /**
     * Checks that all units-before are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsBefore(Stopwatch stopwatch)
    {
        assertThat(stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(stopwatch.getCounter(CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(stopwatch.getCounter(USER_TIME).getUnitsBefore(), is(equalTo(USER_TIME_BEFORE)));
        assertThat(stopwatch.getCounter(SYSTEM_TIME).getUnitsBefore(), is(equalTo(SYSTEM_TIME_BEFORE)));
    }

    /**
     * Checks that all units-after are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsAfter(Stopwatch stopwatch)
    {
        assertThat(stopwatch.getCounter(WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(stopwatch.getCounter(CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
        assertThat(stopwatch.getCounter(USER_TIME).getUnitsAfter(), is(equalTo(USER_TIME_AFTER)));
        assertThat(stopwatch.getCounter(SYSTEM_TIME).getUnitsAfter(), is(equalTo(SYSTEM_TIME_AFTER)));
    }

    /**
     * Checks that all units-before are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */
    private void assertAllUnitsBeforeEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getCounters())
            assertThat("For the counter of type: " + c.getType(), c.getUnitsBefore(), is(equalTo(0L)));
    }

    /**
     * Checks that all units-after are equal to zero for the given stopwatch
     *
     * @param the stopwatch to be evaluated
     */

    private void assertAllUnitsAfterEqualZero(Stopwatch stopwatch)
    {
        for (Counter c : stopwatch.getCounters())
            assertThat("For the counter of type:" + c.getType(), c.getUnitsAfter(), is(equalTo(0L)));
    }

    /**
     * Checks that all of the known counters are available by default in a stopwatch
     */
    @Test
    public void constructor_withNoArguments_assignsAllAvailableTypes()
    {
        Stopwatch sw = new Stopwatch();
        assertThat(sw.getCounters().size(), is(equalTo(Type.values().length)));
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
        assertThat(sw.getCounters().size(), is(equalTo(1)));
        assertNotNull("System-time counter not set", sw.getCounter(SYSTEM_TIME));
    }

    /**
     * Checks that only the counters passed to the constructor will be maintained (two types)
     */
    @Test
    public void constructor_withTwoArguments_assignsCorrectCounters()
    {
        Stopwatch sw = new Stopwatch(CPU_TIME, USER_TIME);
        assertThat(sw.getCounters().size(), is(equalTo(2)));
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
        assertThat(sw.isStarted(), is(equalTo(false)));
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
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        assertThat(sw.getCounters().size(), is(equalTo(Type.values().length)));
        assertThat(sw.isStarted(), is(equalTo(true)));
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
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted(WALL_CLOCK_TIME);
        assertThat(sw.isStarted(), is(equalTo(true)));
        assertThat(sw.getCounters().size(), is(equalTo(1)));
        assertThat(sw.getCounter(WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(sw.getCounter(WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(0L)));
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch with default
     * counters.
     */
    @Test
    public void stop_withAllAvailableTypes_updatesAllUnitsAfterAccordingly()
    {
        Stopwatch sw = new Stopwatch();
        assertThat(sw.getCounters().size(), is(equalTo(Type.values().length)));
        setupExpectsBefore();
        sw.start();
        assertThat(sw.isStarted(), is(true));
        setupExpectsAfter();
        sw.stop();
        assertThat(sw.isStarted(), is(false));
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
        Stopwatch sw = new Stopwatch(WALL_CLOCK_TIME, CPU_TIME);
        assertThat(sw.getCounters().size(), is(equalTo(2)));
        setupExpectsBefore();
        sw.start();
        assertThat(sw.isStarted(), is(true));
        setupExpectsAfter();
        sw.stop();
        assertThat(sw.isStarted(), is(false));
        assertThat(sw.getCounter(WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(sw.getCounter(WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(sw.getCounter(CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(sw.getCounter(CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used stopwatch
     * created with default counters
     */
    @Test
    public void reset_withAllAvailableTypes_setsAllUnitsToZeroAccordingly()
    {
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted();
        assertThat(sw.isStarted(), is(true));
        setupExpectsAfter();
        sw.stop();
        assertThat(sw.isStarted(), is(false));
        sw.reset();
        assertThat(sw.getCounters().size(), is(equalTo(Type.values().length)));
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
        setupExpectsBefore();
        Stopwatch sw = Stopwatch.createStarted(USER_TIME, SYSTEM_TIME);
        assertThat(sw.isStarted(), is(true));
        setupExpectsAfter();
        sw.stop();
        assertThat(sw.isStarted(), is(false));
        sw.reset();
        assertThat(sw.isStarted(), is(false));
        assertThat(sw.getCounters().size(), is(equalTo(2)));
        assertThat(sw.getCounter(USER_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(sw.getCounter(USER_TIME).getUnitsAfter(), is(equalTo(0L)));
        assertThat(sw.getCounter(SYSTEM_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(sw.getCounter(SYSTEM_TIME).getUnitsAfter(), is(equalTo(0L)));
    }

    /**
     * Tests that the method that prints stopwatch data calls the PrintUtils class
     */
    @Test
    public void printStatistics_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        Stopwatch sw = new Stopwatch();
        sw.printStatistics(System.out);
        verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printStopwatch(sw, System.out);
    }

    /**
     * Tests that the method that prints stopwatch data in custom time unit calls the correct
     * PrintUtils method
     */
    @Test
    public void printStatistics_withPrintWriterAndTimeUnitArguments_callsCorrectPrintUtilMethod()
    {
        Stopwatch sw = new Stopwatch();
        sw.printStatistics(System.out, SECONDS);
        verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printStopwatch(sw, System.out, SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCounter_invalidType_throwsException()
    {
        Stopwatch sw = new Stopwatch(CPU_TIME, SYSTEM_TIME);
        sw.getCounter(USER_TIME);
    }

    @Test(expected = IllegalStateException.class)
    public void start_alreadyStarted_illegalStateException()
    {
        Stopwatch sw = Stopwatch.createStarted();
        sw.start();
    }

    @Test(expected = IllegalStateException.class)
    public void start_stopped_illegalStateException()
    {
        Stopwatch sw = Stopwatch.createStarted();
        sw.stop();
        sw.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_notStarted_illegalStateException()
    {
        Stopwatch sw = new Stopwatch();
        sw.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_alreadyStopped_illegalStateException()
    {
        Stopwatch sw = Stopwatch.createStarted();
        sw.stop();
        sw.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidType_throwsException()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        sw.elapsedTime(USER_TIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnit_throwsException()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        sw.elapsedTime(USER_TIME, HOURS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_throwsException()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        sw.elapsedTime(USER_TIME, HOURS, FAST);
    }

    @Test()
    public void elapsedTime_validType_returnsValidDurations()
    {
        Stopwatch sw = new Stopwatch();
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME),
                is(equalTo(Duration.of(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE, NANOSECONDS))));
        assertThat(sw.elapsedTime(CPU_TIME),
                is(equalTo(Duration.of(CPU_TIME_AFTER - CPU_TIME_BEFORE, NANOSECONDS))));
        assertThat(sw.elapsedTime(USER_TIME),
                is(equalTo(Duration.of(USER_TIME_AFTER - USER_TIME_BEFORE, NANOSECONDS))));
        assertThat(sw.elapsedTime(SYSTEM_TIME),
                is(equalTo(Duration.of(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE, NANOSECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounters()
    {
        Stopwatch sw = new Stopwatch();
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS),
                is(equalTo(sw.getCounter(WALL_CLOCK_TIME).elapsedTime(SECONDS))));
        assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS),
                is(equalTo(sw.getCounter(CPU_TIME).elapsedTime(MILLISECONDS))));
        assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS),
                is(equalTo(sw.getCounter(USER_TIME).elapsedTime(NANOSECONDS))));
        assertThat(sw.elapsedTime(SYSTEM_TIME, SECONDS),
                is(equalTo(sw.getCounter(SYSTEM_TIME).elapsedTime(SECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounters()
    {
        Stopwatch sw = new Stopwatch();
        setupExpectsBefore();
        sw.start();
        setupExpectsAfter();

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS, FAST),
                is(equalTo(sw.getCounter(WALL_CLOCK_TIME).elapsedTime(SECONDS, FAST))));
        assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS, DOUBLE_PRECISION),
                is(equalTo(sw.getCounter(CPU_TIME).elapsedTime(MILLISECONDS, DOUBLE_PRECISION))));
        assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS, FAST),
                is(equalTo(sw.getCounter(USER_TIME).elapsedTime(NANOSECONDS, FAST))));
        assertThat(sw.elapsedTime(SYSTEM_TIME, HOURS, DOUBLE_PRECISION),
                is(equalTo(sw.getCounter(SYSTEM_TIME).elapsedTime(HOURS, DOUBLE_PRECISION))));
    }

}
