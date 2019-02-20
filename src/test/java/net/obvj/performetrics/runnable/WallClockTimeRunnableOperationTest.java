package net.obvj.performetrics.runnable;

import static org.junit.Assert.*;

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
     * A dummy Runnable that does nothing but sleeping, for testing purposes
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
    public void testCounterAndInitialValues()
    {
        WallClockTimeRunnableOperation operation = new WallClockTimeRunnableOperation(DUMMY_RUNNABLE);
        Counter counter = operation.getCounter();
        assertEquals(Counter.Type.WALL_CLOCK_TIME, counter.getType());
        assertEquals(0, counter.getUnitsBefore());
        assertEquals(0, counter.getUnitsAfter());
    }

    /**
     * Tests the elapsed time for a dummy runnable by asserting that the value is at least the
     * amount of sleep time executed by that runnable
     */
    @Test
    public void testCounterElapsedTime()
    {
        WallClockTimeRunnableOperation operation = new WallClockTimeRunnableOperation(DUMMY_RUNNABLE);
        operation.run();
        Counter counter = operation.getCounter();
        assertTrue("Units-before was not updated", 0 < counter.getUnitsBefore());
        assertTrue("Units-after was not updated", 0 < counter.getUnitsAfter());
        assertTrue("The elapsed time was lower than the Runnable's sleep time",
                DUMMY_SLEEP_TIME <= operation.getTimeUnit().toMillis(counter.elapsedTime()));
    }

}
