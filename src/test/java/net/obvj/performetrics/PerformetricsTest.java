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
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
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
import net.obvj.performetrics.util.print.PrintStyle;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class PerformetricsTest
{
    // Default values
    private static final ConversionMode INITIAL_CONVERSION_MODE = ConfigurationHolder.getConfiguration().getConversionMode();
    private static final int INITIAL_SCALE = ConfigurationHolder.getConfiguration().getScale();
    private static final PrintStyle INITIAL_PRINT_STYLE = ConfigurationHolder.getConfiguration().getPrintStyle();
    private static final PrintStyle INITIAL_PRINT_STYLE_FOR_SUMMARY = ConfigurationHolder.getConfiguration().getPrintStyleForSummary();
    private static final PrintStyle INITIAL_PRINT_STYLE_FOR_DETAILS = ConfigurationHolder.getConfiguration().getPrintStyleForDetails();

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
        ConfigurationHolder.reset();
    }

    private void checkAllDefaultValues()
    {
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyle();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    private void checkDefaultConversionMode()
    {
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(INITIAL_CONVERSION_MODE)));
    }

    private void checkDefaulScale()
    {
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(equalTo(INITIAL_SCALE)));
    }

    private void checkDefaulPrintStyle()
    {
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyle(),
                is(equalTo(INITIAL_PRINT_STYLE)));
    }

    private void checkDefaulPrintStyleForSummary()
    {
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForSummary(),
                is(equalTo(INITIAL_PRINT_STYLE_FOR_SUMMARY)));
    }

    private void checkDefaulPrintStyleForDetails()
    {
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForDetails(),
                is(equalTo(INITIAL_PRINT_STYLE_FOR_DETAILS)));
    }

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(Performetrics.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void setDefaultConversionMode_fast_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(FAST);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(FAST)));
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    void setDefaultConversionMode_doublePrecision_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(DOUBLE_PRECISION);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(DOUBLE_PRECISION)));
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    void setScale_valid_updatesConfiguration()
    {
        Performetrics.setScale(16);
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(equalTo(16)));
        checkDefaultConversionMode();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();

    }

    @Test
    void setScale_invalid_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setScale(-1);
        }
        catch (IllegalArgumentException e)
        {
            checkAllDefaultValues();
        }
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
    void setDefaultPrintStyle_valid_updatesConfiguration()
    {
        PrintStyle ps = mock(PrintStyle.class);
        Performetrics.setDefaultPrintStyle(ps);
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyle(), is(equalTo(ps)));
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    void setDefaultPrintStyle_null_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setDefaultPrintStyleForSummary(null);
        }
        catch (NullPointerException e)
        {
            checkAllDefaultValues();
        }
    }

    @Test
    void setDefaultPrintStyleForSummary_valid_updatesConfiguration()
    {
        PrintStyle ps = mock(PrintStyle.class);
        Performetrics.setDefaultPrintStyleForSummary(ps);
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForSummary(), is(equalTo(ps)));
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyle();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    void setDefaultPrintStyleForSummary_null_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setDefaultPrintStyleForSummary(null);
        }
        catch (NullPointerException e)
        {
            checkAllDefaultValues();
        }
    }

    @Test
    void setDefaultPrintStyleForDetails_valid_updatesConfiguration()
    {
        PrintStyle ps = mock(PrintStyle.class);
        Performetrics.setDefaultPrintStyleForDetails(ps);
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForDetails(), is(equalTo(ps)));
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyle();
        checkDefaulPrintStyleForSummary();
    }

    @Test
    void setDefaultPrintStyleForDetails_null_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setDefaultPrintStyleForDetails(null);
        }
        catch (NullPointerException e)
        {
            checkAllDefaultValues();
        }
    }

}
