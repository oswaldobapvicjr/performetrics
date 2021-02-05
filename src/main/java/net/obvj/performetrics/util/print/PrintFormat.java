package net.obvj.performetrics.util.print;

import java.util.List;
import java.util.Map;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Provides different formatters for printing stopwatch data as strings in tabular layout.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public enum PrintFormat
{
    /**
     * Generates a summarized view which represents the total elapsed time for each counter
     * type available in the stopwatch, one row for each type.
     * <p>
     * Sample output (applying {@code PrintStyle.SUMMARIZED_TABLE_FULL}):
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
     * @see PrintStyle
     */
    SUMMARIZED
    {
        @Override
        public String format(Stopwatch stopwatch, PrintStyle style)
        {
            checkCompatibility(style);
            StringBuilder builder = new StringBuilder();
            appendLine(builder, style.getAlternativeLine());
            if (style.isPrintHeader())
            {
                appendLine(builder, style.getHeaderFormat(), HEADER_COUNTER, HEADER_ELAPSED_TIME);
                appendLine(builder, style.getSimpleLine());
            }
            stopwatch.getTypes().forEach(type -> appendLine(builder, toRowFormat(stopwatch, type, style)));
            appendLine(builder, style.getAlternativeLine());
            return builder.toString();
        }

        private String toRowFormat(Stopwatch stopwatch, Type type, PrintStyle style)
        {
            return String.format(style.getRowFormat(), type,
                    style.getDurationFormat().format(stopwatch.elapsedTime(type), style.isPrintLegend()));
        }
    },

    /**
     * Generates a detailed view of each counter type and timing session available in the
     * stopwatch, one row for each timing session, and the total elapsed time for each type,
     * as well.
     * <p>
     * Sample output (applying {@code PrintStyle.DETAILED_TABLE_FULL}):
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
     * @see PrintStyle
     */
    DETAILED
    {
        @Override
        public String format(Stopwatch stopwatch, PrintStyle style)
        {
            checkCompatibility(style);
            StringBuilder builder = new StringBuilder();

            if (style.isPrintHeader())
            {
                appendLine(builder, style.getAlternativeLine());
                appendLine(builder, style.getHeaderFormat(), HEADER_SESSION, HEADER_ELAPSED_TIME, HEADER_ELAPSED_TIME_ACC, HEADER_COUNTER);
            }
            Map<Type, List<Counter>> countersByType = stopwatch.getAllCountersByType();
            countersByType.forEach((Type type, List<Counter> counters) ->
            {
                appendLine(builder, style.getAlternativeLine());
                appendLine(builder, style.getSectionHeaderFormat(), type.toString());
                appendLine(builder, style.getSimpleLine());

                Duration elapsedTimeAcc = Duration.ZERO;
                // Iterate through each counter to update the accumulated duration and print values
                for (int sequence = 0; sequence < counters.size(); sequence++)
                {
                    Counter counter = counters.get(sequence);
                    Duration elapsedTime = counter.elapsedTime();
                    elapsedTimeAcc = elapsedTimeAcc.plus(elapsedTime);
                    appendLine(builder, toRowFormat(sequence + 1, elapsedTime, elapsedTimeAcc, style, type));
                }
                if (style.isPrintSectionSummary())
                {
                    appendLine(builder, style.getSimpleLine());
                    appendLine(builder, toTotalRowFormat(elapsedTimeAcc, style));
                }
            });
            appendLine(builder, style.getAlternativeLine());
            return builder.toString();
        }

        private String toRowFormat(int sequence, Duration elapsedTime, Duration elapsedTimeAcc, PrintStyle style, Type type)
        {
            DurationFormat durationFormat = style.getDurationFormat();
            boolean printLegend = style.isPrintLegend();
            return String.format(style.getRowFormat(), sequence, durationFormat.format(elapsedTime, printLegend),
                    durationFormat.format(elapsedTimeAcc, printLegend), type);
        }

        private String toTotalRowFormat(Duration elapsedTimeAcc, PrintStyle style)
        {
            return String.format(style.getSectionSummaryRowFormat(),
                    style.getDurationFormat().format(elapsedTimeAcc, style.isPrintLegend()));
        }
    };

    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String HEADER_SESSION = "#";
    private static final String HEADER_COUNTER = "Counter";
    private static final String HEADER_ELAPSED_TIME = "Elapsed time";
    private static final String HEADER_ELAPSED_TIME_ACC = "Elapsed time (+)";

    /**
     * Generates a string with formatted stopwatch data and custom style.
     *
     * @param stopwatch the stopwatch to be printed
     * @param style     the {@link PrintStyle} to be applied
     * @return a string with formatted stopwatch data
     *
     * @throws IllegalArgumentException if the specified PrintStyle is not compatible with this PrintFormat.
     */
    public abstract String format(Stopwatch stopwatch, PrintStyle style);

    /**
     * Appends the specified string followed by a line separator.
     *
     * @param builder the character sequence to be incremented
     * @param string  the string to be appended
     */
    protected static void appendLine(StringBuilder builder, String string)
    {
        if (!isEmpty(string))
        {
            builder.append(string);
            builder.append(LINE_SEPARATOR);
        }
    }

    /**
     * Appends a formatted string using the specified format string and arguments, followed by
     * a line separator.
     *
     * @param builder the character sequence to be incremented
     * @param format  a format string
     * @param string  the arguments referenced by the format specifiers in the format string
     */
    protected static void appendLine(StringBuilder builder, String format, Object... args)
    {
        if (!isEmpty(format))
        {
            appendLine(builder, String.format(format, args));
        }
    }

    private static boolean isEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    /**
     * Checks if a given {@link PrintStyle} is compatible with this {@link PrintFormat}.
     *
     * @param style the {@link PrintStyle} to be checked, not null
     * @throws IllegalArgumentException if the style is not compatible
     *
     * @since 2.2.2
     */
    public void checkCompatibility(PrintStyle style)
    {
        if (style.getFormat() != this)
        {
            throw new IllegalArgumentException(
                    String.format("Incompatible PrintStyle. Expected %s but received %s.", this, style.getFormat()));
        }
    }

}
