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

package net.obvj.performetrics.monitors;

import java.util.Objects;
import java.util.concurrent.Callable;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;

/**
 * <p>
 * A {@link Callable} wrapper that maintains one or more counters for monitoring the time
 * spent by the Callable's {@code call()} method.
 * </p>
 *
 * <p>
 * Specify a target {@code Callable} via constructor, then execute the {@code call()}
 * method available in this wrapper. The target {@code Callable}'s {@code call()} method
 * will be executed and monitored.
 * </p>
 *
 * <p>
 * After the operation, call {@code printSummary()} or {@code printDetails()} to print the
 * elapsed times or {@code elapsedTime(Counter.Type)}, to retrieve the elapsed time
 * duration for a particular counter. E.g.:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * Duration cpuTime = monitoredRunnable.elapsedTime(Counter.Type.CPU_TIME);
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * By default, all available counter types will be measured, if no specific counter types
 * are passed to the constructor. If required, an additional constructor may be used to
 * set up one or more specific counters to be maintained. E.g.:
 * </p>
 *
 * <blockquote>
 *
 * <pre>
 * new MonitoredCallable(callable); // maintains all available counter types
 * new MonitoredCallable(callable, Counter.Type.WALL_CLOCK_TIME); // wall-clock time only
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * For a list of available counters, refer to {@link Counter.Type}.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is not thread-safe. In a multi-thread context, different
 * instances must be created for each thread.
 * </p>
 *
 * @param <V> the result type of method call
 *
 * @author oswaldo.bapvic.jr
 * @see Counter
 * @see Counter.Type
 */
public class MonitoredCallable<V> extends MonitoredOperation implements Callable<V>
{
    private Callable<V> callable;

    /**
     * Builds this monitored operation with a given {@link Callable}. All available counter
     * types will be maintained.
     *
     * @param callable the Callable to be executed
     */
    public MonitoredCallable(Callable<V> callable)
    {
        this(callable, NO_SPECIFIC_TYPE);
    }

    /**
     * Builds this monitored operation with a given {@link Callable} and one or more specific
     * counter types to be maintained.
     *
     * @param callable the Callable to be executed
     * @param types    the counter types to be maintained with the operation
     */
    public MonitoredCallable(Callable<V> callable, Type... types)
    {
        super(types);
        this.callable = callable;
    }

    @Override
    public V call() throws Exception
    {
        Objects.requireNonNull(callable, "The target Callable must not be null");
        stopwatch.start();
        try
        {
            return callable.call();
        }
        finally
        {
            stopwatch.stop();
        }
    }

}
