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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test methods for the {@link SystemUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
class SystemUtilsTest
{
    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(SystemUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void getWallClockTimeMillis_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeMillis() > 0, is(true));
    }

    @Test
    void getWallClockTimeNanos_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeNanos() > 0, is(true));
    }
}
