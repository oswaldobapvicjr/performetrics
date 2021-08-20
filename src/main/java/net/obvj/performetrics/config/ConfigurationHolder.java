/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.performetrics.config;

import java.util.Objects;

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
     * @param configuration the configuration to set; must not be null
     * @throws NullPointerException if the specified configuration is null
     */
    public static void setConfiguration(Configuration configuration)
    {
        ConfigurationHolder.configuration = Objects.requireNonNull(configuration,
                "the default configuration must not be null");
    }

    /**
     * Resets current configuration to default.
     */
    public static void reset()
    {
        configuration = new Configuration();
    }

}
