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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TimeUnitConverter}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class TimeUnitConverterTest
{
    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(TimeUnitConverter.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void convertAndRound_2MinutesToMilliseconds()
    {
        assertThat(TimeUnitConverter.convertAndRound(2, TimeUnit.MINUTES, TimeUnit.MILLISECONDS), is(equalTo(2.0 * 60 * 1000)));
    }

    @Test
    void convertAndRound_90SecondsToMinutes()
    {
        assertThat(TimeUnitConverter.convertAndRound(30, TimeUnit.SECONDS, TimeUnit.MINUTES), is(equalTo(0.5)));
    }

    @Test
    void convertAndRound_999MillisecondsToSeconds()
    {
        assertThat(TimeUnitConverter.convertAndRound(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS), is(equalTo(0.999)));
    }

    @Test
    void convertAndRound_988MillisecondsToSecondsAnd2DecimalPlaces()
    {
        assertThat(TimeUnitConverter.convertAndRound(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 2), is(equalTo(0.99)));
    }

    @Test
    void convertAndRound_988MillisecondsToSecondsAnd0DecimalPlaces()
    {
        assertThat(TimeUnitConverter.convertAndRound(988, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, 0), is(equalTo(1.0)));
    }

    @Test
    void round_positiveDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, 2), is(equalTo(22.86)));
    }

    @Test
    void round_zeroDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, 0), is(equalTo(23.0)));
    }

    @Test
    void round_negativeDecimalPlaces()
    {
        assertThat(TimeUnitConverter.round(22.859, -1), is(equalTo(20.0)));
    }

    @Test
    void convert_999MillisecondsToSeconds()
    {
        assertThat(TimeUnitConverter.convert(999, TimeUnit.MILLISECONDS, TimeUnit.SECONDS), is(equalTo(0.999)));
    }

    @Test
    void convert_sameTimeUnit_success()
    {
        assertThat(TimeUnitConverter.convert(999, TimeUnit.NANOSECONDS, TimeUnit.NANOSECONDS), equalTo(999.0));
    }

}
