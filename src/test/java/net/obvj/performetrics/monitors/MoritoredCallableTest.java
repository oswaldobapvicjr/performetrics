package net.obvj.performetrics.monitors;

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * Test methods for the {@link MonitoredCallable}.
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemUtils.class, PrintUtils.class })
public class MoritoredCallableTest
{
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;
    private static final String STRING_CALLABLE_RETURN = "test234";

    @Mock
    private Callable<String> callable;

    @Before
    public void setup()
    {
        PowerMockito.mockStatic(PrintUtils.class);
    }

    /**
     * Setup the expects on {@link SystemUtils} mock with constant values
     */
    private void setupExpects()
    {
        given(SystemUtils.getWallClockTimeNanos()).willReturn(MOCKED_WALL_CLOCK_TIME);
        given(SystemUtils.getCpuTimeNanos()).willReturn(MOCKED_CPU_TIME);
        given(SystemUtils.getUserTimeNanos()).willReturn(MOCKED_USER_TIME);
        given(SystemUtils.getSystemTimeNanos()).willReturn(MOCKED_SYSTEM_TIME);
    }

    /**
     * Setup the expects on the mocked {@link Callable} object
     */
    public void setupMockedCallable() throws Exception
    {
        PowerMockito.when(callable.call()).thenReturn(STRING_CALLABLE_RETURN);

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
        PowerMockito.mockStatic(SystemUtils.class);
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        setupExpects();
        assertThat(operation.call(), is(equalTo(STRING_CALLABLE_RETURN)));
        assertAllUnitsBefore(operation, 0);
        assertAllUnitsAfter(operation, 0);
    }

    /**
     * Tests that the method that prints operation statistics calls the PrintUtils class
     */
    @Test
    public void printStatistics_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        operation.printStatistics(System.out);
        PowerMockito.verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printCounters(operation.getCounters(), System.out);
    }

    /**
     * Tests that the method that prints operation statistics in custom time unit calls the correct
     * PrintUtils method
     */
    @Test
    public void printStatistics_withPrintWriterAndTimeUnitArguments_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        operation.printStatistics(System.out, TimeUnit.SECONDS);
        PowerMockito.verifyStatic(PrintUtils.class, times(1));
        PrintUtils.printCounters(operation.getCounters(), System.out, TimeUnit.SECONDS);
    }

}
