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

import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintUtils;

/**
 * Test methods for the {@link MonitoredCallable}.
 *
 * @author oswaldo.bapvic.jr
 */
class MonitoredCallableTest
{
    private static final long MOCKED_WALL_CLOCK_TIME = 2000000000l;
    private static final long MOCKED_CPU_TIME = 1200000000l;
    private static final long MOCKED_USER_TIME = 1200000001l;
    private static final long MOCKED_SYSTEM_TIME = 1200000002l;
    private static final String STRING_CALLABLE_RETURN = "test234";

    // Since JDK 17, Mockito cannot mock java.util.Callable
    private Callable<String> callable = () ->
    {
        return STRING_CALLABLE_RETURN;
    };

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
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsBefore(), is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    private void assertAllUnitsAfter(MonitoredOperation operation, int session)
    {
        assertThat(operation.getCounters(WALL_CLOCK_TIME).get(session).getUnitsAfter(),
                is(equalTo(MOCKED_WALL_CLOCK_TIME)));
        assertThat(operation.getCounters(CPU_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_CPU_TIME)));
        assertThat(operation.getCounters(USER_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_USER_TIME)));
        assertThat(operation.getCounters(SYSTEM_TIME).get(session).getUnitsAfter(), is(equalTo(MOCKED_SYSTEM_TIME)));
    }

    @Test
    void constructor_withOneType_assignsCorrectType()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, WALL_CLOCK_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(1)));
        assertTrue(types.contains(WALL_CLOCK_TIME));
    }

    @Test
    void constructor_withTwoTypes_assignsCorrectTypes()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable, SYSTEM_TIME, USER_TIME);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(2)));
        assertTrue(types.containsAll(Arrays.asList(SYSTEM_TIME, USER_TIME)));
    }

    @Test
    void constructor_withoutType_assignsAllAvailableCounterTypes()
    {
        MonitoredCallable<String> op = new MonitoredCallable<>(callable);
        List<Type> types = op.getTypes();
        assertThat(types.size(), is(equalTo(Type.values().length)));
        assertTrue(types.containsAll(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME, USER_TIME, SYSTEM_TIME)));
    }

    /**
     * Tests that the elapsed time for a dummy {@link Callable} with no specific counter set
     * (all counters) is updated and the {@link Callable} result is retrieved
     *
     * @throws Exception in case of an exception inside the {@link Callable}
     */
    @Test
    void call_givenAllTypes_updatesAllCounters() throws Exception
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpects(systemUtils);
            assertThat(operation.call(), is(equalTo(STRING_CALLABLE_RETURN)));
        }
        assertAllUnitsBefore(operation, 0);
        assertAllUnitsAfter(operation, 0);
    }

    @Test
    void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printSummary(System.out);
            printUtils.verify(() -> PrintUtils.printSummary(operation.stopwatch, System.out, null), times(1));
        }
    }

    @Test
    void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable);
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            operation.printDetails(System.out);
            printUtils.verify(() -> PrintUtils.printDetails(operation.stopwatch, System.out, null), times(1));
        }
    }

    @Test()
    void reset_callsStopwatchReset() throws Exception
    {
        Stopwatch stopwatch = mock(Stopwatch.class);
        MonitoredCallable<String> operation = new MonitoredCallable<>(callable, WALL_CLOCK_TIME);
        operation.stopwatch = stopwatch;
        operation.reset();
        verify(stopwatch).reset();
    }

}
