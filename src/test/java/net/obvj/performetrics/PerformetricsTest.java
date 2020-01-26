package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.performetrics.strategy.ConversionStrategy.DOUBLE_PRECISION;
import static net.obvj.performetrics.strategy.ConversionStrategy.FAST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import net.obvj.performetrics.configuration.ConfigurationHolder;
import net.obvj.performetrics.strategy.ConversionStrategy;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class PerformetricsTest
{
    private static final TimeUnit INITIAL_TIME_UNIT = ConfigurationHolder.getConfiguration().getTimeUnit();
    private static final ConversionStrategy INITIAL_CONVERSION_STRATEGY = ConfigurationHolder.getConfiguration()
            .getConversionStrategy();
    private static final int INITIAL_SCALE = ConfigurationHolder.getConfiguration().getScale();

    @After
    public void reset()
    {
        ConfigurationHolder.reset();
    }

    /**
     * Tests that no instances of this utility class are created.
     *
     * @throws ReflectiveOperationException in case of error getting class metadata
     */
    @Test
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        TestUtils.assertNoInstancesAllowed(Performetrics.class);
    }

    @Test
    public void setDefaultConversionStrategy_fast_updatesConfiguration()
    {
        Performetrics.setDefaultConversionStrategy(FAST);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionStrategy(), is(FAST));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setDefaultTimeUnit_seconds_updatesConfiguration()
    {
        Performetrics.setDefaultTimeUnit(MILLISECONDS);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(MILLISECONDS));
        assertThat(ConfigurationHolder.getConfiguration().getConversionStrategy(), is(INITIAL_CONVERSION_STRATEGY));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setDefaultConversionStrategy_doublePrecision_updatesConfiguration()
    {
        Performetrics.setDefaultConversionStrategy(DOUBLE_PRECISION);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionStrategy(), is(DOUBLE_PRECISION));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setScale_valid_updatesConfiguration()
    {
        Performetrics.setScale(16);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionStrategy(), is(INITIAL_CONVERSION_STRATEGY));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(16));
    }

    @Test
    public void setScale_invalid_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setScale(-1);
        }
        catch (IllegalArgumentException e)
        {
            assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
            assertThat(ConfigurationHolder.getConfiguration().getConversionStrategy(), is(INITIAL_CONVERSION_STRATEGY));
            assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
        }
    }

}
