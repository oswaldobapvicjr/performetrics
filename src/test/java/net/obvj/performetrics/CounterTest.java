package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

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
        assertThat(counter.getTimeUnit(), is(equalTo(NANOSECONDS)));
    }

    @Test
    public void constructor_withTypeAndTimeUnit_assignsDefaultConversionMode()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getConversionMode(), is(equalTo(DOUBLE_PRECISION)));
    }

    @Test
    public void constructor_withTypeAndTimeUnitAndConversionMode_succeeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS, FAST);
        assertThat(counter.getTimeUnit(), is(equalTo(MILLISECONDS)));
        assertThat(counter.getConversionMode(), is(equalTo(FAST)));
    }

    @Test
    public void getters_succeed()
    {
        Counter counter = new Counter(CPU_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertThat(counter.getType(), is(equalTo(CPU_TIME)));
        assertThat(counter.getTimeUnit(), is(equalTo(MILLISECONDS)));
        assertThat(counter.getUnitsBefore(), is(equalTo(5L)));
        assertThat(counter.getUnitsAfter(), is(equalTo(10L)));
    }

    @Test
    public void toString_withAllFieldsSet_suceeds()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME, MILLISECONDS);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        String expectedString = String.format(Counter.STRING_FORMAT, WALL_CLOCK_TIME, MILLISECONDS, 5, 10);
        assertThat(counter.toString(), is(equalTo(expectedString)));
    }

    @Test
    public void elapsedTimeInternal_withUnitsSet_returnsDifferenceInSeconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(equalTo(SECONDS)));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTimeInternal(), is(equalTo(1L)));
    }

    @Test
    public void elapsedTimeInternal_withUnitsSet_returnsDifferenceInMilliseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(equalTo(MILLISECONDS)));
        counter.setUnitsBefore(1000);
        counter.setUnitsAfter(1500); // 500 milliseconds after
        assertThat(counter.elapsedTimeInternal(), is(500L));
    }

    @Test
    public void elapsedTimeInternal_withUnitsSet_returnsDifferenceInNanoseconds()
    {
        Counter counter = new Counter(SYSTEM_TIME, NANOSECONDS);
        assertThat(counter.getTimeUnit(), is(NANOSECONDS));
        counter.setUnitsBefore(1000000000L);
        counter.setUnitsAfter(6000000000L); // 5 seconds after
        assertThat(counter.elapsedTimeInternal(), is(equalTo(5000000000L)));
    }

    @Test
    public void elapsedTime_withTimeUnitEqualToTheOriginal_returnsDifferenceInOriginalTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(SECONDS), is(equalTo(1.0)));
    }

    @Test
    public void elapsedTime_withTimeUnitLowerThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(MILLISECONDS), is(equalTo(1000.0)));
    }

    @Test
    public void elapsedTime_withTimeUnitHigherThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(2000);
        counter.setUnitsAfter(3500); // 1.5 second after
        assertThat(counter.elapsedTime(SECONDS), is(equalTo(1.5)));
    }

    @Test
    public void elapsedTimeInternal_withUnitsBeforeSetOnly_returnsDifferenceBetweenUnitsBeforeAndCurrentTime()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME, NANOSECONDS);
        counter.setUnitsBefore(2000);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(9000L);
            assertThat(counter.elapsedTimeInternal(), is(equalTo(7000L)));
        }
    }

    @Test
    public void elapsedTimeInternal_unitsAfterLowerThanUnitsBefore_negative1()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(5000);
        counter.setUnitsAfter(500);
        assertThat(counter.elapsedTimeInternal(), is(equalTo(-1L)));
    }

    @Test
    public void elapsedTime_withCoarserTimeUnitAndFastConversion_differenceIsTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, FAST);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(59); // 59 seconds after
        assertThat(counter.elapsedTime(MINUTES), is(equalTo(0.0)));
    }

    @Test
    public void elapsedTime_withCoarserTimeUnitAndDoublePrecisionConversion_differenceIsNotTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, DOUBLE_PRECISION);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(59); // 59 seconds after
        assertThat(counter.elapsedTime(MINUTES), is(equalTo(0.983333333)));
    }

    @Test
    public void elapsedTime_withFinerTimeUnitAndFastConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, FAST);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(2); // 2 seconds
        assertThat(counter.elapsedTime(MILLISECONDS), is(equalTo(2000.0)));
    }

    @Test
    public void elapsedTime_withFinerTimeUnitAndDoublePrecisionConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS, DOUBLE_PRECISION);
        assertThat(counter.getTimeUnit(), is(SECONDS));
        counter.setUnitsAfter(2); // 2 seconds
        assertThat(counter.elapsedTime(MILLISECONDS), is(equalTo(2000.0)));
    }

    @Test
    public void elapsedTime_withTimeUnitAndCustomConversionMode_appliesCustomConversion()
    {
        Counter counter = new Counter(SYSTEM_TIME, MILLISECONDS);
        assertThat(counter.getTimeUnit(), is(MILLISECONDS));
        counter.setUnitsBefore(2000);
        counter.setUnitsAfter(3500); // 1.5 second after
        assertThat(counter.elapsedTime(SECONDS, FAST), is(equalTo(1.0)));
    }

    @Test
    public void elapsedTime_withoutParams_returnsDurationWithValidDifference()
    {
        Counter counter = new Counter(SYSTEM_TIME, SECONDS);
        assertThat(counter.getTimeUnit(), is(equalTo(SECONDS)));
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3); // 1 second after
        assertThat(counter.elapsedTime(), is(equalTo(Duration.of(1, SECONDS))));
    }

}
