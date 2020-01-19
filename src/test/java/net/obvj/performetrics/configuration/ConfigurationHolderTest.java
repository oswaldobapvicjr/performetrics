package net.obvj.performetrics.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.After;
import org.junit.Test;

import net.obvj.performetrics.strategy.ConversionStrategy;

/**
 * Unit tests for the {@link ConfigurationHolder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ConfigurationHolderTest
{
    @After
    public void reset()
    {
        ConfigurationHolder.reset();
    }

    /**
     * Tests that no instances of this utility class are created
     *
     * @throws ReflectiveOperationException in case of error getting constructor metadata or
     *                                      instantiating the private constructor
     */
    @Test(expected = InvocationTargetException.class)
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        Constructor<ConfigurationHolder> constructor = ConfigurationHolder.class.getDeclaredConstructor();
        assertThat("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void getConfiguration_initial_defaultValues()
    {
        Configuration configuration = ConfigurationHolder.getConfiguration();
        assertThat(configuration.getConversionStrategy(), is(Configuration.DEFAULT_CONVERSION_STRATEGY));
        assertThat(configuration.getScale(), is(Configuration.DEFAULT_SCALE));
    }

    @Test
    public void setConfiguration_ValidConfiguration_suceeds()
    {
        Configuration newConfiguration = new Configuration();
        newConfiguration.setConversionStrategy(ConversionStrategy.FAST);
        newConfiguration.setScale(11);

        ConfigurationHolder.setConfiguration(newConfiguration);
        assertThat(ConfigurationHolder.getConfiguration(), is(newConfiguration));
    }

}
