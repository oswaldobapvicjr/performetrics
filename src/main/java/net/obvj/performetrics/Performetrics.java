package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * A Facade class meant to provide a simple interface for common parameters setup.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Performetrics
{
    /**
     * This is a utility class, not meant to be instantiated.
     */
    private Performetrics()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Sets a time unit to be maintained by default if no specific time unit is specified.
     *
     * @param timeUnit the {@link TimeUnit} to set
     */
    public static void setDefaultTimeUnit(TimeUnit timeUnit)
    {
        ConfigurationHolder.getConfiguration().setTimeUnit(timeUnit);
    }

    /**
     * Sets a conversion mode to be applied by supported operations if no specific mode is
     * set.
     *
     * @param conversionMode the {@link ConversionMode} to set
     */
    public static void setDefaultConversionMode(ConversionMode conversionMode)
    {
        ConfigurationHolder.getConfiguration().setConversionMode(conversionMode);
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * mode is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if a number outside the allowed range is received
     */
    public static void setScale(int scale)
    {
        ConfigurationHolder.getConfiguration().setScale(scale);
    }
}
