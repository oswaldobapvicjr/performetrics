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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintUtils;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchTest
{
    private static final long WALL_CLOCK_TIME_BEFORE = 2000000000l;
    private static final long CPU_TIME_BEFORE = 1200000000l;
    private static final long USER_TIME_BEFORE = 1200000001l;
    private static final long SYSTEM_TIME_BEFORE = 1200000002l;

    private static final long WALL_CLOCK_TIME_AFTER = 3000000000l;
    private static final long CPU_TIME_AFTER = 1200000300l;
    private static final long USER_TIME_AFTER = 1200000201l;
    private static final long SYSTEM_TIME_AFTER = 1200000102l;

    private static final long WALL_CLOCK_TIME_AFTER_2 = 5000000000l;
    private static final long CPU_TIME_AFTER_2 = 1200000500l;
    private static final long USER_TIME_AFTER_2 = 1200000501l;
    private static final long SYSTEM_TIME_AFTER_2 = 1200000502l;

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

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter2(MockedStatic<SystemUtils> systemUtils)
    {
        systemUtils.when(SystemUtils::getWallClockTimeNanos).thenReturn(WALL_CLOCK_TIME_AFTER_2);
        systemUtils.when(SystemUtils::getCpuTimeNanos).thenReturn(CPU_TIME_AFTER_2);
        systemUtils.when(SystemUtils::getUserTimeNanos).thenReturn(USER_TIME_AFTER_2);
        systemUtils.when(SystemUtils::getSystemTimeNanos).thenReturn(SYSTEM_TIME_AFTER_2);
    }

    private Counter getFirstCounter(Stopwatch stopwatch, Type type)
    {
        return getCounter(stopwatch, type, 0);
    }

    private Counter getCounter(Stopwatch stopwatch, Type type, int index)
    {
        return stopwatch.getCounters(type).get(index);
    }

    /**
     * Checks that all units-before are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsBefore(Stopwatch stopwatch)
    {
        assertThat(getFirstCounter(stopwatch, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getFirstCounter(stopwatch, CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(getFirstCounter(stopwatch, USER_TIME).getUnitsBefore(), is(equalTo(USER_TIME_BEFORE)));
        assertThat(getFirstCounter(stopwatch, SYSTEM_TIME).getUnitsBefore(), is(equalTo(SYSTEM_TIME_BEFORE)));
    }

    /**
     * Checks that all units-after are equal to the test constants
     *
     * @param stopwatch the stopwatch to be evaluated
     */
    private void assertAllUnitsAfter(Stopwatch stopwatch)
    {
        assertThat(getFirstCounter(stopwatch, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(getFirstCounter(stopwatch, CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
        assertThat(getFirstCounter(stopwatch, USER_TIME).getUnitsAfter(), is(equalTo(USER_TIME_AFTER)));
        assertThat(getFirstCounter(stopwatch, SYSTEM_TIME).getUnitsAfter(), is(equalTo(SYSTEM_TIME_AFTER)));
    }

    /**
     * Checks that the stopwatch contains to timing session
     *
     * @param the stopwatch to be evaluated
     */
    private void assertTimingSessionsClear(Stopwatch stopwatch)
    {
        assertThat(stopwatch.getTimingSessions().size(), is(equalTo(0)));
    }

    /**
     * Checks that all of the known counters are available by default in a stopwatch
     */
    @Test
    public void constructor_noArguments_assignsAllAvailableTypes()
    {
        Stopwatch stopwatch = new Stopwatch();
        List<Type> types = stopwatch.getTypes();
        assertThat(types.size(), is(equalTo(Type.values().length)));
        assertTrue(types.containsAll(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME, SYSTEM_TIME, USER_TIME)));
    }

    /**
     * Checks that only the counter passed to the constructor will be maintained (one type)
     */
    @Test
    public void constructor_oneArgument_assignsCorrectCounter()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        List<Type> types = sw.getTypes();
        assertThat(types.size(), is(equalTo(1)));
        assertThat(types.get(0), is(equalTo(SYSTEM_TIME)));
    }

    /**
     * Checks that only the counters passed to the constructor will be maintained (two types)
     */
    @Test
    public void constructor_twoArguments_assignsCorrectCounters()
    {
        Stopwatch sw = new Stopwatch(CPU_TIME, USER_TIME);
        List<Type> types = sw.getTypes();
        assertThat(types.size(), is(equalTo(2)));
        assertTrue(types.containsAll(Arrays.asList(CPU_TIME, USER_TIME)));
    }

    @Test
    public void getAllCounters_withStopwatchUnstarted_emptyList()
    {
        Stopwatch sw = new Stopwatch();
        assertThat(sw.isStarted(), is(equalTo(false)));
        assertTimingSessionsClear(sw);
    }

    /**
     * Checks that all units-before are duly set upon creation of an started stopwatch with
     * default types. All units-after shall still be equal to zero.
     */
    @Test
    public void createdStarted_noArguments_assignsAllAvailableTypesWithAllUnitsBeforeSetAndUnitsAfterUnset()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw = Stopwatch.createStarted();
        }
        assertThat(sw.getCounters().size(), is(equalTo(Type.values().length)));
        assertThat(sw.isStarted(), is(equalTo(true)));
        assertAllUnitsBefore(sw);
    }

    /**
     * Checks that units-before is duly set upon creation of an started stopwatch with a
     * specific counter type. The units-after shall still be equal to zero.
     */
    @Test
    public void createdStarted_withOneType_assignsSpecificTimeWithAllUnitsBeforeSetAndUnitsAfterUnset()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw = Stopwatch.createStarted(WALL_CLOCK_TIME);
        }
        assertThat(sw.isStarted(), is(equalTo(true)));
        assertThat(sw.getCounters().size(), is(equalTo(1)));
        assertThat(getFirstCounter(sw, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getFirstCounter(sw, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(0L)));
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch with default
     * counters.
     */
    @Test
    public void stop_withAllAvailableTypes_updatesAllUnitsAfterAccordingly()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            sw = new Stopwatch();
            setupExpectsBefore(systemUtils);
            sw.start();
            assertThat(sw.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            sw.stop();
        }
        assertThat(sw.isStarted(), is(false));
        assertAllUnitsBefore(sw);
        assertAllUnitsAfter(sw);
    }

    /**
     * Checks that all units are duly set after starting and stopping a stopwatch with
     * specific counters
     */
    @Test
    public void stop_withTwoTypes_updatesAllUnitsAfterAccordingly()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            sw = new Stopwatch(WALL_CLOCK_TIME, CPU_TIME);
            setupExpectsBefore(systemUtils);
            sw.start();
            assertThat(sw.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            sw.stop();
        }
        assertThat(sw.getCounters().size(), is(equalTo(2)));
        assertThat(sw.isStarted(), is(false));
        assertThat(getFirstCounter(sw, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getFirstCounter(sw, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(getFirstCounter(sw, CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(getFirstCounter(sw, CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
    }

    @Test
    public void reset_withAllAvailableTypes_cleansAllSessions()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw = Stopwatch.createStarted();
            assertThat(sw.isStarted(), is(true));
            setupExpectsAfter(systemUtils);
            sw.stop();
            assertThat(sw.isStarted(), is(false));
        }
        sw.reset();
        assertThat(sw.getCounters(), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void printSummary_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            sw.printSummary(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printSummary(sw, System.out));
        }
    }

    @Test
    public void printDetails_withPrintWriterArgument_callsCorrectPrintUtilMethod()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<PrintUtils> printUtils = mockStatic(PrintUtils.class))
        {
            sw.printDetails(System.out);
            printUtils.verify(times(1), () -> PrintUtils.printDetails(sw, System.out));
        }
    }

    @Test
    public void getCounters_noSession_emptyList()
    {
        Stopwatch sw = new Stopwatch(CPU_TIME, SYSTEM_TIME);
        assertThat(sw.getCounters(CPU_TIME), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void getCounters_singleTypeAndSingleSession_singletonList()
    {
        Stopwatch sw = new Stopwatch(WALL_CLOCK_TIME);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);
            sw.stop();
        }
        List<Counter> counters = sw.getCounters(WALL_CLOCK_TIME);
        assertThat(counters.size(), is(equalTo(1)));
    }

    @Test
    public void getCounters_singleTypeAndTwoSessions_twoCountersOfSameType()
    {
        Stopwatch sw = new Stopwatch(WALL_CLOCK_TIME);
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);
            sw.stop();
            sw.start();
        }
        List<Counter> counters = sw.getCounters(WALL_CLOCK_TIME);
        assertThat(counters.size(), is(equalTo(2)));
        assertThat(counters.get(0).getType(), is(equalTo(WALL_CLOCK_TIME)));
        assertThat(counters.get(1).getType(), is(equalTo(WALL_CLOCK_TIME)));
    }

    @Test
    public void getCounters_multipleTypesAndTwoSessions_twoCountersOfSameType()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);
            sw.stop();
            sw.start();
        }
        List<Counter> counters = sw.getCounters(WALL_CLOCK_TIME);
        assertThat(counters.size(), is(equalTo(2)));
        assertThat(counters.get(0).getType(), is(equalTo(WALL_CLOCK_TIME)));
        assertThat(counters.get(1).getType(), is(equalTo(WALL_CLOCK_TIME)));
    }

    @Test
    public void start_ready_createsNewTimingSessionWithProperTypes()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw = Stopwatch.createStarted(SYSTEM_TIME);
        }
        List<TimingSession> sessions = sw.getTimingSessions();
        assertThat(sessions.size(), is(equalTo(1)));
        List<Type> sessionTypes = Arrays.asList(sessions.get(0).getTypes());
        assertThat(sessionTypes.size(), is(equalTo(1)));
        assertTrue(sessionTypes.contains(SYSTEM_TIME));
        assertTrue(sw.isStarted());
    }

    @Test
    public void start_alreadyStarted_createsNewTimingSession()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw = Stopwatch.createStarted(WALL_CLOCK_TIME);
            assertThat(sw.getTimingSessions().size(), is(equalTo(1)));
            assertTrue(sw.isStarted());
            setupExpectsAfter(systemUtils);
            sw.start();
        }
        assertThat(sw.getTimingSessions().size(), is(equalTo(2)));
        assertTrue(sw.isStarted());
    }

    @Test
    public void start_stopped_timingSessionsIncreased()
    {
        Stopwatch sw;
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            sw = Stopwatch.createStarted();
        }
        assertThat(sw.getTimingSessions().size(), is(equalTo(1)));
        sw.stop();
        sw.start();
        assertThat(sw.getTimingSessions().size(), is(equalTo(2)));
    }

    @Test(expected = IllegalStateException.class)
    public void stop_notStarted_illegalStateException()
    {
        Stopwatch sw = new Stopwatch();
        sw.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_alreadyStopped_illegalStateException()
    {
        Stopwatch sw = Stopwatch.createStarted();
        sw.stop();
        sw.stop();
    }

    public void elapsedTime_invalidType_zero()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        assertThat(sw.elapsedTime(USER_TIME), is(equalTo(Duration.ZERO)));
    }

    public void elapsedTime_invalidTypeAndValidTimeUnit_throwsException()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        assertThat(sw.elapsedTime(USER_TIME, HOURS), is(equalTo(0.0)));
    }

    public void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_zero()
    {
        Stopwatch sw = new Stopwatch(SYSTEM_TIME);
        assertThat(sw.elapsedTime(USER_TIME, HOURS, FAST), is(equalTo(0.0)));
    }

    @Test()
    public void elapsedTime_validType_returnsValidDurations()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);

            assertThat(sw.elapsedTime(WALL_CLOCK_TIME),
                    is(equalTo(Duration.of(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE, NANOSECONDS))));
            assertThat(sw.elapsedTime(CPU_TIME),
                    is(equalTo(Duration.of(CPU_TIME_AFTER - CPU_TIME_BEFORE, NANOSECONDS))));
            assertThat(sw.elapsedTime(USER_TIME),
                    is(equalTo(Duration.of(USER_TIME_AFTER - USER_TIME_BEFORE, NANOSECONDS))));
            assertThat(sw.elapsedTime(SYSTEM_TIME),
                    is(equalTo(Duration.of(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE, NANOSECONDS))));
        }
    }

    @Test()
    public void elapsedTime_validTypeAndTwoSessions_returnsSumOfDurations()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            // First session
            setupExpectsBefore(systemUtils);
            sw.start();

            setupExpectsAfter(systemUtils);
            sw.stop();

            // 2nd session - let the "before" values be the same "after" values of the previous
            // sessions
            sw.start();

            setupExpectsAfter2(systemUtils);
            sw.stop();
        }

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME),
                is(equalTo(
                    Duration.of(
                            (WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE)
                            + (WALL_CLOCK_TIME_AFTER_2 - WALL_CLOCK_TIME_AFTER), NANOSECONDS))));
        assertThat(sw.elapsedTime(CPU_TIME),
                is(equalTo(
                    Duration.of(
                            (CPU_TIME_AFTER - CPU_TIME_BEFORE)
                            + (CPU_TIME_AFTER_2 - CPU_TIME_AFTER), NANOSECONDS))));
        assertThat(sw.elapsedTime(USER_TIME),
                is(equalTo(
                    Duration.of(
                            (USER_TIME_AFTER - USER_TIME_BEFORE)
                            + (USER_TIME_AFTER_2 - USER_TIME_AFTER), NANOSECONDS))));
        assertThat(sw.elapsedTime(SYSTEM_TIME),
                is(equalTo(
                    Duration.of(
                            (SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE)
                            + (SYSTEM_TIME_AFTER_2 - SYSTEM_TIME_AFTER), NANOSECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounters()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);

            assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS),
                    is(equalTo(getFirstCounter(sw, WALL_CLOCK_TIME).elapsedTime(SECONDS))));
            assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS),
                    is(equalTo(getFirstCounter(sw, CPU_TIME).elapsedTime(MILLISECONDS))));
            assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS),
                    is(equalTo(getFirstCounter(sw, USER_TIME).elapsedTime(NANOSECONDS))));
            assertThat(sw.elapsedTime(SYSTEM_TIME, SECONDS),
                    is(equalTo(getFirstCounter(sw, SYSTEM_TIME).elapsedTime(SECONDS))));
        }
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndTwoSessions_sumCountersCorrectly()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            // First session
            setupExpectsBefore(systemUtils);
            sw.start();

            setupExpectsAfter(systemUtils);
            sw.stop();

            // 2nd session - let the "before" values be the same "after" values of the previous
            // sessions
            sw.start();

            setupExpectsAfter2(systemUtils);
            sw.stop();
        }

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS),
                is(equalTo(getCounter(sw, WALL_CLOCK_TIME, 0).elapsedTime(SECONDS)
                        + getCounter(sw, WALL_CLOCK_TIME, 1).elapsedTime(SECONDS))));
        assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS),
                is(equalTo(getCounter(sw, CPU_TIME, 0).elapsedTime(MILLISECONDS)
                        + getCounter(sw, CPU_TIME, 1).elapsedTime(MILLISECONDS))));
        assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS),
                is(equalTo(getCounter(sw, USER_TIME, 0).elapsedTime(NANOSECONDS)
                        + getCounter(sw, USER_TIME, 1).elapsedTime(NANOSECONDS))));
        assertThat(sw.elapsedTime(SYSTEM_TIME, SECONDS),
                is(equalTo(getCounter(sw, SYSTEM_TIME, 0).elapsedTime(SECONDS)
                        + getCounter(sw, SYSTEM_TIME, 1).elapsedTime(SECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounters()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
            setupExpectsAfter(systemUtils);

            assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS, FAST),
                    is(equalTo(getFirstCounter(sw, WALL_CLOCK_TIME).elapsedTime(SECONDS, FAST))));
            assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS, DOUBLE_PRECISION),
                    is(equalTo(getFirstCounter(sw, CPU_TIME).elapsedTime(MILLISECONDS, DOUBLE_PRECISION))));
            assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS, FAST),
                    is(equalTo(getFirstCounter(sw, USER_TIME).elapsedTime(NANOSECONDS, FAST))));
            assertThat(sw.elapsedTime(SYSTEM_TIME, HOURS, DOUBLE_PRECISION),
                    is(equalTo(getFirstCounter(sw, SYSTEM_TIME).elapsedTime(HOURS, DOUBLE_PRECISION))));
        }
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndConversionModeAndTwoSessions_sumsCorrectly()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            // First session
            setupExpectsBefore(systemUtils);
            sw.start();

            setupExpectsAfter(systemUtils);
            sw.stop();

            // 2nd session - let the "before" values be the same "after" values of the previous
            // sessions
            sw.start();

            setupExpectsAfter2(systemUtils);
            sw.stop();
        }

        assertThat(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS, FAST),
                is(equalTo(getCounter(sw, WALL_CLOCK_TIME, 0).elapsedTime(SECONDS, FAST)
                        + getCounter(sw, WALL_CLOCK_TIME, 1).elapsedTime(SECONDS, FAST))));
        assertThat(sw.elapsedTime(CPU_TIME, MILLISECONDS, DOUBLE_PRECISION),
                is(equalTo(getCounter(sw, CPU_TIME, 0).elapsedTime(MILLISECONDS, DOUBLE_PRECISION)
                        + getCounter(sw, CPU_TIME, 1).elapsedTime(MILLISECONDS, DOUBLE_PRECISION))));
        assertThat(sw.elapsedTime(USER_TIME, NANOSECONDS, FAST),
                is(equalTo(getCounter(sw, USER_TIME, 0).elapsedTime(NANOSECONDS, FAST)
                        + getCounter(sw, USER_TIME, 1).elapsedTime(NANOSECONDS, FAST))));
        assertThat(sw.elapsedTime(SYSTEM_TIME, HOURS, DOUBLE_PRECISION),
                is(equalTo(getCounter(sw, SYSTEM_TIME, 0).elapsedTime(HOURS, DOUBLE_PRECISION)
                        + getCounter(sw, SYSTEM_TIME, 1).elapsedTime(HOURS, DOUBLE_PRECISION))));
    }

    @Test
    public void getCurrentTimingSession_unstarted_empty()
    {
        Stopwatch sw = new Stopwatch();
        assertThat(sw.getCurrentTimingSession().isPresent(), is(equalTo(false)));
    }

    @Test
    public void getCurrentTimingSession_oneSession_success()
    {
        Stopwatch sw = new Stopwatch();
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            setupExpectsBefore(systemUtils);
            sw.start();
        }
        assertThat(sw.getCurrentTimingSession().get(), is(notNullValue()));
    }

}
