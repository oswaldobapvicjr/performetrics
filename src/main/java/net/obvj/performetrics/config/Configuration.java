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

package net.obvj.performetrics.config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.monitors.MonitoredOperation;
import net.obvj.performetrics.util.print.PrintFormat;
import net.obvj.performetrics.util.print.PrintStyle;

/**
 * An object that maintains common configurable parameters for Perfometrics objects.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Configuration
{
    /**
     * The initial time unit to be maintained if no specific time unit informed.
     */
    static final TimeUnit INITIAL_TIME_UNIT = TimeUnit.NANOSECONDS;

    /**
     * The initial conversion mode to be applied if no specific mode is set.
     */
    static final ConversionMode INITIAL_CONVERSION_MODE = ConversionMode.DOUBLE_PRECISION;

    /**
     * The initial maximum number of decimal places applicable if double-precision conversion
     * mode is set.
     */
    static final int INITIAL_SCALE = 9;

    /**
     * The initial {@link PrintStyle} to be applied by default.
     *
     * @since 2.4.0
     */
    static final PrintStyle INITIAL_PRINT_STYLE = PrintStyle.SUMMARIZED_TABLE_FULL;

    /**
     * The initial {@link PrintStyle} to be applied by default for the summarized
     * {@link PrintFormat}.
     *
     * @since 2.2.1
     */
    static final PrintStyle INITIAL_PRINT_STYLE_FOR_SUMMARY = PrintStyle.SUMMARIZED_TABLE_FULL;

    /**
     * The initial {@link PrintStyle} to be applied by default for the detailed
     * {@link PrintFormat}.
     *
     * @since 2.2.1
     */
    static final PrintStyle INITIAL_PRINT_STYLE_FOR_DETAILS = PrintStyle.DETAILED_TABLE_FULL;

    private static final String MSG_PRINT_STYLE_MUST_NOT_BE_NULL = "The default PrintStyle must not be null";

    private TimeUnit timeUnit;
    private ConversionMode conversionMode;
    private int scale;
    private PrintStyle printStyle;
    private PrintStyle printStyleForSummary;
    private PrintStyle printStyleForDetails;

    /**
     * Builds a new {@code Configuration} with all initial values.
     */
    public Configuration()
    {
        timeUnit = INITIAL_TIME_UNIT;
        conversionMode = INITIAL_CONVERSION_MODE;
        scale = INITIAL_SCALE;
        printStyle = INITIAL_PRINT_STYLE;
        printStyleForSummary = INITIAL_PRINT_STYLE_FOR_SUMMARY;
        printStyleForDetails = INITIAL_PRINT_STYLE_FOR_DETAILS;
    }

    /**
     * Returns the time unit maintained by default if no specific time unit is specified.
     *
     * @return the time unit maintained by default
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Defines the time unit to be maintained by default if no specific time unit is
     * specified.
     *
     * @param timeUnit the {@link TimeUnit} to set
     * @throws NullPointerException if the specified time unit is null
     */
    public void setTimeUnit(TimeUnit timeUnit)
    {
        this.timeUnit = Objects.requireNonNull(timeUnit, "the default time unit must not be null");
    }

    /**
     * Returns the conversion mode applied in supported operations if no specific mode is set.
     *
     * @return the conversion mode applied in supported operations
     */
    public ConversionMode getConversionMode()
    {
        return conversionMode;
    }

    /**
     * Defines the default conversion mode to be applied in supported operations if no
     * specific mode is set.
     *
     * @param conversionMode the {@link ConversionMode} to set
     * @throws NullPointerException if the specified conversion mode is null
     */
    public void setConversionMode(ConversionMode conversionMode)
    {
        this.conversionMode = Objects.requireNonNull(conversionMode, "the default conversion mode must not be null");
    }

    /**
     * Returns the maximum number of decimal places applied if double-precision conversion
     * mode is set.
     *
     * @return the the maximum number of decimal places applied
     */
    public int getScale()
    {
        return scale;
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * mode is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if the specified number is outside the allowed range
     */
    public void setScale(int scale)
    {
        if (scale < 0 || scale > 16)
        {
            throw new IllegalArgumentException("The scale must be a number between 0 and 16");
        }
        this.scale = scale;
    }

    /**
     * Returns the default {@link PrintStyle} to be applied when no style is specified.
     *
     * @return the default {@link PrintStyle} to be applied when no style is specified
     * @since 2.4.0
     */
    public PrintStyle getPrintStyle()
    {
        return printStyle;
    }

    /**
     * Defines the default {@link PrintStyle} to be applied when no style is specified.
     * <p>
     * The object will be applied by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#print(java.io.PrintStream)}</li>
     * <li>{@link Stopwatch#toString()}</li>
     * <li>{@link MonitoredOperation#print(java.io.PrintStream)}</li>
     * <li>{@link MonitoredOperation#toString()}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
     *
     * @since 2.4.0
     */
    public void setPrintStyle(PrintStyle printStyle)
    {
        this.printStyle = Objects.requireNonNull(printStyle, MSG_PRINT_STYLE_MUST_NOT_BE_NULL);
    }

    /**
     * Returns the default {@link PrintStyle} for the summarized stopwatch formatter.
     *
     * @return the default {@link PrintStyle} for the summarized stopwatch formatter
     * @since 2.2.1
     */
    public PrintStyle getPrintStyleForSummary()
    {
        return printStyleForSummary;
    }

    /**
     * Defines the default {@link PrintStyle} for the summarized stopwatch formatter.
     * <p>
     * The object will be used by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#printSummary(java.io.PrintStream)}</li>
     * <li>{@link MonitoredOperation#printSummary(java.io.PrintStream)}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
     *
     * @since 2.2.1
     */
    public void setPrintStyleForSummary(PrintStyle printStyle)
    {
        this.printStyleForSummary = Objects.requireNonNull(printStyle, MSG_PRINT_STYLE_MUST_NOT_BE_NULL);
    }

    /**
     * Returns the default {@link PrintStyle} for the detailed stopwatch formatter.
     *
     * @return the default {@link PrintStyle} for the detailed stopwatch formatter
     * @since 2.2.1
     */
    public PrintStyle getPrintStyleForDetails()
    {
        return printStyleForDetails;
    }

    /**
     * Defines the default {@link PrintStyle} for the detailed stopwatch formatter.
     * <p>
     * The object will be used by the following operations:
     * </p>
     * <ul>
     * <li>{@link Stopwatch#printDetails(java.io.PrintStream)}</li>
     * <li>{@link MonitoredOperation#printDetails(java.io.PrintStream)}</li>
     * </ul>
     *
     * @param printStyle the {@link PrintStyle} to be set; not null
     * @throws NullPointerException if the specified {@code PrintStyle} is null
     *
     * @since 2.2.1
     */
    public void setPrintStyleForDetails(PrintStyle printStyle)
    {
        this.printStyleForDetails = Objects.requireNonNull(printStyle, MSG_PRINT_STYLE_MUST_NOT_BE_NULL);
    }

}
