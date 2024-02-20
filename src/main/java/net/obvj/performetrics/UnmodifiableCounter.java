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
 * A "wrapper" class that allows retrieving values from a pre-built {@link Counter} but
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
     * Builds a Counter with a given type and default time unit.
     *
     * @param type the type to set; cannot be null
     * @throws NullPointerException if the specified type is null
     */
    public UnmodifiableCounter(Counter counter)
    {
        super(counter.getType(), counter.getTimeUnit(), counter.getConversionMode());
        this.counter = counter;
    }

    private static UnsupportedOperationException unsupportedOperation(String methodName)
    {
        return new UnsupportedOperationException(
                String.format("%s operation received on an unmodifiable Counter", methodName));
    }

    /**
     * Returns the value of the {@code unitsBefore} field.
     *
     * @return the value of the {@code unitsBefore} field
     */
    @Override
    public long getUnitsBefore()
    {
        return counter.getUnitsBefore();
    }

    /**
     * Populates the {@code unitsBefore} field with an arbitrary value.
     *
     * @param unitsBefore the units to be set
     */
    @Override
    public void setUnitsBefore(long unitsBefore)
    {
        throw unsupportedOperation("setUnitsBefore");
    }

    /**
     * Returns the value of the {@code unitsAfter} field.
     *
     * @return the value of the {@code unitsAfter} field
     */
    @Override
    public long getUnitsAfter()
    {
        return counter.getUnitsAfter();
    }

    /**
     * Populates the {@code unitsAfter} field with an arbitrary value.
     *
     * @param unitsAfter the units to be set
     */
    @Override
    public void setUnitsAfter(long unitsAfter)
    {
        throw unsupportedOperation("setUnitsAfter");
    }

    /**
     * Populates the {@code unitsBefore} field with the value retrieved by the time source
     * defined by this counter's type.
     */
    @Override
    void setUnitsBefore()
    {
        throw unsupportedOperation("setUnitsBefore");
    }

    /**
     * Populates the {@code unitsAfter} field with the value retrieved by the time source
     * defined by this counter's type.
     */
    @Override
    void setUnitsAfter()
    {
        throw unsupportedOperation("setUnitsAfter");
    }

    /**
     * Returns the elapsed time.
     *
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's time source, if {@code unitsAfter} is not set.
     */
    @Override
    public Duration elapsedTime()
    {
        return counter.elapsedTime();
    }

    /**
     * Returns the elapsed time in the specified {@link TimeUnit}.
     *
     * @param timeUnit the time unit to which the elapsed time will be converted
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's time source, if {@code unitsAfter} is not set.
     *         The value is converted into the specified time unit applying the default
     *         conversion mode.
     */
    @Override
    public double elapsedTime(TimeUnit timeUnit)
    {
        return counter.elapsedTime(timeUnit);
    }

    /**
     * Returns the elapsed time, in a given {@link TimeUnit}, with a custom
     * {@link ConversionMode}.
     *
     * @param timeUnit       the time unit to which the elapsed time will be converted
     * @param conversionMode the {@link ConversionMode} to be used
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's time source, if {@code unitsAfter} is not set.
     *         The value is converted into the specified time unit applying the given
     *         conversion mode.
     * @since 2.0.0
     */
    @Override
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return counter.elapsedTime(timeUnit, conversionMode);
    }

    /**
     * Returns a string representation of this object.
     *
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return counter.toString();
    }

}
