package net.obvj.performetrics.callable;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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
    private static final String STRING_CALLABLE_RETURN = "test123";

    @Mock
    private Callable<String> callable;

    /**
     * Setup the expects on the mocked {@link Callable} object
     */
    public void setupMockedCallable() throws Exception
    {
        when(callable.call()).thenReturn(STRING_CALLABLE_RETURN);
    }

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void constructor_assignsCorrectCounterAndInitialValues()
    {
        CpuTimeCallableOperation<String> operation = new CpuTimeCallableOperation<>(callable);
        Counter counter = operation.getCounter();
        assertThat(counter.getType(), is(Counter.Type.CPU_TIME));
        assertThat(counter.getUnitsBefore(), is(0L));
        assertThat(counter.getUnitsAfter(), is(0L));
    }

    /**
     * Tests the elapsed CPU time for a dummy callable by asserting that a call to the correct
     * method from PerformetricsUtils is called and the units before and after are updated
     *
     * @throws Exception if unable to compute Callable's result
     */
    @Test
    public void call_updatesCounter() throws Exception
    {
        long mockedCpuTime = 1200000000L;

        mockStatic(PerformetricsUtils.class);
        when(PerformetricsUtils.getCpuTimeNanos()).thenReturn(mockedCpuTime);

        setupMockedCallable();
        CpuTimeCallableOperation<String> operation = new CpuTimeCallableOperation<String>(callable);

        // Check that the result from the inner Callable is returned by the outer Callable
        assertThat(operation.call(), is(STRING_CALLABLE_RETURN));

        Counter counter = operation.getCounter();
        // Check that both units-before and units-after have been updated
        assertThat("Units-before should have been updated", counter.getUnitsBefore(), is(mockedCpuTime));
        assertThat("Units-after should have been updated", counter.getUnitsAfter(), is(mockedCpuTime));

        // Check that the correct measure (CPU time) method was called exactly twice
        verifyStatic(PerformetricsUtils.class, BDDMockito.times(2));
        PerformetricsUtils.getCpuTimeNanos();
    }

}
