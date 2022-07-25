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

package net.obvj.performetrics.util;

/**
 * Enumerates different duration format styles, each one with particular characteristics.
 * <p>
 * Examples:
 * </p>
 * <ul>
 * <li><b>DurationFormat.FULL:</b> {@code 0:00:03.200000000}</li>
 * <li><b>DurationFormat.SHORT:</b> {@code 3.200000000 second(s)}</li>
 * <li><b>DurationFormat.SHORTER:</b> {@code 3.2 second(s)}</li>
 * <li><b>DurationFormat.ISO_8601:</b> {@code PT3.2S}</li>
 * </ul>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public enum DurationFormat
{
    /**
     * Formats a time duration in the following format: {@code H:M:S.ns}. For example:
     * {@code 1:59:59.987654321}
     */
    FULL
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            return String.format(TimeUnit.HOURS.format, duration.getHours(), duration.getMinutes(),
                    duration.getSeconds(), duration.getNanoseconds()) + legend(printLegend, TimeUnit.HOURS.legend);
        }
    },

    /**
     * Formats a time duration in any of the following formats: {@code H:M:S.ns},
     * {@code M:S.ns}, or {@code S.ns}, always choosing the shortest possible format.
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
                return String.format(TimeUnit.MINUTES.format, duration.getMinutes(), duration.getSeconds(),
                        duration.getNanoseconds()) + legend(printLegend, TimeUnit.MINUTES.legend);
            }
            return String.format(TimeUnit.SECONDS.format, duration.getSeconds(), duration.getNanoseconds())
                    + legend(printLegend, TimeUnit.SECONDS.legend);
        }

    },

    /**
     * Formats a time duration in any of the following formats: {@code H:M:S.ns},
     * {@code M:S.ns}, or {@code S.ns}, always choosing the shortest possible format and
     * suppressing trailing zeros from the nanoseconds part.
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
                return format + legend(true, TimeUnit.HOURS.legend);
            }
            if (duration.getMinutes() > 0)
            {
                return format + legend(true, TimeUnit.MINUTES.legend);
            }
            return format + legend(true, TimeUnit.SECONDS.legend);
        }

    },

    /**
     * Formats a time duration using ISO-8601 seconds based representation, such as
     * {@code PT8H6M12.345S}, where:
     * <ul>
     * <li>{@code P} is the duration designator (referred to as "period"), and is always
     * placed at the beginning of the duration</li>
     * <li>{@code T} is the time designator that precedes the time components</li>
     * <li>{@code H} is the hour designator that follows the value for the number of hours
     * </li>
     * <li>{@code M} is the minute designator that follows the value for the number of
     * minutes</li>
     * <li>{@code S} is the second designator that follows the value for the number of
     * seconds</li>
     * </ul>
     * <p>
     * Examples:
     * <ul>
     * <li>{@code PT0.001S}</li>
     * <li>{@code PT3.2S}</li>
     * <li>{@code PT15M0.00589S}</li>
     * </ul>
     * <p>
     * <b>Note:</b> The {@code printLegend} flag has no effect in this format style.
     */
    ISO_8601
    {
        @Override
        public String format(final Duration duration, boolean printLegend)
        {
            return duration.getInternalDuration().toString();
        }

    };

    /**
     * Formats a given duration.
     *
     * @param duration    the {@link Duration} to be formatted, not null
     * @param printLegend a flag indicating whether or not to include the legend in the
     *                    generated string
     * @return a string representing the specified duration, formatted
     *
     * @throws NullPointerException if the specified Duration is null
     */
    public abstract String format(final Duration duration, boolean printLegend);

    /**
     * Returns the {@code legend}, prepended with a white-space, if the
     * {@code printLegendFlag} argument is {@code true}; or an empty string, otherwise.
     *
     * @param printLegend the flag to be evaluated
     * @param legend      the string to be used as legend
     * @return the legend string
     */
    private static String legend(boolean printLegend, final String legend)
    {
        return printLegend ? " " + legend : "";
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

    /**
     * Enumerates the time units and associated formatting objects.
     *
     * @author oswaldo.bavic.jr
     * @since 2.2.4
     */
    private enum TimeUnit
    {
        HOURS("hour(s)", "%d:%02d:%02d.%09d"),

        MINUTES("minute(s)", "%d:%02d.%09d"),

        SECONDS("second(s)", "%d.%09d");

        private final String legend;
        private final String format;

        private TimeUnit(String legend, String format)
        {
            this.legend = legend;
            this.format = format;
        }

    }

}
