package net.obvj.performetrics.runnable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * Test methods for the {@link CpuTimeRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PerformetricsUtils.class)
public class CpuTimeRunnableOperationTest
{
    /**
     * A dummy Runnable for testing purposes
     */
    private static final Runnable MOCKED_RUNNABLE = PowerMockito.mock(Runnable.class);

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void testCounterAndInitialValues()
    {
        CpuTimeRunnableOperation operation = new CpuTimeRunnableOperation(MOCKED_RUNNABLE);
        Counter counter = operation.getCounter();
        assertEquals(Counter.Type.CPU_TIME, counter.getType());
        assertEquals(0, counter.getUnitsBefore());
        assertEquals(0, counter.getUnitsAfter());
    }

    /**
     * Tests the elapsed CPU time for a dummy runnable by asserting that a call to the correct
     * method from PerformetricsUtils is called and the units before and after are updated
     */
    @Test
    public void testCounterElapsedTime()
    {
        long mockedCpuTime = 1200000000l;

        PowerMockito.mockStatic(PerformetricsUtils.class);
        PowerMockito.when(PerformetricsUtils.getCpuTimeNanos()).thenReturn(mockedCpuTime);

        CpuTimeRunnableOperation operation = new CpuTimeRunnableOperation(MOCKED_RUNNABLE);
        operation.run();

        Counter counter = operation.getCounter();
        // Check that both units-before and units-after have been updated
        assertEquals("Units-before was not updated", mockedCpuTime, counter.getUnitsBefore());
        assertEquals("Units-after was not updated", mockedCpuTime, counter.getUnitsAfter());

        // Check that the correct measure (CPU time) method was called exactly twice
        PowerMockito.verifyStatic(PerformetricsUtils.class, BDDMockito.times(2));
        PerformetricsUtils.getCpuTimeNanos();
    }

}
