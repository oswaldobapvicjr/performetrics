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

import static net.obvj.performetrics.Performetrics.ALL_TYPES;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.TimingSessionContainer;

/**
 * A {@link Callable} and {@link Supplier} wrapper that maintains one or more counters for
 * monitoring the execution time of a target operation.
 *
 * <p>Specify a target {@code Callable} via constructor, then execute the operation using
 * {@link #call()} or {@link #get()}.</p>
 *
 * <ul>
 *   <li>{@link #call()} always executes the underlying target operation, records timing metrics,
 *       and updates the stored result.</li>
 *   <li>{@link #get()} returns the cached result if already executed, or invokes {@link #callUnchecked()}
 *       on the first call.</li>
 * </ul>
 *
 * <p>After execution, retrieve or output timing results using methods such as
 * {@link #print(java.io.PrintStream)}, or {@link #elapsedTime(Type)}:</p>
 *
 * <pre>{@code
 * MonitoredCallable<String> monitoredCallable = new MonitoredCallable<>(callable);
 * String result = monitoredCallable.get(); // Executes once
 * String cached = monitoredCallable.get(); // Returns cached value
 * monitoredCallable.call();                // Re-executes and updates cached value
 * }</pre>
 *
 * <p><b>Note:</b> This class is not thread-safe. In a multi-threaded context, separate
 * instances must be created for each thread.</p>
 *
 * @param <V> the result type returned by the operation
 *
 * @author oswaldo.bapvic.jr
 * @see Counter
 * @see Counter.Type
 * @see Supplier
 * @see Callable
 */
public class MonitoredCallable<V> extends TimingSessionContainer implements Callable<V>, Supplier<V>
{
    private final Callable<V> callable;
    private V result;
    private boolean executed;

    /**
     * Builds a monitored operation for the given {@link Callable} with all available counter
     * types enabled.
     *
     * @param callable the {@link Callable} operation to be monitored; must not be {@code null}
     */
    public MonitoredCallable(Callable<V> callable)
    {
        this(callable, ALL_TYPES);
    }

    /**
     * Builds a monitored operation for the given {@link Callable} with specific counter types.
     * <p>If no types are specified, all available counter types are maintained.</p>
     *
     * @param callable the {@link Callable} operation to be monitored; must not be {@code null}
     * @param types    the counter types to maintain during execution
     */
    public MonitoredCallable(Callable<V> callable, Type... types)
    {
        this(callable, asList(types));
    }

    private MonitoredCallable(Callable<V> callable, List<Type> types)
    {
        super(types);
        this.callable = callable;
    }

    /**
     * Executes the target {@link Callable}, records execution timing metrics, and caches the result.
     * <p>Every call to this method forces a new execution and updates the cached value.</p>
     *
     * @return the result produced by the target operation
     * @throws NullPointerException if the target {@code Callable} is {@code null}
     * @throws Exception if unable to compute a result
     */
    @Override
    public V call() throws Exception
    {
        Objects.requireNonNull(callable, "The target Callable must not be null");
        super.startNewSession();
        try
        {
            result = callable.call();
            executed = true; // Mark as executed even if result is null
            return result;
        }
        finally
        {
            super.stopCurrentSession();
        }
    }

    /**
     * Returns the cached result if this operation has already been executed, or executes the
     * target operation once via {@link #callUnchecked()} if it has not been run yet.
     *
     * @return the cached or newly executed result produced by the target operation
     * @throws RuntimeException wrapping any checked exception thrown during execution
     * @see #callUnchecked()
     * @since 2.8.0
     */
    @Override
    public V get()
    {
        return executed ? result : callUnchecked();
    }

    /**
     * Executes the target operation, wrapping any checked {@link Exception} in a
     * {@link RuntimeException}.
     * <p>Forces execution and updates the cached result.</p>
     *
     * @return the result produced by the target operation
     * @throws RuntimeException if the target operation fails or throws an exception
     * @since 2.8.0
     */
    public V callUnchecked()
    {
        try
        {
            return call();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}