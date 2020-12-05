package net.obvj.performetrics.util;

/**
 * Enumerates different time format styles, each one with particular behaviors.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public enum DurationFormat
{
    /**
     * Formats a time duration in the following format: {@code H:M:s:ns}. For example:
     * {@code 1:59:59.987654321}
     */
    FULL
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            return String.format(HOURS_FORMAT, duration.getHours(), duration.getMinutes(), duration.getSeconds(),
                    duration.getNanoseconds()) + legend(printLegend, HOURS_LEGEND);
        }
    },

    /**
     * Formats a time duration in any of the following formats: {@code H:M:s:ns},
     * {@code M:S:ns}, or {@code S.ns}, always choosing the shortest possible format.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code 0.001000000 second(s)}</li>
     * <li>{@code 3.200000000 second(s)}</li>
     * <li>{@code 15:00.005890000 minute(s)}</li>
     * </ul>
     */
    SHORT
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            if (duration.getHours() > 0)
            {
                return DurationFormat.FULL.format(duration, printLegend);
            }
            if (duration.getMinutes() > 0)
            {
                return String.format(MINUTES_FORMAT, duration.getMinutes(), duration.getSeconds(),
                        duration.getNanoseconds()) + legend(printLegend, MINUTES_LEGEND);
            }
            return String.format(SECONDS_FORMAT, duration.getSeconds(), duration.getNanoseconds())
                    + legend(printLegend, SECONDS_LEGEND);
        }

    },

    /**
     * Formats a time duration in any of the following formats: {@code H:M:s:ns},
     * {@code M:S:ns}, or {@code S.ns}, suppressing trailing zeros from the nanosecond part.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code 0.001 second(s)}</li>
     * <li>{@code 3.2 second(s)}</li>
     * <li>{@code 15:00.00589 minute(s)}</li>
     * </ul>
     */
    SHORTER
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            String format = removeTrailingZeros(SHORT.format(duration, false));

            if (!printLegend)
            {
                return format;
            }
            if (duration.getHours() > 0)
            {
                return format + legend(true, HOURS_LEGEND);
            }
            if (duration.getMinutes() > 0)
            {
                return format + legend(true, MINUTES_LEGEND);
            }
            return format + legend(true, SECONDS_LEGEND);
        }

    },

    /**
     * Formats a time duration using ISO-8601 seconds based representation, such as
     * {@code PT8H6M12.345S}.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code PT0.001S}</li>
     * <li>{@code PT3.2S}</li>
     * <li>{@code PT15M0.00589S}</li>
     * </ul>
     */
    ISO_8601
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            if (Duration.ZERO.equals(duration))
            {
                return "PT0S";
            }
            StringBuilder builder = new StringBuilder();
            builder.append("PT");
            if (duration.getHours() > 0)
            {
                builder.append(duration.getHours()).append('H');
            }
            if (duration.getMinutes() > 0)
            {
                builder.append(duration.getMinutes()).append('M');
            }
            if (duration.getSeconds() > 0 || duration.getNanoseconds() > 0)
            {
                builder.append(duration.getSeconds());
                if (duration.getNanoseconds() > 0)
                {
                    String nanos = removeTrailingZeros(String.format(NANOSECONDS_FORMAT, duration.getNanoseconds()));
                    builder.append(".").append(nanos);
                }
                builder.append('S');
            }
            return builder.toString();
        }

    };

    private static final String HOURS_FORMAT = "%d:%02d:%02d.%09d";
    private static final String MINUTES_FORMAT = "%d:%02d.%09d";
    private static final String SECONDS_FORMAT = "%d.%09d";
    private static final String NANOSECONDS_FORMAT = "%09d";

    private static final String HOURS_LEGEND = "hour(s)";
    private static final String MINUTES_LEGEND = "minute(s)";
    private static final String SECONDS_LEGEND = "second(s)";

    /**
     * Formats a time duration.
     *
     * @param duration    the {@link Duration} to be formatted
     * @param printLegend a flag indicating whether or not to include the legend in the
     *                    generated string
     * @return a formatted time duration
     */
    public abstract String format(final Duration duration, boolean printLegend);

    /**
     * Returns the {@code legend}, prepended with a white-space, if the
     * {@code printLegendFlag} argument is {@code true}; or an empty string, otherwise.
     *
     * @param printLegendFlag the flag to be evaluated
     * @param legend          the string to be used as legend
     * @return the legend string
     */
    static String legend(boolean printLegendFlag, final String legend)
    {
        return printLegendFlag ? " " + legend : "";
    }

    /**
     * Removes trailing zeros from the specified string. For example:
     *
     * <pre>
     * removeTrailingZeros("9.009000000) //returns: "9.009"
     * removeTrailingZeros("9.000000009) //returns: "9.000000009"
     * removeTrailingZeros("9.000000000) //returns: "9"
     * </pre>
     *
     * @param string the string whose trailing zeros are to be removed
     * @return a string without trailing zeros
     */
    static String removeTrailingZeros(final String string)
    {
        StringBuilder builder = new StringBuilder(string);
        while (builder.charAt(builder.length() - 1) == '0')
        {
            builder.setLength(builder.length() - 1);
        }
        if (builder.charAt(builder.length() - 1) == '.')
        {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }
}