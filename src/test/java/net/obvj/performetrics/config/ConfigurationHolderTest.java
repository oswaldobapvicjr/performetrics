package net.obvj.performetrics.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Test;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.TestUtils;
import net.obvj.performetrics.config.Configuration;
import net.obvj.performetrics.config.ConfigurationHolder;

/**
 * Unit tests for the {@link ConfigurationHolder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class ConfigurationHolderTest
{
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
        TestUtils.assertNoInstancesAllowed(ConfigurationHolder.class);
    }

    @Test
    public void getConfiguration_initial_defaultValues()
    {
        Configuration configuration = ConfigurationHolder.getConfiguration();
        assertThat(configuration.getConversionMode(), is(Configuration.INITIAL_CONVERSION_MODE));
        assertThat(configuration.getScale(), is(Configuration.INITIAL_SCALE));
    }

    @Test
    public void setConfiguration_ValidConfiguration_suceeds()
    {
        Configuration newConfiguration = new Configuration();
        newConfiguration.setConversionMode(ConversionMode.FAST);
        newConfiguration.setScale(11);

        ConfigurationHolder.setConfiguration(newConfiguration);
        assertThat(ConfigurationHolder.getConfiguration(), is(newConfiguration));
    }

}
