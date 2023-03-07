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

import static java.util.concurrent.TimeUnit.*;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <li><b>DurationFormat.LINUX:</b> {@code 0m3.200s}</li>
 * </ul>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public enum DurationFormat
{

    /**
     * Formats a time duration in the following format: {@code H:M:S.ns}. For example:
     * {@code 1:59:59.987654321}.
     */
    FULL
    {
        @Override
        public String doFormat(final Duration duration, boolean printLegend)
        {
            return String.format(MyTimeUnit.HOURS.format, duration.getHours(),
                    duration.getMinutes(), duration.getSeconds(), duration.getNanoseconds())
                    + legend(printLegend, MyTimeUnit.HOURS.legend);
        }

        @Override
        public Duration doParse(final String string)
        {
            return parseDurationHMS(string);
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
        public String doFormat(final Duration duration, boolean printLegend)
        {
            if (duration.getHours() > 0)
            {
                return DurationFormat.FULL.doFormat(duration, printLegend);
            }
            if (duration.getMinutes() > 0)
            {
                return String.format(MyTimeUnit.MINUTES.format, duration.getMinutes(),
                        duration.getSeconds(), duration.getNanoseconds())
                        + legend(printLegend, MyTimeUnit.MINUTES.legend);
            }
            return String.format(MyTimeUnit.SECONDS.format, duration.getSeconds(),
                    duration.getNanoseconds()) + legend(printLegend, MyTimeUnit.SECONDS.legend);
        }

        @Override
        public Duration doParse(final String string)
        {
            return parseDurationHMS(string);
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
        public String doFormat(final Duration duration, boolean printLegend)
        {
            String format = removeTrailingZeros(SHORT.doFormat(duration, false));

            if (!printLegend)
            {
                return format;
            }
            if (duration.getHours() > 0)
            {
                return format + legend(true, MyTimeUnit.HOURS.legend);
            }
            if (duration.getMinutes() > 0)
            {
                return format + legend(true, MyTimeUnit.MINUTES.legend);
            }
            return format + legend(true, MyTimeUnit.SECONDS.legend);
        }

        @Override
        public Duration doParse(final String string)
        {
            return parseDurationHMS(string);
        }

    },

    /**
     * Formats a time duration using ISO-8601 seconds-based representation.
     * <p>
     * For example: {@code PT8H6M12.345S}, where:
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
     * Additional examples:
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
        public String doFormat(final Duration duration, boolean printLegend)
        {
            return duration.internal().toString();
        }

        @Override
        public Duration doParse(final String string)
        {
            try
            {
                return new Duration(java.time.Duration.parse(string));
            }
            catch (DateTimeParseException exception)
            {
                throw new IllegalArgumentException(String.format(MSG_UNPARSEABLE_DURATION, string),
                        exception);
            }
        }

    },

    /**
     * Formats a time duration in Linux style, applying the form {@code M'm':S.ms's'}.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code 0m0.125s}</li>
     * <li>{@code 0m4.999s}</li>
     * <li>{@code 3m12.038s}</li>
     * </ul>
     * <p>
     * <b>Note:</b> The {@code printLegend} flag has no effect in this format style.
     *
     * @since 2.4.0
     */
    LINUX
    {
        private static final String LINUX_FORMAT = "%dm%d.%03ds";

        private final Pattern linuxPattern = Pattern.compile(
                "^(?<minutes>\\d+)m"
                + "(?<seconds>\\d+)."
                + "(?<milliseconds>\\d+)s");

        @Override
        public String doFormat(final Duration duration, boolean printLegend)
        {
            return String.format(LINUX_FORMAT,
                    SECONDS.toMinutes(duration.internal().getSeconds()),
                    duration.getSeconds(), // the seconds within the minute
                    NANOSECONDS.toMillis(duration.getNanoseconds()));
        }

        @Override
        public Duration doParse(final String string)
        {
            Matcher matcher = linuxPattern.matcher(string);
            if (matcher.matches())
            {
                return parseDuration(matcher.group("minutes"), MINUTES)
                        .plus(parseDuration(matcher.group("seconds"), SECONDS))
                        .plus(parseDuration(matcher.group("milliseconds"), MILLISECONDS));
            }
            throw new IllegalArgumentException(String.format(MSG_UNPARSEABLE_DURATION, string));
        }
    };


    /**
     * The pattern for parsing durations in the format {@code [H:][M:]S[.ns]}.
     */
    private static final Pattern HMS_PATTERN = Pattern.compile(
            "^(((?<hours>\\d*):)?"
            + "((?<minutes>\\d*):))?"
            + "(?<seconds>\\d+)"
            + "([.,](?<nanoseconds>\\d+))?"
            + "(.)*"); // legend

    static final String MSG_DURATION_MUST_NOT_BE_NULL = "The duration must not be null";
    static final String MSG_UNPARSEABLE_DURATION = "Unrecognized duration: %s";

    /**
     * Formats a given duration.
     *
     * @param duration    the {@link Duration} to be formatted, not null
     * @param printLegend a flag indicating whether or not to include the legend in the
     *                    generated string
     * @return a string representing the specified duration, formatted
     *
     * @throws NullPointerException if the specified duration is null
     */
    public String format(final Duration duration, boolean printLegend)
    {
        Objects.requireNonNull(duration, MSG_DURATION_MUST_NOT_BE_NULL);
        return doFormat(duration, printLegend);
    }

    abstract String doFormat(final Duration duration, boolean printLegend);

    /**
     * Parses a textual representation of a {@link Duration}.
     *
     * @param string the string to parse, not null
     * @return the parsed {@code Duration}, not null
     * @throws NullPointerException     if the specified string is null
     * @throws IllegalArgumentException if the string cannot be parsed as {@link Duration}
     * @since 2.4.0
     */
    public Duration parse(final String string)
    {
        Objects.requireNonNull(string);
        return doParse(string);
    }

    abstract Duration doParse(final String string);

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
     * Right-pads a string with zeros.
     *
     * @param string the string to pad out; not null
     * @param size   the size to pad to
     *
     * @return a right-padded string or the original string if no padding is necessary
     * @throws NullPointerException if the string is null
     * @since 2.4.0
     */
    static String rightPadZeros(String string, int size)
    {
        if (string.length() >= size)
        {
            return string;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(string);
        builder.setLength(size);
        return builder.toString().replace('\u0000', '0');
    }

    /**
     * Parses a {@link Duration} in the format {@code [H:][M:]S[.ns]}.
     *
     * @param string the string to parse, not null
     * @return the parsed {@code Duration}, not null
     * @throws IllegalArgumentException if the string cannot be parsed as {@link Duration}
     * @since 2.4.0
     */
    private static Duration parseDurationHMS(String string)
    {
        Matcher matcher = HMS_PATTERN.matcher(string);
        if (matcher.matches())
        {
            return parseDuration(matcher.group("hours"), HOURS)
                    .plus(parseDuration(matcher.group("minutes"), MINUTES))
                    .plus(parseDuration(matcher.group("seconds"), SECONDS))
                    .plus(parseNanoseconds(matcher.group("nanoseconds")));
        }
        throw new IllegalArgumentException(String.format(MSG_UNPARSEABLE_DURATION, string));
    }

    /**
     * Obtains a {@link Duration} representing an amount in the specified time unit.
     *
     * @param string   a string representing the amount of the duration; may be null
     * @param timeUnit the unit that the amount argument is measured in; not null
     * @return the parsed {@link Duration} or {@link Duration#ZERO} if the string is null
     * @since 2.4.0
     */
    private static Duration parseDuration(String string, TimeUnit timeUnit)
    {
        if (string != null)
        {
            long value = Long.parseLong(string);
            return Duration.of(value, timeUnit);
        }
        return Duration.ZERO;
    }

    /**
     * Obtains a {@link Duration} from the decimal part of a second-based duration, in
     * nanoseconds.
     * <p>
     * Since trailing zeros from the decimal part may have been removed during formatting,
     * they will be reintroduced to the right of the string.
     *
     * @param string a string representing the decimal part of a duration; may be null
     * @return the parsed {@link Duration} or {@link Duration#ZERO} if the string is null
     * @since 2.4.0
     */
    private static Duration parseNanoseconds(String string)
    {
        if (string != null)
        {
            String nanoseconds = rightPadZeros(string, 9);
            return parseDuration(nanoseconds, NANOSECONDS);
        }
        return Duration.ZERO;
    }

    /**
     * Enumerates the time units and associated formatting objects.
     *
     * @author oswaldo.bavic.jr
     * @since 2.2.4
     */
    private enum MyTimeUnit
    {
        HOURS("hour(s)", "%d:%02d:%02d.%09d"),

        MINUTES("minute(s)", "%d:%02d.%09d"),

        SECONDS("second(s)", "%d.%09d");

        private final String legend;
        private final String format;

        private MyTimeUnit(String legend, String format)
        {
            this.legend = legend;
            this.format = format;
        }

    }

}
