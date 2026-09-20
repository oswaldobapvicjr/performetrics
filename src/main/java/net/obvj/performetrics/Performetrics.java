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

package net.obvj.performetrics;

import java.util.List;
import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.Configuration;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredCallable;
import net.obvj.performetrics.monitors.MonitoredRunnable;

/**
 * A Facade class meant to provide a simple interface for common parameters setup and
 * other operations.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Performetrics
{

    /**
     * A list containing all the available counter types.
     *
     * @since 2.5.0
     */
    public static final List<Type> ALL_TYPES = List.of(Type.values());

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private Performetrics()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Returns the current {@link Configuration}.
     *
     * @return the current configuration
     * @since 2.5.3
     */
    public static Configuration configuration()
    {
        return ConfigurationHolder.getConfiguration();
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for all available counter types.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredRunnable runnable =}
     * {@code
     * Performetrics.monitorOperation(() -> myObj.exec());
     * }
     * {@code
     * Duration elapsedTime = runnable.elapsedTime(Type.WALL_CLOCK_TIME);
     * }
     * </pre>
     *
     * </blockquote>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @return the resulting {@link MonitoredRunnable}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredRunnable monitorOperation(Runnable runnable)
    {
        return monitorOperation(runnable, new Type[0]);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for the specified counter type(s) only.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredRunnable runnable = Performetrics}
     * {@code         .monitorOperation(() -> myObj.exec(), Type.CPU_TIME);}
     * {@code Duration elapsedTime = runnable.elapsedTime(Type.CPU_TIME);}
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * <b>Note:</b> If no type is specified, then all of the available types will be
     * maintained.
     *
     * <p>
     * <b>Note:</b> If the provided {@link Runnable} throws an exception during execution,
     * the method will not be able to generate the monitored {@link MonitoredRunnable}.
     * In this case, using the {@link MonitoredRunnable} class is recommended.
     * Then, the exception can be handled as necessary.
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @param types    the counter types to be measured in the operation
     * @return the resulting {@link MonitoredRunnable}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredRunnable monitorOperation(Runnable runnable, Type... types)
    {
        MonitoredRunnable monitoredRunnable = new MonitoredRunnable(runnable, types);
        monitoredRunnable.run();
        return monitoredRunnable;
    }

    /**
     * Calls the specified {@link Callable}, which can also be a lambda expression, and
     * collects metrics for all available counter types.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredCallable<String> callable =}
     * {@code
     * Performetrics.monitorOperation(() -> database.query());
     * }
     * {@code
     * String result = callable.getResult();
     * Duration elapsedTime = callable.elapsedTime(Type.WALL_CLOCK_TIME);
     * }
     * </pre>
     *
     * </blockquote>
     *
     * @param <V>    the result type of the callable
     * @param callable the {@link Callable} to be called and monitored
     * @return the resulting {@link MonitoredCallable}, which can be used to retrieve the
     *         collected results and the return value.
     * @throws Exception if the callable throws an exception during execution
     * @since 2.7.1
     */
    public static <V> MonitoredCallable<V> monitorOperation(Callable<V> callable) throws Exception
    {
        return monitorOperation(callable, new Type[0]);
    }

    /**
     * Calls the specified {@link Callable}, which can also be a lambda expression, and
     * collects metrics for the specified counter type(s) only.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code MonitoredCallable<Integer> callable = Performetrics}
     * {@code         .monitorOperation(() -> database.count(), Type.CPU_TIME);}
     * {@code Integer count = callable.getResult();}
     * {@code Duration elapsedTime = callable.elapsedTime(Type.CPU_TIME);}
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * <b>Note:</b> If no type is specified, then all of the available types will be
     * maintained.
     *
     * <p>
     * <b>Note:</b> If the provided {@link Callable} throws an exception during execution,
     * the exception will be propagated to the caller.
     *
     * @param <V>      the result type of the callable
     * @param callable the {@link Callable} to be called and monitored
     * @param types    the counter types to be measured in the operation
     * @return the resulting {@link MonitoredCallable}, which can be used to retrieve the
     *         collected results and the return value.
     * @throws Exception if the callable throws an exception during execution
     * @since 2.8.0
     */
    public static <V> MonitoredCallable<V> monitorOperation(Callable<V> callable, Type... types) throws Exception
    {
        MonitoredCallable<V> monitoredCallable = new MonitoredCallable<>(callable, types);
        monitoredCallable.call();
        return monitoredCallable;
    }
}
