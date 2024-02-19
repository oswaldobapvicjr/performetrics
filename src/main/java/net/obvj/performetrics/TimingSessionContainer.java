package net.obvj.performetrics;

import static net.obvj.performetrics.Performetrics.ALL_TYPES;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch.State;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.print.PrintFormat;
import net.obvj.performetrics.util.print.PrintStyle;
import net.obvj.performetrics.util.print.PrintUtils;

public class TimingSessionContainer
{

    static final String MSG_TYPE_NOT_SPECIFIED = "\"{0}\" was not assigned during instantiation. Available type(s): {1}";
    static final String MSG_NOT_A_SINGLE_TYPE = "This stopwatch is keeping more than one type. Please inform a specific type for this operation.";
    static final String MSG_NO_SESSION_RECORDED = "No session recorded";

    private final List<Type> types;
    private List<TimingSession> sessions;

    public TimingSessionContainer(List<Type> types)
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
     * Cleans all timing sessions in this stopwatch.
     */
    public void reset()
    {
        sessions = new ArrayList<>();
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
    protected List<Counter> getCounters()
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
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
     */
    protected void startNewSession()
    {
        TimingSession session = new TimingSession(types);
        sessions.add(session);
        session.start();
    }

    /**
     * Stops the current timing session.
     * <p>
     * <b>Note:</b> This method is internal as the current {@link State} defines whether or
     * not this action is allowed.
     */
    protected void stopCurrentSession()
    {
        getLastSession().ifPresent(TimingSession::stop);
    }

    /**
     * Returns the current/last timing session available in this stopwatch, or
     * {@code Optional.empty()} if no timing session available yet.
     *
     * @return an {@link Optional} possibly containing the current/last timing session
     *         available in this stopwatch instance
     */
    protected Optional<TimingSession> getLastSession()
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
    protected List<TimingSession> getAllSessions()
    {
        return sessions;
    }

    /**
     * Returns a string containing a formatted output for this stopwatch in default style.
     * <p>
     * The default {@link PrintStyle} (defined by
     * {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be applied.
     * <p>
     * For a custom {@code PrintStyle}, use {@link #toString(PrintStyle)}.
     *
     * @return a string containing a formatted output for this stopwatch in default style
     * @since 2.2.4
     */
    @Override
    public String toString()
    {
        return PrintUtils.toString(this);
    }

    /**
     * Returns a string containing a formatted output for this stopwatch in a custom
     * {@link PrintStyle}.
     * <p>
     * The {@link PrintFormat} (whether to generate a summarized or detailed view) will be
     * determined by the specified {@link PrintStyle}.
     *
     * @param printStyle the {@link PrintStyle}; if {@code null}, the default
     *                   {@code PrintStyle} (defined by
     *                   {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be
     *                   applied
     * @return a string containing a formatted output for this stopwatch in the specified
     *         {@link PrintStyle}
     * @since 2.4.0
     */
    public String toString(PrintStyle printStyle)
    {
        return PrintUtils.toString(this, printStyle);
    }

}
