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
    public static final PrintStyle SUMMARIZED_HORIZONTAL_LINES = new PrintStyle(DurationFormat.FULL, "%-15s  %19s",
            "%-15s  %19s", null, generateLine('-', 36), generateLine('=', 36));

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
    public static final PrintStyle DETAILED_HORIZONTAL_LINES = new PrintStyle(DurationFormat.FULL, "%5s  %19s  %19s",
            "%5s  %19s  %19s", "TOTAL %41s", generateLine('-', 47), generateLine('=', 47));

    private final DurationFormat durationFormat;
    private final String headerFormat;
    private final String rowFormat;
    private final String totalRowFormat;
    private final String simpleLine;
    private final String alternativeLine;

    /**
     * Creates a PrintStyle with all parameters.
     *
     * @param durationFormat  the {@link DurationFormat} to be applied for each row
     * @param headerFormat    the string format to be applied to the table header
     * @param rowFormat       the string format to be applied to all rows in general
     * @param totalRowFormat  the sting format for the total/summary row(s)
     * @param simpleLine      a string to be used as simple split line
     * @param alternativeLine a string to be used as alternative split line
     */
    protected PrintStyle(DurationFormat durationFormat, String headerFormat, String rowFormat, String totalRowFormat,
            String simpleLine, String alternativeLine)
    {
        this.durationFormat = durationFormat;
        this.headerFormat = headerFormat;
        this.rowFormat = rowFormat;
        this.totalRowFormat = totalRowFormat;
        this.simpleLine = simpleLine;
        this.alternativeLine = alternativeLine;
    }

    /**
     * @return the {@link DurationFormat} to be applied to all rows in general
     */
    public DurationFormat getDurationFormat()
    {
        return durationFormat;
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

    /**
     * @return the string format for the total/summary row(s)
     */
    public String getTotalRowFormat()
    {
        return totalRowFormat;
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
