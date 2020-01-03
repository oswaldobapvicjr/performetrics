package net.obvj.performetrics.callable;

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MonitoredOperation;
import net.obvj.performetrics.util.PerformetricsUtils;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * Test methods for the {@link MonitoredCallable}.
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PerformetricsUtils.class, PrintUtils.class })
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
     * Setup the expects on {@link PerformetricsUtils} mock with constant values
     */
    private void setupExpects()
    {
        given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(MOCKED_WALL_CLOCK_TIME);
        given(PerformetricsUtils.getCpuTimeNanos()).willReturn(MOCKED_CPU_TIME);
        given(PerformetricsUtils.getUserTimeNanos()).willReturn(MOCKED_USER_TIME);
        given(PerformetricsUtils.getSystemTimeNanos()).willReturn(MOCKED_SYSTEM_TIME);
    }

    /**
     * Setup the expects on the mocked {@link Callable} object
     */
    public void setupMockedCallable() throws Exception
    {
        PowerMockito.when(callable.call()).thenReturn(STRING_CALLABLE_RETURN);

    }

    /**
     * Checks that all units-before are equal to the test constants
     */
    private void assertAllUnitsBefore(MonitoredOperation operation)
    {
        assertThat(operation.getCounter(WALL_CLOCK_TIME).getUnitsBefore(), is(MOCKED_WALL_CLOCK_TIME));
        assertThat(operation.getCounter(CPU_TIME).getUnitsBefore(), is(MOCKED_CPU_TIME));
        assertThat(operation.getCounter(USER_TIME).getUnitsBefore(), is(MOCKED_USER_TIME));
        assertThat(operation.getCounter(SYSTEM_TIME).getUnitsBefore(), is(MOCKED_SYSTEM_TIME));
    }

    /**
     * Checks that all units-after are equal to the test constants
     */
    private void assertAllUnitsAfter(MonitoredOperation operation)
    {
        assertThat(operation.getCounter(WALL_CLOCK_TIME).getUnitsAfter(), is(MOCKED_WALL_CLOCK_TIME));
        assertThat(operation.getCounter(CPU_TIME).getUnitsAfter(), is(MOCKED_CPU_TIME));
        assertThat(operation.getCounter(USER_TIME).getUnitsAfter(), is(MOCKED_USER_TIME));
        assertThat(operation.getCounter(SYSTEM_TIME).getUnitsAfter(), is(MOCKED_SYSTEM_TIME));
    }

    /**
     * Checks that all units-before are equal to zero for the given counters list
     */
    private void assertAllUnitsBeforeEqualZero(Counter... counters)
    {
        for (Counter c : counters)
            assertThat("For the counter of type: " + c.getType(), c.getUnitsBefore(), is(0L));
    }

    /**
     * Checks that all units-after are equal to zero for the given counters list
     */

    private void assertAllUnitsAfterEqualZero(Counter... counters)
    {
        for (Counter c : counters)
            assertThat("For the counter of type: " + c.getType(), c.getUnitsAfter(), is(0L));
    }

    /**
     * Tests, for a given {@link Callable} and a single counter, that the correct counter is
     * specified for this operation and the initial values are zero
     */
    @Test
    public void constructor_withOneType_assignsCorrectCounteAndInitialValues()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, WALL_CLOCK_TIME);
        assertThat(op.getCounters().size(), is(1));
        Counter counter = op.getCounter(WALL_CLOCK_TIME);
        assertAllUnitsBeforeEqualZero(counter);
        assertAllUnitsAfterEqualZero(counter);
    }

    /**
     * Tests, for a given {@link Callable} and more than one counter, that the correct
     * counters are specified for this operation and the initial values are zero
     */
    @Test
    public void constructor_withTwoTypes_assignsCorrectCountersAndInitialValues()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, SYSTEM_TIME,
                USER_TIME);
        assertThat(op.getCounters().size(), is(2));
        Counter counter1 = op.getCounter(SYSTEM_TIME);
        Counter counter2 = op.getCounter(USER_TIME);
        assertAllUnitsBeforeEqualZero(counter1, counter2);
        assertAllUnitsAfterEqualZero(counter1, counter2);
    }

    /**
     * Tests, for a given {@link Callable} and no specific counter, that all available
     * counters are specified for this operation
     */
    @Test
    public void constructor_withoutType_assignsAllAvailableCounterTypes()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable);
        assertThat(op.getCounters().size(), is(Type.values().length));
        assertNotNull("Wall-clock-time counter not set", op.getCounter(WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter not set", op.getCounter(CPU_TIME));
        assertNotNull("User-time counter not set", op.getCounter(USER_TIME));
        assertNotNull("System-time counter not set", op.getCounter(SYSTEM_TIME));
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
        PowerMockito.mockStatic(PerformetricsUtils.class);
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        setupExpects();
        assertThat(operation.call(), is(STRING_CALLABLE_RETURN));
        assertAllUnitsBefore(operation);
        assertAllUnitsAfter(operation);
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
