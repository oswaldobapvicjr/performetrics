package net.obvj.performetrics;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;

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
 * Use {@code elapsedTime(Counter.Type)} to retrieve the elapsed time for a particular
 * counter. E.g.:
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
 * Although it is intended that the output methods {@code printStatistics()} should only
 * be called after the stop, some suitable, temporary data may be returned if the
 * stopwatch is still running. In this scenario, the initial values will be compared to
 * the most up-to-date ones, retrieved at the moment of the call. The same applies to the
 * {@code elapsedTime()} methods available for each counter instance.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, different
 * instances must be created for each thread.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 * @see Counter
 * @see Counter.Type
 */
public class TimingSession
{
    private static final String MSG_STOPWATCH_ALREADY_STARTED = "The timing session is already started";
    private static final String MSG_NOT_STARTED = "The timing session is not started";
    private static final String MSG_TYPE_NOT_AVAILABLE = "\"{0}\" is not available in this timing session. Available type(s): {1}";
    private static final String MSG_ALREADY_FINISHED = "A finished timing session cannot be restarted";


    private static final Type[] DEFAULT_TYPES = Type.values();

    /**
     * Enumerates possible states for a Timing Session, with proper behaviors for each of
     * them.
     */
    private enum State
    {
        READY
        {
            @Override
            void start(TimingSession stopwatch)
            {
                stopwatch.doStart();
            }

            @Override
            void stop(TimingSession stopwatch)
            {
                throw new IllegalStateException(MSG_NOT_STARTED);
            }
        },

        STARTED
        {
            @Override
            void start(TimingSession stopwatch)
            {
                throw new IllegalStateException(MSG_STOPWATCH_ALREADY_STARTED);
            }

            @Override
            void stop(TimingSession stopwatch)
            {
                stopwatch.doStop();
            }
        },

        FINISHED
        {
            @Override
            void start(TimingSession stopwatch)
            {
                throw new IllegalStateException(MSG_ALREADY_FINISHED);
            }

            @Override
            void stop(TimingSession stopwatch)
            {
                throw new IllegalStateException(MSG_NOT_STARTED);
            }
        };

        /**
         * Starts the given stopwatch, if the state is appropriate for the operation.
         *
         * @param stopwatch the stopwatch to be started
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void start(TimingSession stopwatch);

        /**
         * Stops the given stopwatch, if the state is appropriate for the operation.
         *
         * @param stopwatch the stopwatch to be stopped
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void stop(TimingSession stopwatch);
    }

    private final Type[] types;
    private Map<Type, Counter> counters;
    private State state;

    /**
     * Creates a new Timing Session with default counter types.
     */
    public TimingSession()
    {
        this(DEFAULT_TYPES);
    }

    /**
     * Creates a new Timing Session with custom counter types.
     *
     * @param types the counter types to set
     */
    public TimingSession(Type... types)
    {
        this.types = types;
        reset();
    }

    /**
     * Resets all counters associated with this session instance.
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
     * Returns {@code true} if this timing session is started.
     *
     * @return true if the timing session is started; otherwise, false
     */
    public boolean isStarted()
    {
        return state == State.STARTED;
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter.
     * <p>
     * This has the same effect as calling: {@code stopwatch.getCounter(type).elapsedTime()}
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  stopwatch instance
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return getCounter(type).elapsedTime();
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter, in the
     * specified time unit.
     * <p>
     * This has the same effect as calling:
     * {@code stopwatch.getCounter(type).elapsedTime(timeUnit)}
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  stopwatch instance
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return getCounter(type).elapsedTime(timeUnit);
    }

    /**
     * A convenient method that returns the elapsed time of a specific counter, in the
     * specified time unit, by applying a custom {@link ConversionMode}.
     * <p>
     * This has the same effect as calling:
     * {@code stopwatch.getCounter(type).elapsedTime(timeUnit, conversionMode)}
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     * @throws IllegalArgumentException if the specified type is not available in this
     *                                  stopwatch instance
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCounter(type).elapsedTime(timeUnit, conversionMode);
    }

    /**
     * Returns the counter types associated with this stopwatch instance.
     *
     * @return all counter types associated with this stopwatch instance
     */
    protected Type[] getTypes()
    {
        return types;
    }

    /**
     * Returns the counters associated with this stopwatch instance.
     *
     * @return all counters associated with this stopwatch instance
     */
    public Collection<Counter> getCounters()
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
            throw new IllegalArgumentException(MessageFormat.format(MSG_TYPE_NOT_AVAILABLE, type, counters.keySet()));
        }
        return counters.get(type);
    }

    /**
     * Starts the timing session. Only called internally. The current {@link State} defines
     * whether or not this action is allowed.
     */
    private void doStart()
    {
        for (Counter counter : counters.values())
        {
            counter.setUnitsBefore();
        }
        state = State.STARTED;
    }

    /**
     * Stops the timing session. Only called internally. The current {@link State} defines
     * whether or not this action is allowed.
     */
    private void doStop()
    {
        for (Counter counter : counters.values())
        {
            counter.setUnitsAfter();
        }
        state = State.FINISHED;
    }

}
