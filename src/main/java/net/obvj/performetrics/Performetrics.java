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

import java.util.List;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
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
     * A list containing all the available counter types.
     *
     * @since 2.5.0
     */
    public static final List<Type> ALL_TYPES = List.of(Type.values());

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private Performetrics()
    {
        throw new IllegalStateException("Instantiation not allowed");
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
     * Defines the default {@link PrintStyle} to be applied when no style is specified.
     * <p>
     * The object will be used by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#print(java.io.PrintStream)}</li>
     * <li>{@link Stopwatch#toString()}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
     *
     * @since 2.4.0
     */
    public static void setDefaultPrintStyle(PrintStyle printStyle)
    {
        ConfigurationHolder.getConfiguration().setPrintStyle(printStyle);
    }

    /**
     * Defines the default {@link PrintStyle} to be applied by the summarized stopwatch
     * formatter.
     * <p>
     * The object will be used by the following operation:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#printSummary(java.io.PrintStream)}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
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
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
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
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredRunnable runnable =}
     * {@code         Performetrics.monitorOperation(() -> myObj.exec());}
     * {@code Duration elapsedTime = runnable.elapsedTime(Type.WALL_CLOCK_TIME);}
     * </pre>
     *
     * </blockquote>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @return the resulting {@link MonitoredRunnable}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredRunnable monitorOperation(Runnable runnable)
    {
        return monitorOperation(runnable, new Type[0]);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for the specified counter type(s) only.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredRunnable runnable = Performetrics}
     * {@code         .monitorOperation(() -> myObj.exec(), Type.CPU_TIME);}
     * {@code Duration elapsedTime = runnable.elapsedTime(Type.CPU_TIME);}
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * <b>Note:</b> If no type is specified, then all of the available types will be
     * maintained.
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @param types    the counter types to be measured in the operation
     * @return the resulting {@link MonitoredRunnable}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredRunnable monitorOperation(Runnable runnable, Type... types)
    {
        MonitoredRunnable monitoredRunnable = new MonitoredRunnable(runnable, types);
        monitoredRunnable.run();
        return monitoredRunnable;
    }
}
