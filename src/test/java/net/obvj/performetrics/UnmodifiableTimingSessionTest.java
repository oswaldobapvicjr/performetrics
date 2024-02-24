/*
 * Copyright 2023 obvj.net
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

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.SYSTEM_TIME;
import static net.obvj.performetrics.Counter.Type.USER_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.util.Collection;

import static java.util.Arrays.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Unit tests for the {@link UnmodifiableTimingSession} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.3.0
 */
class UnmodifiableTimingSessionTest
{
    private static final long WALL_CLOCK_TIME_BEFORE = 2000000000l;
    private static final long CPU_TIME_BEFORE = 1200000000l;
    private static final long USER_TIME_BEFORE = 1200000001l;
    private static final long SYSTEM_TIME_BEFORE = 1200000002l;

    private static final long WALL_CLOCK_TIME_AFTER = 3000000000l;
    private static final long CPU_TIME_AFTER = 1200000300l;
    private static final long USER_TIME_AFTER = 1200000201l;
    private static final long SYSTEM_TIME_AFTER = 1200000102l;

    /**
     * Setup the expects on PerformetricUtils mock with "_BEFORE" constant values
     */
    private void setupExpectsBefore(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(WALL_CLOCK_TIME_BEFORE);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(CPU_TIME_BEFORE);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(USER_TIME_BEFORE);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(SYSTEM_TIME_BEFORE);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(WALL_CLOCK_TIME_AFTER);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(CPU_TIME_AFTER);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(USER_TIME_AFTER);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(SYSTEM_TIME_AFTER);
    }

    private Counter getCounter(TimingSession timingSession, Type type)
    {
        return timingSession.getCounter(type);
    }

    @Test
    void getAllCounters_allCounters_unmodifiableWithSameValueAsOriginalObject()
    {
        TimingSession original = new TimingSession(Performetrics.ALL_TYPES);
        TimingSession unmodifiable = new UnmodifiableTimingSession(original);
        assertThat(unmodifiable.getTypes(), equalTo(original.getTypes()));
        assertThat(unmodifiable.getCounters().toString(),
                equalTo(original.getCounters().toString()));

        // Modification on counters is not allowed too
        assertModificationNotAllowed(unmodifiable.getCounters());
    }

    @Test
    void getAllCounters_customCounters_unmodifiableWithSameValueAsOriginalObject()
    {
        TimingSession original = new TimingSession(asList(USER_TIME));
        TimingSession unmodifiable = new UnmodifiableTimingSession(original);
        assertThat(unmodifiable.getTypes(), equalTo(original.getTypes()));
        assertThat(unmodifiable.getCounters().toString(),
                equalTo(original.getCounters().toString()));

        // Modification on counters is not allowed too
        assertModificationNotAllowed(unmodifiable.getCounters());
    }

    @Test
    void getCounter_unmodifiableWitSameValueAsOriginalObject()
    {
        TimingSession original = new TimingSession(Performetrics.ALL_TYPES);
        TimingSession unmodifiable = new UnmodifiableTimingSession(original);
        assertThat(unmodifiable.getCounter(Type.WALL_CLOCK_TIME).toString(),
                equalTo(original.getCounter(Type.WALL_CLOCK_TIME).toString()));

        // Modification on counters is not allowed too
        assertModificationNotAllowed(unmodifiable.getCounter(Type.WALL_CLOCK_TIME));
    }

    private void assertModificationNotAllowed(Collection<Counter> counters)
    {
        counters.forEach(this::assertModificationNotAllowed);
    }

    private void assertModificationNotAllowed(Counter counter)
    {
        assertThat(() -> counter.setUnitsBefore(CPU_TIME_AFTER),
                throwsException(UnsupportedOperationException.class));
    }

    @Test
    void start_unsupportedOperation()
    {
        TimingSession unmodifiable = new UnmodifiableTimingSession(
                new TimingSession(asList(USER_TIME)));
        assertThat(() -> unmodifiable.start(),
                throwsException(UnsupportedOperationException.class));
    }

    @Test
    void stop_unsupportedOperation()
    {
        TimingSession unmodifiable = new UnmodifiableTimingSession(
                new TimingSession(asList(USER_TIME)));
        assertThat(() -> unmodifiable.stop(),
                throwsException(UnsupportedOperationException.class));
    }

    @Test
    void reset_unsupportedOperation()
    {
        TimingSession unmodifiable = new UnmodifiableTimingSession(
                new TimingSession(asList(USER_TIME)));
        assertThat(() -> unmodifiable.reset(),
                throwsException(UnsupportedOperationException.class));
    }

    @Test
    void getCounter_invalidType_throwsException()
    {
        TimingSession session = new UnmodifiableTimingSession(
                new TimingSession(asList(CPU_TIME, SYSTEM_TIME)));
        assertThrows(IllegalArgumentException.class, () -> getCounter(session, USER_TIME));
    }

    @Test()
    void elapsedTime_validType_returnsValidDurations()
    {
        TimingSession original = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            original.start();
            setupExpectsAfter(systemUtils);

            TimingSession session = new UnmodifiableTimingSession(original);
            assertThat(session.elapsedTime(WALL_CLOCK_TIME),
                    is(equalTo(Duration.of(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE, NANOSECONDS))));
            assertThat(session.elapsedTime(CPU_TIME),
                    is(equalTo(Duration.of(CPU_TIME_AFTER - CPU_TIME_BEFORE, NANOSECONDS))));
            assertThat(session.elapsedTime(USER_TIME),
                    is(equalTo(Duration.of(USER_TIME_AFTER - USER_TIME_BEFORE, NANOSECONDS))));
            assertThat(session.elapsedTime(SYSTEM_TIME),
                    is(equalTo(Duration.of(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE, NANOSECONDS))));
        }
    }

    @Test()
    void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounters()
    {
        TimingSession original = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            original.start();
            setupExpectsAfter(systemUtils);

            TimingSession session = new UnmodifiableTimingSession(original);
            assertThat(session.elapsedTime(WALL_CLOCK_TIME, SECONDS),
                    is(equalTo(getCounter(session, WALL_CLOCK_TIME).elapsedTime(SECONDS))));
            assertThat(session.elapsedTime(CPU_TIME, MILLISECONDS),
                    is(equalTo(getCounter(session, CPU_TIME).elapsedTime(MILLISECONDS))));
            assertThat(session.elapsedTime(USER_TIME, NANOSECONDS),
                    is(equalTo(getCounter(session, USER_TIME).elapsedTime(NANOSECONDS))));
            assertThat(session.elapsedTime(SYSTEM_TIME, SECONDS),
                    is(equalTo(getCounter(session, SYSTEM_TIME).elapsedTime(SECONDS))));
        }
    }

    @Test()
    void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounters()
    {
        TimingSession original = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            original.start();
            setupExpectsAfter(systemUtils);

            TimingSession session = new UnmodifiableTimingSession(original);
            assertThat(session.elapsedTime(WALL_CLOCK_TIME, SECONDS, FAST),
                    is(equalTo(getCounter(session, WALL_CLOCK_TIME).elapsedTime(SECONDS, FAST))));
            assertThat(session.elapsedTime(CPU_TIME, MILLISECONDS, DOUBLE_PRECISION),
                    is(equalTo(getCounter(session, CPU_TIME).elapsedTime(MILLISECONDS, DOUBLE_PRECISION))));
            assertThat(session.elapsedTime(USER_TIME, NANOSECONDS, FAST),
                    is(equalTo(getCounter(session, USER_TIME).elapsedTime(NANOSECONDS, FAST))));
            assertThat(session.elapsedTime(SYSTEM_TIME, HOURS, DOUBLE_PRECISION),
                    is(equalTo(getCounter(session, SYSTEM_TIME).elapsedTime(HOURS, DOUBLE_PRECISION))));
        }
    }

}
