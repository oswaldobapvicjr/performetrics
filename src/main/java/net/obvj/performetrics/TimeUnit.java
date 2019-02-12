package net.obvj.performetrics;

/**
 * An enumeration of all supported time units.
 *
 * @author oswaldo.bapvic.jr
 */
public enum TimeUnit
{
    /**
     * A time unit equal to one billionth of a second, that is, 10^-9 seconds
     */
    NANOSECOND
    {
        @Override
        public long fromNanoseconds(long nanoseconds)
        {
            return nanoseconds;
        }

        @Override
        public long fromMilliseconds(long milliseconds)
        {
            return milliseconds > 0 ? milliseconds * NANO_TO_MILLIS : milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds > 0 ? seconds * NANO_TO_SECOND : seconds;
        }
    },

    /**
     * A time unit equal to a thousandth of a second, that is, 10^-3 seconds
     */
    MILLISECOND
    {
        @Override
        public long fromNanoseconds(long nanoseconds)
        {
            return nanoseconds > 0 ? nanoseconds / NANO_TO_MILLIS : nanoseconds;
        }

        @Override
        public long fromMilliseconds(long milliseconds)
        {
            return milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds > 0 ? seconds * MILLIS_TO_SECOND : seconds;
        }
    },

    /**
     * A time unit equal to a second
     */
    SECOND
    {
        @Override
        public long fromNanoseconds(long nanoseconds)
        {
            return nanoseconds > 0 ? nanoseconds / NANO_TO_SECOND : nanoseconds;
        }

        @Override
        public long fromMilliseconds(long milliseconds)
        {
            return milliseconds > 0 ? milliseconds / MILLIS_TO_SECOND : milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds;
        }
    };

    private static final long MILLIS_TO_SECOND = 1000L;
    private static final long NANO_TO_MILLIS = 1000000L;
    private static final long NANO_TO_SECOND = 1000000000L;

    public abstract long fromNanoseconds(long nanoseconds);

    public abstract long fromMilliseconds(long milliseconds);

    public abstract long fromSeconds(long seconds);
}
