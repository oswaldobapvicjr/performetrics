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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ConversionMode}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class ConversionModeTest
{

    @Test
    void convert_fast999millisecondsToSeconds()
    {
        assertThat(ConversionMode.FAST.convert(999, MILLISECONDS, SECONDS), is(0.0));
    }

    @Test
    void convert_doublePrecision999millisecondsToSeconds()
    {
        assertThat(ConversionMode.DOUBLE_PRECISION.convert(999, MILLISECONDS, SECONDS), is(0.999));
    }

}
