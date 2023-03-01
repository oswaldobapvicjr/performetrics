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

package net.obvj.performetrics.monitors;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Performetrics;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.print.PrintFormat;
import net.obvj.performetrics.util.print.PrintStyle;
import net.obvj.performetrics.util.print.PrintUtils;

/**
 * A base object for monitorable operations that maintains one or more counters.
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class MonitoredOperation
{
    protected static final Type[] NO_SPECIFIC_TYPE = new Type[0];

    protected Stopwatch stopwatch;

    /**
     * Builds a monitored operation with the specified type(s).
     *
     * @param types the counter type(s) to be maintained by this operation
     */
    protected MonitoredOperation(Type... types)
    {
        if (types.length > 0)
        {
            this.stopwatch = new Stopwatch(types);
        }
        else
        {
            this.stopwatch = new Stopwatch();
        }
    }

    /**
     * Returns the counter types associated with this monitored operation.
     *
     * @return all counter types associated with this monitored operation
     */
    protected List<Type> getTypes()
    {
        return stopwatch.getTypes();
    }

    /**
     * Returns all counters associated with a given type in this monitored operation.
     *
     * @param type the counter type to be fetched
     * @return all counters associated with the given type
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  operation during instantiation
     */
    public List<Counter> getCounters(Type type)
    {
        return stopwatch.getCounters(type);
    }

    /**
     * Returns the total elapsed time for a single counter type, provided that this monitor is
     * keeping a single type.
     *
     * @return the elapsed time for a single counter type in this monitor
     *
     * @throws IllegalStateException if this monitor is keeping more than one counter type
     * @since 2.2.4
     */
    public Duration elapsedTime()
    {
        return stopwatch.elapsedTime();
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this monitor is keeping a single type.
     *
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for a single counter type in this monitor, converted to the
     *         given time unit with the default conversion mode
     *
     * @throws IllegalStateException if this monitor is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit)
    {
        return stopwatch.elapsedTime(timeUnit);
    }

    /**
     * Returns the total elapsed time in the specified time unit for a single counter type,
     * provided that this monitor is keeping a single type.
     *
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for a single counter type in this monitor, converted to the
     *         given time unit with the given conversion mode
     *
     * @throws IllegalStateException if this monitor is keeping more than one counter type
     * @since 2.2.4
     */
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return stopwatch.elapsedTime(timeUnit, conversionMode);
    }

    /**
     * Returns the total elapsed time of a specific counter type.
     *
     * @param type the counter type to be fetched
     * @return the total elapsed time for the specified counter
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  operation during instantiation
     * @since 2.1.0
     */
    public Duration elapsedTime(Type type)
    {
        return stopwatch.elapsedTime(type);
    }

    /**
     * Returns the total elapsed time of a specific counter type, in the specified time unit.
     *
     * @param type     the counter type to be fetched
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the default conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  operation during instantiation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return stopwatch.elapsedTime(type, timeUnit);
    }

    /**
     * Returns the total elapsed time of a specific counter, in the specified time unit, with
     * a custom {@link ConversionMode} applied.
     *
     * @param type           the counter type to be fetched
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be applied
     * @return the elapsed time for the specified counter, converted to the given time unit
     *         using the given conversion mode.
     *
     * @throws IllegalArgumentException if the specified type was not assigned to the
     *                                  operation during instantiation
     * @since 2.1.0
     */
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return stopwatch.elapsedTime(type, timeUnit, conversionMode);
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
        PrintUtils.print(stopwatch, printStream);
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
        PrintUtils.print(stopwatch, printStream, printStyle);
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
        PrintUtils.printSummary(stopwatch, printStream, printStyle);
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
     * @throws NullPointerException     if the {@code PrintStream} is null
     * @throws IllegalArgumentException if the specified {@code PrintStyle} is not compatible
     *                                  with {@link PrintFormat#DETAILED}
     * @since 2.2.1
     */
    public void printDetails(PrintStream printStream, PrintStyle printStyle)
    {
        PrintUtils.printDetails(stopwatch, printStream, printStyle);
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
        return stopwatch.getAllCountersByType();
    }

    /**
     * Cleans all counters available in this monitored operation.
     *
     * @since 2.2.0
     */
    public void reset()
    {
        stopwatch.reset();
    }

    /**
     * Returns a string containing a formatted output for this operation.
     * <p>
     * The default {@link PrintStyle} (defined by
     * {@link Performetrics#setDefaultPrintStyle(PrintStyle)}) will be applied.
     * <p>
     *
     * @return a string containing formatted elapsed times for this opearation
     * @since 2.2.4
     */
    @Override
    public String toString()
    {
        return stopwatch.toString();
    }
}
