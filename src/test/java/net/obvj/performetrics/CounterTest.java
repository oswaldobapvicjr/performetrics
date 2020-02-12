package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.strategy.ConversionStrategy;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Test methods for the {@Counter} class.
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemUtils.class)
public class CounterTest
{

    @Before
    public void setup()
    {
        PowerMockito.mockStatic(SystemUtils.class);
    }

    @Test
    public void constructor_withType_assignsDefaultTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        assertThat(counter.getTimeUnit(), is(NANOSECONDS));
    }

    @Test
    public void constructor_withTypeAndTimeUnit_assignsDefaultConversionStrategy()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getConversionStrategy(), is(ConversionStrategy.DOUBLE_PRECISION));
    }

    @Test
    public void constructor_withTypeAndTimeUnitAndConversionStrategy_succeeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS, ConversionStrategy.FAST);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        assertThat(counter.getConversionStrategy(), is(ConversionStrategy.FAST));
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
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInSeconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(), is(1L));
    }

    @Test
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInMilliseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertThat(counter.elapsedTime(), is(500L));
    }

    @Test
    public void elapsedTime_withoutParamAndwithUnitsSet_returnsDifferenceInNanoseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, NANOSECONDS);
        assertThat(counter.getTimeUnit(), is(NANOSECONDS));
        counter.setUnitsBefore(1000000000L);
        counter.setUnitsAfter(6000000000L); // 5 seconds after
        assertThat(counter.elapsedTime(), is(5000000000L));
    }

    @Test
    public void elapsedTime_withTimeUnitEqualToTheOriginal_returnsDifferenceInOriginalTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(TimeUnit.SECONDS), is(1.0));
    }

    @Test
    public void elapsedTime_withTimeUnitLowerThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(TimeUnit.MILLISECONDS), is(1000.0));
    }

    @Test
    public void elapsedTime_withTimeUnitHigherThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(2000);
        counter.setUnitsAfter(3500); // 1.5 second after
        assertThat(counter.elapsedTime(TimeUnit.SECONDS), is(1.5));
    }

    @Test
    public void elapsedTime_withUnitsBeforeSetOnly_returnsDifferenceBetweenUnitsBeforeAndCurrentTime()
    {
        Mockito.when(SystemUtils.getWallClockTimeNanos()).thenReturn(9000L);
        Counter counter = new Counter(WALL_CLOCK_TIME, NANOSECONDS);
        counter.setUnitsBefore(2000);
        assertThat(counter.elapsedTime(), is(7000L));
    }

    @Test
    public void elapsedTime_unitsAfterLowerThanUnitsBefore_negative1()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(5000);
        counter.setUnitsAfter(500);
        assertThat(counter.elapsedTime(), is(-1L));
    }

    @Test
    public void elapsedTime_withCoarserTimeUnitAndFastConversion_differenceIsTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, ConversionStrategy.FAST);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(59); // 59 seconds after
        assertThat(counter.elapsedTime(TimeUnit.MINUTES), is(0.0));
    }

    @Test
    public void elapsedTime_withCoarserTimeUnitAndDoublePrecisionConversion_differenceIsNotTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, ConversionStrategy.DOUBLE_PRECISION);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(59); // 59 seconds after
        assertThat(counter.elapsedTime(TimeUnit.MINUTES), is(0.98333));
    }

    @Test
    public void elapsedTime_withFinerTimeUnitAndFastConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, ConversionStrategy.FAST);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(2); // 2 seconds
        assertThat(counter.elapsedTime(TimeUnit.MILLISECONDS), is(2000.0));
    }

    @Test
    public void elapsedTime_withFinerTimeUnitAndDoublePrecisionConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, ConversionStrategy.DOUBLE_PRECISION);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(2); // 2 seconds
        assertThat(counter.elapsedTime(TimeUnit.MILLISECONDS), is(2000.0));
    }

    @Test
    public void elapsedTime_withTimeUnitAndCustomConversionStrategy_appliesCustomConversion()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(2000);
        counter.setUnitsAfter(3500); // 1.5 second after
        assertThat(counter.elapsedTime(TimeUnit.SECONDS, ConversionStrategy.FAST), is(1.0));
    }

}
