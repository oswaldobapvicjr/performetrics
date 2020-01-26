package net.obvj.performetrics.configuration;

/**
 * A class that holds current {@link Configuration} object.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class ConfigurationHolder
{
    private static Configuration configuration = new Configuration();

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private ConfigurationHolder()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Returns the current configuration.
     *
     * @return the current configuration
     */
    public static Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * Sets a given configuration as current.
     *
     * @param configuration the configuration to set
     */
    public static void setConfiguration(Configuration configuration)
    {
        ConfigurationHolder.configuration = configuration;
    }

    /**
     * Resets current configuration to default.
     */
    public static void reset()
    {
        configuration = new Configuration();
    }

}
