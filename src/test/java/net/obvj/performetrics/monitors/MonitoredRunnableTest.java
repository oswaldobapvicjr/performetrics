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

package net.obvj.performetrics.monitors;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintStyle;
import net.obvj.performetrics.util.print.PrintUtils;

/**
 * Test methods for the {@link MonitoredRunnable}.
 *
 * @author oswaldo.bapvic.jr
 */
class MonitoredRunnableTest
{
    private static final long WALL_CLOCK_TIME_BEFORE = 2000000000l;
    private static final long CPU_TIME_BEFORE = 1200000000l;
    private static final long USER_TIME_BEFORE = 1200000001l;
    private static final long SYSTEM_TIME_BEFORE = 1200000002l;

    private static final long WALL_CLOCK_TIME_AFTER = 3000000000l;
    private static final long CPU_TIME_AFTER = 1200000300l;
    private static final long USER_TIME_AFTER = 1200000201l;
    private static final long SYSTEM_TIME_AFTER = 1200000102l;

    // Since JDK 17, Mockito cannot mock java.util.Runnable
    private Runnable runnable = () -> {};

    /**
     * Setup the expects with pairs of "_BEFORE" and "_AFTER" constant values
     */
    private static void setupExpectsBeforeAndAfter(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(WALL_CLOCK_TIME_BEFORE, WALL_CLOCK_TIME_AFTER);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(CPU_TIME_BEFORE, CPU_TIME_AFTER);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(USER_TIME_BEFORE, USER_TIME_AFTER);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(SYSTEM_TIME_BEFORE, SYSTEM_TIME_AFTER);
    }

    @Test
    void constructor_withOneType_assignsCorrectType()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable, CPU_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(1)));
        assertTrue(types.contains(CPU_TIME));
    }

    @Test
    void constructor_withTwoTypes_assignsCorrectTypes()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable, CPU_TIME, USER_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(2)));
        assertTrue(types.containsAll(Arrays.asList(CPU_TIME, USER_TIME)));
    }

    /**
     * Tests, for a given {@link Runnable} and no specific counter, that all available types
     * are specified for this operation
     */
    @Test
    void constructor_withoutType_assignsAllAvailableCounterTypes()
    {
        MonitoredRunnable op = new MonitoredRunnable(runnable);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(Type.values().length)));
        assertTrue(types.containsAll(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME, USER_TIME, SYSTEM_TIME)));
    }

    @Test
    void print_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.print(System.out);
            printUtils.verify(() -> PrintUtils.print(operation, System.out), times(1));
        }
    }

    @Test
    void print_withPrintWriterAndPrintStyle_callsCorrectPrintUtilMethod()
    {
        PrintStyle ps = mock(PrintStyle.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.print(System.out, ps);
            printUtils.verify(() -> PrintUtils.print(operation, System.out, ps), times(1));
        }
    }

    @Test
    void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out);
            printUtils.verify(() -> PrintUtils.printSummary(operation, System.out, null), times(1));
        }
    }

    @Test
    void printSummary_withPrintWriterAndPrintStyle_callsCorrectPrintUtilMethod()
    {
        PrintStyle ps = mock(PrintStyle.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out, ps);
            printUtils.verify(() -> PrintUtils.printSummary(operation, System.out, ps), times(1));
        }
    }

    @Test
    void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out);
            printUtils.verify(() -> PrintUtils.printDetails(operation, System.out, null), times(1));
        }
    }

    @Test
    void printDetails_withPrintWriterAndPrintStyle_callsCorrectPrintUtilMethod()
    {
        PrintStyle ps = mock(PrintStyle.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out, ps);
            printUtils.verify(() -> PrintUtils.printDetails(operation, System.out, ps), times(1));
        }
    }

    @Test
    void elapsedTime_invalidType_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        assertThrows(IllegalArgumentException.class, () -> operation.elapsedTime(USER_TIME));
    }

    @Test
    void elapsedTime_invalidTypeAndValidTimeUnit_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        assertThrows(IllegalArgumentException.class, () -> operation.elapsedTime(USER_TIME, HOURS));
    }

    @Test
    void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_zero()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, CPU_TIME);
        assertThrows(IllegalArgumentException.class, () -> operation.elapsedTime(USER_TIME, HOURS, FAST));
    }

    @Test
    void run_defaultTypes_validElapsedTimes()
    {
        MonitoredRunnable monitored = new MonitoredRunnable(this.runnable);

        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBeforeAndAfter(systemUtils);
            monitored.run();

            assertThat(monitored.elapsedTime(WALL_CLOCK_TIME),
                    is(equalTo(Duration.of(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE, NANOSECONDS))));
            assertThat(monitored.elapsedTime(CPU_TIME),
                    is(equalTo(Duration.of(CPU_TIME_AFTER - CPU_TIME_BEFORE, NANOSECONDS))));
            assertThat(monitored.elapsedTime(USER_TIME),
                    is(equalTo(Duration.of(USER_TIME_AFTER - USER_TIME_BEFORE, NANOSECONDS))));
            assertThat(monitored.elapsedTime(SYSTEM_TIME),
                    is(equalTo(Duration.of(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE, NANOSECONDS))));
        }
    }

    @Test
    void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable monitored = new MonitoredRunnable(this.runnable);

        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBeforeAndAfter(systemUtils);
            monitored.run();

            assertThat(monitored.elapsedTime(WALL_CLOCK_TIME, TimeUnit.NANOSECONDS),
                    is(equalTo(Double.valueOf(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE))));
            assertThat(monitored.elapsedTime(CPU_TIME, TimeUnit.NANOSECONDS),
                    is(equalTo(Double.valueOf(CPU_TIME_AFTER - CPU_TIME_BEFORE))));
            assertThat(monitored.elapsedTime(USER_TIME, TimeUnit.NANOSECONDS),
                    is(equalTo(Double.valueOf(USER_TIME_AFTER - USER_TIME_BEFORE))));
            assertThat(monitored.elapsedTime(SYSTEM_TIME, TimeUnit.NANOSECONDS),
                    is(equalTo(Double.valueOf(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE))));
        }
    }

    @Test
    void toString_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.toString();
            printUtils.verify(() -> PrintUtils.toString(operation), times(1));
        }
    }

    @Test
    void toString_customPrintStyle_callsCorrectPrintUtilMethod()
    {
        PrintStyle ps = mock(PrintStyle.class);
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.toString(ps);
            printUtils.verify(() -> PrintUtils.toString(operation, ps), times(1));
        }
    }

}
