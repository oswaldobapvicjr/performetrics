package net.obvj.performetrics.monitors;

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintUtils;

/**
 * Test methods for the {@link MonitoredCallable}.
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(MockitoJUnitRunner.class)
public class MonitoredCallableTest
{
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;
    private static final String STRING_CALLABLE_RETURN = "test234";

    @Mock
    private Callable<String> callable;

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

    /**
     * Setup the expects on the mocked {@link Callable} object
     */
    public void setupMockedCallable() throws Exception
    {
        when(callable.call()).thenReturn(STRING_CALLABLE_RETURN);
    }

    private void assertAllUnitsBefore(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    private void assertAllUnitsAfter(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    @Test
    public void constructor_withOneType_assignsCorrectType()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, WALL_CLOCK_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(1)));
        assertTrue(types.contains(WALL_CLOCK_TIME));
    }

    @Test
    public void constructor_withTwoTypes_assignsCorrectTypes()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, SYSTEM_TIME, USER_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(2)));
        assertTrue(types.containsAll(Arrays.asList(SYSTEM_TIME, USER_TIME)));
    }

    @Test
    public void constructor_withoutType_assignsAllAvailableCounterTypes()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(Type.values().length)));
        assertTrue(types.containsAll(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME, USER_TIME, SYSTEM_TIME)));
    }

    /**
     * Tests that the elapsed time for a dummy {@link Callable} with no specific counter set
     * (all counters) is updated and the {@link Callable} result is retrieved
     *
     * @throws Exception in case of an exception inside the {@link Callable}
     */
    @Test
    public void call_givenAllTypes_updatesAllCounters() throws Exception
    {
        setupMockedCallable();
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpects(systemUtils);
            assertThat(operation.call(), is(equalTo(STRING_CALLABLE_RETURN)));
        }
        assertAllUnitsBefore(operation, 0);
        assertAllUnitsAfter(operation, 0);
    }

    @Test
    public void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printSummary(operation.stopwatch, System.out));
        }
    }

    @Test
    public void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printDetails(operation.stopwatch, System.out));
        }
    }

    @Test()
    public void reset_callsStopwatchReset() throws Exception
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable, WALL_CLOCK_TIME);
        operation.stopwatch = stopwatch;
        operation.reset();
        verify(stopwatch).reset();
    }

}
