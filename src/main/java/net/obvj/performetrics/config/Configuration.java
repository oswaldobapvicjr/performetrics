package net.obvj.performetrics.config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.ConversionMode;

/**
 * An object that maintains common configurable parameters for Perfometrics objects.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Configuration
{
    /**
     * The initial time unit to be maintained if no specific time unit informed.
     */
    protected static final TimeUnit INITIAL_TIME_UNIT = TimeUnit.NANOSECONDS;

    /**
     * The initial conversion mode to be applied if no specific mode is set.
     */
    protected static final ConversionMode INITIAL_CONVERSION_MODE = ConversionMode.DOUBLE_PRECISION;

    /**
     * The initial maximum number of decimal places applicable if double-precision conversion
     * mode is set.
     */
    protected static final int INITIAL_SCALE = 5;

    private TimeUnit timeUnit = INITIAL_TIME_UNIT;
    private ConversionMode conversionMode = INITIAL_CONVERSION_MODE;
    private int scale = INITIAL_SCALE;

    /**
     * Returns the time unit maintained by default if no specific time unit is specified.
     *
     * @return the time unit maintained by default
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Defines the time unit to be maintained by default if no specific time unit is
     * specified.
     *
     * @param timeUnit the {@link TimeUnit} to set
     * @throws NullPointerException if the specified time unit is null
     */
    public void setTimeUnit(TimeUnit timeUnit)
    {
        this.timeUnit = Objects.requireNonNull(timeUnit, "the default time unit must not be null");
    }

    /**
     * Returns the conversion mode applied in supported operations if no specific mode is set.
     *
     * @return the conversion mode applied in supported operations
     */
    public ConversionMode getConversionMode()
    {
        return conversionMode;
    }

    /**
     * Defines the default conversion mode to be applied in supported operations if no
     * specific mode is set.
     *
     * @param conversionMode the {@link ConversionMode} to set
     * @throws NullPointerException if the specified conversion mode is null
     */
    public void setConversionMode(ConversionMode conversionMode)
    {
        this.conversionMode = Objects.requireNonNull(conversionMode, "the default conversion mode must not be null");
    }

    /**
     * Returns the maximum number of decimal places applied if double-precision conversion
     * mode is set.
     *
     * @return the the maximum number of decimal places applied
     */
    public int getScale()
    {
        return scale;
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * mode is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if the specified number is outside the allowed range
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
