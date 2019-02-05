package net.obvj.performetrics;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test methods for the {@code TimeUnit} enumeration
 *
 * @author oswaldo.bapvic.jr
 */
public class TimeUnitTest
{
    private static final long NANOSECONDS_1 = 100000000;
    private static final long MILLISECONDS_1 = 100;
    private static final long SECONDS_1 = 0;

    private static final long NANOSECONDS_2 = 10000000000l;
    private static final long MILLISECONDS_2 = 10000;
    private static final long SECONDS_2 = 10;

    private static final long NANOSECONDS_3 = 0;
    private static final long MILLISECONDS_3 = 0;
    private static final long SECONDS_3 = 0;

    @Test
    public void testConvertedTimeFromNanosecondsToNanoseconds()
    {
        assertEquals(NANOSECONDS_1, TimeUnit.NANOSECOND.fromNanoseconds(NANOSECONDS_1));
        assertEquals(NANOSECONDS_2, TimeUnit.NANOSECOND.fromNanoseconds(NANOSECONDS_2));
        assertEquals(NANOSECONDS_3, TimeUnit.NANOSECOND.fromNanoseconds(NANOSECONDS_3));
    }

    @Test
    public void testConvertedTimeFromNanosecondsToMilliseconds()
    {
        assertEquals(MILLISECONDS_1, TimeUnit.MILLISECOND.fromNanoseconds(NANOSECONDS_1));
        assertEquals(MILLISECONDS_2, TimeUnit.MILLISECOND.fromNanoseconds(NANOSECONDS_2));
        assertEquals(MILLISECONDS_3, TimeUnit.MILLISECOND.fromNanoseconds(NANOSECONDS_3));
    }

    @Test
    public void testConvertedTimeFromNanosecondsToSeconds()
    {
        assertEquals(SECONDS_1, TimeUnit.SECOND.fromNanoseconds(NANOSECONDS_1));
        assertEquals(SECONDS_2, TimeUnit.SECOND.fromNanoseconds(NANOSECONDS_2));
        assertEquals(SECONDS_3, TimeUnit.SECOND.fromNanoseconds(NANOSECONDS_3));
    }

}
