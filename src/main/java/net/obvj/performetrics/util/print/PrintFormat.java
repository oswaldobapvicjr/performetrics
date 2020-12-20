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
     */
    SUMMARIZED(PrintStyle.SUMMARIZED_HORIZONTAL_LINES)
    {
        @Override
        public String format(Stopwatch stopwatch, PrintStyle style)
        {
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
     */
    DETAILED(PrintStyle.DETAILED_HORIZONTAL_LINES)
    {
        @Override
        public String format(Stopwatch stopwatch, PrintStyle style)
        {
            StringBuilder builder = new StringBuilder();

            if (style.isPrintHeader())
            {
                appendLine(builder, style.getAlternativeLine());
                appendLine(builder, style.getHeaderFormat(), HEADER_SESSION, HEADER_ELAPSED_TIME, HEADER_ELAPSED_TIME_ACC);
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
                    appendLine(builder, toRowFormat(sequence + 1, elapsedTime, elapsedTimeAcc, style));
                }
                if (style.isPrintSectionTotals())
                {
                    appendLine(builder, style.getSimpleLine());
                    appendLine(builder, toTotalRowFormat(elapsedTimeAcc, style));
                }
            });
            appendLine(builder, style.getAlternativeLine());
            return builder.toString();
        }

        private String toRowFormat(int sequence, Duration elapsedTime, Duration elapsedTimeAcc, PrintStyle style)
        {
            DurationFormat durationFormat = style.getDurationFormat();
            boolean printLegend = style.isPrintLegend();
            return String.format(style.getRowFormat(), sequence, durationFormat.format(elapsedTime, printLegend),
                    durationFormat.format(elapsedTimeAcc, printLegend));
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

    private final PrintStyle defaultStyle;

    private PrintFormat(PrintStyle defaultStyle)
    {
        this.defaultStyle = defaultStyle;
    }

    /**
     * Generates a string with formatted stopwatch data.
     *
     * @param stopwatch the stopwatch to be printed
     * @return a string with formatted stopwatch data
     */
    public String format(Stopwatch stopwatch)
    {
        return format(stopwatch, defaultStyle);
    }

    /**
     * Generates a string with formatted stopwatch data and custom style.
     *
     * @param stopwatch the stopwatch to be printed
     * @param style     the {@link PrintStyle} to be applied
     * @return a string with formatted stopwatch data
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

}
