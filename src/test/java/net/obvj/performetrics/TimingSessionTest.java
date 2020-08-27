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
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemUtils.class)
public class TimingSessionTest
{
    private static final long WALL_CLOCK_TIME_BEFORE = 2000000000l;
    private static final long CPU_TIME_BEFORE = 1200000000l;
    private static final long USER_TIME_BEFORE = 1200000001l;
    private static final long SYSTEM_TIME_BEFORE = 1200000002l;

    private static final long WALL_CLOCK_TIME_AFTER = 3000000000l;
    private static final long CPU_TIME_AFTER = 1200000300l;
    private static final long USER_TIME_AFTER = 1200000201l;
    private static final long SYSTEM_TIME_AFTER = 1200000102l;

    @Before
    public void setup()
    {
        mockStatic(SystemUtils.class);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_BEFORE" constant values
     */
    private void setupExpectsBefore()
    {
        given(SystemUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_BEFORE);
        given(SystemUtils.getCpuTimeNanos()).willReturn(CPU_TIME_BEFORE);
        given(SystemUtils.getUserTimeNanos()).willReturn(USER_TIME_BEFORE);
        given(SystemUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_BEFORE);
    }

    /**
     * Setup the expects on PerformetricUtils mock with "_AFTER" constant values
     */
    private void setupExpectsAfter()
    {
        given(SystemUtils.getWallClockTimeNanos()).willReturn(WALL_CLOCK_TIME_AFTER);
        given(SystemUtils.getCpuTimeNanos()).willReturn(CPU_TIME_AFTER);
        given(SystemUtils.getUserTimeNanos()).willReturn(USER_TIME_AFTER);
        given(SystemUtils.getSystemTimeNanos()).willReturn(SYSTEM_TIME_AFTER);
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
     * Checks that all of the known counters are available by default in a TimingSession
     */
    @Test
    public void constructor_withNoArguments_assignsAllAvailableTypes()
    {
        TimingSession session = new TimingSession();
        assertThat(session.getCounters().size(), is(equalTo(Type.values().length)));
        assertNotNull("Wall-clock-time counter should not be null", getCounter(session, WALL_CLOCK_TIME));
        assertNotNull("CPU-time counter should not be null", getCounter(session, CPU_TIME));
        assertNotNull("User-time counter should not be null", getCounter(session, USER_TIME));
        assertNotNull("System-time counter should not be null", getCounter(session, SYSTEM_TIME));
    }

    /**
     * Checks that only the counter passed to the constructor will be maintained (one type)
     */
    @Test
    public void constructor_withOneArgument_assignsCorrectCounter()
    {
        TimingSession session = new TimingSession(SYSTEM_TIME);
        assertThat(session.getCounters().size(), is(equalTo(1)));
        assertNotNull("System-time counter not set", getCounter(session, SYSTEM_TIME));
    }

    /**
     * Checks that only the counters passed to the constructor will be maintained (two types)
     */
    @Test
    public void constructor_withTwoArguments_assignsCorrectCounters()
    {
        TimingSession session = new TimingSession(CPU_TIME, USER_TIME);
        assertThat(session.getCounters().size(), is(equalTo(2)));
        assertNotNull("CPU-time counter not set", getCounter(session, CPU_TIME));
        assertNotNull("User-time counter not set", getCounter(session, USER_TIME));
    }

    /**
     * Checks that all units are equal to zero when a new TimingSession is created
     */
    @Test
    public void getAllCounters_withTimingSessionUnstarted_returnsAllUnitsEqualToZero()
    {
        TimingSession session = new TimingSession();
        assertThat(session.isStarted(), is(equalTo(false)));
        assertAllUnitsBeforeEqualZero(session);
        assertAllUnitsAfterEqualZero(session);
    }

    /**
     * Checks that all units are duly set after starting and stopping a TimingSession with
     * default counters.
     */
    @Test
    public void stop_withAllAvailableTypes_updatesAllUnitsAfterAccordingly()
    {
        TimingSession session = new TimingSession();
        assertThat(session.getCounters().size(), is(equalTo(Type.values().length)));
        setupExpectsBefore();
        session.start();
        assertThat(session.isStarted(), is(true));
        setupExpectsAfter();
        session.stop();
        assertThat(session.isStarted(), is(false));
        assertAllUnitsBefore(session);
        assertAllUnitsAfter(session);
    }

    /**
     * Checks that all units are duly set after starting and stopping a TimingSession with
     * specific counters
     */
    @Test
    public void stop_withTwoTypes_updatesAllUnitsAfterAccordingly()
    {
        TimingSession session = new TimingSession(WALL_CLOCK_TIME, CPU_TIME);
        assertThat(session.getCounters().size(), is(equalTo(2)));
        setupExpectsBefore();
        session.start();
        assertThat(session.isStarted(), is(true));
        setupExpectsAfter();
        session.stop();
        assertThat(session.isStarted(), is(false));
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsBefore(), is(equalTo(WALL_CLOCK_TIME_BEFORE)));
        assertThat(getCounter(session, WALL_CLOCK_TIME).getUnitsAfter(), is(equalTo(WALL_CLOCK_TIME_AFTER)));
        assertThat(getCounter(session, CPU_TIME).getUnitsBefore(), is(equalTo(CPU_TIME_BEFORE)));
        assertThat(getCounter(session, CPU_TIME).getUnitsAfter(), is(equalTo(CPU_TIME_AFTER)));
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used TimingSession
     * created with default counters
     */
    @Test
    public void reset_withAllAvailableTypes_setsAllUnitsToZeroAccordingly()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        assertThat(session.isStarted(), is(true));
        setupExpectsAfter();
        session.stop();
        assertThat(session.isStarted(), is(false));
        session.reset();
        assertThat(session.getCounters().size(), is(equalTo(Type.values().length)));
        assertAllUnitsBeforeEqualZero(session);
        assertAllUnitsAfterEqualZero(session);
    }

    /**
     * Checks that all units are reset to zero after calling reset() on an used TimingSession
     * created with specific counters
     */
    @Test
    public void reset_withTwoTypes_setsAllUnitsToZeroAccordingly()
    {
        TimingSession session = new TimingSession(USER_TIME, SYSTEM_TIME);
        session.start();
        setupExpectsBefore();
        assertThat(session.isStarted(), is(true));
        setupExpectsAfter();
        session.stop();
        assertThat(session.isStarted(), is(false));
        session.reset();
        assertThat(session.isStarted(), is(false));
        assertThat(session.getCounters().size(), is(equalTo(2)));
        assertThat(getCounter(session, USER_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(getCounter(session, USER_TIME).getUnitsAfter(), is(equalTo(0L)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsBefore(), is(equalTo(0L)));
        assertThat(getCounter(session, SYSTEM_TIME).getUnitsAfter(), is(equalTo(0L)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCounter_invalidType_throwsException()
    {
        TimingSession session = new TimingSession(CPU_TIME, SYSTEM_TIME);
        getCounter(session, USER_TIME);
    }

    @Test(expected = IllegalStateException.class)
    public void start_alreadyStarted_illegalStateException()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        session.start();
    }

    @Test(expected = IllegalStateException.class)
    public void start_stopped_illegalStateException()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        setupExpectsAfter();
        session.stop();
        session.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_notStarted_illegalStateException()
    {
        TimingSession session = new TimingSession();
        session.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_alreadyStopped_illegalStateException()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        setupExpectsAfter();
        session.stop();
        session.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidType_throwsException()
    {
        TimingSession session = new TimingSession(SYSTEM_TIME);
        session.elapsedTime(USER_TIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnit_throwsException()
    {
        TimingSession session = new TimingSession(SYSTEM_TIME);
        session.elapsedTime(USER_TIME, HOURS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elapsedTime_invalidTypeAndValidTimeUnitAndConversionMode_throwsException()
    {
        TimingSession session = new TimingSession(SYSTEM_TIME);
        session.elapsedTime(USER_TIME, HOURS, FAST);
    }

    @Test()
    public void elapsedTime_validType_returnsValidDurations()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        setupExpectsAfter();

        assertThat(session.elapsedTime(WALL_CLOCK_TIME),
                is(equalTo(Duration.of(WALL_CLOCK_TIME_AFTER - WALL_CLOCK_TIME_BEFORE, NANOSECONDS))));
        assertThat(session.elapsedTime(CPU_TIME),
                is(equalTo(Duration.of(CPU_TIME_AFTER - CPU_TIME_BEFORE, NANOSECONDS))));
        assertThat(session.elapsedTime(USER_TIME),
                is(equalTo(Duration.of(USER_TIME_AFTER - USER_TIME_BEFORE, NANOSECONDS))));
        assertThat(session.elapsedTime(SYSTEM_TIME),
                is(equalTo(Duration.of(SYSTEM_TIME_AFTER - SYSTEM_TIME_BEFORE, NANOSECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnit_callsCorrectElapsedTimeFromCounters()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        setupExpectsAfter();

        assertThat(session.elapsedTime(WALL_CLOCK_TIME, SECONDS),
                is(equalTo(getCounter(session, WALL_CLOCK_TIME).elapsedTime(SECONDS))));
        assertThat(session.elapsedTime(CPU_TIME, MILLISECONDS),
                is(equalTo(getCounter(session, CPU_TIME).elapsedTime(MILLISECONDS))));
        assertThat(session.elapsedTime(USER_TIME, NANOSECONDS),
                is(equalTo(getCounter(session, USER_TIME).elapsedTime(NANOSECONDS))));
        assertThat(session.elapsedTime(SYSTEM_TIME, SECONDS),
                is(equalTo(getCounter(session, SYSTEM_TIME).elapsedTime(SECONDS))));
    }

    @Test()
    public void elapsedTime_validTypeAndTimeUnitAndConversionMode_callsCorrectElapsedTimeFromCounters()
    {
        TimingSession session = new TimingSession();
        setupExpectsBefore();
        session.start();
        setupExpectsAfter();

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
