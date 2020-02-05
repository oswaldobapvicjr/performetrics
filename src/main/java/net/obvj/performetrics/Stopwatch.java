package net.obvj.performetrics;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.printer.PrintUtils;

/**
 * <p>
 * A convenient timing object that supports multiple counter types.
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
 * Use {@code getCounter(Counter.Type)}, then {@code elapsedTime()} to retrieve the
 * elapsed time of a particular counter. E.g.:
 * </p>
 *
 * <pre>
 * Counter cpuTime = stopwatch.getCounter(Counter.Type.CPU_TIME);
 * double elapsedTimeMillis = cpuTime.elapsedTime(TimeUnit.MILLISECONDS);
 * </pre>
 *
 * <p>
 * Use the output method {@code printStatistics(System.out)} to print stopwatch statistics
 * to the system console.
 * </p>
 *
 * <p>
 * Although it is intended that the output methods {@code printStatistics()} should only
 * be called after stop, some suitable, temporary data may be returned if the stopwatch is
 * still running. In this scenario, the initial values will be compared to the most
 * up-to-date ones, retrieved at the moment of the call. The same applies to the
 * {@code elapsedTime()} methods available for each counter instance.
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
 */
public class Stopwatch
{
    private static final String MSG_STOPWATCH_ALREADY_STARTED = "The stopwatch is already started";
    private static final String MSG_STOPWATCH_MUST_BE_RESET = "The stopwatch must be reset before being restarted";
    private static final String MSG_STOPWATCH_NOT_RUNNING = "The stopwatch is not running";
    private static final String MSG_TYPE_NOT_AVAILABLE = "\"{0}\" is not available in this stopwatch. Available type(s): {1}";

    private static final Type[] DEFAULT_TYPES = Type.values();

    /**
     * Enumerates possible stopwatch states, with proper behaviors for each of them.
     *
     * @since 2.0.0
     */
    private enum State
    {
        READY
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.doStart();
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                throw new IllegalStateException(MSG_STOPWATCH_NOT_RUNNING);
            }
        },

        RUNNING
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                throw new IllegalStateException(MSG_STOPWATCH_ALREADY_STARTED);
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                stopwatch.doStop();
            }
        },

        STOPPED
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                throw new IllegalStateException(MSG_STOPWATCH_MUST_BE_RESET);
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                throw new IllegalStateException(MSG_STOPWATCH_NOT_RUNNING);
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

    private final Type[] types;
    private Map<Type, Counter> counters;
    private State state = State.READY;

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
        this.types = types;
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
     * Resets all counters associated with this stopwatch instance.
     */
    public void reset()
    {
        counters = new EnumMap<>(Type.class);
        for (Type type : types)
        {
            counters.put(type, new Counter(type));
        }
        state = State.READY;
    }

    /**
     * Starts the timing session.
     *
     * @throws IllegalStateException if the stopwatch state is not suitable for this action
     */
    public void start()
    {
        state.start(this);
    }

    /**
     * Stops the timing session.
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
     * Returns the counters associated with this stopwatch instance.
     *
     * @return all counters associated with this stopwatch instance
     */
    public Collection<Counter> getAllCounters()
    {
        return counters.values();
    }

    /**
     * Returns the counter instance associated with a given type in this stopwatch.
     *
     * @param type the counter type to be fetched
     * @return the counter instance associated with the given type in this stopwatch
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  stopwatch instance
     */
    public Counter getCounter(Type type)
    {
        if (!counters.containsKey(type))
        {
            throw new IllegalArgumentException(
                    MessageFormat.format(MSG_TYPE_NOT_AVAILABLE, type, counters.keySet()));
        }
        return counters.get(type);
    }

    /**
     * Prints stopwatch statistics in the specified print stream.
     *
     * @param printStream the print stream to which statistics will be sent
     */
    public void printStatistics(PrintStream printStream)
    {
        PrintUtils.printStopwatch(this, printStream);
    }

    /**
     * Prints stopwatch statistics in the specified print stream, with a custom time unit.
     *
     * @param printStream the print stream to which statistics will be sent
     * @param timeUnit    the time unit for the elapsed times to be displayed
     */
    public void printStatistics(PrintStream printStream, TimeUnit timeUnit)
    {
        PrintUtils.printStopwatch(this, printStream, timeUnit);
    }

    /**
     * Starts the timing session. Only called internally. The current {@link State} defines
     * whether or not this action is allowed.
     */
    private void doStart()
    {
        for (Counter counter : counters.values())
        {
            counter.before();
        }
        state = State.RUNNING;
    }

    /**
     * Stops the timing session. Only called internally. The current {@link State} defines
     * whether or not this action is allowed.
     */
    private void doStop()
    {
        for (Counter counter : counters.values())
        {
            counter.after();
        }
        state = State.STOPPED;
    }

}
