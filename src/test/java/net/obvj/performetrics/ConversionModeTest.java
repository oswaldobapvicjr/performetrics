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

package net.obvj.performetrics;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for the {@link ConversionMode}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class ConversionModeTest
{

    @ParameterizedTest
    @CsvSource({
        "1,    MILLISECONDS, SECONDS,   0.0",
        "999,  MILLISECONDS, SECONDS,   0.0",
        "1000, MILLISECONDS, SECONDS,   1.0",
        "1500, MILLISECONDS, SECONDS,   1.0",
        "2,    HOURS,        MINUTES, 120.0"
    })
    void convert_fast(long amount, TimeUnit source, TimeUnit target, double result)
    {
        assertThat(ConversionMode.FAST.convert(amount, source, target), is(result));
    }

    @ParameterizedTest
    @CsvSource({
        "1,    MILLISECONDS, SECONDS, 0.001",
        "999,  MILLISECONDS, SECONDS, 0.999",
        "1000, MILLISECONDS, SECONDS,   1.0",
        "1500, MILLISECONDS, SECONDS,   1.5",
        "2,    HOURS,        MINUTES, 120.0"
    })
    void convert_doublePrecision(long amount, TimeUnit source, TimeUnit target, double result)
    {
        assertThat(ConversionMode.DOUBLE_PRECISION.convert(amount, source, target), is(result));
    }

}
