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
            return milliseconds > 0 ? milliseconds * 1000000 : milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds > 0 ? seconds * 1000000000 : seconds;
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
            return nanoseconds > 0 ? nanoseconds / 1000000 : nanoseconds;
        }

        @Override
        public long fromMilliseconds(long milliseconds)
        {
            return milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds > 0 ? seconds * 1000 : seconds;
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
            return nanoseconds > 0 ? (nanoseconds / 1000000) / 1000 : nanoseconds;
        }

        @Override
        public long fromMilliseconds(long milliseconds)
        {
            return milliseconds > 0 ? milliseconds / 1000 : milliseconds;
        }

        @Override
        public long fromSeconds(long seconds)
        {
            return seconds;
        }
    };

    public abstract long fromNanoseconds(long nanoseconds);

    public abstract long fromMilliseconds(long milliseconds);

    public abstract long fromSeconds(long seconds);
}
