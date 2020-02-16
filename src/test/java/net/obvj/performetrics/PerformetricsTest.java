package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class PerformetricsTest
{
    private static final TimeUnit INITIAL_TIME_UNIT = ConfigurationHolder.getConfiguration().getTimeUnit();
    private static final ConversionMode INITIAL_CONVERSION_MODE = ConfigurationHolder.getConfiguration()
            .getConversionMode();
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
    public void setDefaultConversionMode_fast_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(FAST);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(FAST));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setDefaultTimeUnit_seconds_updatesConfiguration()
    {
        Performetrics.setDefaultTimeUnit(MILLISECONDS);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(MILLISECONDS));
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(INITIAL_CONVERSION_MODE));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setDefaultConversionMode_doublePrecision_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(DOUBLE_PRECISION);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(DOUBLE_PRECISION));
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
    }

    @Test
    public void setScale_valid_updatesConfiguration()
    {
        Performetrics.setScale(16);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(INITIAL_TIME_UNIT));
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(INITIAL_CONVERSION_MODE));
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
            assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(INITIAL_CONVERSION_MODE));
            assertThat(ConfigurationHolder.getConfiguration().getScale(), is(INITIAL_SCALE));
        }
    }

}
