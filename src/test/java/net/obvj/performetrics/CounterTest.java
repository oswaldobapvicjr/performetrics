package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.*;
import static net.obvj.performetrics.Counter.Type.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Test methods for the {@Counter} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class CounterTest
{
    @Test
    public void constructor_withType_assignsDefaultTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        assertThat(counter.getTimeUnit(), is(NANOSECONDS));
    }

    @Test
    public void getters_succeed()
    {
        Counter counter = new Counter(CPU_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertThat(counter.getType(), is(CPU_TIME));
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        assertThat(counter.getUnitsBefore(), is(5L));
        assertThat(counter.getUnitsAfter(), is(10L));
    }

    @Test
    public void toString_withAllFieldsSet_suceeds()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertThat(counter.toString(), is(String.format(Counter.STRING_FORMAT, WALL_CLOCK_TIME, MILLISECONDS, 5, 10)));
    }

    @Test
    public void getElapsedTime_withUnitsSet_returnsDifferenceInSeconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(), is(1L));
        assertThat(counter.getTimeUnit(), is(SECONDS));
    }

    @Test
    public void getElapsedTime_withUnitsSet_returnsDifferenceInMilliseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertThat(counter.elapsedTime(), is(500L));
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
    }

    @Test
    public void getElapsedTime_withUnitsSet_returnsDifferenceInNanoseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, NANOSECONDS);
        counter.setUnitsBefore(1000000000);
        counter.setUnitsAfter(6000000000l); // 5 seconds after
        assertThat(counter.getTimeUnit().toSeconds(counter.elapsedTime()), is(5L));
        assertThat(counter.getTimeUnit(), is(NANOSECONDS));
    }

}
