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
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
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
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;

    // Since JDK 17, Mockito cannot mock java.util.Runnable
    private Runnable runnable = () -> {};

    Stopwatch stopwatch = mock(Stopwatch.class);

    /**
     * Setup the expects on {@link SystemUtils} mock with constant values
     */
    private void setupExpects(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(MOCKED_WALL_CLOCK_TIME);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(MOCKED_CPU_TIME);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(MOCKED_USER_TIME);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(MOCKED_SYSTEM_TIME);
    }

    private void assertAllUnitsBefore(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsBefore(),
                is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    private void assertAllUnitsAfter(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    private MonitoredRunnable newMonitoredRunnableWithMockedStopwatch()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable, WALL_CLOCK_TIME);
        operation.stopwatch = this.stopwatch;
        return operation;
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

    /**
     * Tests the elapsed time for a dummy {@link Runnable} with no specific counter set (all
     * counters)
     */
    @Test
    void run_givenAllTypes_updatesAllCounters()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpects(systemUtils);
            operation.run();
        }
        assertAllUnitsBefore(operation, 0);
        assertAllUnitsAfter(operation, 0);
    }

    @Test
    void print_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.print(System.out);
            printUtils.verify(() -> PrintUtils.print(operation.stopwatch, System.out), times(1));
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
            printUtils.verify(() -> PrintUtils.print(operation.stopwatch, System.out, ps), times(1));
        }
    }

    @Test
    void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out);
            printUtils.verify(() -> PrintUtils.printSummary(operation.stopwatch, System.out, null), times(1));
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
            printUtils.verify(() -> PrintUtils.printSummary(operation.stopwatch, System.out, ps), times(1));
        }
    }

    @Test
    void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out);
            printUtils.verify(() -> PrintUtils.printDetails(operation.stopwatch, System.out, null), times(1));
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
            printUtils.verify(() -> PrintUtils.printDetails(operation.stopwatch, System.out, ps), times(1));
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
    void elapsedTime_validType_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.elapsedTime(WALL_CLOCK_TIME);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME);
    }

    @Test
    void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.elapsedTime(WALL_CLOCK_TIME, HOURS);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME, HOURS);
    }

    @Test
    void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.elapsedTime(WALL_CLOCK_TIME, HOURS, ConversionMode.FAST);
        verify(stopwatch).elapsedTime(WALL_CLOCK_TIME, HOURS, ConversionMode.FAST);
    }

    @Test
    void elapsedTime_noTypeOnSingleTypeMonitor_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.elapsedTime();
        verify(stopwatch).elapsedTime();
    }

    @Test
    void elapsedTime_timeUnitOnSingleTypeMonitor_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        operation.stopwatch = this.stopwatch;

        operation.elapsedTime(NANOSECONDS);
        verify(stopwatch).elapsedTime(NANOSECONDS);
    }

    @Test
    void elapsedTime_timeUnitAndConversionModeOnSingleTypeMonitor_callsCorrectElapsedTimeFromCounter()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.elapsedTime(NANOSECONDS, FAST);
        verify(stopwatch).elapsedTime(NANOSECONDS, FAST);
    }

    @Test
    void reset_callsStopwatchReset()
    {
        MonitoredRunnable operation = newMonitoredRunnableWithMockedStopwatch();
        operation.reset();
        verify(stopwatch).reset();
    }

    @Test
    void toString_callsCorrectPrintUtilMethod()
    {
        MonitoredRunnable operation = new MonitoredRunnable(runnable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.toString();
            printUtils.verify(() -> PrintUtils.toString(operation.stopwatch), times(1));
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
            printUtils.verify(() -> PrintUtils.toString(operation.stopwatch, ps), times(1));
        }
    }

}
