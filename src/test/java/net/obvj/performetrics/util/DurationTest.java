package net.obvj.performetrics.util;

import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.HashSet;
import java.util.Set;

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
        Duration td1 = Duration.of(1999888777, NANOSECONDS);
        assertThat(td1.getHours(),       is(equalTo(0L)));
        assertThat(td1.getMinutes(),     is(equalTo(0)));
        assertThat(td1.getSeconds(),     is(equalTo(1)));
        assertThat(td1.getNanoseconds(), is(equalTo(999888777)));
    }

    @Test
    public void of_validAmountAndTimeUnitAndSecondsPrecision_populatesAccordingly()
    {
        Duration td1 = Duration.of(5410, SECONDS);
        assertThat(td1.getHours(),       is(equalTo(1L)));
        assertThat(td1.getMinutes(),     is(equalTo(30)));
        assertThat(td1.getSeconds(),     is(equalTo(10)));
        assertThat(td1.getNanoseconds(), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_negativeAmount_illegalArgumentException()
    {
        Duration.of(-1999888777, NANOSECONDS);
    }

    @Test
    public void toString_full_displaysAllUnits()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:00.000000000")));

        assertThat(Duration.of(1,          NANOSECONDS ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:00.000000001")));
        assertThat(Duration.of(1,          MILLISECONDS).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:00.001000000")));
        assertThat(Duration.of(1,          SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:01.000000000")));
        assertThat(Duration.of(1,          MINUTES     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:01:00.000000000")));
        assertThat(Duration.of(1,          HOURS       ).toString(FormatStyle.FULL, false), is(equalTo(  "1:00:00.000000000")));
        assertThat(Duration.of(1,          DAYS        ).toString(FormatStyle.FULL, false), is(equalTo( "24:00:00.000000000")));

        assertThat(Duration.of(789,        NANOSECONDS ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:00.000000789")));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:00.123456789")));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:01.000000000")));
        assertThat(Duration.of(1001,       MILLISECONDS).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:01.001000000")));
        assertThat(Duration.of(1601,       MILLISECONDS).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:01.601000000")));
        assertThat(Duration.of(3601,       MILLISECONDS).toString(FormatStyle.FULL, false), is(equalTo(  "0:00:03.601000000")));
        assertThat(Duration.of(70,         SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:01:10.000000000")));
        assertThat(Duration.of(601,        SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:10:01.000000000")));
        assertThat(Duration.of(959,        SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:15:59.000000000")));
        assertThat(Duration.of(960,        SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:16:00.000000000")));
        assertThat(Duration.of(970,        SECONDS     ).toString(FormatStyle.FULL, false), is(equalTo(  "0:16:10.000000000")));
        assertThat(Duration.of(3601,       MINUTES     ).toString(FormatStyle.FULL, false), is(equalTo( "60:01:00.000000000")));
        assertThat(Duration.of(2,          HOURS       ).toString(FormatStyle.FULL, false), is(equalTo(  "2:00:00.000000000")));
        assertThat(Duration.of(100,        HOURS       ).toString(FormatStyle.FULL, false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    public void toString_short_abbreviatesIfPossible()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toString(FormatStyle.SHORT, false), is(equalTo(        "0.000000000")));

        assertThat(Duration.of(1,          NANOSECONDS ).toString(FormatStyle.SHORT, false), is(equalTo(        "0.000000001")));
        assertThat(Duration.of(1,          MILLISECONDS).toString(FormatStyle.SHORT, false), is(equalTo(        "0.001000000")));
        assertThat(Duration.of(1,          SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(        "1.000000000")));
        assertThat(Duration.of(1,          MINUTES     ).toString(FormatStyle.SHORT, false), is(equalTo(     "1:00.000000000")));
        assertThat(Duration.of(1,          HOURS       ).toString(FormatStyle.SHORT, false), is(equalTo(  "1:00:00.000000000")));
        assertThat(Duration.of(1,          DAYS        ).toString(FormatStyle.SHORT, false), is(equalTo( "24:00:00.000000000")));

        assertThat(Duration.of(789,        NANOSECONDS ).toString(FormatStyle.SHORT, false), is(equalTo(        "0.000000789")));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toString(FormatStyle.SHORT, false), is(equalTo(        "0.123456789")));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toString(FormatStyle.SHORT, false), is(equalTo(        "1.000000000")));
        assertThat(Duration.of(1001,       MILLISECONDS).toString(FormatStyle.SHORT, false), is(equalTo(        "1.001000000")));
        assertThat(Duration.of(1601,       MILLISECONDS).toString(FormatStyle.SHORT, false), is(equalTo(        "1.601000000")));
        assertThat(Duration.of(3601,       MILLISECONDS).toString(FormatStyle.SHORT, false), is(equalTo(        "3.601000000")));
        assertThat(Duration.of(70,         SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(     "1:10.000000000")));
        assertThat(Duration.of(601,        SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(    "10:01.000000000")));
        assertThat(Duration.of(959,        SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(    "15:59.000000000")));
        assertThat(Duration.of(960,        SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(    "16:00.000000000")));
        assertThat(Duration.of(970,        SECONDS     ).toString(FormatStyle.SHORT, false), is(equalTo(    "16:10.000000000")));
        assertThat(Duration.of(3601,       MINUTES     ).toString(FormatStyle.SHORT, false), is(equalTo( "60:01:00.000000000")));
        assertThat(Duration.of(2,          HOURS       ).toString(FormatStyle.SHORT, false), is(equalTo(  "2:00:00.000000000")));
        assertThat(Duration.of(100,        HOURS       ).toString(FormatStyle.SHORT, false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    public void toString_shorterWithLegend_supressesTrailingZeros()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toString(FormatStyle.SHORTER, true), is(equalTo(          "0 second(s)")));

        assertThat(Duration.of(1,          NANOSECONDS ).toString(FormatStyle.SHORTER, true), is(equalTo("0.000000001 second(s)")));
        assertThat(Duration.of(1,          MILLISECONDS).toString(FormatStyle.SHORTER, true), is(equalTo(      "0.001 second(s)")));
        assertThat(Duration.of(1,          SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(          "1 second(s)")));
        assertThat(Duration.of(1,          MINUTES     ).toString(FormatStyle.SHORTER, true), is(equalTo(       "1:00 minute(s)")));
        assertThat(Duration.of(1,          HOURS       ).toString(FormatStyle.SHORTER, true), is(equalTo(      "1:00:00 hour(s)")));
        assertThat(Duration.of(1,          DAYS        ).toString(FormatStyle.SHORTER, true), is(equalTo(     "24:00:00 hour(s)")));

        assertThat(Duration.of(789,        NANOSECONDS ).toString(FormatStyle.SHORTER, true), is(equalTo("0.000000789 second(s)")));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toString(FormatStyle.SHORTER, true), is(equalTo("0.123456789 second(s)")));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toString(FormatStyle.SHORTER, true), is(equalTo(          "1 second(s)")));
        assertThat(Duration.of(1001,       MILLISECONDS).toString(FormatStyle.SHORTER, true), is(equalTo(      "1.001 second(s)")));
        assertThat(Duration.of(1601,       MILLISECONDS).toString(FormatStyle.SHORTER, true), is(equalTo(      "1.601 second(s)")));
        assertThat(Duration.of(3601,       MILLISECONDS).toString(FormatStyle.SHORTER, true), is(equalTo(      "3.601 second(s)")));
        assertThat(Duration.of(70,         SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(       "1:10 minute(s)")));
        assertThat(Duration.of(601,        SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(      "10:01 minute(s)")));
        assertThat(Duration.of(959,        SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(      "15:59 minute(s)")));
        assertThat(Duration.of(960,        SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(      "16:00 minute(s)")));
        assertThat(Duration.of(970,        SECONDS     ).toString(FormatStyle.SHORTER, true), is(equalTo(      "16:10 minute(s)")));
        assertThat(Duration.of(3601,       MINUTES     ).toString(FormatStyle.SHORTER, true), is(equalTo(     "60:01:00 hour(s)")));
        assertThat(Duration.of(2,          HOURS       ).toString(FormatStyle.SHORTER, true), is(equalTo(      "2:00:00 hour(s)")));
        assertThat(Duration.of(100,        HOURS       ).toString(FormatStyle.SHORTER, true), is(equalTo(    "100:00:00 hour(s)")));
    }

    @Test
    public void toString_shorterWithoutLegend_supressesTrailingZeros()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toString(FormatStyle.SHORTER, false), is(equalTo("0")));

        assertThat(Duration.of(1,          NANOSECONDS ).toString(FormatStyle.SHORTER, false), is(equalTo("0.000000001")));
        assertThat(Duration.of(1,          MILLISECONDS).toString(FormatStyle.SHORTER, false), is(equalTo(      "0.001")));
        assertThat(Duration.of(1,          SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(          "1")));
        assertThat(Duration.of(1,          MINUTES     ).toString(FormatStyle.SHORTER, false), is(equalTo(       "1:00")));
        assertThat(Duration.of(1,          HOURS       ).toString(FormatStyle.SHORTER, false), is(equalTo(    "1:00:00")));
        assertThat(Duration.of(1,          DAYS        ).toString(FormatStyle.SHORTER, false), is(equalTo(   "24:00:00")));

        assertThat(Duration.of(789,        NANOSECONDS ).toString(FormatStyle.SHORTER, false), is(equalTo("0.000000789")));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toString(FormatStyle.SHORTER, false), is(equalTo("0.123456789")));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toString(FormatStyle.SHORTER, false), is(equalTo(          "1")));
        assertThat(Duration.of(1001,       MILLISECONDS).toString(FormatStyle.SHORTER, false), is(equalTo(      "1.001")));
        assertThat(Duration.of(1601,       MILLISECONDS).toString(FormatStyle.SHORTER, false), is(equalTo(      "1.601")));
        assertThat(Duration.of(3601,       MILLISECONDS).toString(FormatStyle.SHORTER, false), is(equalTo(      "3.601")));
        assertThat(Duration.of(70,         SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(       "1:10")));
        assertThat(Duration.of(601,        SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(      "10:01")));
        assertThat(Duration.of(959,        SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(      "15:59")));
        assertThat(Duration.of(960,        SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(      "16:00")));
        assertThat(Duration.of(970,        SECONDS     ).toString(FormatStyle.SHORTER, false), is(equalTo(      "16:10")));
        assertThat(Duration.of(3601,       MINUTES     ).toString(FormatStyle.SHORTER, false), is(equalTo(   "60:01:00")));
        assertThat(Duration.of(2,          HOURS       ).toString(FormatStyle.SHORTER, false), is(equalTo(    "2:00:00")));
        assertThat(Duration.of(100,        HOURS       ).toString(FormatStyle.SHORTER, false), is(equalTo(  "100:00:00")));
    }

    @Test
    public void toString_iso8601()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT0S")));

        assertThat(Duration.of(1,          NANOSECONDS ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT0.000000001S")));
        assertThat(Duration.of(1,          MILLISECONDS).toString(FormatStyle.ISO_8601, false), is(equalTo("PT0.001S")));
        assertThat(Duration.of(1,          SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1S")));
        assertThat(Duration.of(1,          MINUTES     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1M")));
        assertThat(Duration.of(1,          HOURS       ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1H")));
        assertThat(Duration.of(1,          DAYS        ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT24H")));

        assertThat(Duration.of(789,        NANOSECONDS ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT0.000000789S")));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT0.123456789S")));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1S")));
        assertThat(Duration.of(1001,       MILLISECONDS).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1.001S")));
        assertThat(Duration.of(1601,       MILLISECONDS).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1.601S")));
        assertThat(Duration.of(3601,       MILLISECONDS).toString(FormatStyle.ISO_8601, false), is(equalTo("PT3.601S")));
        assertThat(Duration.of(70,         SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT1M10S")));
        assertThat(Duration.of(601,        SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT10M1S")));
        assertThat(Duration.of(959,        SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT15M59S")));
        assertThat(Duration.of(960,        SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT16M")));
        assertThat(Duration.of(970,        SECONDS     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT16M10S")));
        assertThat(Duration.of(3601,       MINUTES     ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT60H1M")));
        assertThat(Duration.of(2,          HOURS       ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT2H")));
        assertThat(Duration.of(100,        HOURS       ).toString(FormatStyle.ISO_8601, false), is(equalTo("PT100H")));
    }

    @Test
    public void toString_noArguments_appliesShorterStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(), is(equalTo("3.601 second(s)")));
    }

    @Test
    public void toString_full_appliesFullStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(FormatStyle.FULL),
                is(equalTo("0:00:03.601000000 hour(s)")));
    }

    @Test
    public void equals_similarButNotSameObjects_true()
    {
        Duration td60seconds = Duration.of(60, SECONDS);
        Duration td1Minute = Duration.of(1, MINUTES);
        assertThat(td60seconds.equals(td1Minute), is(true));
    }

    @Test
    public void equals_similarZeroObjects_true()
    {
        assertThat(Duration.of(0, NANOSECONDS).equals(Duration.ZERO), is(true));
    }

    @Test
    public void equals_differentTimes_false()
    {
        Duration td1001millis = Duration.of(1001, MILLISECONDS);
        Duration td1second = Duration.of(1, SECONDS);
        assertThat(td1001millis.equals(td1second), is(false));
    }

    @Test
    public void hashCode_twoSimilarObjectsInAHashSet_setMaintainsOnlyOneObject()
    {
        Set<Duration> set = new HashSet<>();
        set.add(Duration.of(60, SECONDS));
        set.add(Duration.of(1, MINUTES));
        assertThat(set.size(), is(equalTo(1)));
    }

    @Test
    public void removeTrailingZeros_validStrings_success()
    {
        assertThat(FormatStyle.removeTrailingZeros("9.009000000"), is(equalTo("9.009")));
        assertThat(FormatStyle.removeTrailingZeros("9.000000009"), is(equalTo("9.000000009")));
        assertThat(FormatStyle.removeTrailingZeros("9.000000000"), is(equalTo("9")));
    }

}
