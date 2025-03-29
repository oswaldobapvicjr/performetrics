/*
 * Copyright 2025 obvj.net
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link DurationStats} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.7.0
 */
class DurationStatsTest
{
    private static final Duration D_500_MILLIS = Duration.of(500, MILLISECONDS);
    private static final Duration D_750_MILLIS = Duration.of(750, MILLISECONDS);
    private static final Duration D_1250_MILLIS = Duration.of(1250, MILLISECONDS);
    private static final Duration D_1500_MILLIS = Duration.of(1500, MILLISECONDS);
    private static final Duration D_1_SECOND = Duration.of(1, SECONDS);
    private static final Duration D_2_SECONDS = Duration.of(2, SECONDS);
    private static final Duration D_3000_MILLIS = Duration.of(3000, MILLISECONDS);

    private static void assertDurationStats(DurationStats actualStats, Duration sum,
            Duration average, Duration min, Duration max)
    {
        assertThat(actualStats.sum(), equalTo(sum));
        assertThat(actualStats.average(), equalTo(average));
        assertThat(actualStats.min(), equalTo(min));
        assertThat(actualStats.max(), equalTo(max));
    }

    @Test
    void accept_validDurations_success()
    {
        DurationStats ds1 = new DurationStats().accept(D_500_MILLIS);
        assertDurationStats(ds1, D_500_MILLIS, D_500_MILLIS, D_500_MILLIS, D_500_MILLIS);

        DurationStats ds2 = ds1.accept(D_1_SECOND);
        assertDurationStats(ds2, D_1500_MILLIS, D_750_MILLIS, D_500_MILLIS, D_1_SECOND);

        DurationStats ds3 = ds2.accept(D_1500_MILLIS);
        assertDurationStats(ds3, D_3000_MILLIS, D_1_SECOND, D_500_MILLIS, D_1500_MILLIS);

        DurationStats ds4 = ds3.accept(D_2_SECONDS);
        assertDurationStats(ds4, Duration.of(5, SECONDS), D_1250_MILLIS, D_500_MILLIS, D_2_SECONDS);
    }

}
