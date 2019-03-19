package net.obvj.performetrics.callable;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.util.PerformetricsUtils;

/**
 * Test methods for the {@link CpuTimeCallableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PerformetricsUtils.class)
public class CpuTimeCallableOperationTest
{
    /**
     * A dummy Callable for testing purposes
     */
    private static final Callable<String> MOCKED_CALLABLE = PowerMockito.mock(Callable.class);
    private static final String STRING_CALLABLE_RETURN = "test123";

    /**
     * Setup the expects on the mocked Callable object
     */
    public static void setupMockedCallable()
    {
        try
        {
            PowerMockito.when(MOCKED_CALLABLE.call()).thenReturn(STRING_CALLABLE_RETURN);
        }
        catch (Exception e)
        {
            fail("Unable to mock Callable result: " + e.getClass().getName());
        }
    }

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

    /**
     * Tests the elapsed CPU time for a dummy runnable by asserting that a call to the correct
     * method from PerformetricsUtils is called and the units before and after are updated
     *
     * @throws Exception if unable to compute Callable's result
     */
    @Test
    public void testCounterElapsedTime() throws Exception
    {
        long mockedCpuTime = 1200000000l;

        PowerMockito.mockStatic(PerformetricsUtils.class);
        PowerMockito.when(PerformetricsUtils.getCpuTimeNanos()).thenReturn(mockedCpuTime);

        setupMockedCallable();
        CpuTimeCallableOperation<String> operation = new CpuTimeCallableOperation<String>(MOCKED_CALLABLE);

        // Check that the result from the inner Callable is returned by the outer Callable
        assertEquals(STRING_CALLABLE_RETURN, operation.call());

        Counter counter = operation.getCounter();
        // Check that both units-before and units-after have been updated
        assertEquals("Units-before was not updated", mockedCpuTime, counter.getUnitsBefore());
        assertEquals("Units-after was not updated", mockedCpuTime, counter.getUnitsAfter());

        // Check that the correct measure (CPU time) method was called exactly twice
        PowerMockito.verifyStatic(PerformetricsUtils.class, BDDMockito.times(2));
        PerformetricsUtils.getCpuTimeNanos();
    }

}
