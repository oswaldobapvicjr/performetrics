package net.obvj.performetrics.util;

import java.util.Objects;

/**
 * A formatter for {@link Duration} objects.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.0
 */
public class DurationFormatter
{
    /**
     * The default {@link DurationFormat} to be applied.
     */
    public static final DurationFormat DEFAULT_FORMAT = DurationFormat.SHORTER;

    protected static final String MSG_DURATION_MUST_NOT_BE_NULL = "The Duration to be formatted must not be null";
    protected static final String MSG_DURATION_FORMAT_MUST_NOT_BE_NULL = "The DurationFormat must not be null";

    /**
     * This secures that no instance of this class can be created
     */
    private DurationFormatter()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    /**
     * Returns a string representation of the given {@link Duration} using default format.
     *
     * @param duration the {@link Duration} to be formatted
     * @return a string representation of the given Duration using default format
     *
     * @throws NullPointerException if the specified duration is null
     */
    public static final String format(Duration duration)
    {
        return format(duration, DEFAULT_FORMAT);
    }

    /**
     * Returns a string representation of the given {@link Duration} with a specific format.
     * <p>
     * <b>Note:</b> This is equivalent to calling:
     * {@code DurationFormatter.format(duration, format, true)}
     *
     * @param duration the {@link Duration} to be formatted
     * @param format   the {@link DurationFormat} to be applied
     * @return a string representation of the given Duration in the specified format
     *
     * @throws NullPointerException if either the specified duration or format is null
     */
    public static final String format(Duration duration, DurationFormat format)
    {
        return format(duration, format, true);
    }

    /**
     * Returns a string representation of the given {@link Duration} with a specific format.
     *
     * @param duration    the {@link Duration} to be formatted
     * @param format      the {@link DurationFormat} to be applied
     * @param printLegend a flag indicating whether of not to include a legend in the
     *                    generated string
     * @return a string representation of the given Duration in the specified format
     *
     * @throws NullPointerException if wither of the specified duration or format is null
     */
    public static final String format(Duration duration, DurationFormat format, boolean printLegend)
    {
        Objects.requireNonNull(duration, MSG_DURATION_MUST_NOT_BE_NULL);
        Objects.requireNonNull(format, MSG_DURATION_FORMAT_MUST_NOT_BE_NULL);
        return format.format(duration, printLegend);
    }

}
