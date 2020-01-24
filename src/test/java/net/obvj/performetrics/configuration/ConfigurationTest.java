package net.obvj.performetrics.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.obvj.performetrics.strategy.ConversionStrategy;

/**
 * Unit tests for the {@link Configuration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class ConfigurationTest
{

    @Test
    public void constructor_default_defaultValues()
    {
        Configuration configuration = new Configuration();
        assertThat(configuration.getConversionStrategy(), is(Configuration.INITIAL_CONVERSION_STRATEGY));
        assertThat(configuration.getScale(), is(Configuration.INITIAL_SCALE));
    }

    @Test
    public void setConversionStrategy_validStrategy_suceeds()
    {
        Configuration configuration = new Configuration();
        configuration.setConversionStrategy(ConversionStrategy.FAST);
        assertThat(configuration.getConversionStrategy(), is(ConversionStrategy.FAST));
    }

    @Test
    public void setScale_validNumber_suceeds()
    {
        Configuration configuration = new Configuration();
        configuration.setScale(16);
        assertThat(configuration.getScale(), is(16));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setScale_negativeNumber_fails()
    {
        Configuration configuration = new Configuration();
        configuration.setScale(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setScale_higherThanMaximum_fails()
    {
        Configuration configuration = new Configuration();
        configuration.setScale(17);
    }

}
