/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.performetrics.util;

import static java.util.concurrent.TimeUnit.*;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static net.obvj.performetrics.util.DurationFormat.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link DurationFormat}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
class DurationFormatTest
{

    @Test
    void format_full_displaysAllUnits()
    {
        assertThat(FULL.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo(  "0:00:00.000000000")));
        assertThat(FULL.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo(  "0:00:00.000000001")));
        assertThat(FULL.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(  "0:00:00.001000000")));
        assertThat(FULL.format(Duration.of(1,          SECONDS     ), false), is(equalTo(  "0:00:01.000000000")));
        assertThat(FULL.format(Duration.of(1,          MINUTES     ), false), is(equalTo(  "0:01:00.000000000")));
        assertThat(FULL.format(Duration.of(1,          HOURS       ), false), is(equalTo(  "1:00:00.000000000")));
        assertThat(FULL.format(Duration.of(1,          DAYS        ), false), is(equalTo( "24:00:00.000000000")));
        assertThat(FULL.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo(  "0:00:00.000000789")));
        assertThat(FULL.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo(  "0:00:00.123456789")));
        assertThat(FULL.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(  "0:00:01.000000000")));
        assertThat(FULL.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(  "0:00:01.001000000")));
        assertThat(FULL.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(  "0:00:01.601000000")));
        assertThat(FULL.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(  "0:00:03.601000000")));
        assertThat(FULL.format(Duration.of(70,         SECONDS     ), false), is(equalTo(  "0:01:10.000000000")));
        assertThat(FULL.format(Duration.of(601,        SECONDS     ), false), is(equalTo(  "0:10:01.000000000")));
        assertThat(FULL.format(Duration.of(959,        SECONDS     ), false), is(equalTo(  "0:15:59.000000000")));
        assertThat(FULL.format(Duration.of(960,        SECONDS     ), false), is(equalTo(  "0:16:00.000000000")));
        assertThat(FULL.format(Duration.of(970,        SECONDS     ), false), is(equalTo(  "0:16:10.000000000")));
        assertThat(FULL.format(Duration.of(3601,       MINUTES     ), false), is(equalTo( "60:01:00.000000000")));
        assertThat(FULL.format(Duration.of(2,          HOURS       ), false), is(equalTo(  "2:00:00.000000000")));
        assertThat(FULL.format(Duration.of(100,        HOURS       ), false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    void format_short_abbreviatesIfPossible()
    {
        assertThat(SHORT.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo(        "0.000000000")));
        assertThat(SHORT.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo(        "0.000000001")));
        assertThat(SHORT.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(        "0.001000000")));
        assertThat(SHORT.format(Duration.of(1,          SECONDS     ), false), is(equalTo(        "1.000000000")));
        assertThat(SHORT.format(Duration.of(1,          MINUTES     ), false), is(equalTo(     "1:00.000000000")));
        assertThat(SHORT.format(Duration.of(1,          HOURS       ), false), is(equalTo(  "1:00:00.000000000")));
        assertThat(SHORT.format(Duration.of(1,          DAYS        ), false), is(equalTo( "24:00:00.000000000")));
        assertThat(SHORT.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo(        "0.000000789")));
        assertThat(SHORT.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo(        "0.123456789")));
        assertThat(SHORT.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(        "1.000000000")));
        assertThat(SHORT.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(        "1.001000000")));
        assertThat(SHORT.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(        "1.601000000")));
        assertThat(SHORT.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(        "3.601000000")));
        assertThat(SHORT.format(Duration.of(70,         SECONDS     ), false), is(equalTo(     "1:10.000000000")));
        assertThat(SHORT.format(Duration.of(601,        SECONDS     ), false), is(equalTo(    "10:01.000000000")));
        assertThat(SHORT.format(Duration.of(959,        SECONDS     ), false), is(equalTo(    "15:59.000000000")));
        assertThat(SHORT.format(Duration.of(960,        SECONDS     ), false), is(equalTo(    "16:00.000000000")));
        assertThat(SHORT.format(Duration.of(970,        SECONDS     ), false), is(equalTo(    "16:10.000000000")));
        assertThat(SHORT.format(Duration.of(3601,       MINUTES     ), false), is(equalTo( "60:01:00.000000000")));
        assertThat(SHORT.format(Duration.of(2,          HOURS       ), false), is(equalTo(  "2:00:00.000000000")));
        assertThat(SHORT.format(Duration.of(100,        HOURS       ), false), is(equalTo("100:00:00.000000000")));
    }

    @Test
    void format_shorterWithLegend_supressesTrailingZeros()
    {
        assertThat(SHORTER.format(Duration.of(0,          NANOSECONDS ), true), is(equalTo(          "0 second(s)")));
        assertThat(SHORTER.format(Duration.of(1,          NANOSECONDS ), true), is(equalTo("0.000000001 second(s)")));
        assertThat(SHORTER.format(Duration.of(1,          MILLISECONDS), true), is(equalTo(      "0.001 second(s)")));
        assertThat(SHORTER.format(Duration.of(1,          SECONDS     ), true), is(equalTo(          "1 second(s)")));
        assertThat(SHORTER.format(Duration.of(1,          MINUTES     ), true), is(equalTo(       "1:00 minute(s)")));
        assertThat(SHORTER.format(Duration.of(1,          HOURS       ), true), is(equalTo(      "1:00:00 hour(s)")));
        assertThat(SHORTER.format(Duration.of(1,          DAYS        ), true), is(equalTo(     "24:00:00 hour(s)")));
        assertThat(SHORTER.format(Duration.of(789,        NANOSECONDS ), true), is(equalTo("0.000000789 second(s)")));
        assertThat(SHORTER.format(Duration.of(123456789,  NANOSECONDS ), true), is(equalTo("0.123456789 second(s)")));
        assertThat(SHORTER.format(Duration.of(1000000000, NANOSECONDS ), true), is(equalTo(          "1 second(s)")));
        assertThat(SHORTER.format(Duration.of(1001,       MILLISECONDS), true), is(equalTo(      "1.001 second(s)")));
        assertThat(SHORTER.format(Duration.of(1601,       MILLISECONDS), true), is(equalTo(      "1.601 second(s)")));
        assertThat(SHORTER.format(Duration.of(3601,       MILLISECONDS), true), is(equalTo(      "3.601 second(s)")));
        assertThat(SHORTER.format(Duration.of(70,         SECONDS     ), true), is(equalTo(       "1:10 minute(s)")));
        assertThat(SHORTER.format(Duration.of(601,        SECONDS     ), true), is(equalTo(      "10:01 minute(s)")));
        assertThat(SHORTER.format(Duration.of(959,        SECONDS     ), true), is(equalTo(      "15:59 minute(s)")));
        assertThat(SHORTER.format(Duration.of(960,        SECONDS     ), true), is(equalTo(      "16:00 minute(s)")));
        assertThat(SHORTER.format(Duration.of(970,        SECONDS     ), true), is(equalTo(      "16:10 minute(s)")));
        assertThat(SHORTER.format(Duration.of(3601,       MINUTES     ), true), is(equalTo(     "60:01:00 hour(s)")));
        assertThat(SHORTER.format(Duration.of(2,          HOURS       ), true), is(equalTo(      "2:00:00 hour(s)")));
        assertThat(SHORTER.format(Duration.of(100,        HOURS       ), true), is(equalTo(    "100:00:00 hour(s)")));
    }

    @Test
    void format_shorterWithoutLegend_supressesTrailingZeros()
    {
        assertThat(SHORTER.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo("0")));
        assertThat(SHORTER.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo("0.000000001")));
        assertThat(SHORTER.format(Duration.of(1,          MILLISECONDS), false), is(equalTo(      "0.001")));
        assertThat(SHORTER.format(Duration.of(1,          SECONDS     ), false), is(equalTo(          "1")));
        assertThat(SHORTER.format(Duration.of(1,          MINUTES     ), false), is(equalTo(       "1:00")));
        assertThat(SHORTER.format(Duration.of(1,          HOURS       ), false), is(equalTo(    "1:00:00")));
        assertThat(SHORTER.format(Duration.of(1,          DAYS        ), false), is(equalTo(   "24:00:00")));
        assertThat(SHORTER.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo("0.000000789")));
        assertThat(SHORTER.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo("0.123456789")));
        assertThat(SHORTER.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo(          "1")));
        assertThat(SHORTER.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo(      "1.001")));
        assertThat(SHORTER.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo(      "1.601")));
        assertThat(SHORTER.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo(      "3.601")));
        assertThat(SHORTER.format(Duration.of(70,         SECONDS     ), false), is(equalTo(       "1:10")));
        assertThat(SHORTER.format(Duration.of(601,        SECONDS     ), false), is(equalTo(      "10:01")));
        assertThat(SHORTER.format(Duration.of(959,        SECONDS     ), false), is(equalTo(      "15:59")));
        assertThat(SHORTER.format(Duration.of(960,        SECONDS     ), false), is(equalTo(      "16:00")));
        assertThat(SHORTER.format(Duration.of(970,        SECONDS     ), false), is(equalTo(      "16:10")));
        assertThat(SHORTER.format(Duration.of(3601,       MINUTES     ), false), is(equalTo(   "60:01:00")));
        assertThat(SHORTER.format(Duration.of(2,          HOURS       ), false), is(equalTo(    "2:00:00")));
        assertThat(SHORTER.format(Duration.of(100,        HOURS       ), false), is(equalTo(  "100:00:00")));
    }

    @Test
    void format_iso8601()
    {
        assertThat(ISO_8601.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo("PT0S")));
        assertThat(ISO_8601.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo("PT0.000000001S")));
        assertThat(ISO_8601.format(Duration.of(1,          MILLISECONDS), false), is(equalTo("PT0.001S")));
        assertThat(ISO_8601.format(Duration.of(1,          SECONDS     ), false), is(equalTo("PT1S")));
        assertThat(ISO_8601.format(Duration.of(1,          MINUTES     ), false), is(equalTo("PT1M")));
        assertThat(ISO_8601.format(Duration.of(1,          HOURS       ), false), is(equalTo("PT1H")));
        assertThat(ISO_8601.format(Duration.of(1,          DAYS        ), false), is(equalTo("PT24H")));
        assertThat(ISO_8601.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo("PT0.000000789S")));
        assertThat(ISO_8601.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo("PT0.123456789S")));
        assertThat(ISO_8601.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo("PT1S")));
        assertThat(ISO_8601.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo("PT1.001S")));
        assertThat(ISO_8601.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo("PT1.601S")));
        assertThat(ISO_8601.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo("PT3.601S")));
        assertThat(ISO_8601.format(Duration.of(70,         SECONDS     ), false), is(equalTo("PT1M10S")));
        assertThat(ISO_8601.format(Duration.of(601,        SECONDS     ), false), is(equalTo("PT10M1S")));
        assertThat(ISO_8601.format(Duration.of(959,        SECONDS     ), false), is(equalTo("PT15M59S")));
        assertThat(ISO_8601.format(Duration.of(960,        SECONDS     ), false), is(equalTo("PT16M")));
        assertThat(ISO_8601.format(Duration.of(970,        SECONDS     ), false), is(equalTo("PT16M10S")));
        assertThat(ISO_8601.format(Duration.of(3601,       MINUTES     ), false), is(equalTo("PT60H1M")));
        assertThat(ISO_8601.format(Duration.of(2,          HOURS       ), false), is(equalTo("PT2H")));
        assertThat(ISO_8601.format(Duration.of(100,        HOURS       ), false), is(equalTo("PT100H")));
    }

    @Test
    void format_linux()
    {
        assertThat(LINUX.format(Duration.of(0,          NANOSECONDS ), false), is(equalTo("0m0.000s")));
        assertThat(LINUX.format(Duration.of(1,          NANOSECONDS ), false), is(equalTo("0m0.000s")));
        assertThat(LINUX.format(Duration.of(1,          MILLISECONDS), false), is(equalTo("0m0.001s")));
        assertThat(LINUX.format(Duration.of(1,          SECONDS     ), false), is(equalTo("0m1.000s")));
        assertThat(LINUX.format(Duration.of(1,          MINUTES     ), false), is(equalTo("1m0.000s")));
        assertThat(LINUX.format(Duration.of(1,          HOURS       ), false), is(equalTo("60m0.000s")));
        assertThat(LINUX.format(Duration.of(1,          DAYS        ), false), is(equalTo("1440m0.000s")));
        assertThat(LINUX.format(Duration.of(789,        NANOSECONDS ), false), is(equalTo("0m0.000s")));
        assertThat(LINUX.format(Duration.of(123456789,  NANOSECONDS ), false), is(equalTo("0m0.123s")));
        assertThat(LINUX.format(Duration.of(1000000000, NANOSECONDS ), false), is(equalTo("0m1.000s")));
        assertThat(LINUX.format(Duration.of(1001,       MILLISECONDS), false), is(equalTo("0m1.001s")));
        assertThat(LINUX.format(Duration.of(1601,       MILLISECONDS), false), is(equalTo("0m1.601s")));
        assertThat(LINUX.format(Duration.of(3601,       MILLISECONDS), false), is(equalTo("0m3.601s")));
        assertThat(LINUX.format(Duration.of(70,         SECONDS     ), false), is(equalTo("1m10.000s")));
        assertThat(LINUX.format(Duration.of(601,        SECONDS     ), false), is(equalTo("10m1.000s")));
        assertThat(LINUX.format(Duration.of(959,        SECONDS     ), false), is(equalTo("15m59.000s")));
        assertThat(LINUX.format(Duration.of(960,        SECONDS     ), false), is(equalTo("16m0.000s")));
        assertThat(LINUX.format(Duration.of(970,        SECONDS     ), false), is(equalTo("16m10.000s")));
        assertThat(LINUX.format(Duration.of(3601,       MINUTES     ), false), is(equalTo("3601m0.000s")));
        assertThat(LINUX.format(Duration.of(2,          HOURS       ), false), is(equalTo("120m0.000s")));
        assertThat(LINUX.format(Duration.of(100,        HOURS       ), false), is(equalTo("6000m0.000s")));
    }

    @Test
    void toString_noArguments_appliesShorterStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(), is(equalTo("3.601 second(s)")));
    }

    @Test
    void toString_full_appliesFullStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(FULL),
                is(equalTo("0:00:03.601000000 hour(s)")));
    }

    @Test
    void removeTrailingZeros_validStrings_success()
    {
        assertThat(removeTrailingZeros("9.009000000"), is(equalTo("9.009")));
        assertThat(removeTrailingZeros("9.000000009"), is(equalTo("9.000000009")));
        assertThat(removeTrailingZeros("9.000000000"), is(equalTo("9")));
    }

    @Test
    void parse_fullAndValidString_success()
    {
        assertThat(FULL.parse("00:00:00.000000000"), equalTo(Duration.ZERO));
        assertThat(FULL.parse("00:00:00.100000000"), equalTo(Duration.of(    100, MILLISECONDS)));
        assertThat(FULL.parse("00:00:01.000000000"), equalTo(Duration.of(      1, SECONDS)));
        assertThat(FULL.parse("00:01:01.000000000"), equalTo(Duration.of(     61, SECONDS)));
        assertThat(FULL.parse("01:01:00.000000000"), equalTo(Duration.of(     61, MINUTES)));
        assertThat(FULL.parse("01:01:01.000000000"), equalTo(Duration.of(   3661, SECONDS)));
        assertThat(FULL.parse("01:01:01.100000000"), equalTo(Duration.of(3661100, MILLISECONDS)));

        // Field overflow is OK
        assertThat(FULL.parse("00:01:70.000000000"), equalTo(Duration.of( 130, SECONDS)));
        assertThat(FULL.parse("01:60:00.000000000"), equalTo(Duration.of(   2, HOURS)));
    }

    @Test
    void parse_shortAndValidString_success()
    {
        assertThat(SHORT.parse(       "0.000000000 second(s)"), equalTo(Duration.ZERO));
        assertThat(SHORT.parse(       "0.100000000 second(s)"), equalTo(Duration.of( 100, MILLISECONDS)));
        assertThat(SHORT.parse(       "1.000000000 second(s)"), equalTo(Duration.of(   1, SECONDS)));
        assertThat(SHORT.parse(   "01:01.000000000 minute(s)"), equalTo(Duration.of(  61, SECONDS)));
        assertThat(SHORT.parse("01:01:00.000000000 hour(s)"  ), equalTo(Duration.of(  61, MINUTES)));
        assertThat(SHORT.parse("01:01:01.000000000 hour(s)"  ), equalTo(Duration.of(3661, SECONDS)));
        // Without legend
        assertThat(SHORT.parse(      "1.100000000"), equalTo(Duration.of(1100, MILLISECONDS)));
        assertThat(SHORT.parse(   "1:30.000000000"), equalTo(Duration.of(  90, SECONDS)));
        assertThat(SHORT.parse("1:30:00.000000000"), equalTo(Duration.of(  90, MINUTES)));    }

    @Test
    void parse_shorterAndValidString_success()
    {
        assertThat(SHORTER.parse(       "0 second(s)"), equalTo(Duration.ZERO));
        assertThat(SHORTER.parse(     "0.2 second(s)"), equalTo(Duration.of( 200, MILLISECONDS)));
        assertThat(SHORTER.parse(       "2 second(s)"), equalTo(Duration.of(   2, SECONDS)));
        assertThat(SHORTER.parse(   "01:02 minute(s)"), equalTo(Duration.of(  62, SECONDS)));
        assertThat(SHORTER.parse("01:02:00 hour(s)"  ), equalTo(Duration.of(  62, MINUTES)));
        assertThat(SHORTER.parse("01:01:02 hour(s)"  ), equalTo(Duration.of(3662, SECONDS)));

        // Without legend
        assertThat(SHORTER.parse(      "2"), equalTo(Duration.of(   2, SECONDS)));
        assertThat(SHORTER.parse(    "2.2"), equalTo(Duration.of(2200, MILLISECONDS)));
        assertThat(SHORTER.parse(   "1:45"), equalTo(Duration.of( 105, SECONDS)));
        assertThat(SHORTER.parse("1:45:00"), equalTo(Duration.of( 105, MINUTES)));
    }

    @Test
    void parse_linux_success()
    {
        assertThat(LINUX.parse( "0m0.000s"), equalTo(Duration.ZERO));
        assertThat(LINUX.parse( "0m0.100s"), equalTo(Duration.of(100, MILLISECONDS)));
        assertThat(LINUX.parse( "0m1.000s"), equalTo(Duration.of(  1, SECONDS)));
        assertThat(LINUX.parse( "1m1.000s"), equalTo(Duration.of( 61, SECONDS)));

        // Field overflow is OK
        assertThat(LINUX.parse("0m1.1000s"), equalTo(Duration.of(  2, SECONDS)));
        assertThat(LINUX.parse("1m70.000s"), equalTo(Duration.of(130, SECONDS)));
    }

    @Test
    void parse_fullAndNull_nullPointerException()
    {
        assertThat(() -> FULL.parse(null), throwsException(NullPointerException.class));
    }

    @Test
    void parse_fullAndInvalidString_illegalArgumentException()
    {
        assertThat(() -> FULL.parse("invalid"),
                throwsException(IllegalArgumentException.class)
                        .withMessage("Unrecognized duration: invalid"));
    }

    @Test
    void parse_iso8601AndInvalidString_illegalArgumentException()
    {
        assertThat(() -> ISO_8601.parse("invalid2"),
                throwsException(IllegalArgumentException.class)
                        .withMessage("Unrecognized duration: invalid2")
                .withCause(DateTimeParseException.class));
    }

    @Test
    void parse_linuxAndInvalidString_illegalArgumentException()
    {
        assertThat(() -> LINUX.parse("invalid3"),
                throwsException(IllegalArgumentException.class)
                        .withMessage("Unrecognized duration: invalid3"));
    }
}
