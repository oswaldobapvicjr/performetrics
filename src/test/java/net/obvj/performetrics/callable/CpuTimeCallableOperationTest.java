package net.obvj.performetrics.callable;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.runnable.CpuTimeRunnableOperation;

/**
 * Test methods for the {@link CpuTimeRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
public class CpuTimeCallableOperationTest
{
    /**
     * A dummy Callable for testing purposes
     */
    private static final Callable<String> MOCKED_CALLABLE = PowerMockito.mock(Callable.class);

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void testCounterAndInitialValues()
    {
        CpuTimeCallableOperation<String> operation = new CpuTimeCallableOperation<>(MOCKED_CALLABLE);
        Counter counter = operation.getCounter();
        assertEquals(Counter.Type.CPU_TIME, counter.getType());
        assertEquals(0, counter.getUnitsBefore());
        assertEquals(0, counter.getUnitsAfter());
    }

}
