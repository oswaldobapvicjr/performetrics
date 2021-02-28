package net.obvj.performetrics.config;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import net.obvj.performetrics.ConversionMode;

/**
 * Unit tests for the {@link ConfigurationHolder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class ConfigurationHolderTest
{
    @AfterEach
    void reset()
    {
        ConfigurationHolder.reset();
    }

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(ConfigurationHolder.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void getConfiguration_initial_defaultValues()
    {
        Configuration configuration = ConfigurationHolder.getConfiguration();
        assertThat(configuration.getConversionMode(), is(equalTo(Configuration.INITIAL_CONVERSION_MODE)));
        assertThat(configuration.getScale(), is(equalTo(Configuration.INITIAL_SCALE)));
    }

    @Test
    void setConfiguration_ValidConfiguration_suceeds()
    {
        Configuration newConfiguration = new Configuration();
        newConfiguration.setConversionMode(ConversionMode.FAST);
        newConfiguration.setScale(11);

        ConfigurationHolder.setConfiguration(newConfiguration);
        assertThat(ConfigurationHolder.getConfiguration(), is(equalTo(newConfiguration)));
    }

}
