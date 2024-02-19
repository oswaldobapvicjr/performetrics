/*
 * Copyright 2024 obvj.net
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
 * An object that maintains multiple timing sessions for different counter types, and
 * provides methods to retrieve elapsed times is different formats.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.5.0
 *
 * @see TimingSession
 */
public abstract class TimingSessionContainer
{
    static final String MSG_TYPE_NOT_SPECIFIED = "\"{0}\" was not assigned during instantiation. Available type(s): {1}";
    static final String MSG_NOT_A_SINGLE_TYPE = "This stopwatch is keeping more than one type. Please inform a specific type for this operation.";
    static final String MSG_NO_SESSION_RECORDED = "No session recorded";

    private final List<Type> types;
    private List<TimingSession> sessions;

    protected TimingSessionContainer(List<Type> types)
    {
        this.types = types;
        reset();
    }

    /**
     * @param types the types to be parsed
     * @return a list containing either the types passed as var-args, or
     *         {@link Performetrics#ALL_TYPES} if no type is specified
     */
    protected static List<Type> asList(Type... types)
    {
        return types.length > 0 ? Arrays.asList(types) : ALL_TYPES;
    }

    /**
     * Cleans all timing sessions in this object.
     */
    public void reset()
    {
        sessions = new ArrayList<>();
    }

    /**
     * Returns the counter types associated with this object.
     *
     * @return a list of counter types associated with this object
     * @see Type
     */
    public List<Type> getTypes()
    {
        return types;
    }

    /**
     * Returns a map of counters grouped by type, where each entry in the counters list
     * represents a timing session.
     *
     * @return a map of populated counters grouped by type
     * @since 2.2.1
     * @see Counter
     */
    public Map<Type, List<Counter>> getAllCountersByType()
    {
        Map<Type, List<Counter>> enumMap = new EnumMap<>(Type.class);
        types.forEach(type -> enumMap.put(type, getCounters(type)));
        return enumMap;
    }

    /**
     * Returns a list of counters available in this object.
     * <p>
     * New counters are created every time a new session is started
     *
     * @return all counters available in this object
     * @see Counter
     */
    protected List<Counter> getCounters()
    {
        return sessions.stream().map(TimingSession::getCounters).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of available counters for a specific type in this object, or an empty
     * list if no counter is found -- for example, if no timing session was started, or after
     * calling {@code reset()}.
     *
     * @param type the counter type to be fetched
     * @return a list of counters associated with the given type, or an empty list
     *
     * @throws IllegalArgumentException if the specified type was not assigned to this object
     *                                  during instantiation
     * @since 2.2.0
     * @see Counter
     */
    public List<Counter> getCounters(Type type)
    {
        return getCountersAsStream(type).collect(Collectors.toList());
    }

    /**
     * Returns a stream of available counters for a specific type in this object.
     *
     * @param type the counter type to be fetched
     * @return a stream of counters associated with the given type, not null
     *
     * @throws IllegalArgumentException if the specified type was not assigned to this object
     *                                  during instantiation
     * @since 2.2.0
     * @see Counter
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
     * Checks whether this object maintains exactly one counter type.
     *
     * @throws IllegalStateException if the object is keeping more than one counter type
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
     * Returns the total elapsed time for a single counter type, provided that this object is
     * keeping a single type.
     *
     * @return the elapsed time for a single counter type in this object
     *
     * @throws IllegalStateException if the object is keeping more than one counter type
     * @since 2.2.4
     */
    public Duration elapsedTime()
    {
        checkSingleCounter();
        return elapsedTime(types.get(0));
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this object is keeping a single type.
     *
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for a single counter type in this object, converted to the
     *         given time unit with the default conversion mode
     *
     * @throws IllegalStateException if the object is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit)
    {
        checkSingleCounter();
        return elapsedTime(types.get(0), timeUnit);
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this object is keeping a single type.
     *
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for a single counter type in this object, converted to the
     *         given time unit with the given conversion mode
     *
     * @throws IllegalStateException if the object is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        checkSingleCounter();
        return elapsedTime(types.get(0), timeUnit, conversionMode);
    }

    /**
     * Returns the total elapsed time for a specific counter type.
     *
     * @param type the counter type to be fetched
     * @return the elapsed time for the specified counter
     *
     * @throws IllegalArgumentException if the specified type was not assigned to this object
     *                                  during instantiation
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return getCountersAsStream(type).map(Counter::elapsedTime)
                .reduce(Duration.ZERO, Duration::sum);
    }

    /**
     * Returns the total elapsed time for a specific counter type, in the specified time unit.
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         with the default conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to this object
     *                                  during instantiation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return getCountersAsStream(type).map(counter -> counter.elapsedTime(timeUnit))
                .reduce(0.0, Double::sum);
    }

    /**
     * Returns the total elapsed time for a specific counter type, in the specified time unit,
     * with a custom {@link ConversionMode} applied.
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         with the given conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to this object
     *                                  during instantiation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return getCountersAsStream(type).map(counter -> counter.elapsedTime(timeUnit, conversionMode))
                .reduce(0.0, Double::sum);
    }

    /**
     * Prints elapsed times in the specified print stream.
     * <p>
     * The default {@link PrintStyle} (defined by
     * {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be applied.
     * <p>
     * For a custom {@code PrintStyle}, use {@link #print(PrintStream, PrintStyle)}.
     *
     * @param printStream the print stream to which data will be sent
     * @throws NullPointerException if the {@code PrintStream} is null
     * @since 2.4.0
     */
    public void print(PrintStream printStream)
    {
        PrintUtils.print(this, printStream);
    }

    /**
     * Prints elapsed times in the specified print stream with a custom {@link PrintStyle}.
     * <p>
     * The format (whether to generate a summarized or detailed view) will be determined by
     * the specified {@link PrintStyle}.
     *
     * @param printStream the print stream to which data will be sent
     * @param printStyle  the {@link PrintStyle}; if {@code null}, the default
     *                    {@code PrintStyle} (defined by
     *                    {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be
     *                    applied
     *
     * @throws NullPointerException if the {@code PrintStream} is null
     * @since 2.4.0
     */
    public void print(PrintStream printStream, PrintStyle printStyle)
    {
        PrintUtils.print(this, printStream, printStyle);
    }

    /**
     * Prints summarized elapsed times in the specified print stream.
     *
     * @param printStream the print stream to which data will be sent
     *
     * @throws NullPointerException if the {@code PrintStream} is null
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
     * @throws NullPointerException     if the {@code PrintStream} is null
     * @throws IllegalArgumentException if the specified {@code PrintStyle} is not compatible
     *                                  with {@link PrintFormat#SUMMARIZED}
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
     * @throws NullPointerException if the {@code PrintStream} is null
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
     * @throws NullPointerException     if the {@code PrintStream} is null
     * @throws IllegalArgumentException if the specified {@code PrintStyle} is not compatible
     *                                  with {@link PrintFormat#DETAILED}
     * @since 2.2.1
     */
    public void printDetails(PrintStream printStream, PrintStyle printStyle)
    {
        PrintUtils.printDetails(this, printStream, printStyle);
    }

    /**
     * Creates and starts a new timing session.
     */
    protected void startNewSession()
    {
        TimingSession session = new TimingSession(types);
        sessions.add(session);
        session.start();
    }

    /**
     * Stops the current timing session.
     */
    protected void stopCurrentSession()
    {
        getLastSession().ifPresent(TimingSession::stop);
    }

    /**
     * Returns the current/last timing session available in this object, or
     * {@code Optional.empty()} if no timing session available yet.
     * <p>
     * <b>Note:</b> This method is only for internal use only since it does not create a
     * defensive copy of the timing session.
     *
     * @return an {@link Optional} possibly containing the current/last timing session
     *         available in this object instance
     */
    Optional<TimingSession> getLastSession()
    {
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(sessions.size() - 1));
    }

    /**
     * Returns the current/last timing session recorded in this object.
     *
     * @return the current/last timing session available in this object
     * @throws IllegalStateException if the object does not contain any recorded session
     * @since 2.4.0
     */
    public TimingSession lastSession()
    {
        return new UnmodifiableTimingSession(getLastSession()
                .orElseThrow(() -> new IllegalStateException(MSG_NO_SESSION_RECORDED)));
    }

    /**
     * Returns all timing sessions recorded in this object.
     *
     * @return a list of timing sessions
     * @since 2.2.0
     */
    protected List<TimingSession> getAllSessions()
    {
        return sessions;
    }

    /**
     * Returns a string containing a formatted output for this timing-session container in
     * default style.
     * <p>
     * The default {@link PrintStyle} (defined by
     * {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be applied.
     * <p>
     * For a custom {@code PrintStyle}, use {@link #toString(PrintStyle)}.
     *
     * @return a string containing a formatted output for this timing-session container in
     *         default style
     * @since 2.2.4
     */
    @Override
    public String toString()
    {
        return PrintUtils.toString(this);
    }

    /**
     * Returns a string containing a formatted output for this timing-session container in a
     * custom {@link PrintStyle}.
     * <p>
     * The {@link PrintFormat} (whether to generate a summarized or detailed view) will be
     * determined by the specified {@link PrintStyle}.
     *
     * @param printStyle the {@link PrintStyle}; if {@code null}, the default
     *                   {@code PrintStyle} (defined by
     *                   {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be
     *                   applied
     * @return a string containing a formatted output for this timing-session container in the
     *         specified {@link PrintStyle}
     * @since 2.4.0
     */
    public String toString(PrintStyle printStyle)
    {
        return PrintUtils.toString(this, printStyle);
    }

}
