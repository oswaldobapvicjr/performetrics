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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
        "  2, MINUTES,      MILLISECONDS, 120_000",
        " 30, SECONDS,      MINUTES,      0.5",
        "999, MILLISECONDS, SECONDS,      0.999"
    })
    void convertAndRound_threeParameters(long amount, TimeUnit source, TimeUnit target, double expected)
    {
        assertThat(TimeUnitConverter.convertAndRound(amount, source, target),
                is(equalTo(expected)));
    }

    @ParameterizedTest
    @CsvSource({
        "988, MILLISECONDS, SECONDS, 2, 0.99",
        "988, MILLISECONDS, SECONDS, 0, 1.0"
    })
    void convertAndRound_allParameters(long amount, TimeUnit source, TimeUnit target, int decimalPlaces, double expected)
    {
        assertThat(TimeUnitConverter.convertAndRound(amount, source, target, decimalPlaces),
                is(equalTo(expected)));
    }

    @ParameterizedTest
    @CsvSource({
        "22.859,  2, 22.86",
        "22.859,  0, 23.0",
        "22.859, -1, 20.0"
    })
    void round_twoParameters(double amount, int decimalPlaces, double expected)
    {
        assertThat(TimeUnitConverter.round(amount, decimalPlaces), is(equalTo(expected)));
    }

    @ParameterizedTest
    @CsvSource({
        "999, MILLISECONDS, SECONDS,     0.999",
        "999, NANOSECONDS,  NANOSECONDS, 999.0",
    })
    void convert_threeParameters(long amount, TimeUnit source, TimeUnit target, double expected)
    {
        assertThat(TimeUnitConverter.convert(amount, source, target), is(equalTo(expected)));
    }

}
