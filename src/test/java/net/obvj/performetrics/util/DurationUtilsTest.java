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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.performetrics.util.DurationUtils.average;
import static net.obvj.performetrics.util.DurationUtils.max;
import static net.obvj.performetrics.util.DurationUtils.min;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import net.obvj.performetrics.util.DurationStats.Flag;

/**
 * Unit tests for the {@link DurationUtils} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
class DurationUtilsTest
{
    private static final Duration D_ZERO = Duration.ZERO;
    private static final Duration D_500_MILLIS = Duration.of(500, MILLISECONDS);
    private static final Duration D_750_MILLIS = Duration.of(750, MILLISECONDS);
    private static final Duration D_1250_MILLIS = Duration.of(1250, MILLISECONDS);
    private static final Duration D_1_SECOND = Duration.of(1, SECONDS);
    private static final Duration D_2_SECONDS = Duration.of(2, SECONDS);

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(DurationUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void average_null_zero()
    {
        assertThat(average(null), is(equalTo(D_ZERO)));
    }

    @Test
    void average_empty_zero()
    {
        assertThat(average(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    void average_oneElement_sameValue()
    {
        assertThat(average(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    void average_twoOrMoreElements_meanValue()
    {
        assertThat(average(Arrays.asList(D_500_MILLIS, D_1_SECOND)), is(equalTo(D_750_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1250_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1_SECOND)));
    }

    @Test
    void average_containingNullElements_nullElementsDontCount()
    {
        assertThat(average(Arrays.asList(D_500_MILLIS, null)), is(equalTo(D_500_MILLIS)));
        assertThat(average(Arrays.asList(null, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1250_MILLIS)));
        assertThat(average(Arrays.asList(D_500_MILLIS, null, D_500_MILLIS, D_2_SECONDS)), is(equalTo(D_1_SECOND)));
    }

    @Test
    void average_containingOnlyNullElements_zero()
    {
        assertThat(average(Collections.singleton((Duration) null)), is(equalTo(D_ZERO)));
    }

    @Test
    void min_null_zero()
    {
        assertThat(min(null), is(equalTo(D_ZERO)));
    }

    @Test
    void min_empty_zero()
    {
        assertThat(min(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    void min_oneElement_sameValue()
    {
        assertThat(min(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    void min_twoOrMoreElements_lowestValue()
    {
        assertThat(min(Arrays.asList(D_750_MILLIS, D_1_SECOND)), is(equalTo(D_750_MILLIS)));
        assertThat(min(Arrays.asList(D_750_MILLIS, D_2_SECONDS, D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    void max_null_zero()
    {
        assertThat(max(null), is(equalTo(D_ZERO)));
    }

    @Test
    void max_empty_zero()
    {
        assertThat(max(Collections.emptyList()), is(equalTo(D_ZERO)));
    }

    @Test
    void max_oneElement_sameValue()
    {
        assertThat(max(Collections.singleton(D_500_MILLIS)), is(equalTo(D_500_MILLIS)));
    }

    @Test
    void max_twoOrMoreElements_lowestValue()
    {
        assertThat(max(Arrays.asList(D_750_MILLIS, D_1_SECOND)), is(equalTo(D_1_SECOND)));
        assertThat(max(Arrays.asList(D_750_MILLIS, D_2_SECONDS, D_500_MILLIS)), is(equalTo(D_2_SECONDS)));
    }

    @Test
    void analyzeDurations_multipleValuesAndAllFlags_validMetrics()
    {
        List<Duration> durations = Arrays.asList(D_1_SECOND, D_2_SECONDS, D_500_MILLIS, D_1250_MILLIS);
        DurationStats stats = DurationUtils.analyzeDurations(durations, Flag.ALL);
        assertThat(stats.average(), equalTo(Duration.of(1_187_500_000, TimeUnit.NANOSECONDS)));
        assertThat(stats.min(), equalTo(D_500_MILLIS));
        assertThat(stats.max(), equalTo(D_2_SECONDS));
    }

}
