package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.*;
import static net.obvj.performetrics.Counter.Type.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

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
        assertThat(counter.getDefaultTimeUnit(), is(NANOSECONDS));
    }

    @Test
    public void getters_succeed()
    {
        Counter counter = new Counter(CPU_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertThat(counter.getType(), is(CPU_TIME));
        assertThat(counter.getDefaultTimeUnit(), is(MILLISECONDS));
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
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInSeconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(), is(1L));
    }

    @Test
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInMilliseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertThat(counter.elapsedTime(), is(500L));
    }

    @Test
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInNanoseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, NANOSECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(NANOSECONDS));
        counter.setUnitsBefore(1000000000);
        counter.setUnitsAfter(6000000000l); // 5 seconds after
        assertThat(counter.getDefaultTimeUnit().toSeconds(counter.elapsedTime()), is(5L));
    }
    
    @Test
    public void elapsedTime_withTimeUnitEqualToTheOriginal_returnsDifferenceInOriginalTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(TimeUnit.SECONDS), is(1L));
    }
    
    @Test
    public void elapsedTime_withTimeUnitLowerThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(TimeUnit.MILLISECONDS), is(1000L));
    }
    
    @Test
    public void elapsedTime_withTimeUnitHigherThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getDefaultTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(2000);
        counter.setUnitsAfter(3500); // 1.5 second after
        assertThat(counter.elapsedTime(TimeUnit.SECONDS), is(1L));
    }


}
