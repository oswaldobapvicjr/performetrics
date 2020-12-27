package net.obvj.performetrics.monitors;

import static java.util.concurrent.TimeUnit.HOURS;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintUtils;

/**
 * Test methods for the {@link MonitoredRunnable}.
 *
 * @author oswaldo.bapvic.jr
 */
public class MonitoredRunnableTest
{
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;

    // Since JDK 17, Mockito cannot mock java.util.Runnable
    private Runnable runnable = () -> {};

    /**
     * Setup the expects on {@link SystemUtils} mock with constant values
     */
    private void setupExpects(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(MOCKED_WALL_CLOCK_TIME);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(MOCKED_CPU_TIME);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(MOCKED_USER_TIME);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(MOCKED_SYSTEM_TIME);
    }

    private void assertAllUnitsBefore(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    private void assertAllUnitsAfter(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    @Test
    public void constructor_withOneType_assignsCorrectType()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable, CPU_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(1)));
        assertTrue(types.contains(CPU_TIME));
    }

    @Test
    public void constructor_withTwoTypes_assignsCorrectTypes()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable, CPU_TIME, USER_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(2)));
        assertTrue(types.containsAll(Arrays.asList(CPU_TIME, USER_TIME)));
    }

    /**
     * Tests, for a given {@link Runnable} and no specific counter, that all available types
     * are specified for this operation
     */
    @Test
    public void constructor_withoutType_assignsAllAvailableCounterTypes()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(Type.values().length)));
        assertTrue(types.containsAll(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME, USER_TIME, SYSTEM_TIME)));
    }

    /**
     * Tests the elapsed time for a dummy {@link Runnable} with no specific counter set (all
     * counters)
     */
    @Test
    public void run_givenAllTypes_updatesAllCounters()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpects(systemUtils);
            operation.run();
        }
        assertAllUnitsBefore(operation, 0);
        assertAllUnitsAfter(operation, 0);
    }

    @Test
    public void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printSummary(operation.stopwatch, System.out));
        }
    }

    @Test
    public void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printDetails(operation.stopwatch, System.out));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidType_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        operation.elapsedTime(USER_TIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnit_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        operation.elapsedTime(USER_TIME, HOURS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        operation.elapsedTime(USER_TIME, HOURS, FAST);
    }

    @Test()
    public void elapsedTime_validType_callsCorrectElapsedTimeFromCounter()
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        operation.stopwatch = stopwatch;

        operation.elapsedTime(WALL_CLOCK_TIME);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME);
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounter()
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        operation.stopwatch = stopwatch;

        operation.elapsedTime(WALL_CLOCK_TIME, HOURS);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME, HOURS);
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounter()
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        operation.stopwatch = stopwatch;

        operation.elapsedTime(WALL_CLOCK_TIME, HOURS, ConversionMode.FAST);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME, HOURS, ConversionMode.FAST);
    }

    @Test()
    public void reset_callsStopwatchReset()
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable, WALL_CLOCK_TIME);
        operation.stopwatch = stopwatch;
        operation.reset();
        verify(stopwatch).reset();
    }

}
