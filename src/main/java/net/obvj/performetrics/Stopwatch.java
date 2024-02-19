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

import static net.obvj.performetrics.Performetrics.ALL_TYPES;

import java.util.List;
import java.util.function.Supplier;

import net.obvj.performetrics.Counter.Type;

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
 * <blockquote>
 *
 * <pre>
 * new Stopwatch(); // provides all available counter types
 * new Stopwatch(Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
 * new Stopwatch(Counter.Type.CPU_TIME, Counter.Type.USER_TIME); // two counters
 * </pre>
 *
 * </blockquote>
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
 *
 * <p>
 * Use {@code elapsedTime(Counter.Type)} to retrieve the total elapsed time for a
 * particular counter type. E.g.:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * Duration cpuTime = stopwatch.elapsedTime(Counter.Type.CPU_TIME);
 * </pre>
 *
 * </blockquote>
 *
 *
 * <p>
 * Use {@code elapsedTime(Counter.Type, TimeUnit)} to retrieve the total elapsed time
 * converted into a specific time unit. E.g.:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * double cpuTimeNanos = stopwatch.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.NANOSECONDS);
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * <strong>Hint:</strong> If the stopwatch was created with only one counter type, then no
 * argument is needed to retrieve the total elapsed time. E.g.:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * Duration elapsedTime = stopwatch.elapsedTime();
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * Use the output methods {@code printSummary(System.out)} and
 * {@code printDetails(System.out)} to print stopwatch statistics to the system console.
 * </p>
 *
 * <p>
 * Although it is intended that the output methods {@code elapsedTime()},
 * {@code printSummary()}, and {@code printDetails()} should be called after the stop,
 * some suitable, temporary data may be returned if the current timing session is still
 * running. In this scenario, the initial values will be compared to the most up-to-date
 * ones, retrieved at the moment of the call.
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
public class Stopwatch extends TimingSessionContainer
{
    private static final String MSG_NOT_RUNNING = "The stopwatch is not running";

    /**
     * Enumerates possible stopwatch states, with proper behaviors for each of them.
     *
     * @since 2.0.0
     */
    enum State
    {
        RUNNING
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.stopCurrentSession();
                stopwatch.startNewSession();
                stopwatch.state = State.RUNNING;
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                stopwatch.stopCurrentSession();
                stopwatch.state = State.STOPPED;
            }
        },

        STOPPED
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.startNewSession();
                stopwatch.state = State.RUNNING;
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

    private State state;

    /**
     * Creates a new stopwatch with default counter types.
     */
    public Stopwatch()
    {
        this(ALL_TYPES);
    }

    /**
     * Creates a new stopwatch with specific counter types.
     * <p>
     * If no type is specified, then all of the available types will be maintained.
     *
     * @param types the types to be set
     */
    public Stopwatch(Type... types)
    {
        this(asList(types));
    }

    /**
     * Creates a new stopwatch with specific counter types.
     *
     * @param types the types to be set
     * @since 2.5.0
     */
    private Stopwatch(List<Type> types)
    {
        super(types);
    }

    /**
     * Provides a started stopwatch with default counter types, for convenience.
     *
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted()
    {
        return createStarted(ALL_TYPES);
    }

    /**
     * Provides a started stopwatch with specific counter types, for convenience.
     * <p>
     * If no type is specified, then all of the available types will be maintained.
     *
     * @param types the types to be set
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted(Type... types)
    {
        return createStarted(() -> new Stopwatch(types));
    }

    /**
     * Provides a started stopwatch with specific counter types, for convenience.
     * <p>
     * If no type is specified, then all of the available types will be maintained.
     *
     * @param types the types to be set
     * @return a new, started stopwatch
     * @since 2.5.0
     */
    private static Stopwatch createStarted(List<Type> types)
    {
        return createStarted(() -> new Stopwatch(types));
    }

    private static Stopwatch createStarted(Supplier<Stopwatch> supplier)
    {
        Stopwatch stopwatch = supplier.get();
        stopwatch.start();
        return stopwatch;
    }

    /**
     * Cleans all timing sessions in this stopwatch.
     */
    @Override
    public void reset()
    {
        super.reset();
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

}
