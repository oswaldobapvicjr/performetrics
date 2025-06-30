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

package net.obvj.performetrics.util.print;

import java.io.PrintStream;

import net.obvj.performetrics.Performetrics;
import net.obvj.performetrics.TimingSessionContainer;
import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * This class groups all custom printing operations in a single place.
 *
 * @author oswaldo.bapvic.jr
 */
public class PrintUtils
{

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private PrintUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints summarized elapsed times from the given {@link TimingSessionContainer} into the
     * specified print stream.
     * <p>
     * The default {@link PrintStyle} (defined via
     * {@link Performetrics#configuration()}) will be applied.
     *
     * @param container   the {@link TimingSessionContainer} to be printed
     * @param printStream the print stream to which data will be sent
     *
     * @throws NullPointerException if a null {@link TimingSessionContainer} or print stream
     *                              is received
     *
     * @since 2.2.1
     */
    public static void printSummary(TimingSessionContainer container, PrintStream printStream)
    {
        printSummary(container, printStream, null);
    }

    /**
     * Prints summarized elapsed times from the given {@link TimingSessionContainer} into the
     * specified print stream, with a custom {@link PrintStyle}.
     *
     * @param container   the {@link TimingSessionContainer} to be printed
     * @param printStream the print stream to which data will be sent
     * @param printStyle  the {@link PrintStyle}; if {@code null}, the default
     *                    {@code PrintStyle} (defined via
     *                    {@link Performetrics#configuration()}) will be applied
     *
     * @throws NullPointerException     if a null {@link TimingSessionContainer} or print
     *                                  stream is received
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with
     *                                  {@link PrintFormat#SUMMARIZED}
     *
     * @since 2.2.1
     */
    public static void printSummary(TimingSessionContainer container, PrintStream printStream, PrintStyle printStyle)
    {
        PrintStyle style = printStyle != null ? printStyle
                : ConfigurationHolder.getConfiguration().getPrintStyleForSummary();
        printStream.print(PrintFormat.SUMMARIZED.format(container, style));
    }

    /**
     * Prints detailed information about timing sessions from the given
     * {@link TimingSessionContainer} into the specified print stream.
     * <p>
     * The default {@link PrintStyle} (defined via
     * {@link Performetrics#configuration()}) will be applied.
     *
     * @param container   the {@link TimingSessionContainer} to be printed
     * @param printStream the print stream to which information will be sent
     *
     * @throws NullPointerException if a null {@link TimingSessionContainer} or print stream
     *                              is received
     *
     * @since 2.2.1
     */
    public static void printDetails(TimingSessionContainer container, PrintStream printStream)
    {
        printDetails(container, printStream, null);
    }

    /**
     * Prints detailed information about timing sessions from the given
     * {@link TimingSessionContainer} into the specified print stream, with a custom
     * {@link PrintStyle}.
     *
     * @param container   the {@link TimingSessionContainer} to be printed
     * @param printStream the print stream to which information will be sent
     * @param printStyle  the {@link PrintStyle}; if {@code null}, the default
     *                    {@code PrintStyle} (defined via
     *                    {@link Performetrics#configuration()}) will be applied
     *
     * @throws NullPointerException     if a null {@link TimingSessionContainer} or print
     *                                  stream is received
     * @throws IllegalArgumentException if the specified {@link PrintStyle} is not compatible
     *                                  with {@link PrintFormat#DETAILED}
     *
     * @since 2.2.1
     */
    public static void printDetails(TimingSessionContainer container, PrintStream printStream, PrintStyle printStyle)
    {
        PrintStyle style = printStyle != null ? printStyle
                : ConfigurationHolder.getConfiguration().getPrintStyleForDetails();
        printStream.print(PrintFormat.DETAILED.format(container, style));
    }

    /**
     * Prints elapsed times from the given {@link TimingSessionContainer} into the specified
     * print stream.
     * <p>
     * The default {@link PrintStyle} (defined via {@link Performetrics#configuration()})
     * will be applied.
     *
     * @param container   the {@link TimingSessionContainer} to be printed
     * @param printStream the print stream to which data will be sent
     *
     * @throws NullPointerException if a null {@link TimingSessionContainer} or print stream
     *                              is received
     * @since 2.4.0
     */
    public static void print(TimingSessionContainer container, PrintStream printStream)
    {
        print(container, printStream, null);
    }

    /**
     * Prints elapsed times from the given {@link TimingSessionContainer} into the specified
     * print stream, with a custom {@link PrintStyle}.
     * <p>
     * The {@link PrintFormat} (whether to generate a summarized or detailed view) will be
     * determined by the specified {@link PrintStyle}.
     *
     * @param container   the {@link TimingSessionContainer} to be printed; not null
     * @param printStream the print stream to which data will be sent; not null
     * @param printStyle  the {@link PrintStyle}; if {@code null}, the default
     *                    {@code PrintStyle} (defined via
     *                    {@link Performetrics#configuration()})will be applied
     *
     * @throws NullPointerException if a null {@link TimingSessionContainer} or print stream
     *                              is received
     * @since 2.4.0
     */
    public static void print(TimingSessionContainer container, PrintStream printStream, PrintStyle printStyle)
    {
        printStream.print(toString(container, printStyle));
    }

    /**
     * Returns a string containing a formatted summary from the given
     * {@link TimingSessionContainer} in default style.
     * <p>
     * The default {@link PrintStyle} (defined via
     * {@link Performetrics#configuration()}) will be applied.
     *
     * @param container to {@link TimingSessionContainer} to be used; not null
     * @return string containing a formatted output from the given
     *         {@link TimingSessionContainer}
     *
     * @since 2.4.0
     */
    public static String toString(TimingSessionContainer container)
    {
        return toString(container, null);
    }

    /**
     * Returns a string containing a formatted output from the given
     * {@link TimingSessionContainer} in a custom {@link PrintStyle}.
     * <p>
     * The {@link PrintFormat} (whether to generate a summarized or detailed view) will be
     * determined by the specified {@link PrintStyle}.
     *
     * @param container  to {@link TimingSessionContainer} to be used
     * @param printStyle the {@link PrintStyle}; if {@code null}, the default
     *                   {@code PrintStyle} (defined via
     *                   {@link Performetrics#configuration()}) will be applied
     * @return string containing a formatted output from the given
     *         {@link TimingSessionContainer}
     *
     * @since 2.4.0
     */
    public static String toString(TimingSessionContainer container, PrintStyle printStyle)
    {
        PrintStyle style = printStyle != null ? printStyle
                : ConfigurationHolder.getConfiguration().getPrintStyle();
        return style.toString(container);
    }

}
