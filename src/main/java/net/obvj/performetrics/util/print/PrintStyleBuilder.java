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

import java.util.Objects;

import net.obvj.performetrics.util.DurationFormat;
import net.obvj.performetrics.util.DurationFormatter;

/**
 * A builder for creating {@link PrintStyle} objects. Example:
 *
 * <pre>
 * <code>
 * PrintStyle printStyle = new PrintStyleBuilder()
 *     .withHeader()
 *     .withRowFormat("%-15s  %19s")
 *     .withDurationFormat(DurationFormat.FULL)
 *     .withoutLegends()
 *     .withSimpleLine('-', 36)
 *     .build();
 * </code>
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 * @see PrintFormat
 */
public class PrintStyleBuilder
{
    protected static final String DEFAULT_FORMAT = "%s";

    private final PrintFormat printFormat;

    private boolean printHeader;
    private String headerFormat;

    private String rowFormat;
    private String sectionHeaderFormat;

    private boolean printSectionSummary;
    private String sectionSummaryRowFormat;

    private DurationFormat durationFormat;
    private boolean printLegend;

    private String simpleLine;
    private String alternativeLine;

    /**
     * Creates an empty PrintStyle builder.
     *
     * @param printFormat the target {@link PrintFormat}, not null
     *
     * @throws NullPointerException if the specified PrintFormat is null
     * @since 2.2.2
     */
    public PrintStyleBuilder(PrintFormat printFormat)
    {
        this.printFormat = Objects.requireNonNull(printFormat, "The target PrintFormat must not be null");
    }

    /**
     * Creates a new PrintStyle builder with the same attributes of an existing PrintStyle.
     *
     * @param source the PrintStyle whose attributes are to be copied
     *
     * @throws NullPointerException if the specified PrintStyle is null
     */
    public PrintStyleBuilder(PrintStyle source)
    {
        Objects.requireNonNull(source, "The base PrintStyle must not be null");

        printFormat = source.getPrintFormat();

        printHeader = source.isPrintHeader();
        headerFormat = source.getHeaderFormat();

        rowFormat = source.getRowFormat();
        sectionHeaderFormat = source.getSectionHeaderFormat();

        printSectionSummary = source.isPrintSectionSummary();
        sectionSummaryRowFormat = source.getSectionSummaryRowFormat();

        durationFormat = source.getDurationFormat();
        printLegend = source.isPrintLegend();

        simpleLine = source.getSimpleLine();
        alternativeLine = source.getAlternativeLine();
    }

    /**
     * Defines a format string in printf-style to be applied for all rows.
     * <p>
     * The position of fields must be defined according to the target {@code PrintFormat}:
     * </p>
     *
     * <ul>
     * <li>
     * <p>
     * <b>SUMMARIZED</b>
     * </p>
     * <ol>
     * <li>Counter type (e.g.: "Wall-clock time")</li>
     * <li>Elapsed time</li>
     * </ol>
     * </li>
     *
     * <li>
     * <p>
     * <b>DETAILED</b>
     * </p>
     * <ol>
     * <li>Sequential timing session identifier</li>
     * <li>Elapsed time</li>
     * <li>Elapsed time (accumulated)</li>
     * <li>(Optional) Counter type (e.g.: "Wall-clock time")</li>
     * </ol>
     * </li>
     * </ul>
     *
     * <p>
     * For example: considering the {@code PrintFormat.SUMMARIZED} and
     * {@code DurationFormat.FULL}, the format string {@code "%1$s %2$s"} produces
     * {@code "Wall-clock time 0:00:00.049085700"}, while the format string
     * {@code "%2$s %1$s"} produces {@code "0:00:00.049085700 Wall-clock time"}.
     * </p>
     * <p>
     * Not all fields are mandatory, so the format string {@code "%2$s"} is also valid, and
     * produces {@code "0:00:00.049085700"}.
     * </p>
     *
     * @param format the format string to be applied
     * @return a reference to this builder object for chained calls
     *
     * @see java.util.Formatter
     * @see PrintStyle
     */
    public PrintStyleBuilder withRowFormat(String format)
    {
        rowFormat = format;
        return this;
    }

    /**
     * Enables the header row, to be formatted using the same general row format and preset
     * column names.
     * <p>
     * To specify a different format for the header row, use {@link #withHeader(String)}.
     *
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withHeader()
    {
        printHeader = true;
        return this;
    }

    /**
     * Enables the header row and defines a specific format string in printf-style to be
     * applied.
     * <p>
     * The number and sequence of string positions must be defined according to the target
     * stopwatch formatter:
     * </p>
     *
     * <ul>
     * <li>
     * <p>
     * <b>SUMMARIZED</b>
     * </p>
     * <ol>
     * <li>Counter type</li>
     * <li>Elapsed time</li>
     * </ol>
     * </li>
     *
     * <li>
     * <p>
     * <b>DETAILED</b>
     * </p>
     * <ol>
     * <li>Sequential timing session identifier</li>
     * <li>Elapsed time</li>
     * <li>Elapsed time (accumulated)</li>
     * <li>(Optional) Counter type</li>
     * </ol>
     * </li>
     * </ul>
     *
     * <p>
     * To enable the header without specifying a custom format, use the zero-argument option
     * {@link #withHeader()}.
     *
     * @param format the format string to be applied for the header row
     * @return a reference to this builder object for chained calls
     *
     * @see java.util.Formatter
     * @see PrintStyle
     */
    public PrintStyleBuilder withHeader(String format)
    {
        printHeader = true;
        headerFormat = format;
        return this;
    }

    /**
     * Explicitly disables the header row.
     *
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withoutHeader()
    {
        printHeader = false;
        return this;
    }

    /**
     * Defines a format string in printf-style to the applied for the section headers.
     *
     * @param format the format string to be applied for each section header row
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withSectionHeaderFormat(String format)
    {
        sectionHeaderFormat = format;
        return this;
    }

    /**
     * Explicitly disables the section summary row.
     *
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withoutSectionSummary()
    {
        printSectionSummary = false;
        return this;
    }

    /**
     * Enables the section summary row and defines the format string in printf-style to be
     * applied.
     * <p>
     * <b>Note:</b> The property modified by this method is only applicable for the
     * <b>detailed</b> stopwatch formatter.
     *
     * @param format the format string to be applied for the section summary row
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withSectionSummary(String format)
    {
        printSectionSummary = true;
        sectionSummaryRowFormat = format;
        return this;
    }

    /**
     * Defines a simple line, to be generated using the specified character repeated to a
     * given length.
     *
     * @param character a character to compose the string
     * @param length    number of times to repeat the character; must be &gt; 0
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withSimpleLine(char character, int length)
    {
        return withSimpleLine(generateLine(character, length));
    }

    /**
     * Defines a simple line.
     *
     * @param string the string to be used as the simple line
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withSimpleLine(String string)
    {
        simpleLine = string;
        return this;
    }

    /**
     * Defines an alternative line, to be generated using the specified character repeated to
     * a given length.
     *
     * @param character a character to compose the string
     * @param length    number of times to repeat the character; must be &gt; 0
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withAlternativeLine(char character, int length)
    {
        return withAlternativeLine(generateLine(character, length));
    }

    /**
     * Defines an alternative line.
     *
     * @param string the string to be used as the alternative line
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withAlternativeLine(String string)
    {
        alternativeLine = string;
        return this;
    }

    /**
     * Defines the {@link DurationFormat} to be applied on all rows containing a time
     * duration.
     *
     * @param format the {@link DurationFormat} to be applied
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withDurationFormat(DurationFormat format)
    {
        durationFormat = format;
        return this;
    }

    /**
     * Enables printing legends after time durations.
     *
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withLegends()
    {
        this.printLegend = true;
        return this;
    }

    /**
     * Explicitly disables legends after time durations.
     *
     * @return a reference to this builder object for chained calls
     */
    public PrintStyleBuilder withoutLegends()
    {
        this.printLegend = false;
        return this;
    }

    /**
     * Builds the PrintStyle.
     *
     * @return an immutable {@link PrintStyle}; not null
     */
    public PrintStyle build()
    {
        if (isEmpty(rowFormat))
        {
            rowFormat = DEFAULT_FORMAT;
        }
        if (printHeader && isEmpty(headerFormat))
        {
            // If the header line is not specified, let the general row format be used
            headerFormat = rowFormat;
        }
        if (isEmpty(alternativeLine) && !isEmpty(simpleLine))
        {
            // If the alternative line is not specified let the simple line be used
            alternativeLine = simpleLine;
        }
        if (durationFormat == null)
        {
            durationFormat = DurationFormatter.DEFAULT_FORMAT;
        }
        return new PrintStyle(this);
    }

    /**
     * @return the target {@link PrintFormat}
     */
    protected PrintFormat getPrintFormat()
    {
        return printFormat;
    }

    /**
     * @return the {@link DurationFormat} to be applied on all rows
     */
    protected DurationFormat getDurationFormat()
    {
        return durationFormat;
    }

    /**
     * @return a flag indicating whether or not duration legends shall be printed
     */
    protected boolean isPrintLegend()
    {
        return printLegend;
    }

    /**
     * @return a flag indicating whether or not the header shall be printed
     */
    protected boolean isPrintHeader()
    {
        return printHeader;
    }

    /**
     * @return a flag indicating whether or not a summary shall be printed for each section
     */
    protected boolean isPrintSectionSummary()
    {
        return printSectionSummary;
    }

    /**
     * @return the string format to be applied to the table header
     */
    protected String getHeaderFormat()
    {
        return headerFormat;
    }

    /**
     * @return the string format to be applied to all rows in general
     */
    protected String getRowFormat()
    {
        return rowFormat;
    }

    /**
     * @return the string format to be applied to each section header
     */
    protected String getSectionHeaderFormat()
    {
        return sectionHeaderFormat;
    }

    /**
     * @return the string format for the total/summary row(s)
     */
    protected String getSectionSummaryRowFormat()
    {
        return sectionSummaryRowFormat;
    }

    /**
     * @return a string to be used as simple split line
     */
    protected String getSimpleLine()
    {
        return simpleLine;
    }

    /**
     * @return a string to be used as alternative split line
     */
    protected String getAlternativeLine()
    {
        return alternativeLine;
    }

    /**
     * Returns {@code true} if the specified string is either null or empty.
     *
     * @param string the string to be checked
     * @return true if the specified string is either null or empty.
     */
    public static boolean isEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    /**
     * Generates a string using the specified character repeated to a given length.
     *
     * @param character a character to compose the string
     * @param length    number of times to repeat the character; must be &gt; 0
     * @return a string with repeated character
     */
    public static String generateLine(char character, int length)
    {
        if (length < 1)
        {
            return "";
        }
        String format = "%" + length + "s";
        return String.format(format, "").replace(' ', character);
    }

}
