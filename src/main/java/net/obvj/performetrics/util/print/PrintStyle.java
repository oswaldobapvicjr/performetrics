package net.obvj.performetrics.util.print;

import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Defines a set of attributes used by a {@link PrintFormat} to generate a String output
 * out of a {@link Stopwatch} object.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public class PrintStyle
{

    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * in tabular format without header.
     * <p>
     * Sample output:
     *
     * <pre>
     * Wall clock time    0:00:04.455383500
     * CPU time           0:00:00.109375000
     * User time          0:00:00.046875000
     * System time        0:00:00.062500000
     * </pre>
     *
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_TABLE_NO_HEADER = PrintStyle.builder(PrintFormat.SUMMARIZED)
            .withRowFormat("%-15s  %19s")
            .withoutHeader()
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .build();


    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * in tabular format, with horizontal lines separating each row.
     * <p>
     * Sample output:
     *
     * <pre>
     * ====================================
     * Counter                 Elapsed time
     * ------------------------------------
     * Wall clock time    0:00:04.455383500
     * CPU time           0:00:00.109375000
     * User time          0:00:00.046875000
     * System time        0:00:00.062500000
     * ====================================
     * </pre>
     *
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_TABLE_FULL = PrintStyle.builder(SUMMARIZED_TABLE_NO_HEADER)
            .withHeader()
            .withSimpleLine('-', 36)
            .withAlternativeLine('=', 36)
            .build();


    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values).
     * <p>
     * Sample output:
     *
     * <pre>
     * "Counter","Elapsed time"
     * "Wall clock time","0:00:04.455383500"
     * "CPU time","0:00:00.109375000"
     * "User time","0:00:00.046875000"
     * "System time","0:00:00.062500000"
     * </pre>
     *
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_CSV = PrintStyle.builder(PrintFormat.SUMMARIZED)
            .withHeader()
            .withRowFormat("\"%s\",\"%s\"")
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .build();

    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values), <b>without</b> header.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Wall clock time","0:00:04.455383500"
     * "CPU time","0:00:00.109375000"
     * "User time","0:00:00.046875000"
     * "System time","0:00:00.062500000"
     * </pre>
     *
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_CSV_NO_HEADER = PrintStyle.builder(SUMMARIZED_CSV)
            .withoutHeader()
            .build();

    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values), with elapsed times expressed using the ISO-8601
     * duration format.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Counter","Elapsed time"
     * "Wall clock time","PT4.4553835S"
     * "CPU time","PT0.109375S"
     * "User time","PT0.046875S"
     * "System time","PT0.0625S"
     * </pre>
     *
     * @since 2.2.4
     * @see DurationFormat#ISO_8601
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_CSV_ISO_8601 = PrintStyle.builder(SUMMARIZED_CSV)
            .withDurationFormat(DurationFormat.ISO_8601)
            .build();

    /**
     * A string-based style for the <b>summarized</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values), with elapsed times expressed using the ISO-8601
     * duration format and <b>no</b> header.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Wall clock time","PT4.4553835S"
     * "CPU time","PT0.109375S"
     * "User time","PT0.046875S"
     * "System time","PT0.0625S"
     * </pre>
     *
     * @since 2.2.4
     * @see DurationFormat#ISO_8601
     * @see PrintFormat#SUMMARIZED
     */
    public static final PrintStyle SUMMARIZED_CSV_ISO_8601_NO_HEADER = PrintStyle.builder(SUMMARIZED_CSV_ISO_8601)
            .withoutHeader()
            .build();

    /**
     * A string-based style for the <b>detailed</b> stopwatch formatter, with horizontal lines
     * separating each row, and total elapsed time for each counter.
     * <p>
     * Sample output:
     *
     * <pre>
     * ===============================================
     *     #         Elapsed time     Elapsed time (+)
     * ===============================================
     * Wall clock time
     * -----------------------------------------------
     *     1    0:00:01.055824100    0:00:01.055824100
     *     2    0:00:00.836569500    0:00:01.892393600
     *     3    0:00:00.836091100    0:00:02.728484700
     *     4    0:00:00.837092700    0:00:03.565577400
     * -----------------------------------------------
     * TOTAL                         0:00:03.565577400
     * ===============================================
     * CPU time
     * -----------------------------------------------
     *     1    0:00:00.109375000    0:00:00.109375000
     *     2    0:00:00.000000000    0:00:00.109375000
     *     3    0:00:00.000000000    0:00:00.109375000
     *     4    0:00:00.015625000    0:00:00.125000000
     * -----------------------------------------------
     * TOTAL                         0:00:00.125000000
     * ===============================================
     * </pre>
     *
     * @see PrintFormat#DETAILED
     */
    public static final PrintStyle DETAILED_TABLE_FULL = PrintStyle.builder(PrintFormat.DETAILED)
            .withRowFormat("%5s  %19s  %19s")
            .withHeader()
            .withSectionHeaderFormat("%s")
            .withSectionSummary("TOTAL %41s")
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .withSimpleLine('-', 47)
            .withAlternativeLine('=', 47)
            .build();

    /**
     * A string-based style for the <b>detailed</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values).
     * <p>
     * Sample output:
     *
     * <pre>
     * "Counter","Session","Elapsed time","Elapsed time (+)"
     * "Wall clock time",1,"0:00:00.032319200","0:00:00.032319200"
     * "Wall clock time",2,"0:00:00.016766500","0:00:00.049085700"
     * "Wall clock time",3,"0:00:00.014459500","0:00:00.063545200"
     * "CPU time",1,"0:00:00.031250000","0:00:00.031250000"
     * "CPU time",2,"0:00:00.015625000","0:00:00.046875000"
     * "CPU time",3,"0:00:00.015625000","0:00:00.062500000"
     * </pre>
     *
     * @since 2.2.2
     * @see PrintFormat#DETAILED
     */
    public static final PrintStyle DETAILED_CSV = PrintStyle.builder(PrintFormat.DETAILED)
            .withRowFormat("\"%4$s\",%1$s,\"%2$s\",\"%3$s\"")
            .withHeader("\"%4$s\",\"Session\",\"%2$s\",\"%3$s\"")
            .withoutSectionSummary()
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .build();

    /**
     * A string-based style for the <b>detailed</b> stopwatch formatter, which prints data
     * as CSV (comma-separated values), <b>without</b> header.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Wall clock time",1,"0:00:00.032319200","0:00:00.032319200"
     * "Wall clock time",2,"0:00:00.016766500","0:00:00.049085700"
     * "Wall clock time",3,"0:00:00.014459500","0:00:00.063545200"
     * "CPU time",1,"0:00:00.031250000","0:00:00.031250000"
     * "CPU time",2,"0:00:00.015625000","0:00:00.046875000"
     * "CPU time",3,"0:00:00.015625000","0:00:00.062500000"
     * </pre>
     *
     * @since 2.2.2
     * @see PrintFormat#DETAILED
     */
    public static final PrintStyle DETAILED_CSV_NO_HEADER = PrintStyle.builder(DETAILED_CSV)
            .withoutHeader()
            .build();

    /**
     * A string-based style for the <b>detailed</b> stopwatch formatter, which prints data as
     * CSV (comma-separated values), with elapsed times expressed using the ISO-8601 duration
     * format.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Counter","Session","Elapsed time","Elapsed time (+)"
     * "Wall clock time",1,"PT0.0323192S","PT0.0323192S"
     * "Wall clock time",2,"PT0.0167665S","PT0.0490857S"
     * "CPU time",1,"PT0.03125S","PT0.03125S"
     * "CPU time",2,"PT0.015625S","PT0.046875S"
     * </pre>
     *
     * @since 2.2.4
     * @see DurationFormat#ISO_8601
     * @see PrintFormat#DETAILED
     */
    public static final PrintStyle DETAILED_CSV_ISO_8601 = PrintStyle.builder(DETAILED_CSV)
            .withDurationFormat(DurationFormat.ISO_8601)
            .build();

    /**
     * A string-based style for the <b>detailed</b> stopwatch formatter, which prints data as
     * CSV (comma-separated values), with elapsed times expressed using the ISO-8601 duration
     * format and <b>no</b> header.
     * <p>
     * Sample output:
     *
     * <pre>
     * "Wall clock time",1,"PT0.0323192S","PT0.0323192S"
     * "Wall clock time",2,"PT0.0167665S","PT0.0490857S"
     * "CPU time",1,"PT0.03125S","PT0.03125S"
     * "CPU time",2,"PT0.015625S","PT0.046875S"
     * </pre>
     *
     * @since 2.2.4
     * @see DurationFormat#ISO_8601
     * @see PrintFormat#DETAILED
     */
    public static final PrintStyle DETAILED_CSV_ISO_8601_NO_HEADER = PrintStyle.builder(DETAILED_CSV_ISO_8601)
            .withoutHeader()
            .build();


    private final PrintFormat printFormat;

    private final boolean printHeader;
    private final String headerFormat;

    private final String rowFormat;
    private final String sectionHeaderFormat;

    private final boolean printSectionSummary;
    private final String sectionSummaryRowFormat;

    private final DurationFormat durationFormat;
    private final boolean printLegend;

    private final String simpleLine;
    private final String alternativeLine;

    /**
     * Returns an empty PrintStyle builder.
     *
     * @param printFormat the target {@link PrintFormat}, not null
     * @return a {@link PrintStyleBuilder} instance
     *
     * @throws NullPointerException if the specified PrintFormat is null
     * @since 2.2.2
     */
    public static PrintStyleBuilder builder(PrintFormat printFormat)
    {
        return new PrintStyleBuilder(printFormat);
    }

    /**
     * Returns a PrintStyle builder with the same attributes of an existing PrintStyle.
     *
     * @param source the PrintStyle whose attributes are to be copied
     * @return a new {@link PrintStyleBuilder} instance with the same attributes of the
     *         specified source object
     *
     * @throws NullPointerException if the specified PrintStyle is null
     * @since 2.2.2
     */
    public static PrintStyleBuilder builder(PrintStyle source)
    {
        return new PrintStyleBuilder(source);
    }

    /**
     * Creates a new PrintStyle.
     *
     * @param builder the {@link PrintStyleBuilder}
     */
    protected PrintStyle(PrintStyleBuilder builder)
    {
        printFormat = builder.getPrintFormat();
        printHeader = builder.isPrintHeader();
        headerFormat = builder.getHeaderFormat();
        rowFormat = builder.getRowFormat();
        sectionHeaderFormat = builder.getSectionHeaderFormat();
        printSectionSummary = builder.isPrintSectionSummary();
        sectionSummaryRowFormat = builder.getSectionSummaryRowFormat();
        durationFormat = builder.getDurationFormat();
        printLegend = builder.isPrintLegend();
        simpleLine = builder.getSimpleLine();
        alternativeLine = builder.getAlternativeLine();
    }

    /**
     * Returns the target {@code PrintFormat}.
     *
     * @return the target {@link PrintFormat}
     */
    public PrintFormat getPrintFormat()
    {
        return printFormat;
    }

    /**
     * Returns the format to be applied to all durations in the output.
     *
     * @return the {@link DurationFormat} to be applied to all rows
     */
    public DurationFormat getDurationFormat()
    {
        return durationFormat;
    }

    /**
     * Returns a flag indicating whether or not duration legends shall be printed for
     * durations in the output.
     *
     * @return a flag indicating whether or not duration legends shall be printed
     */
    public boolean isPrintLegend()
    {
        return printLegend;
    }

    /**
     * Returns a flag indicating whether or not the header shall be printed.
     *
     * @return a flag indicating whether or not the header shall be printed
     */
    public boolean isPrintHeader()
    {
        return printHeader;
    }

    /**
     * Returns a flag indicating whether or not a summary line shall be printed for each
     * section in the output.
     *
     * @return a flag indicating whether or not the section summary shall be printed
     */
    public boolean isPrintSectionSummary()
    {
        return printSectionSummary;
    }

    /**
     * Returns the format to be applied to the header string of the output.
     *
     * @return the string format to be applied to the header
     */
    public String getHeaderFormat()
    {
        return headerFormat;
    }

    /**
     * Returns the general row format to be applied.
     *
     * @return the format to be applied to all rows in general
     */
    public String getRowFormat()
    {
        return rowFormat;
    }

    /**
     * Returns the format to be applied to each section header.
     *
     * @return the format to be applied to each section header
     */
    public String getSectionHeaderFormat()
    {
        return sectionHeaderFormat;
    }

    /**
     * Returns the format to be applied to the total/summary row for each section.
     *
     * @return the format to be applied to the total/summary row(s)
     */
    public String getSectionSummaryRowFormat()
    {
        return sectionSummaryRowFormat;
    }

    /**
     * Returns a simple string to be applied to the table style.
     *
     * @return a string to be used as simple split line
     */
    public String getSimpleLine()
    {
        return simpleLine;
    }

    /**
     * Returns an alternative string to be applied to the table style.
     *
     * @return a string to be used as alternative split line
     */
    public String getAlternativeLine()
    {
        return alternativeLine;
    }

}
