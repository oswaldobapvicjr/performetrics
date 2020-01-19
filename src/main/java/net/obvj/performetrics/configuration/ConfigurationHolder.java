package net.obvj.performetrics.configuration;

/**
 * A class that holds current {@link Configuration} object.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ConfigurationHolder
{
    private static Configuration configuration = new Configuration();

    private ConfigurationHolder()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * @return the configuration
     */
    public static Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public static void setConfiguration(Configuration configuration)
    {
        ConfigurationHolder.configuration = configuration;
    }

    /**
     * Resets the configuration to defaults.
     */
    protected static void reset()
    {
        configuration = new Configuration();
    }

}
