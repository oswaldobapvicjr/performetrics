package net.obvj.performetrics.util.print;

import java.util.ArrayList;
import java.util.EnumMap;
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
            appendLine(builder, style.getHeaderFormat(), HEADER_COUNTER, HEADER_ELAPSED_TIME);
            appendLine(builder, style.getSimpleLine());
            stopwatch.getTypes().forEach(type -> appendLine(builder, toRowFormat(stopwatch, type, style)));
            appendLine(builder, style.getAlternativeLine());
            return builder.toString();
        }

        private String toRowFormat(Stopwatch stopwatch, Type type, PrintStyle style)
        {
            return String.format(style.getRowFormat(), type,
                    style.getDurationFormat().format(stopwatch.elapsedTime(type), false));
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
            appendLine(builder, style.getAlternativeLine());
            appendLine(builder, style.getHeaderFormat(), HEADER_SESSION, HEADER_ELAPSED_TIME, HEADER_ELAPSED_TIME_ACC);

            groupCountersByType(stopwatch).forEach((Type type, List<Counter> counters) ->
            {
                appendLine(builder, style.getAlternativeLine());
                appendLine(builder, type.toString());
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

                appendLine(builder, style.getSimpleLine());
                appendLine(builder, toTotalRowFormat(elapsedTimeAcc, style));
            });
            appendLine(builder, style.getAlternativeLine());
            return builder.toString();
        }

        private String toRowFormat(int sequence, Duration elapsedTime, Duration elapsedTimeAcc, PrintStyle style)
        {
            DurationFormat durationFormat = style.getDurationFormat();
            return String.format(style.getRowFormat(), sequence, durationFormat.format(elapsedTime, false),
                    durationFormat.format(elapsedTimeAcc, false));
        }

        private String toTotalRowFormat(Duration elapsedTimeAcc, PrintStyle style)
        {
            return String.format(style.getTotalRowFormat(), style.getDurationFormat().format(elapsedTimeAcc, false));
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
     * Returns a map of counters by type from a given stopwatch.
     *
     * @param stopwatch the stopwatch which counters are to be grouped
     */
    protected static Map<Type, List<Counter>> groupCountersByType(Stopwatch stopwatch)
    {
        return groupCountersByType(stopwatch.getCounters());
    }

    /**
     * Returns a map of counters by type from a given counters list
     *
     * @param counters a list containing counters to be grouped
     */
    protected static Map<Type, List<Counter>> groupCountersByType(List<Counter> counters)
    {
        Map<Type, List<Counter>> countersByType = new EnumMap<>(Type.class);
        for (Counter counter : counters)
        {
            countersByType.compute(counter.getType(), (Type type, List<Counter> internalList) ->
            {
                if (internalList == null)
                {
                    internalList = new ArrayList<>();
                }
                internalList.add(counter);
                return internalList;
            });
        }
        return countersByType;
    }

    /**
     * Appends the specified string followed by a line separator.
     *
     * @param builder the character sequence to be incremented
     * @param string  the string to be appended
     */
    protected static void appendLine(StringBuilder builder, String string)
    {
        if (string != null && !string.isEmpty())
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
        if (format != null && !format.isEmpty())
        {
            appendLine(builder, String.format(format, args));
        }
    }

}
