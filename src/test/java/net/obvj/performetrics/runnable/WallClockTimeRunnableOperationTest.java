package net.obvj.performetrics.runnable;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Test;

import net.obvj.performetrics.Counter;

/**
 * Test methods for the {@link WallClockTimeRunnableOperation}
 *
 * @author oswaldo.bapvic.jr
 */
public class WallClockTimeRunnableOperationTest
{
    private static final long DUMMY_SLEEP_TIME = 500L;

    /**
     * A dummy {@link Runnable} that does nothing but sleeping, for testing purposes
     */
    private static final Runnable DUMMY_RUNNABLE = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                Thread.sleep(DUMMY_SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().severe("Thread interrupted on sleep");
            }
        }
    };

    /**
     * Tests that the correct counter is specified for this operation and the initial values
     */
    @Test
    public void constructor_assignsCorrectCounterAndInitialValues()
    {
        WallClockTimeRunnableOperation operation = new WallClockTimeRunnableOperation(DUMMY_RUNNABLE);
        Counter counter = operation.getCounter();
        assertThat(counter.getType(), is(Counter.Type.WALL_CLOCK_TIME));
        assertThat(counter.getUnitsBefore(), is(0L));
        assertThat(counter.getUnitsAfter(), is(0L));
    }

    /**
     * Tests the elapsed time for a dummy {@link Runnable} by asserting that the value is at
     * least the amount of sleep time executed by that runnable
     */
    @Test
    public void run_updatesCounter()
    {
        WallClockTimeRunnableOperation operation = new WallClockTimeRunnableOperation(DUMMY_RUNNABLE);
        operation.run();
        Counter counter = operation.getCounter();
        assertThat("Units-before should have been updated", counter.getUnitsBefore(), is(greaterThan(0L)));
        assertThat("Units-after should have been updated", counter.getUnitsAfter(), is(greaterThan(0L)));
        assertThat("The elapsed time should be greater than the Callable's sleep time",
                counter.getDefaultTimeUnit().toMillis(counter.elapsedTime()), is(greaterThanOrEqualTo(DUMMY_SLEEP_TIME)));
        assertThat(operation.elapsedTime(TimeUnit.MILLISECONDS), is(counter.elapsedTime()));
    }

}
