package net.obvj.performetrics.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.obvj.performetrics.TestUtils;

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
        assertThat(TimeUnitConverter.convert(2, TimeUnit.MINUTES, TimeUnit.MILLISECONDS), is(2.0 * 60 * 1000));
    }

    @Test
    public void convert_90SecondsToMinutes()
    {
        assertThat(TimeUnitConverter.convert(30, TimeUnit.SECONDS, TimeUnit.MINUTES), is(0.5));
    }

    @Test
    public void convert_999MillisecondsToSeconds()
    {
        assertThat(TimeUnitConverter.convert(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS), is(0.999));
    }

}
