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

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredOperation;
import net.obvj.performetrics.monitors.MonitoredRunnable;
import net.obvj.performetrics.util.print.PrintStyle;

/**
 * A Facade class meant to provide a simple interface for common parameters setup and
 * other operations.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Performetrics
{
    /**
     * This is a utility class, not meant to be instantiated.
     */
    private Performetrics()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Sets a time unit to be maintained by default if no specific time unit is specified.
     *
     * @param timeUnit the {@link TimeUnit} to set
     */
    public static void setDefaultTimeUnit(TimeUnit timeUnit)
    {
        ConfigurationHolder.getConfiguration().setTimeUnit(timeUnit);
    }

    /**
     * Sets a conversion mode to be applied by supported operations if no specific mode is
     * set.
     *
     * @param conversionMode the {@link ConversionMode} to set
     */
    public static void setDefaultConversionMode(ConversionMode conversionMode)
    {
        ConfigurationHolder.getConfiguration().setConversionMode(conversionMode);
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * mode is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if a number outside the allowed range is received
     */
    public static void setScale(int scale)
    {
        ConfigurationHolder.getConfiguration().setScale(scale);
    }

    /**
     * Defines the default {@link PrintStyle} to be applied by the summarized stopwatch
     * formatter.
     * <p>
     * The object will be used by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#printSummary(java.io.PrintStream)}</li>
     * <li>{@link MonitoredOperation#printSummary(java.io.PrintStream)}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified PrintStyle is null
     *
     * @since 2.2.1
     */
    public static void setDefaultPrintStyleForSummary(PrintStyle printStyle)
    {
        ConfigurationHolder.getConfiguration().setPrintStyleForSummary(printStyle);
    }

    /**
     * Defines the default {@link PrintStyle} to be applied by the detailed stopwatch
     * formatter.
     * <p>
     * The object will be used by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#printDetails(java.io.PrintStream)}</li>
     * <li>{@link MonitoredOperation#printDetails(java.io.PrintStream)}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified PrintStyle is null
     *
     * @since 2.2.1
     */
    public static void setDefaultPrintStyleForDetails(PrintStyle printStyle)
    {
        ConfigurationHolder.getConfiguration().setPrintStyleForDetails(printStyle);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for all available counter types.
     * <p>
     * For example:
     *
     * <pre>
     * {@code MonitoredOperation operation =
     *         Performetrics.monitorOperation(() -> myObj.exec());
     * Duration elapsedTime = operation.elapsedTime(Type.WALL_CLOCK_TIME);}
     * </pre>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @return the resulting {@link MonitoredOperation}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredOperation monitorOperation(Runnable runnable)
    {
        return monitorOperation(runnable, new Type[0]);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for the specified counter type(s) only.
     * <p>
     * For example:
     *
     * <pre>
     * {@code MonitoredOperation operation =
     *         Performetrics.monitorOperation(() -> myObj.exec(), Type.CPU_TIME);
     * Duration elapsedTime = operation.elapsedTime(Type.CPU_TIME);}
     * </pre>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @param types    the counter types to be measured in the operation
     * @return the resulting {@link MonitoredOperation}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredOperation monitorOperation(Runnable runnable, Type... types)
    {
        MonitoredRunnable monitoredRunnable = new MonitoredRunnable(runnable, types);
        monitoredRunnable.run();
        return monitoredRunnable;
    }
}
