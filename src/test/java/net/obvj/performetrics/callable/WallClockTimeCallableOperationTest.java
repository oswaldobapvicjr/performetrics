package net.obvj.performetrics.callable;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.junit.Test;

import net.obvj.performetrics.Counter;

/**
 * Test methods for the {@link WallClockTimeRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
public class WallClockTimeCallableOperationTest
{
    private static final long DUMMY_SLEEP_TIME = 500L;
    private static final String STR_TEST = "test123";

    /**
     * A dummy {@link Callable} that does nothing but sleeping and returning a constant value,
     * for testing purposes
     */
    private static final Callable<String> DUMMY_CALLABLE = new Callable<String>()
    {
        @Override
        public String call() throws Exception
        {
            try
            {
                Thread.sleep(DUMMY_SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().severe("Thread interrupted on sleep");
            }
            return STR_TEST;
        }
    };

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void constructor_assignsCorrectCounterAndInitialValues()
    {
        WallClockTimeCallableOperation<String> operation = new WallClockTimeCallableOperation<>(DUMMY_CALLABLE);
        Counter counter = operation.getCounter();
        assertThat(counter.getType(), is(Counter.Type.WALL_CLOCK_TIME));
        assertThat(counter.getUnitsBefore(), is(0L));
        assertThat(counter.getUnitsAfter(), is(0L));
    }

    /**
     * Tests the elapsed time for a dummy {@link Callable} by asserting that the value is at
     * least the amount of sleep time executed by that runnable
     *
     * @throws Exception if unable to mock the {@link Callable}
     */
    @Test
    public void call_updatesCounter() throws Exception
    {
        WallClockTimeCallableOperation<String> operation = new WallClockTimeCallableOperation<>(DUMMY_CALLABLE);
        assertEquals(STR_TEST, operation.call());
        Counter counter = operation.getCounter();
        assertThat("Units-before should have been updated", counter.getUnitsBefore(), is(greaterThan(0L)));
        assertThat("Units-after should have been updated", counter.getUnitsAfter(), is(greaterThan(0L)));
        assertThat("The elapsed time should be greater than the Callable's sleep time",
                operation.getTimeUnit().toMillis(counter.elapsedTime()), is(greaterThanOrEqualTo(DUMMY_SLEEP_TIME)));
    }
}
