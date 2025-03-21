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

/**
 * Unit tests for the {@link StatFlags} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.6.0
 */
class StatFlagsTest
{

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(StatFlags.class, instantiationNotAllowed().throwing(IllegalStateException.class)
                .withMessage("Instantiation not allowed"));
    }

    @Test
    void isFlagEnabled_severalCombinations()
    {
        int myFlags = StatFlags.MIN | StatFlags.MAX;
        assertThat(StatFlags.isEnabled(StatFlags.AVERAGE, myFlags), equalTo(false));
        assertThat(StatFlags.isEnabled(StatFlags.MIN, myFlags), equalTo(true));
        assertThat(StatFlags.isEnabled(StatFlags.MAX, myFlags), equalTo(true));

        int allFlags = StatFlags.ALL;
        assertThat(StatFlags.isEnabled(StatFlags.AVERAGE, allFlags), equalTo(true));
        assertThat(StatFlags.isEnabled(StatFlags.MIN, allFlags), equalTo(true));
        assertThat(StatFlags.isEnabled(StatFlags.MAX, allFlags), equalTo(true));
    }

}
