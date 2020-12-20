package net.obvj.performetrics.util.print;

import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Defines a set of attributes used by a {@link PrintFormat} to generate a tabular
 * string output out of a {@link Stopwatch} object.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public class PrintStyle
{
    /**
     * A string-based style for use with the summarized stopwatch formatter, with horizontal
     * lines separating each row.
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
    public static final PrintStyle SUMMARIZED_HORIZONTAL_LINES = new PrintStyleBuilder()
            .withRowFormat("%-15s  %19s")
            .withHeader()
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .withSimpleLine('-', 36)
            .withAlternativeLine('=', 36)
            .build();

    /**
     * A string-based style for use with the detailed stopwatch formatter, with horizontal
     * lines separating each row.
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
    public static final PrintStyle DETAILED_HORIZONTAL_LINES = new PrintStyleBuilder()
            .withRowFormat("%5s  %19s  %19s")
            .withHeader()
            .withSectionHeaderFormat("%s")
            .withSectionSummary("TOTAL %41s")
            .withDurationFormat(DurationFormat.FULL)
            .withoutLegends()
            .withSimpleLine('-', 47)
            .withAlternativeLine('=', 47)
            .build();

    private final DurationFormat durationFormat;
    private final boolean printLegend;
    private final boolean printHeader;
    private final boolean printSectionTotals;
    private final String headerFormat;
    private final String rowFormat;
    private final String sectionHeaderFormat;
    private final String sectionSummaryRowFormat;
    private final String simpleLine;
    private final String alternativeLine;

    /**
     * Creates a PrintStyle with all parameters.
     */
    protected PrintStyle(PrintStyleBuilder builder)
    {
        durationFormat = builder.getDurationFormat();
        printLegend = builder.isPrintLegend();
        printHeader = builder.isPrintHeader();
        printSectionTotals = builder.isPrintSectionTotals();
        headerFormat = builder.getHeaderFormat();
        rowFormat = builder.getRowFormat();
        sectionHeaderFormat = builder.getSectionHeaderFormat();
        sectionSummaryRowFormat = builder.getSectionSummaryRowFormat();
        simpleLine = builder.getSimpleLine();
        alternativeLine = builder.getAlternativeLine();
    }

    /**
     * @return the {@link DurationFormat} to be applied to all rows in general
     */
    public DurationFormat getDurationFormat()
    {
        return durationFormat;
    }

    /**
     * @return a flag indicating whether or not duration legends shall be printed
     */
    public boolean isPrintLegend()
    {
        return printLegend;
    }

    /**
     * @return a flag indicating whether or not the header shall be printed
     */
    public boolean isPrintHeader()
    {
        return printHeader;
    }

    /**
     * @return a flag indicating whether or not a summary shall be printed for each section
     */
    public boolean isPrintSectionTotals()
    {
        return printSectionTotals;
    }

    /**
     * @return the string format to be applied to the table header
     */
    public String getHeaderFormat()
    {
        return headerFormat;
    }

    /**
     * @return the string format to be applied to all rows in general
     */
    public String getRowFormat()
    {
        return rowFormat;
    }

    public String getSectionHeaderFormat()
    {
        return sectionHeaderFormat;
    }

    /**
     * @return the string format for the total/summary row(s)
     */
    public String getSectionSummaryRowFormat()
    {
        return sectionSummaryRowFormat;
    }

    /**
     * @return a string to be used as simple split line
     */
    public String getSimpleLine()
    {
        return simpleLine;
    }

    /**
     * @return a string to be used as alternative split line
     */
    public String getAlternativeLine()
    {
        return alternativeLine;
    }

}
