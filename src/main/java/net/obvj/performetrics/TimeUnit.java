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
        public long convertedTime(long nanoseconds)
        {
            return nanoseconds;
        }
    },

    /**
     * A time unit equal to a thousandth of a second, that is, 10^-3 seconds
     */
    MILLISECOND
    {
        @Override
        public long convertedTime(long nanoseconds)
        {
            return nanoseconds > 0 ? (nanoseconds / 1000000) % 1000000 : -1;
        }
    },

    /**
     * A time unit equal to a second
     */
    SECOND
    {
        @Override
        public long convertedTime(long nanoseconds)
        {
            return nanoseconds > 0 ? ((nanoseconds / 1000000) % 1000000) / 1000 : -1;
        }
    };

    public abstract long convertedTime(long nanoseconds);
}
