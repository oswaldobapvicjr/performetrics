package net.obvj.performetrics.runnable;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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
    @Mock
    private Runnable runnable;

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void constructor_assignsCorrectCounterAndInitialValues()
    {
        CpuTimeRunnableOperation operation = new CpuTimeRunnableOperation(runnable);
        Counter counter = operation.getCounter();
        assertEquals(Counter.Type.CPU_TIME, counter.getType());
        assertEquals(0, counter.getUnitsBefore());
        assertEquals(0, counter.getUnitsAfter());
    }

    /**
     * Tests the elapsed CPU time for a dummy {@link Runnable} by asserting that a call to the
     * correct method from {@link PerformetricsUtils} is called and the units before and after
     * are updated
     */
    @Test
    public void run_updatesCounter()
    {
        long mockedCpuTime = 1200000000l;

        mockStatic(PerformetricsUtils.class);
        when(PerformetricsUtils.getCpuTimeNanos()).thenReturn(mockedCpuTime);

        CpuTimeRunnableOperation operation = new CpuTimeRunnableOperation(runnable);
        operation.run();

        Counter counter = operation.getCounter();
        // Check that both units-before and units-after have been updated
        assertEquals("Units-before was not updated", mockedCpuTime, counter.getUnitsBefore());
        assertEquals("Units-after was not updated", mockedCpuTime, counter.getUnitsAfter());

        // Check that the correct measure (CPU time) method was called exactly twice
        verifyStatic(PerformetricsUtils.class, BDDMockito.times(2));
        PerformetricsUtils.getCpuTimeNanos();
    }

}
