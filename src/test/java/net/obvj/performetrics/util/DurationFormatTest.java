package net.obvj.performetrics.util;

import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Unit tests for the {@link DurationFormat}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public class DurationFormatTest
{

    @Test
    public void toString_full_displaysAllUnits()
    {
        assertThat(DurationFormat.FULL.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo(  "0:00:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo(  "0:00:00.000000001")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(  "0:00:00.001000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          SECONDS     ), false), is(equalTo(  "0:00:01.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          MINUTES     ), false), is(equalTo(  "0:01:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          HOURS       ), false), is(equalTo(  "1:00:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1,          DAYS        ), false), is(equalTo( "24:00:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo(  "0:00:00.000000789")));
        assertThat(DurationFormat.FULL.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo(  "0:00:00.123456789")));
        assertThat(DurationFormat.FULL.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(  "0:00:01.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(  "0:00:01.001000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(  "0:00:01.601000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(  "0:00:03.601000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(70,         SECONDS     ), false), is(equalTo(  "0:01:10.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(601,        SECONDS     ), false), is(equalTo(  "0:10:01.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(959,        SECONDS     ), false), is(equalTo(  "0:15:59.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(960,        SECONDS     ), false), is(equalTo(  "0:16:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(970,        SECONDS     ), false), is(equalTo(  "0:16:10.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(3601,       MINUTES     ), false), is(equalTo( "60:01:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(2,          HOURS       ), false), is(equalTo(  "2:00:00.000000000")));
        assertThat(DurationFormat.FULL.format(Duration.of(100,        HOURS       ), false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    public void toString_short_abbreviatesIfPossible()
    {
        assertThat(DurationFormat.SHORT.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo(        "0.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo(        "0.000000001")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(        "0.001000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          SECONDS     ), false), is(equalTo(        "1.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          MINUTES     ), false), is(equalTo(     "1:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          HOURS       ), false), is(equalTo(  "1:00:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1,          DAYS        ), false), is(equalTo( "24:00:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo(        "0.000000789")));
        assertThat(DurationFormat.SHORT.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo(        "0.123456789")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(        "1.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(        "1.001000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(        "1.601000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(        "3.601000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(70,         SECONDS     ), false), is(equalTo(     "1:10.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(601,        SECONDS     ), false), is(equalTo(    "10:01.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(959,        SECONDS     ), false), is(equalTo(    "15:59.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(960,        SECONDS     ), false), is(equalTo(    "16:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(970,        SECONDS     ), false), is(equalTo(    "16:10.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(3601,       MINUTES     ), false), is(equalTo( "60:01:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(2,          HOURS       ), false), is(equalTo(  "2:00:00.000000000")));
        assertThat(DurationFormat.SHORT.format(Duration.of(100,        HOURS       ), false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    public void toString_shorterWithLegend_supressesTrailingZeros()
    {
        assertThat(DurationFormat.SHORTER.format(Duration.of(0,          NANOSECONDS ), true), is(equalTo(          "0 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          NANOSECONDS ), true), is(equalTo("0.000000001 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          MILLISECONDS), true), is(equalTo(      "0.001 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          SECONDS     ), true), is(equalTo(          "1 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          MINUTES     ), true), is(equalTo(       "1:00 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          HOURS       ), true), is(equalTo(      "1:00:00 hour(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          DAYS        ), true), is(equalTo(     "24:00:00 hour(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(789,        NANOSECONDS ), true), is(equalTo("0.000000789 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(123456789,  NANOSECONDS ), true), is(equalTo("0.123456789 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1000000000, NANOSECONDS ), true), is(equalTo(          "1 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1001,       MILLISECONDS), true), is(equalTo(      "1.001 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1601,       MILLISECONDS), true), is(equalTo(      "1.601 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(3601,       MILLISECONDS), true), is(equalTo(      "3.601 second(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(70,         SECONDS     ), true), is(equalTo(       "1:10 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(601,        SECONDS     ), true), is(equalTo(      "10:01 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(959,        SECONDS     ), true), is(equalTo(      "15:59 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(960,        SECONDS     ), true), is(equalTo(      "16:00 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(970,        SECONDS     ), true), is(equalTo(      "16:10 minute(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(3601,       MINUTES     ), true), is(equalTo(     "60:01:00 hour(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(2,          HOURS       ), true), is(equalTo(      "2:00:00 hour(s)")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(100,        HOURS       ), true), is(equalTo(    "100:00:00 hour(s)")));
    }

    @Test
    public void toString_shorterWithoutLegend_supressesTrailingZeros()
    {
        assertThat(DurationFormat.SHORTER.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo("0")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo("0.000000001")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(      "0.001")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          SECONDS     ), false), is(equalTo(          "1")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          MINUTES     ), false), is(equalTo(       "1:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          HOURS       ), false), is(equalTo(    "1:00:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1,          DAYS        ), false), is(equalTo(   "24:00:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo("0.000000789")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo("0.123456789")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(          "1")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(      "1.001")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(      "1.601")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(      "3.601")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(70,         SECONDS     ), false), is(equalTo(       "1:10")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(601,        SECONDS     ), false), is(equalTo(      "10:01")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(959,        SECONDS     ), false), is(equalTo(      "15:59")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(960,        SECONDS     ), false), is(equalTo(      "16:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(970,        SECONDS     ), false), is(equalTo(      "16:10")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(3601,       MINUTES     ), false), is(equalTo(   "60:01:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(2,          HOURS       ), false), is(equalTo(    "2:00:00")));
        assertThat(DurationFormat.SHORTER.format(Duration.of(100,        HOURS       ), false), is(equalTo(  "100:00:00")));
    }

    @Test
    public void toString_iso8601()
    {
        assertThat(DurationFormat.ISO_8601.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo("PT0S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo("PT0.000000001S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          MILLISECONDS), false), is(equalTo("PT0.001S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          SECONDS     ), false), is(equalTo("PT1S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          MINUTES     ), false), is(equalTo("PT1M")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          HOURS       ), false), is(equalTo("PT1H")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1,          DAYS        ), false), is(equalTo("PT24H")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo("PT0.000000789S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo("PT0.123456789S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo("PT1S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo("PT1.001S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo("PT1.601S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo("PT3.601S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(70,         SECONDS     ), false), is(equalTo("PT1M10S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(601,        SECONDS     ), false), is(equalTo("PT10M1S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(959,        SECONDS     ), false), is(equalTo("PT15M59S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(960,        SECONDS     ), false), is(equalTo("PT16M")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(970,        SECONDS     ), false), is(equalTo("PT16M10S")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(3601,       MINUTES     ), false), is(equalTo("PT60H1M")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(2,          HOURS       ), false), is(equalTo("PT2H")));
        assertThat(DurationFormat.ISO_8601.format(Duration.of(100,        HOURS       ), false), is(equalTo("PT100H")));
    }

    @Test
    public void toString_noArguments_appliesShorterStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(), is(equalTo("3.601 second(s)")));
    }

    @Test
    public void toString_full_appliesFullStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(DurationFormat.FULL),
                is(equalTo("0:00:03.601000000 hour(s)")));
    }

    @Test
    public void removeTrailingZeros_validStrings_success()
    {
        assertThat(DurationFormat.removeTrailingZeros("9.009000000"), is(equalTo("9.009")));
        assertThat(DurationFormat.removeTrailingZeros("9.000000009"), is(equalTo("9.000000009")));
        assertThat(DurationFormat.removeTrailingZeros("9.000000000"), is(equalTo("9")));
    }

}
