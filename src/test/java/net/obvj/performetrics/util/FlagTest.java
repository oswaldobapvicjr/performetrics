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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Test;

import net.obvj.performetrics.util.DurationStats.Flag;

/**
 * Unit tests for the {@link Flag} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.6.0
 */
class FlagTest
{

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(Flag.class, instantiationNotAllowed().throwing(IllegalStateException.class)
                .withMessage("Instantiation not allowed"));
    }

    @Test
    void isFlagEnabled_severalCombinations()
    {
        int myFlags = Flag.BASIC | Flag.MAX;
        assertThat(Flag.isEnabled(Flag.BASIC, myFlags), equalTo(true));
        assertThat(Flag.isEnabled(Flag.MIN, myFlags), equalTo(false));
        assertThat(Flag.isEnabled(Flag.MAX, myFlags), equalTo(true));

        int minMaxFlag = Flag.MIN_MAX;
        assertThat(Flag.isEnabled(Flag.BASIC, minMaxFlag), equalTo(false));
        assertThat(Flag.isEnabled(Flag.MIN, minMaxFlag), equalTo(true));
        assertThat(Flag.isEnabled(Flag.MAX, minMaxFlag), equalTo(true));

        int allFlags = Flag.ALL;
        assertThat(Flag.isEnabled(Flag.BASIC, allFlags), equalTo(true));
        assertThat(Flag.isEnabled(Flag.MIN, allFlags), equalTo(true));
        assertThat(Flag.isEnabled(Flag.MAX, allFlags), equalTo(true));
    }

}
