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

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
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
 * Test methods for the {@Counter} class.
 *
 * @author oswaldo.bapvic.jr
 */
class CounterTest
{

    @Test
    void constructor_withType_assignsDefaultConversionMode()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        assertThat(counter.getType(), is(SYSTEM_TIME));
        assertThat(counter.getConversionMode(), is(equalTo(DOUBLE_PRECISION)));
    }

    @Test
    void constructor_withTypeAndConversionMode_succeeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, FAST);
        assertThat(counter.getType(), is(SYSTEM_TIME));
        assertThat(counter.getConversionMode(), is(equalTo(FAST)));
    }

    @Test
    void getters_succeed()
    {
        Counter counter = new Counter(CPU_TIME);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        assertThat(counter.getType(), is(equalTo(CPU_TIME)));
        assertThat(counter.getUnitsBefore(), is(equalTo(5L)));
        assertThat(counter.getUnitsAfter(), is(equalTo(10L)));
    }

    @Test
    void toString_withAllFieldsSet_suceeds()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(5);
        counter.setUnitsAfter(10);
        String expectedString = String.format(Counter.STRING_FORMAT, WALL_CLOCK_TIME, 5, 10);
        assertThat(counter.toString(), is(equalTo(expectedString)));
    }

    @Test
    void elapsedTime_withTimeUnitEqualToTheOriginal_returnsDifferenceInOriginalTimeUnit()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        counter.setUnitsBefore(2);
        counter.setUnitsAfter(3);
        assertThat(counter.elapsedTime(NANOSECONDS), is(equalTo(1.0)));
    }

    @Test
    void elapsedTime_withTimeUnitHigherThanOriginal_returnsDifferenceConverted()
    {
        Counter counter = new Counter(SYSTEM_TIME);
        counter.setUnitsBefore(999_999_999);
        counter.setUnitsAfter(1_000_000_000);
        assertThat(counter.elapsedTime(SECONDS), is(equalTo(0.000000001)));
    }

    @Test
    void elapsedTime_withUnitsBeforeSetOnly_returnsDifferenceBetweenUnitsBeforeAndCurrentTime()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(2_000);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(9000L);
            assertThat(counter.elapsedTime(), is(equalTo(Duration.of(7000L, NANOSECONDS))));
        }
    }

    @Test
    void elapsedTime_unitsAfterLowerThanUnitsBefore_zero()
    {
        Counter counter = new Counter(WALL_CLOCK_TIME);
        counter.setUnitsBefore(5_000);
        counter.setUnitsAfter(500);
        assertThat(counter.elapsedTime(), is(equalTo(Duration.ZERO)));
    }

    @Test
    void elapsedTime_withCoarserTimeUnitAndFastConversion_differenceIsTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, FAST);
        counter.setUnitsAfter(999_999_999);
        assertThat(counter.elapsedTime(SECONDS), is(equalTo(0.0)));
    }

    @Test
    void elapsedTime_withCoarserTimeUnitAndDoublePrecisionConversion_differenceIsNotTruncated()
    {
        Counter counter = new Counter(SYSTEM_TIME, DOUBLE_PRECISION);
        counter.setUnitsAfter(999_999_999);
        assertThat(counter.elapsedTime(SECONDS), is(equalTo(0.999_999_999)));
    }

    @Test
    void elapsedTime_sameTimeUnitAndFastConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, FAST);
        counter.setUnitsAfter(2);
        assertThat(counter.elapsedTime(NANOSECONDS), is(equalTo(2.0)));
    }

    @Test
    void elapsedTime_sameTimeUnitAndDoublePrecisionConversion_conversionSuceeds()
    {
        Counter counter = new Counter(SYSTEM_TIME, DOUBLE_PRECISION);
        counter.setUnitsAfter(2);
        assertThat(counter.elapsedTime(NANOSECONDS), is(equalTo(2.0)));
    }

}
