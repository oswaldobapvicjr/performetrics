/*
 * Copyright 2024 obvj.net
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

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Unit tests for the {@link UnmodifiableCounter} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.5.1
 */
class UnmodifiableCounterTest
{

    @Test
    void getters_succeed()
    {
        Counter counter = new Counter(CPU_TIME);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);

        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.getType(), is(equalTo(CPU_TIME)));
        assertThat(unmodifiable.getUnitsBefore(), is(equalTo(5L)));
        assertThat(unmodifiable.getUnitsAfter(), is(equalTo(10L)));
    }

    @Test
    void toString_withAllFieldsSet_suceeds()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(500);
        counter.setUnitsAfter(1_000);

        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.toString(), is(equalTo(counter.toString())));
    }

    @Test
    void elapsedTime_withTimeUnitNanoseconds_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3);

        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.elapsedTime(NANOSECONDS), is(equalTo(1.0)));
    }

    @Test
    void elapsedTime_withTimeUnitHigherThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        counter.setUnitsBefore(2_000_000_000L);
        counter.setUnitsAfter(3_500_000_000L);

        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.elapsedTime(SECONDS), is(equalTo(1.5)));
    }

    @Test
    void elapsedTime_withoutParams_returnsDifferenceBetweenUnitsBeforeAndCurrentTime()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(2_000);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(9_000L);
            Counter unmodifiable = new UnmodifiableCounter(counter);
            assertThat(unmodifiable.elapsedTime(), is(equalTo(Duration.of(7_000L, NANOSECONDS))));
        }
    }

    @Test
    void elapsedTime_withFinerTimeUnitAndDoublePrecisionConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, DOUBLE_PRECISION);
        assertThat(counter.getConversionMode(), is(DOUBLE_PRECISION));
        counter.setUnitsAfter(2_000_000_001);
        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.elapsedTime(SECONDS), is(equalTo(2.000000001)));
    }

    @Test
    void elapsedTime_withFinerTimeUnitAndFastConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, FAST);
        assertThat(counter.getConversionMode(), is(FAST));
        counter.setUnitsAfter(2_000_000_001);
        Counter unmodifiable = new UnmodifiableCounter(counter);
        assertThat(unmodifiable.elapsedTime(SECONDS), is(equalTo(2.0)));
    }

    @Test
    void setUnitsBefore_withParameter_unsupportedOperation()
    {
        Counter unmodifiable = new UnmodifiableCounter(new Counter(SYSTEM_TIME));
        assertThat(() -> unmodifiable.setUnitsBefore(99L),
                throwsException(UnsupportedOperationException.class)
                        .withMessage("\"setUnitsBefore\" not allowed (unmodifiable Counter)"));
    }

    @Test
    void setUnitsBefore_noParameter_unsupportedOperation()
    {
        Counter unmodifiable = new UnmodifiableCounter(new Counter(SYSTEM_TIME));
        assertThat(() -> unmodifiable.setUnitsBefore(),
                throwsException(UnsupportedOperationException.class)
                        .withMessage("\"setUnitsBefore\" not allowed (unmodifiable Counter)"));
    }

    @Test
    void setUnitsAfter_withParameter_unsupportedOperation()
    {
        Counter unmodifiable = new UnmodifiableCounter(new Counter(SYSTEM_TIME));
        assertThat(() -> unmodifiable.setUnitsAfter(99L),
                throwsException(UnsupportedOperationException.class)
                        .withMessage("\"setUnitsAfter\" not allowed (unmodifiable Counter)"));
    }

    @Test
    void setUnitsAfter_noParameter_unsupportedOperation()
    {
        Counter unmodifiable = new UnmodifiableCounter(new Counter(SYSTEM_TIME));
        assertThat(() -> unmodifiable.setUnitsAfter(),
                throwsException(UnsupportedOperationException.class)
                        .withMessage("\"setUnitsAfter\" not allowed (unmodifiable Counter)"));
    }

}
