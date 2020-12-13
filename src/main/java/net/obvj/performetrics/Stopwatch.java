package net.obvj.performetrics;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * <p>
 * A convenient timing object that supports multiple counter types and timing sessions.
 * </p>
 *
 * <p>
 * Use the default constructor, with no arguments, to create a stopwatch with all of the
 * available metrics. If required, an additional constructor may be used to provide a
 * stopwatch with specific counters. E.g.:
 * </p>
 *
 * <pre>
 * new Stopwatch(); // provides all available counter types
 * new Stopwatch(Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
 * new Stopwatch(Counter.Type.CPU_TIME, Counter.Type.USER_TIME); // two counters
 * </pre>
 *
 * <p>
 * <b>Note:</b> For a list of available counters, refer to {@link Counter.Type}.
 * </p>
 *
 * <p>
 * Call {@code start()} to start the timing session. When you are done, call
 * {@code stop()} to complete the timing session.
 * </p>
 *
 * <p>
 * <b>Hint:</b> A single call to the factory method {@code Stopwatch.createStarted()} may
 * create a started stopwatch for convenience.
 * </p>
 *
 * <p>
 * Use {@code elapsedTime(Counter.Type)} to retrieve the total elapsed time for a
 * particular counter. E.g.:
 * </p>
 *
 * <pre>
 * double elapsedTimeNanos = cpuTime.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.NANOSECONDS);
 * </pre>
 *
 * <p>
 * Use the output method {@code printStatistics(System.out)} to print stopwatch statistics
 * to the system console.
 * </p>
 *
 * <p>
 * Although it is intended that the output methods {@code elapsedTime()} and
 * {@code printStatistics()} should be called after the stop, some suitable, temporary
 * data may be returned if the current timing session is still running. In this scenario,
 * the initial values will be compared to the most up-to-date ones, retrieved at the
 * moment of the call.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, different
 * instances must be created for each thread.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @see Counter
 * @see Counter.Type
 * @see TimingSession
 */
public class Stopwatch
{
    private static final String MSG_NOT_RUNNING = "The stopwatch is not running";
    private static final String MSG_TYPE_NOT_SPECIFIED = "\"{0}\" was not specified in this stopwatch. Available type(s): {1}";

    private static final Type[] DEFAULT_TYPES = Type.values();

    /**
     * Enumerates possible stopwatch states, with proper behaviors for each of them.
     *
     * @since 2.0.0
     */
    private enum State
    {
        RUNNING
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.stopCurrentTimingSession();
                stopwatch.startNewTimingSession();
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                stopwatch.stopCurrentTimingSession();
            }
        },

        STOPPED
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.startNewTimingSession();
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                throw new IllegalStateException(MSG_NOT_RUNNING);
            }
        };

        /**
         * Starts the given stopwatch, if the state is appropriate for the operation.
         *
         * @param stopwatch the stopwatch to be started
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void start(Stopwatch stopwatch);

        /**
         * Stops the given stopwatch, if the state is appropriate for the operation.
         *
         * @param stopwatch the stopwatch to be stopped
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void stop(Stopwatch stopwatch);
    }

    private final List<Type> types;
    private List<TimingSession> sessions;
    private State state;

    /**
     * Creates a new stopwatch with default counter types.
     */
    public Stopwatch()
    {
        this(DEFAULT_TYPES);
    }

    /**
     * Creates a new stopwatch with specific counter types.
     *
     * @param types the types to be set
     */
    public Stopwatch(Type... types)
    {
        this.types = Arrays.asList(types);
        reset();
    }

    /**
     * Provides a started stopwatch for convenience with default counter types.
     *
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted()
    {
        return createStarted(DEFAULT_TYPES);
    }

    /**
     * Provides a started stopwatch for convenience with specific counter types.
     *
     * @param types the types to be set
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted(Type... types)
    {
        Stopwatch stopwatch = new Stopwatch(types);
        stopwatch.start();
        return stopwatch;
    }

    /**
     * Cleans all timing sessions in this stopwatch.
     */
    public void reset()
    {
        sessions = new ArrayList<>();
        state = State.STOPPED;
    }

    /**
     * Starts a new timing session.
     */
    public void start()
    {
        state.start(this);
    }

    /**
     * Stops the current timing session.
     *
     * @throws IllegalStateException if the stopwatch state is not suitable for this action
     */
    public void stop()
    {
        state.stop(this);
    }

    /**
     * Returns {@code true} if this stopwatch is started.
     *
     * @return true if the stopwatch is started; otherwise, false
     */
    public boolean isStarted()
    {
        return state == State.RUNNING;
    }

    /**
     * Returns the counter types associated with this stopwatch instance.
     *
     * @return all counter types associated with this stopwatch instance
     */
    public List<Type> getTypes()
    {
        return types;
    }

    /**
     * Returns a list of counters populated by this stopwatch.
     * <p>
     * New counters are created every time the {@code start()} method is called.
     *
     * @return all counters available in this stopwatch instance
     */
    public List<Counter> getCounters()
    {
        return sessions.stream().map(TimingSession::getCounters).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of populated counters for a specific type in this stopwatch, or an empty
     * list if no counter is found (for example, if the stopwatch was not yet started, or
     * after reset).
     *
     * @param type the counter type to be fetched
     * @return a list of counters associated with the given type, or an empty list
     * @throws IllegalArgumentException if the type is not specified in this stopwatch
     * @since 2.2.0
     */
    public List<Counter> getCounters(Type type)
    {
        return getCountersAsStream(type).collect(Collectors.toList());
    }

    /**
     * Returns a stream of populated counters for a specific type in this stopwatch.
     *
     * @param type the counter type to be fetched
     * @return a stream of counters associated with the given type, not null
     * @throws IllegalArgumentException if the type was not specified in this stopwatch
     * @since 2.2.0
     */
    private Stream<Counter> getCountersAsStream(Type type)
    {
        if (types.contains(type))
        {
            return sessions.stream().map(session -> session.getCounter(type));
        }
        throw new IllegalArgumentException(MessageFormat.format(MSG_TYPE_NOT_SPECIFIED, type, types));
    }

    /**
     * Returns the total elapsed time for a specific counter.
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     * @throws IllegalArgumentException if the type was not specified in this stopwatch
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return getCountersAsStream(type).map(Counter::elapsedTime)
                .reduce(Duration.ZERO, Duration::sum);
    }

    /**
     * Returns the total elapsed time for a specific counter, in the specified time unit.
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this stopwatch
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return getCountersAsStream(type).map(counter -> counter.elapsedTime(timeUnit))
                .reduce(0.0, Double::sum);
    }

    /**
     * Returns the total elapsed time for a specific counter, in the specified time unit, with
     * a custom {@link ConversionMode} applied.
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this stopwatch
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCountersAsStream(type).map(counter -> counter.elapsedTime(timeUnit, conversionMode))
                .reduce(0.0, Double::sum);
    }

    /**
     * Prints stopwatch statistics in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.print(this, printStream);
    }

    /**
     * Creates and starts a new timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
     */
    private void startNewTimingSession()
    {
        TimingSession session = new TimingSession(types.toArray(new Type[types.size()]));
        sessions.add(session);
        session.start();
        state = State.RUNNING;
    }

    /**
     * Stops the current timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
     */
    private void stopCurrentTimingSession()
    {
        getCurrentTimingSession().ifPresent(TimingSession::stop);
        state = State.STOPPED;
    }

    /**
     * Returns the current/last timing session available in this stopwatch, or
     * {@code Optional.empty()} if no timing session available yet.
     *
     * @return an {@link Optional} possibly containing the current/last timing session
     *         available in this stopwatch instance
     */
    protected Optional<TimingSession> getCurrentTimingSession()
    {
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(sessions.size() - 1));
    }

    /**
     * Returns all timing sessions available in this stopwatch.
     *
     * @return a list of timing sessions
     */
    protected List<TimingSession> getTimingSessions()
    {
        return sessions;
    }


}
