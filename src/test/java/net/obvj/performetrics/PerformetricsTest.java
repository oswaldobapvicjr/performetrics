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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredRunnable;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class PerformetricsTest
{
    private boolean runFlag = false;

    // Since JDK 17, Mockito cannot mock java.util.Runnable
    private Runnable runnable = () ->
    {
        runFlag = true;
    };

    @AfterEach
    void resetFlag()
    {
        runFlag = false;
    }

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(Performetrics.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void monitorOperation_noSpecificCounter()
    {
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredRunnable monitored = Performetrics.monitorOperation(runnable);
            assertThat(runFlag, is(equalTo(true)));
            assertThat(monitored.getAllCountersByType().keySet().size(), is(equalTo(Type.values().length)));

            systemUtils.verify(SystemUtils::getWallClockTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getCpuTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getUserTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getSystemTimeNanos, times(2));
        }
    }

    @Test
    void monitorOperation_twoSpecificCounters()
    {
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredRunnable monitored = Performetrics.monitorOperation(this.runnable, WALL_CLOCK_TIME, CPU_TIME);

            assertThat(runFlag, is(equalTo(true)));
            assertThat(monitored.getAllCountersByType().keySet().size(), is(equalTo(2)));

            systemUtils.verify(SystemUtils::getWallClockTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getCpuTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getUserTimeNanos, never());
            systemUtils.verify(SystemUtils::getSystemTimeNanos, never());
        }
    }

    @Test
    void configuration_getCurrentConfiguration()
    {
        assertThat(Performetrics.configuration(), equalTo(ConfigurationHolder.getConfiguration()));
    }

}
