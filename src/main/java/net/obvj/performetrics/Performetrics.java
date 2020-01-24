package net.obvj.performetrics;

import net.obvj.performetrics.configuration.ConfigurationHolder;
import net.obvj.performetrics.strategy.ConversionStrategy;

/**
 * A facade for common Performetrics operations.
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
     * Sets a conversion strategy to be applied in supported operations if no specific
     * strategy is set.
     *
     * @param conversionStrategy the {@link ConversionStrategy} to set
     */
    public static void setDefaultConversionStrategy(ConversionStrategy conversionStrategy)
    {
        ConfigurationHolder.getConfiguration().setConversionStrategy(conversionStrategy);
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * strategy is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if a number outside the allowed range is received
     */
    public static void setScale(int scale)
    {
        ConfigurationHolder.getConfiguration().setScale(scale);
    }
}
