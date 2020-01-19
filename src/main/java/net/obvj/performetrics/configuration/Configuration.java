package net.obvj.performetrics.configuration;

import net.obvj.performetrics.strategy.ConversionStrategy;

/**
 * An object that groups common Performetrics configuration.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class Configuration
{
    /**
     * The default conversion strategy to be used if no specific strategy informed
     */
    public static final ConversionStrategy DEFAULT_CONVERSION_STRATEGY = ConversionStrategy.DOUBLE_PRECISION;

    /**
     * The default maximum number of decimal places applicable if double-precision conversion
     * strategy is set
     */
    public static final int DEFAULT_SCALE = 5;

    private ConversionStrategy conversionStrategy = DEFAULT_CONVERSION_STRATEGY;
    private int scale = DEFAULT_SCALE;

    /**
     * @return the conversion strategy
     */
    public ConversionStrategy getConversionStrategy()
    {
        return conversionStrategy;
    }

    /**
     * Sets a conversion strategy to be used if no specific strategy is passed to a supported
     * operation.
     *
     * @param conversionStrategy the {@link ConversionStrategy} to set
     */
    public void setConversionStrategy(ConversionStrategy conversionStrategy)
    {
        this.conversionStrategy = conversionStrategy;
    }

    /**
     * @return the scale
     */
    public int getScale()
    {
        return scale;
    }

    /**
     * Sets a maximum number of decimal places applicable if double-precision conversion
     * strategy is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if a number outside the allowed range is received
     */
    public void setScale(int scale)
    {
        if (scale < 0 || scale > 16)
        {
            throw new IllegalArgumentException("The scale must be a number between 0 and 16");
        }
        this.scale = scale;
    }

}
