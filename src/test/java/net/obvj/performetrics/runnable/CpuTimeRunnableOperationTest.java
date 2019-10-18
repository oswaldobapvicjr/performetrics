package net.obvj.performetrics.runnable;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

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
        assertThat(counter.getType(), is(Counter.Type.CPU_TIME));
        assertThat(counter.getUnitsBefore(), is(0L));
        assertThat(counter.getUnitsAfter(), is(0L));
    }

    /**
     * Tests the elapsed CPU time for a dummy {@link Runnable} by asserting that a call to the
     * correct method from {@link PerformetricsUtils} is called and the units before and after
     * are updated
     */
    @Test
    public void run_updatesCounter()
    {
        long mockedCpuTime = 1200000000L;

        mockStatic(PerformetricsUtils.class);
        when(PerformetricsUtils.getCpuTimeNanos()).thenReturn(mockedCpuTime);

        CpuTimeRunnableOperation operation = new CpuTimeRunnableOperation(runnable);
        operation.run();

        Counter counter = operation.getCounter();
        // Check that both units-before and units-after have been updated
        assertThat("Units-before should have been updated", counter.getUnitsBefore(), is(mockedCpuTime));
        assertThat("Units-after should have been updated", counter.getUnitsAfter(), is(mockedCpuTime));

        // Check that the correct measure (CPU time) method was called exactly twice
        verifyStatic(PerformetricsUtils.class, BDDMockito.times(2));
        PerformetricsUtils.getCpuTimeNanos();
    }

}
