/*
 * Copyright 2024 obvj.net
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

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.util.Duration;

/**
 * <p>
 * A "wrapper" class that allows retrieving values from a pre-existing {@link Counter} but
 * prevents users from modifying it.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.5.1
 * @see Counter
 */
public final class UnmodifiableCounter extends Counter
{
    private final Counter counter;

    /**
     * Creates an unmodifiable {@link Counter}.
     *
     * @param counter the {@link Counter} to be wrapped; not null
     * @throws NullPointerException if the specified counter is null
     */
    public UnmodifiableCounter(final Counter counter)
    {
        super(counter.getType(), counter.getConversionMode());
        this.counter = counter;
    }

    private static UnsupportedOperationException unsupportedOperation(String methodName)
    {
        return new UnsupportedOperationException(
                String.format("\"%s\" not allowed (unmodifiable Counter)", methodName));
    }

    @Override
    public long getUnitsBefore()
    {
        return counter.getUnitsBefore();
    }

    @Override
    public void setUnitsBefore(long unitsBefore)
    {
        throw unsupportedOperation("setUnitsBefore");
    }

    @Override
    public long getUnitsAfter()
    {
        return counter.getUnitsAfter();
    }

    @Override
    public void setUnitsAfter(long unitsAfter)
    {
        throw unsupportedOperation("setUnitsAfter");
    }

    @Override
    void setUnitsBefore()
    {
        throw unsupportedOperation("setUnitsBefore");
    }

    @Override
    void setUnitsAfter()
    {
        throw unsupportedOperation("setUnitsAfter");
    }

    @Override
    public Duration elapsedTime()
    {
        return counter.elapsedTime();
    }

    @Override
    public double elapsedTime(TimeUnit timeUnit)
    {
        return counter.elapsedTime(timeUnit);
    }

    @Override
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return counter.elapsedTime(timeUnit, conversionMode);
    }

    @Override
    public String toString()
    {
        return counter.toString();
    }

}
