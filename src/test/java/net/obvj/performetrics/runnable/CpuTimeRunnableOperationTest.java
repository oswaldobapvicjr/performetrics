package net.obvj.performetrics.runnable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import net.obvj.performetrics.Counter;

/**
 * Test methods for the {@link CpuTimeRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
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

}
