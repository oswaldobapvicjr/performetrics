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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.obvj.performetrics.util.DurationFormat.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Duration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class DurationTest
{

    @Test
    void of_validAmountAndTimeUnitAndNanosecondsPrecision_populatesAccordingly()
    {
        Duration td1 = Duration.of(1999888777, NANOSECONDS);
        assertThat(td1.getHours(),       is(equalTo(0L)));
        assertThat(td1.getMinutes(),     is(equalTo(0)));
        assertThat(td1.getSeconds(),     is(equalTo(1)));
        assertThat(td1.getNanoseconds(), is(equalTo(999888777)));
    }

    @Test
    void of_validAmountAndTimeUnitAndSecondsPrecision_populatesAccordingly()
    {
        Duration td1 = Duration.of(5410, SECONDS);
        assertThat(td1.getHours(),       is(equalTo(1L)));
        assertThat(td1.getMinutes(),     is(equalTo(30)));
        assertThat(td1.getSeconds(),     is(equalTo(10)));
        assertThat(td1.getNanoseconds(), is(equalTo(0)));
    }

    @Test
    void of_negativeAmount_illegalArgumentException()
    {
        assertThat(() -> Duration.of(-1, NANOSECONDS),
                throwsException(IllegalArgumentException.class)
                        .withMessage("The duration amount must be a positive value"));
    }

    @Test
    void toSeconds_validValues()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toSeconds(), is(equalTo(          0D)));
        assertThat(Duration.of(1,          NANOSECONDS ).toSeconds(), is(equalTo(0.000000001D)));
        assertThat(Duration.of(1,          MILLISECONDS).toSeconds(), is(equalTo(      0.001D)));
        assertThat(Duration.of(1,          SECONDS     ).toSeconds(), is(equalTo(          1D)));
        assertThat(Duration.of(1,          MINUTES     ).toSeconds(), is(equalTo(         60D)));
        assertThat(Duration.of(1,          HOURS       ).toSeconds(), is(equalTo(       3600D)));
        assertThat(Duration.of(1,          DAYS        ).toSeconds(), is(equalTo(      86400D)));
        assertThat(Duration.of(789,        NANOSECONDS ).toSeconds(), is(equalTo(0.000000789D)));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toSeconds(), is(equalTo(0.123456789D)));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toSeconds(), is(equalTo(          1D)));
        assertThat(Duration.of(1001,       MILLISECONDS).toSeconds(), is(equalTo(      1.001D)));
        assertThat(Duration.of(1601,       MILLISECONDS).toSeconds(), is(equalTo(      1.601D)));
        assertThat(Duration.of(3601,       MILLISECONDS).toSeconds(), is(equalTo(      3.601D)));
        assertThat(Duration.of(70,         SECONDS     ).toSeconds(), is(equalTo(         70D)));
        assertThat(Duration.of(601,        SECONDS     ).toSeconds(), is(equalTo(        601D)));
        assertThat(Duration.of(959,        SECONDS     ).toSeconds(), is(equalTo(        959D)));
        assertThat(Duration.of(960,        SECONDS     ).toSeconds(), is(equalTo(        960D)));
        assertThat(Duration.of(970,        SECONDS     ).toSeconds(), is(equalTo(        970D)));
        assertThat(Duration.of(3601,       MINUTES     ).toSeconds(), is(equalTo(     216060D)));
        assertThat(Duration.of(2,          HOURS       ).toSeconds(), is(equalTo(       7200D)));
        assertThat(Duration.of(100,        HOURS       ).toSeconds(), is(equalTo(     360000D)));
    }

    @Test
    void toTimeUnit_milliseconds_validValuesDefaultScale()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toTimeUnit(MILLISECONDS), is(equalTo(          0D)));
        assertThat(Duration.of(1,          NANOSECONDS ).toTimeUnit(MILLISECONDS), is(equalTo(   0.000001D)));
        assertThat(Duration.of(1,          MILLISECONDS).toTimeUnit(MILLISECONDS), is(equalTo(          1D)));
        assertThat(Duration.of(1,          SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(       1000D)));
        assertThat(Duration.of(1,          MINUTES     ).toTimeUnit(MILLISECONDS), is(equalTo(      60000D)));
        assertThat(Duration.of(1,          HOURS       ).toTimeUnit(MILLISECONDS), is(equalTo(    3600000D)));
        assertThat(Duration.of(1,          DAYS        ).toTimeUnit(MILLISECONDS), is(equalTo(   86400000D)));
        assertThat(Duration.of(789,        NANOSECONDS ).toTimeUnit(MILLISECONDS), is(equalTo(   0.000789D)));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toTimeUnit(MILLISECONDS), is(equalTo( 123.456789D)));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toTimeUnit(MILLISECONDS), is(equalTo(       1000D)));
        assertThat(Duration.of(1001,       MILLISECONDS).toTimeUnit(MILLISECONDS), is(equalTo(       1001D)));
        assertThat(Duration.of(1601,       MILLISECONDS).toTimeUnit(MILLISECONDS), is(equalTo(       1601D)));
        assertThat(Duration.of(3601,       MILLISECONDS).toTimeUnit(MILLISECONDS), is(equalTo(       3601D)));
        assertThat(Duration.of(70,         SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(      70000D)));
        assertThat(Duration.of(601,        SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(     601000D)));
        assertThat(Duration.of(959,        SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(     959000D)));
        assertThat(Duration.of(960,        SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(     960000D)));
        assertThat(Duration.of(970,        SECONDS     ).toTimeUnit(MILLISECONDS), is(equalTo(     970000D)));
        assertThat(Duration.of(3601,       MINUTES     ).toTimeUnit(MILLISECONDS), is(equalTo(  216060000D)));
        assertThat(Duration.of(2,          HOURS       ).toTimeUnit(MILLISECONDS), is(equalTo(    7200000D)));
        assertThat(Duration.of(100,        HOURS       ).toTimeUnit(MILLISECONDS), is(equalTo(  360000000D)));
    }

    @Test
    void toTimeUnit_millisecondsAndScale3_validValues()
    {
        assertThat(Duration.of(0,          NANOSECONDS ).toTimeUnit(MILLISECONDS, 3), is(equalTo(          0D)));
        assertThat(Duration.of(1,          NANOSECONDS ).toTimeUnit(MILLISECONDS, 3), is(equalTo(          0D)));
        assertThat(Duration.of(1,          MILLISECONDS).toTimeUnit(MILLISECONDS, 3), is(equalTo(          1D)));
        assertThat(Duration.of(1,          SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(       1000D)));
        assertThat(Duration.of(1,          MINUTES     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(      60000D)));
        assertThat(Duration.of(1,          HOURS       ).toTimeUnit(MILLISECONDS, 3), is(equalTo(    3600000D)));
        assertThat(Duration.of(1,          DAYS        ).toTimeUnit(MILLISECONDS, 3), is(equalTo(   86400000D)));
        assertThat(Duration.of(789,        NANOSECONDS ).toTimeUnit(MILLISECONDS, 3), is(equalTo(      0.001D)));
        assertThat(Duration.of(123456789,  NANOSECONDS ).toTimeUnit(MILLISECONDS, 3), is(equalTo(    123.457D)));
        assertThat(Duration.of(1000000000, NANOSECONDS ).toTimeUnit(MILLISECONDS, 3), is(equalTo(       1000D)));
        assertThat(Duration.of(1001,       MILLISECONDS).toTimeUnit(MILLISECONDS, 3), is(equalTo(       1001D)));
        assertThat(Duration.of(1601,       MILLISECONDS).toTimeUnit(MILLISECONDS, 3), is(equalTo(       1601D)));
        assertThat(Duration.of(3601,       MILLISECONDS).toTimeUnit(MILLISECONDS, 3), is(equalTo(       3601D)));
        assertThat(Duration.of(70,         SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(      70000D)));
        assertThat(Duration.of(601,        SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(     601000D)));
        assertThat(Duration.of(959,        SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(     959000D)));
        assertThat(Duration.of(960,        SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(     960000D)));
        assertThat(Duration.of(970,        SECONDS     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(     970000D)));
        assertThat(Duration.of(3601,       MINUTES     ).toTimeUnit(MILLISECONDS, 3), is(equalTo(  216060000D)));
        assertThat(Duration.of(2,          HOURS       ).toTimeUnit(MILLISECONDS, 3), is(equalTo(    7200000D)));
        assertThat(Duration.of(100,        HOURS       ).toTimeUnit(MILLISECONDS, 3), is(equalTo(  360000000D)));
    }

    @Test
    void toString_noArguments_appliesShorterStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(), is(equalTo("3.601 second(s)")));
    }

    @Test
    void toString_full_appliesFullStyleWithLegend()
    {
        assertThat(Duration.of(3601, MILLISECONDS).toString(DurationFormat.FULL),
                is(equalTo("0:00:03.601000000 hour(s)")));
    }

    @Test
    void equals_sameObjects_true()
    {
        Duration td60seconds = Duration.of(60, SECONDS);
        assertThat(td60seconds.equals(td60seconds), is(true));
    }

    @Test
    void equals_similarButNotSameObjects_true()
    {
        Duration td60seconds = Duration.of(60, SECONDS);
        Duration td1Minute = Duration.of(1, MINUTES);
        assertThat(td60seconds.equals(td1Minute), is(true));
    }

    @Test
    void equals_similarZeroObjects_true()
    {
        assertThat(Duration.of(0, NANOSECONDS).equals(Duration.ZERO), is(true));
    }

    @Test
    void equals_differentTimes_false()
    {
        Duration td1001millis = Duration.of(1001, MILLISECONDS);
        Duration td1second = Duration.of(1, SECONDS);
        assertThat(td1001millis.equals(td1second), is(false));
    }

    @Test
    void equals_incompatibleTypes_false()
    {
        assertThat(Duration.of(60, SECONDS).equals(new Object()), is(false));
    }

    @Test
    void hashCode_twoSimilarObjectsInAHashSet_setMaintainsOnlyOneObject()
    {
        Set<Duration> set = new HashSet<>();
        set.add(Duration.of(60, SECONDS));
        set.add(Duration.of(1, MINUTES));
        assertThat(set.size(), is(equalTo(1)));
    }

    @Test
    void plus_validDurations_success()
    {
        assertThat(Duration.of(  60, SECONDS     ).plus(Duration.of(  60, SECONDS     )), is(equalTo(Duration.of(2, MINUTES))));
        assertThat(Duration.of(1800, SECONDS     ).plus(Duration.of(1800, SECONDS     )), is(equalTo(Duration.of(1, HOURS))));
        assertThat(Duration.of(3599, SECONDS     ).plus(Duration.of(3601, SECONDS     )), is(equalTo(Duration.of(2, HOURS))));
        assertThat(Duration.of( 500, MILLISECONDS).plus(Duration.of( 500, MILLISECONDS)), is(equalTo(Duration.of(1, SECONDS))));
    }

    @Test
    void plus_validDurationsAsLong_success()
    {
        assertThat(Duration.of(  60, SECONDS     ).plus(  60, SECONDS     ), is(equalTo(Duration.of(2, MINUTES))));
        assertThat(Duration.of(1800, SECONDS     ).plus(1800, SECONDS     ), is(equalTo(Duration.of(1, HOURS))));
        assertThat(Duration.of(3599, SECONDS     ).plus(3601, SECONDS     ), is(equalTo(Duration.of(2, HOURS))));
        assertThat(Duration.of( 500, MILLISECONDS).plus( 500, MILLISECONDS), is(equalTo(Duration.of(1, SECONDS))));
    }

    @Test
    void dividedBy_positiveDivisor_success()
    {
        assertThat(Duration.of(   2, HOURS       ).dividedBy(2), is(equalTo(Duration.of(  1, HOURS))));
        assertThat(Duration.of(   3, HOURS       ).dividedBy(2), is(equalTo(Duration.of( 90, MINUTES))));
        assertThat(Duration.of(1000, MILLISECONDS).dividedBy(5), is(equalTo(Duration.of(200, MILLISECONDS))));
    }

    @Test
    void getInternalDuration_success()
    {
        assertThat(Duration.of(90, SECONDS).getInternalDuration(), is(equalTo(java.time.Duration.ofSeconds(90))));
    }

    @Test
    void isZero_validDurations_success()
    {
        assertThat(Duration.of(0, HOURS       ).isZero(), is(true));
        assertThat(Duration.of(0, MILLISECONDS).isZero(), is(true));
        assertThat(Duration.of(1, NANOSECONDS ).isZero(), is(false));
    }

    @Test
    void compareTo_equalDurations_zero()
    {
        assertThat(Duration.of(1, SECONDS     ).compareTo(Duration.of(   1000, MILLISECONDS)), is(equalTo(0)));
        assertThat(Duration.of(1, MILLISECONDS).compareTo(Duration.of(1000000, NANOSECONDS )), is(equalTo(0)));
    }

    @Test
    void compareTo_lowerDurations_positive()
    {
        assertThat(Duration.of(  1, SECONDS    ).compareTo(Duration.of(750, MILLISECONDS)), isPositive());
        assertThat(Duration.of(100, NANOSECONDS).compareTo(Duration.of( 20, NANOSECONDS )), isPositive());
    }

    @Test
    void compareTo_lowerDurations_negative()
    {
        assertThat(Duration.of( 1, MILLISECONDS).compareTo(Duration.of(2000000, NANOSECONDS)), isNegative());
        assertThat(Duration.of(40, MINUTES     ).compareTo(Duration.of(      1, HOURS      )), isNegative());
    }

    @Test
    void parse_stringsGeneratedByToString_success()
    {
        assertParseFromToString(Duration.of(123, NANOSECONDS));
        assertParseFromToString(Duration.of(123, MILLISECONDS));
        assertParseFromToString(Duration.of(123, SECONDS));
        assertParseFromToString(Duration.of(123, MINUTES));
    }

    private void assertParseFromToString(Duration duration)
    {
        assertThat(Duration.parse(duration.toString()), equalTo(duration));
    }

    @Test
    void parse_iso8601AndValidStrings_success()
    {
        assertThat(Duration.parse("PT0.123456789S", ISO_8601), equalTo(Duration.of(123456789, NANOSECONDS)));
        assertThat(Duration.parse("PT1H1M1S",       ISO_8601), equalTo(Duration.of(3661,      SECONDS)));
    }

}
