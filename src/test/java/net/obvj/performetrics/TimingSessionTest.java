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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static java.util.Arrays.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Unit tests for the {@link TimingSession} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
class TimingSessionTest
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

    private void assertAllUnitsBefore(TimingSession session)
    {
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getCounter(session, CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(getCounter(session, USER_TIME).getUnitsBefore(), is(equalTo(USER_TIME_BEFORE)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsBefore(), is(equalTo(SYSTEM_TIME_BEFORE)));
    }

    private void assertAllUnitsAfter(TimingSession session)
    {
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(getCounter(session, CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
        assertThat(getCounter(session, USER_TIME).getUnitsAfter(), is(equalTo(USER_TIME_AFTER)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsAfter(), is(equalTo(SYSTEM_TIME_AFTER)));
    }

    private void assertAllUnitsBeforeEqualZero(TimingSession session)
    {
        for (Counter c : session.getCounters())
            assertThat("For the counter of type: " + c.getType(), c.getUnitsBefore(), is(equalTo(0L)));
    }

    /**
     * Checks that all units-after are equal to zero for the given TimingSession
     *
     * @param session the TimingSession to be evaluated
     */

    private void assertAllUnitsAfterEqualZero(TimingSession session)
    {
        for (Counter c : session.getCounters())
            assertThat("For the counter of type:" + c.getType(), c.getUnitsAfter(), is(equalTo(0L)));
    }

    /**
     * Checks that only the counter passed to the constructor will be maintained (one type)
     */
    @Test
    void constructor_withOneArgument_assignsCorrectCounter()
    {
        TimingSession session = new TimingSession(asList(SYSTEM_TIME));
        assertThat(session.getCounters().size(), is(equalTo(1)));
        assertNotNull(getCounter(session, SYSTEM_TIME), "System-time counter not set");
    }

    /**
     * Checks that only the counters passed to the constructor will be maintained (two types)
     */
    @Test
    void constructor_withTwoArguments_assignsCorrectCounters()
    {
        TimingSession session = new TimingSession(asList(CPU_TIME, USER_TIME));
        assertThat(session.getCounters().size(), is(equalTo(2)));
        assertNotNull(getCounter(session, CPU_TIME), "CPU-time counter not set");
        assertNotNull(getCounter(session, USER_TIME), "User-time counter not set");
    }

    /**
     * Checks that all units are equal to zero when a new TimingSession is created
     */
    @Test
    void getAllCounters_withTimingSessionUnstarted_returnsAllUnitsEqualToZero()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        assertThat(session.isStarted(), is(equalTo(false)));
        assertAllUnitsBeforeEqualZero(session);
        assertAllUnitsAfterEqualZero(session);
    }

    /**
     * Checks that all units are duly set after starting and stopping a TimingSession with
     * default counters.
     */
    @Test
    void stop_withAllAvailableTypes_updatesAllUnitsAfterAccordingly()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        assertThat(session.getCounters().size(), is(equalTo(Type.values().length)));
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            assertThat(session.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            session.stop();
        }
        assertThat(session.isStarted(), is(false));
        assertAllUnitsBefore(session);
        assertAllUnitsAfter(session);
    }

    /**
     * Checks that all units are duly set after starting and stopping a TimingSession with
     * specific counters
     */
    @Test
    void stop_withTwoTypes_updatesAllUnitsAfterAccordingly()
    {
        TimingSession session = new TimingSession(asList(WALL_CLOCK_TIME, CPU_TIME));
        assertThat(session.getCounters().size(), is(equalTo(2)));
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            assertThat(session.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            session.stop();
        }
        assertThat(session.isStarted(), is(false));
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(getCounter(session, CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(getCounter(session, CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used TimingSession
     * created with specific counters
     */
    @Test
    void reset_withTwoTypes_setsAllUnitsToZeroAccordingly()
    {
        TimingSession session = new TimingSession(asList(USER_TIME, SYSTEM_TIME));
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            session.start();
            setupExpectsBefore(systemUtils);
            assertThat(session.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            session.stop();
        }
        assertThat(session.isStarted(), is(false));
        session.reset();
        assertThat(session.isStarted(), is(false));
        assertThat(session.getCounters().size(), is(equalTo(2)));
        assertThat(getCounter(session, USER_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(getCounter(session, USER_TIME).getUnitsAfter(), is(equalTo(0L)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsAfter(), is(equalTo(0L)));
    }

    @Test
    void getCounter_invalidType_throwsException()
    {
        TimingSession session = new TimingSession(asList(CPU_TIME, SYSTEM_TIME));
        assertThrows(IllegalArgumentException.class, () -> getCounter(session, USER_TIME));
    }

    @Test
    void start_alreadyStarted_illegalStateException()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
        }
        assertThrows(IllegalStateException.class, () -> session.start());
    }

    @Test
    void start_stopped_illegalStateException()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            setupExpectsAfter(systemUtils);
            session.stop();
        }
        assertThrows(IllegalStateException.class, () -> session.start());
    }

    @Test
    void stop_notStarted_illegalStateException()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        assertThrows(IllegalStateException.class, () -> session.stop());
    }

    @Test
    void stop_alreadyStopped_illegalStateException()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            setupExpectsAfter(systemUtils);
            session.stop();
        }
        assertThrows(IllegalStateException.class, () -> session.stop());
    }

    @Test
    void elapsedTime_invalidType_throwsException()
    {
        TimingSession session = new TimingSession(asList(SYSTEM_TIME));
        assertThrows(IllegalArgumentException.class, () -> session.elapsedTime(USER_TIME));
    }

    @Test
    void elapsedTime_invalidTypeAndValidTimeUnit_throwsException()
    {
        TimingSession session = new TimingSession(asList(SYSTEM_TIME));
        assertThrows(IllegalArgumentException.class, () -> session.elapsedTime(USER_TIME, HOURS));
    }

    @Test
    void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_throwsException()
    {
        TimingSession session = new TimingSession(asList(SYSTEM_TIME));
        assertThrows(IllegalArgumentException.class, () -> session.elapsedTime(USER_TIME, HOURS, FAST));
    }

    @Test()
    void elapsedTime_validType_returnsValidDurations()
    {
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            setupExpectsAfter(systemUtils);

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
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            setupExpectsAfter(systemUtils);

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
        TimingSession session = new TimingSession(Performetrics.ALL_TYPES);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            session.start();
            setupExpectsAfter(systemUtils);

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
