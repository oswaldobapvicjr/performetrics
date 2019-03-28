package net.obvj.performetrics.runnable;

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.MultiCounterMonitorableOperation;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * Test methods for the {@link MultiCounterRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PerformetricsUtils.class)
public class MultiCounterRunnableOperationTest
{
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;

    /**
     * A dummy Runnable for testing purposes
     */
    private static final Runnable MOCKED_RUNNABLE = PowerMockito.mock(Runnable.class);

    /**
     * Setup the expects on PerformetricUtils mock with constant values
     */
    private void setupExpects()
    {
        BDDMockito.given(PerformetricsUtils.getWallClockTimeNanos()).willReturn(MOCKED_WALL_CLOCK_TIME);
        BDDMockito.given(PerformetricsUtils.getCpuTimeNanos()).willReturn(MOCKED_CPU_TIME);
        BDDMockito.given(PerformetricsUtils.getUserTimeNanos()).willReturn(MOCKED_USER_TIME);
        BDDMockito.given(PerformetricsUtils.getSystemTimeNanos()).willReturn(MOCKED_SYSTEM_TIME);
    }

    /**
     * Checks that all units-before are equal to the test constants
     */
    private void assertAllUnitsBefore(MultiCounterMonitorableOperation operation)
    {
        assertEquals(MOCKED_WALL_CLOCK_TIME, operation.getCounter(WALL_CLOCK_TIME).getUnitsBefore());
        assertEquals(MOCKED_CPU_TIME, operation.getCounter(CPU_TIME).getUnitsBefore());
        assertEquals(MOCKED_USER_TIME, operation.getCounter(USER_TIME).getUnitsBefore());
        assertEquals(MOCKED_SYSTEM_TIME, operation.getCounter(SYSTEM_TIME).getUnitsBefore());
    }

    /**
     * Checks that all units-after are equal to the test constants
     */
    private void assertAllUnitsAfter(MultiCounterMonitorableOperation operation)
    {
        assertEquals(MOCKED_WALL_CLOCK_TIME, operation.getCounter(WALL_CLOCK_TIME).getUnitsAfter());
        assertEquals(MOCKED_CPU_TIME, operation.getCounter(CPU_TIME).getUnitsAfter());
        assertEquals(MOCKED_USER_TIME, operation.getCounter(USER_TIME).getUnitsAfter());
        assertEquals(MOCKED_SYSTEM_TIME, operation.getCounter(SYSTEM_TIME).getUnitsAfter());
    }

    /**
     * Checks that all units-before are equal to zero for the given counters list
     */
    private void assertAllUnitsBeforeEqualZero(Counter... counters)
    {
        for (Counter c : counters)
            assertEquals("Units-before is not zero for " + c.getType(), 0, c.getUnitsBefore());
    }

    /**
     * Checks that all units-after are equal to zero for the given counters list
     */

    private void assertAllUnitsAfterEqualZero(Counter... counters)
    {
        for (Counter c : counters)
            assertEquals("Units-after is not zero for " + c.getType(), 0, c.getUnitsAfter());
    }

    /**
     * Tests, for a given runnable and a single counter, that the correct counter is specified
     * for this operation and the initial values are zero
     */
    @Test
    public void testCounterAndInitialValuesSingleCounter()
    {
        MultiCounterRunnableOperation op = new MultiCounterRunnableOperation(MOCKED_RUNNABLE, CPU_TIME);
        assertEquals(1, op.getCounters().size());
        Counter counter = op.getCounter(CPU_TIME);
        assertAllUnitsBeforeEqualZero(counter);
        assertAllUnitsAfterEqualZero(counter);
    }

    /**
     * Tests, for a given runnable and more than one counter, that the correct counters are
     * specified for this operation and the initial values are zero
     */
    @Test
    public void testCounterAndInitialValuesMultiCounter()
    {
        MultiCounterRunnableOperation op = new MultiCounterRunnableOperation(MOCKED_RUNNABLE, CPU_TIME, USER_TIME);
        assertEquals(2, op.getCounters().size());
        Counter counter1 = op.getCounter(CPU_TIME);
        Counter counter2 = op.getCounter(USER_TIME);
        assertAllUnitsBeforeEqualZero(counter1, counter2);
        assertAllUnitsAfterEqualZero(counter1, counter2);
    }

    /**
     * Tests, for a given runnable and no specific counter, that all available counters are
     * specified for this operation
     */
    @Test
    public void testCountersWhenNoSpecificCounterPassed()
    {
        MultiCounterRunnableOperation op = new MultiCounterRunnableOperation(MOCKED_RUNNABLE);
        assertEquals(Type.values().length, op.getCounters().size());
        assertNotNull("Wall-clock-time counter not set", op.getCounter(WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter not set", op.getCounter(CPU_TIME));
        assertNotNull("User-time counter not set", op.getCounter(USER_TIME));
        assertNotNull("System-time counter not set", op.getCounter(SYSTEM_TIME));
    }

    /**
     * Tests the elapsed time for a dummy runnable with no specific counter set (all counters)
     */
    @Test
    public void testCountersElapsedTime()
    {
        PowerMockito.mockStatic(PerformetricsUtils.class);
        MultiCounterRunnableOperation operation = new MultiCounterRunnableOperation(MOCKED_RUNNABLE);
        setupExpects();
        operation.run();
        assertAllUnitsBefore(operation);
        assertAllUnitsAfter(operation);
    }

}
