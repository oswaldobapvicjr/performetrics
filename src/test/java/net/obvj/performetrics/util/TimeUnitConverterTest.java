package net.obvj.performetrics.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;

/**
 * Unit tests for the {@link TimeUnitConverter}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class TimeUnitConverterTest
{

    /**
     * Tests that no instances of this utility class are created.
     *
     * @throws ReflectiveOperationException in case of error getting class metadata
     */
    @Test
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        TestUtils.assertNoInstancesAllowed(TimeUnitConverter.class);
    }

    @Test
    public void convert_2MinutesToMilliseconds()
    {
        assertThat(TimeUnitConverter.convert(2, TimeUnit.MINUTES, TimeUnit.MILLISECONDS), is(equalTo(2.0 * 60 * 1000)));
    }

    @Test
    public void convert_90SecondsToMinutes()
    {
        assertThat(TimeUnitConverter.convert(30, TimeUnit.SECONDS, TimeUnit.MINUTES), is(equalTo(0.5)));
    }

    @Test
    public void convert_999MillisecondsToSeconds()
    {
        assertThat(TimeUnitConverter.convert(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS), is(equalTo(0.999)));
    }

    @Test
    public void convert_988MillisecondsToSecondsAnd2DecimalPlaces()
    {
        assertThat(TimeUnitConverter.convert(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2), is(equalTo(0.99)));
    }

    @Test
    public void convert_988MillisecondsToSecondsAnd0DecimalPlaces()
    {
        assertThat(TimeUnitConverter.convert(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 0), is(equalTo(1.0)));
    }

    @Test
    public void round_positiveDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, 2), is(equalTo(22.86)));
    }

    @Test
    public void round_zeroDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, 0), is(equalTo(23.0)));
    }

    @Test
    public void round_negativeDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, -1), is(equalTo(20.0)));
    }

}
