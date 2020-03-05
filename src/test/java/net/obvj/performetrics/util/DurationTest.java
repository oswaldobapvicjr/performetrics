package net.obvj.performetrics.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.obvj.performetrics.util.Duration.FormatStyle;

/**
 * Unit tests for the {@link Duration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class DurationTest
{

    @Test
    public void of_validAmountAndTimeUnitAndNanosecondsPrecision_populatesAccordingly()
    {
        Duration td1 = Duration.of(1999888777, TimeUnit.NANOSECONDS);
        assertThat(td1.getHours(),       is(0L));
        assertThat(td1.getMinutes(),     is(0));
        assertThat(td1.getSeconds(),     is(1));
        assertThat(td1.getNanoseconds(), is(999888777));
    }

    @Test
    public void of_validAmountAndTimeUnitAndSecondsPrecision_populatesAccordingly()
    {
        Duration td1 = Duration.of(5410, TimeUnit.SECONDS);
        assertThat(td1.getHours(),       is(1L));
        assertThat(td1.getMinutes(),     is(30));
        assertThat(td1.getSeconds(),     is(10));
        assertThat(td1.getNanoseconds(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_negativeAmount_illegalArgumentException()
    {
        Duration.of(-1999888777, TimeUnit.NANOSECONDS);
    }

    @Test
    public void toString_full_displaysAllUnits()
    {
        assertThat(Duration.of(0,          TimeUnit.NANOSECONDS).toString(FormatStyle.FULL, false),  is(  "0:00:00.000000000"));

        assertThat(Duration.of(1,          TimeUnit.NANOSECONDS).toString(FormatStyle.FULL, false),  is(  "0:00:00.000000001"));
        assertThat(Duration.of(1,          TimeUnit.MILLISECONDS).toString(FormatStyle.FULL, false), is(  "0:00:00.001000000"));
        assertThat(Duration.of(1,          TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:00:01.000000000"));
        assertThat(Duration.of(1,          TimeUnit.MINUTES).toString(FormatStyle.FULL, false),      is(  "0:01:00.000000000"));
        assertThat(Duration.of(1,          TimeUnit.HOURS).toString(FormatStyle.FULL, false),        is(  "1:00:00.000000000"));
        assertThat(Duration.of(1,          TimeUnit.DAYS).toString(FormatStyle.FULL, false),         is( "24:00:00.000000000"));

        assertThat(Duration.of(789,        TimeUnit.NANOSECONDS).toString(FormatStyle.FULL, false),  is(  "0:00:00.000000789"));
        assertThat(Duration.of(123456789,  TimeUnit.NANOSECONDS).toString(FormatStyle.FULL, false),  is(  "0:00:00.123456789"));
        assertThat(Duration.of(1000000000, TimeUnit.NANOSECONDS).toString(FormatStyle.FULL, false),  is(  "0:00:01.000000000"));
        assertThat(Duration.of(1001,       TimeUnit.MILLISECONDS).toString(FormatStyle.FULL, false), is(  "0:00:01.001000000"));
        assertThat(Duration.of(1601,       TimeUnit.MILLISECONDS).toString(FormatStyle.FULL, false), is(  "0:00:01.601000000"));
        assertThat(Duration.of(3601,       TimeUnit.MILLISECONDS).toString(FormatStyle.FULL, false), is(  "0:00:03.601000000"));
        assertThat(Duration.of(70,         TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:01:10.000000000"));
        assertThat(Duration.of(601,        TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:10:01.000000000"));
        assertThat(Duration.of(959,        TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:15:59.000000000"));
        assertThat(Duration.of(960,        TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:16:00.000000000"));
        assertThat(Duration.of(970,        TimeUnit.SECONDS).toString(FormatStyle.FULL, false),      is(  "0:16:10.000000000"));
        assertThat(Duration.of(3601,       TimeUnit.MINUTES).toString(FormatStyle.FULL, false),      is( "60:01:00.000000000"));
        assertThat(Duration.of(2,          TimeUnit.HOURS).toString(FormatStyle.FULL, false),        is(  "2:00:00.000000000"));
        assertThat(Duration.of(100,        TimeUnit.HOURS).toString(FormatStyle.FULL, false),        is("100:00:00.000000000"));
    }

    @Test
    public void toString_short_abbreviatesIfPossible()
    {
        assertThat(Duration.of(0,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORT, false),  is(        "0.000000000"));

        assertThat(Duration.of(1,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORT, false),  is(        "0.000000001"));
        assertThat(Duration.of(1,          TimeUnit.MILLISECONDS).toString(FormatStyle.SHORT, false), is(        "0.001000000"));
        assertThat(Duration.of(1,          TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(        "1.000000000"));
        assertThat(Duration.of(1,          TimeUnit.MINUTES).toString(FormatStyle.SHORT, false),      is(     "1:00.000000000"));
        assertThat(Duration.of(1,          TimeUnit.HOURS).toString(FormatStyle.SHORT, false),        is(  "1:00:00.000000000"));
        assertThat(Duration.of(1,          TimeUnit.DAYS).toString(FormatStyle.SHORT, false),         is( "24:00:00.000000000"));

        assertThat(Duration.of(789,        TimeUnit.NANOSECONDS).toString(FormatStyle.SHORT, false),  is(        "0.000000789"));
        assertThat(Duration.of(123456789,  TimeUnit.NANOSECONDS).toString(FormatStyle.SHORT, false),  is(        "0.123456789"));
        assertThat(Duration.of(1000000000, TimeUnit.NANOSECONDS).toString(FormatStyle.SHORT, false),  is(        "1.000000000"));
        assertThat(Duration.of(1001,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORT, false), is(        "1.001000000"));
        assertThat(Duration.of(1601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORT, false), is(        "1.601000000"));
        assertThat(Duration.of(3601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORT, false), is(        "3.601000000"));
        assertThat(Duration.of(70,         TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(     "1:10.000000000"));
        assertThat(Duration.of(601,        TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(    "10:01.000000000"));
        assertThat(Duration.of(959,        TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(    "15:59.000000000"));
        assertThat(Duration.of(960,        TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(    "16:00.000000000"));
        assertThat(Duration.of(970,        TimeUnit.SECONDS).toString(FormatStyle.SHORT, false),      is(    "16:10.000000000"));
        assertThat(Duration.of(3601,       TimeUnit.MINUTES).toString(FormatStyle.SHORT, false),      is( "60:01:00.000000000"));
        assertThat(Duration.of(2,          TimeUnit.HOURS).toString(FormatStyle.SHORT, false),        is(  "2:00:00.000000000"));
        assertThat(Duration.of(100,        TimeUnit.HOURS).toString(FormatStyle.SHORT, false),        is("100:00:00.000000000"));
    }

    @Test
    public void toString_shorterWithLegend_supressesTrailingZeros()
    {
        assertThat(Duration.of(0,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, true),  is("0 second(s)"));

        assertThat(Duration.of(1,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, true),  is("0.000000001 second(s)"));
        assertThat(Duration.of(1,          TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, true), is("0.001 second(s)"));
        assertThat(Duration.of(1,          TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("1 second(s)"));
        assertThat(Duration.of(1,          TimeUnit.MINUTES).toString(FormatStyle.SHORTER, true),      is("1:00 minute(s)"));
        assertThat(Duration.of(1,          TimeUnit.HOURS).toString(FormatStyle.SHORTER, true),        is("1:00:00 hour(s)"));
        assertThat(Duration.of(1,          TimeUnit.DAYS).toString(FormatStyle.SHORTER, true),         is( "24:00:00 hour(s)"));

        assertThat(Duration.of(789,        TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, true),  is("0.000000789 second(s)"));
        assertThat(Duration.of(123456789,  TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, true),  is("0.123456789 second(s)"));
        assertThat(Duration.of(1000000000, TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, true),  is("1 second(s)"));
        assertThat(Duration.of(1001,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, true), is("1.001 second(s)"));
        assertThat(Duration.of(1601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, true), is("1.601 second(s)"));
        assertThat(Duration.of(3601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, true), is("3.601 second(s)"));
        assertThat(Duration.of(70,         TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("1:10 minute(s)"));
        assertThat(Duration.of(601,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("10:01 minute(s)"));
        assertThat(Duration.of(959,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("15:59 minute(s)"));
        assertThat(Duration.of(960,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("16:00 minute(s)"));
        assertThat(Duration.of(970,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, true),      is("16:10 minute(s)"));
        assertThat(Duration.of(3601,       TimeUnit.MINUTES).toString(FormatStyle.SHORTER, true),      is("60:01:00 hour(s)"));
        assertThat(Duration.of(2,          TimeUnit.HOURS).toString(FormatStyle.SHORTER, true),        is("2:00:00 hour(s)"));
        assertThat(Duration.of(100,        TimeUnit.HOURS).toString(FormatStyle.SHORTER, true),        is("100:00:00 hour(s)"));
    }
    
    @Test
    public void toString_shorterWithoutLegend_supressesTrailingZeros()
    {
        assertThat(Duration.of(0,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, false),  is("0"));

        assertThat(Duration.of(1,          TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, false),  is("0.000000001"));
        assertThat(Duration.of(1,          TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, false), is("0.001"));
        assertThat(Duration.of(1,          TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("1"));
        assertThat(Duration.of(1,          TimeUnit.MINUTES).toString(FormatStyle.SHORTER, false),      is("1:00"));
        assertThat(Duration.of(1,          TimeUnit.HOURS).toString(FormatStyle.SHORTER, false),        is("1:00:00"));
        assertThat(Duration.of(1,          TimeUnit.DAYS).toString(FormatStyle.SHORTER, false),         is( "24:00:00"));

        assertThat(Duration.of(789,        TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, false),  is("0.000000789"));
        assertThat(Duration.of(123456789,  TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, false),  is("0.123456789"));
        assertThat(Duration.of(1000000000, TimeUnit.NANOSECONDS).toString(FormatStyle.SHORTER, false),  is("1"));
        assertThat(Duration.of(1001,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, false), is("1.001"));
        assertThat(Duration.of(1601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, false), is("1.601"));
        assertThat(Duration.of(3601,       TimeUnit.MILLISECONDS).toString(FormatStyle.SHORTER, false), is("3.601"));
        assertThat(Duration.of(70,         TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("1:10"));
        assertThat(Duration.of(601,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("10:01"));
        assertThat(Duration.of(959,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("15:59"));
        assertThat(Duration.of(960,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("16:00"));
        assertThat(Duration.of(970,        TimeUnit.SECONDS).toString(FormatStyle.SHORTER, false),      is("16:10"));
        assertThat(Duration.of(3601,       TimeUnit.MINUTES).toString(FormatStyle.SHORTER, false),      is("60:01:00"));
        assertThat(Duration.of(2,          TimeUnit.HOURS).toString(FormatStyle.SHORTER, false),        is("2:00:00"));
        assertThat(Duration.of(100,        TimeUnit.HOURS).toString(FormatStyle.SHORTER, false),        is("100:00:00"));
    }

    @Test
    public void toString_iso8601()
    {
        assertThat(Duration.of(0,          TimeUnit.NANOSECONDS).toString(FormatStyle.ISO_8601, false),  is("PT0S"));

        assertThat(Duration.of(1,          TimeUnit.NANOSECONDS).toString(FormatStyle.ISO_8601, false),  is("PT0.000000001S"));
        assertThat(Duration.of(1,          TimeUnit.MILLISECONDS).toString(FormatStyle.ISO_8601, false), is("PT0.001S"));
        assertThat(Duration.of(1,          TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT1S"));
        assertThat(Duration.of(1,          TimeUnit.MINUTES).toString(FormatStyle.ISO_8601, false),      is("PT1M"));
        assertThat(Duration.of(1,          TimeUnit.HOURS).toString(FormatStyle.ISO_8601, false),        is("PT1H"));
        assertThat(Duration.of(1,          TimeUnit.DAYS).toString(FormatStyle.ISO_8601, false),         is("PT24H"));

        assertThat(Duration.of(789,        TimeUnit.NANOSECONDS).toString(FormatStyle.ISO_8601, false),  is("PT0.000000789S"));
        assertThat(Duration.of(123456789,  TimeUnit.NANOSECONDS).toString(FormatStyle.ISO_8601, false),  is("PT0.123456789S"));
        assertThat(Duration.of(1000000000, TimeUnit.NANOSECONDS).toString(FormatStyle.ISO_8601, false),  is("PT1S"));
        assertThat(Duration.of(1001,       TimeUnit.MILLISECONDS).toString(FormatStyle.ISO_8601, false), is("PT1.001S"));
        assertThat(Duration.of(1601,       TimeUnit.MILLISECONDS).toString(FormatStyle.ISO_8601, false), is("PT1.601S"));
        assertThat(Duration.of(3601,       TimeUnit.MILLISECONDS).toString(FormatStyle.ISO_8601, false), is("PT3.601S"));
        assertThat(Duration.of(70,         TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT1M10S"));
        assertThat(Duration.of(601,        TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT10M1S"));
        assertThat(Duration.of(959,        TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT15M59S"));
        assertThat(Duration.of(960,        TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT16M"));
        assertThat(Duration.of(970,        TimeUnit.SECONDS).toString(FormatStyle.ISO_8601, false),      is("PT16M10S"));
        assertThat(Duration.of(3601,       TimeUnit.MINUTES).toString(FormatStyle.ISO_8601, false),      is("PT60H1M"));
        assertThat(Duration.of(2,          TimeUnit.HOURS).toString(FormatStyle.ISO_8601, false),        is("PT2H"));
        assertThat(Duration.of(100,        TimeUnit.HOURS).toString(FormatStyle.ISO_8601, false),        is("PT100H"));
    }

    @Test
    public void toString_noArguments_appliesShorterStyleWithLegend()
    {
        assertThat(Duration.of(3601, TimeUnit.MILLISECONDS).toString(), is("3.601 second(s)"));
    }

    @Test
    public void toString_full_appliesFullStyleWithLegend()
    {
        assertThat(Duration.of(3601, TimeUnit.MILLISECONDS).toString(FormatStyle.FULL),
                is("0:00:03.601000000 hour(s)"));
    }

    @Test
    public void equals_similarButNotSameObjects_true()
    {
        Duration td60seconds = Duration.of(60, TimeUnit.SECONDS);
        Duration td1Minute = Duration.of(1, TimeUnit.MINUTES);
        assertThat(td60seconds.equals(td1Minute), is(true));
    }

    @Test
    public void equals_similarZeroObjects_true()
    {
        assertThat(Duration.of(0, TimeUnit.NANOSECONDS).equals(Duration.ZERO), is(true));
    }

    @Test
    public void equals_differentTimes_false()
    {
        Duration td1001millis = Duration.of(1001, TimeUnit.MILLISECONDS);
        Duration td1second = Duration.of(1, TimeUnit.SECONDS);
        assertThat(td1001millis.equals(td1second), is(false));
    }

    @Test
    public void hashCode_twoSimilarObjectsInAHashSet_setMaintainsOnlyOneObject()
    {
        Set<Duration> set = new HashSet<>();
        set.add(Duration.of(60, TimeUnit.SECONDS));
        set.add(Duration.of(1, TimeUnit.MINUTES));
        assertThat(set.size(), is(1));
    }

    @Test
    public void removeTrailingZeros_validStrings_success()
    {
        assertThat(FormatStyle.removeTrailingZeros("9.009000000"), is("9.009"));
        assertThat(FormatStyle.removeTrailingZeros("9.000000009"), is("9.000000009"));
        assertThat(FormatStyle.removeTrailingZeros("9.000000000"), is("9"));
    }

}
