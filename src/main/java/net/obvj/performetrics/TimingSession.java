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
 * Use the default constructor, with no arguments, to create a timing session with all of
 * the available metrics. If required, an additional constructor may be used to provide a
 * timing session with specific counters. E.g.:
 * </p>
 *
 * <pre>
 * new TimingSession(); // provides all available counter types
 * new TimingSession(Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
 * new TimingSession(Counter.Type.CPU_TIME, Counter.Type.USER_TIME); // two counters
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
 * Use {@code elapsedTime(Counter.Type)} to retrieve the elapsed time for a particular
 * counter. E.g.:
 * </p>
 *
 * <pre>
 * double elapsedTimeNanos = cpuTime.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.NANOSECONDS);
 * </pre>
 *
 * <p>
 * Although it is intended that the output methods {@code elapsedTime()} should be called
 * after the stop, some suitable, temporary data may be returned if the timing session is
 * on going. In this scenario, the initial values will be compared to the most up-to-date
 * ones, retrieved at the moment of the call.
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
 * @see Stopwatch
 */
public class TimingSession
{
    private static final String MSG_NOT_STARTED = "The timing session is not started";
    private static final String MSG_ALREADY_STARTED = "The timing session is already started";
    private static final String MSG_ALREADY_FINISHED = "A finished timing session cannot be restarted";
    private static final String MSG_TYPE_NOT_SPECIFIED = "\"{0}\" was not specified in this timing session. Available type(s): {1}";


    private static final Type[] DEFAULT_TYPES = Type.values();

    /**
     * Enumerates possible states of a timing session, with proper behaviors for each of them.
     */
    private enum State
    {
        READY
        {
            @Override
            void start(TimingSession session)
            {
                session.doStart();
            }

            @Override
            void stop(TimingSession session)
            {
                throw new IllegalStateException(MSG_NOT_STARTED);
            }
        },

        STARTED
        {
            @Override
            void start(TimingSession session)
            {
                throw new IllegalStateException(MSG_ALREADY_STARTED);
            }

            @Override
            void stop(TimingSession session)
            {
                session.doStop();
            }
        },

        FINISHED
        {
            @Override
            void start(TimingSession session)
            {
                throw new IllegalStateException(MSG_ALREADY_FINISHED);
            }

            @Override
            void stop(TimingSession session)
            {
                throw new IllegalStateException(MSG_NOT_STARTED);
            }
        };

        /**
         * Starts the given timing session, if the state is appropriate for the operation.
         *
         * @param session the timing session to be started
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void start(TimingSession session);

        /**
         * Stops the given timing session, if the state is appropriate for the operation.
         *
         * @param session the timing session to be stopped
         * @throws IllegalStateException if the state is not appropriate for the operation
         */
        abstract void stop(TimingSession session);
    }

    private final Type[] types;
    private Map<Type, Counter> counters;
    private State state;

    /**
     * Creates a new timing session with default counter types.
     */
    public TimingSession()
    {
        this(DEFAULT_TYPES);
    }

    /**
     * Creates a new timing session with custom counter types.
     *
     * @param types the counter types to set
     */
    public TimingSession(Type... types)
    {
        this.types = types;
        reset();
    }

    /**
     * Resets all counters associated with this timing session.
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
     * @throws IllegalStateException if the session state is not suitable for this action
     */
    public void start()
    {
        state.start(this);
    }

    /**
     * Stops the timing session.
     *
     * @throws IllegalStateException if the session state is not suitable for this action
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
     * This has the same effect as calling:
     * {@code timingSession.getCounter(type).elapsedTime()}
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     * @throws IllegalArgumentException if the type was not specified in this timing session
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
     * {@code timingSession.getCounter(type).elapsedTime(timeUnit)}
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this timing session
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
     * {@code timingSession.getCounter(type).elapsedTime(timeUnit, conversionMode)}
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     * @throws IllegalArgumentException if the type was not specified in this timing session
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCounter(type).elapsedTime(timeUnit, conversionMode);
    }

    /**
     * Returns the counter types specified for this timing session.
     *
     * @return all counter types specified for this timing session
     */
    protected Type[] getTypes()
    {
        return types;
    }

    /**
     * Returns the counters associated with this timing session.
     *
     * @return all counters associated with this timing session
     */
    public Collection<Counter> getCounters()
    {
        return counters.values();
    }

    /**
     * Returns the counter instance associated with a given type in this timing session.
     *
     * @param type the counter type to be fetched
     * @return the counter instance associated with the given type in this timing session
     * @throws IllegalArgumentException if the type was not specified in this timing session
     */
    public Counter getCounter(Type type)
    {
        if (!counters.containsKey(type))
        {
            throw new IllegalArgumentException(MessageFormat.format(MSG_TYPE_NOT_SPECIFIED, type, counters.keySet()));
        }
        return counters.get(type);
    }

    /**
     * Starts the timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
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
     * Stops the timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
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
