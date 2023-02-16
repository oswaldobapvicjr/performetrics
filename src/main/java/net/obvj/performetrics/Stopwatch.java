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

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.print.PrintFormat;
import net.obvj.performetrics.util.print.PrintStyle;
import net.obvj.performetrics.util.print.PrintUtils;

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
public class Stopwatch
{
    static final String MSG_NOT_RUNNING = "The stopwatch is not running";
    static final String MSG_TYPE_NOT_SPECIFIED = "\"{0}\" was not assigned during instantiation. Available type(s): {1}";
    static final String MSG_NOT_A_SINGLE_TYPE = "This stopwatch is keeping more than one type. Please inform a specific type for this operation.";
    static final String MSG_NO_SESSION_RECORDED = "No session recorded";

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
                stopwatch.stopCurrentSession();
                stopwatch.startNewSession();
            }

            @Override
            void stop(Stopwatch stopwatch)
            {
                stopwatch.stopCurrentSession();
            }
        },

        STOPPED
        {
            @Override
            void start(Stopwatch stopwatch)
            {
                stopwatch.startNewSession();
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
     * <p>
     * If no type is specified, then all of the available types will be maintained.
     *
     * @param types the types to be set
     */
    public Stopwatch(Type... types)
    {
        this.types = Arrays.asList(types);
        reset();
    }

    /**
     * Provides a started stopwatch with default counter types, for convenience.
     *
     * @return a new, started stopwatch
     */
    public static Stopwatch createStarted()
    {
        return createStarted(DEFAULT_TYPES);
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
     * Returns a map of populated counters grouped by type, where each entry in the counters
     * list represents a timing session.
     *
     * @return a map of populated counters grouped by type
     *
     * @since 2.2.1
     */
    public Map<Type, List<Counter>> getAllCountersByType()
    {
        Map<Type, List<Counter>> enumMap = new EnumMap<>(Type.class);
        types.forEach(type -> enumMap.put(type, getCounters(type)));
        return enumMap;
    }

    /**
     * Returns a list of counters populated by this stopwatch.
     * <p>
     * New counters are created every time the {@code start()} method is called.
     *
     * @return all counters available in this stopwatch instance
     */
    List<Counter> getCounters()
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
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  stopwatch during instantiation
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
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  stopwatch during instantiation
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
     * Checks whether this stopwatch maintains exactly one counter type.
     *
     * @throws IllegalStateException if the stopwatch is keeping more than one counter type
     * @since 2.2.4
     */
    private void checkSingleCounter()
    {
        if (types.size() != 1)
        {
            throw new IllegalStateException(MSG_NOT_A_SINGLE_TYPE);
        }
    }

    /**
     * Returns the total elapsed time for a single counter type, provided that this stopwatch
     * is keeping a single type.
     *
     * @return the elapsed time for a single counter type in this stopwatch
     *
     * @throws IllegalStateException if the stopwatch is keeping more than one counter type
     * @since 2.2.4
     */
    public Duration elapsedTime()
    {
        checkSingleCounter();
        return elapsedTime(types.get(0));
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this stopwatch is keeping a single type.
     *
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for a single counter type in this stopwatch, converted to the
     *         given time unit with the default conversion mode
     *
     * @throws IllegalStateException if the stopwatch is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit)
    {
        checkSingleCounter();
        return elapsedTime(types.get(0), timeUnit);
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this stopwatch is keeping a single type.
     *
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for a single counter type in this stopwatch, converted to the
     *         given time unit with the given conversion mode
     *
     * @throws IllegalStateException if the stopwatch is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        checkSingleCounter();
        return elapsedTime(types.get(0), timeUnit, conversionMode);
    }

    /**
     * Returns the total elapsed time for a specific counter.
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  stopwatch during instantiation
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return getCountersAsStream(type).map(Counter::elapsedTime).reduce(Duration.ZERO, Duration::sum);
    }

    /**
     * Returns the total elapsed time for a specific counter, in the specified time unit.
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         with the default conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  stopwatch during instantiation
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
     *         with the given conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  stopwatch during instantiation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCountersAsStream(type).map(counter -> counter.elapsedTime(timeUnit, conversionMode))
                .reduce(0.0, Double::sum);
    }

    /**
     * Prints summarized elapsed times in the specified print stream.
     *
     * @param printStream the print stream to which data will be sent
     *
     * @throws NullPointerException if the PrintStream is null
     *
     * @since 2.2.1
     */
    public void printSummary(PrintStream printStream)
    {
        printSummary(printStream, null);
    }

    /**
     * Prints summarized elapsed times in the specified print stream, with a custom
     * {@link PrintStyle}.
     *
     * @param printStream the print stream to which data will be sent
     * @param printStyle  the {@link PrintStyle} to be applied
     *
     * @throws NullPointerException     if the PrintStream is null
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with
     *                                  {@link PrintFormat#SUMMARIZED}
     * @since 2.2.1
     */
    public void printSummary(PrintStream printStream, PrintStyle printStyle)
    {
        PrintUtils.printSummary(this, printStream, printStyle);
    }

    /**
     * Prints detailed information about timing sessions in the specified print stream.
     *
     * @param printStream the print stream to which information will be sent
     *
     * @throws NullPointerException if the PrintStream is null
     *
     * @since 2.2.1
     */
    public void printDetails(PrintStream printStream)
    {
        printDetails(printStream, null);
    }

    /**
     * Prints detailed information about timing sessions in the specified print stream, with a
     * custom {@link PrintStyle}.
     *
     * @param printStream the print stream to which information will be sent
     * @param printStyle  the {@link PrintStyle} to be applied
     *
     * @throws NullPointerException     if the PrintStream is null
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with
     *                                  {@link PrintFormat#DETAILED}
     * @since 2.2.1
     */
    public void printDetails(PrintStream printStream, PrintStyle printStyle)
    {
        PrintUtils.printDetails(this, printStream, printStyle);
    }

    /**
     * Creates and starts a new timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
     */
    private void startNewSession()
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
    private void stopCurrentSession()
    {
        getLastSession().ifPresent(TimingSession::stop);
        state = State.STOPPED;
    }

    /**
     * Returns the current/last timing session available in this stopwatch, or
     * {@code Optional.empty()} if no timing session available yet.
     *
     * @return an {@link Optional} possibly containing the current/last timing session
     *         available in this stopwatch instance
     */
    Optional<TimingSession> getLastSession()
    {
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(sessions.size() - 1));
    }

    /**
     * Returns the current/last timing session recorded in this stopwatch.
     *
     * @return the current/last timing session recorded in this stopwatch
     * @throws IllegalStateException if the stopwatch does not contain any recorded session
     * @since 2.4.0
     */
    public TimingSession lastSession()
    {
        return new UnmodifiableTimingSession(getLastSession()
                .orElseThrow(() -> new IllegalStateException(MSG_NO_SESSION_RECORDED)));
    }

    /**
     * Returns all timing sessions recorded in this stopwatch.
     *
     * @return a list of timing sessions
     * @since 2.2.0
     */
    List<TimingSession> getAllSessions()
    {
        return sessions;
    }

    /**
     * Returns a string containing a formatted stopwatch summary.
     *
     * @return a string containing stopwatch summary
     * @since 2.2.4
     */
    @Override
    public String toString()
    {
        return PrintUtils.summaryToString(this);
    }
}
