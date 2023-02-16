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

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.obvj.performetrics.config.ConfigurationHolder.getConfiguration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.SystemUtils;

/**
 * <p>
 * An object that stores time units of a particular type for elapsed time evaluation.
 * </p>
 * <p>
 * The associated counter type defines the time fetch strategy applied by the methods
 * {@code setUnitsBefore()} and {@code setUnitsAfter()}.
 * <p>
 * Once the units are set, calling {@code elapsedTime()} will return a {@link Duration}
 * that represents either:
 * </p>
 * <ul>
 * <li>the elapsed time between calls to {@code setUnitsBefore()} and
 * {@code setUnitsAfter()} provided that both methods have been called before; or</li>
 * <li>the elapsed time between calls to {@code setUnitsBefore()} and
 * {@code elapsedTime()} if {@code setUnitsAfter()} was not called</li>
 * </ul>
 * <p>
 * Optionally, call {@code elapsedTime(TimeUnit)} to return the total elapsed time in a
 * specific time unit.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @see Counter.Type
 * @see Duration
 * @see ConversionMode
 */
public class Counter
{
    /**
     * The string format applied on {@code toString()} calls.
     */
    protected static final String STRING_FORMAT = "Counter [type=%s, timeUnit=%s, unitsBefore=%s, unitsAfter=%s]";

    /**
     * Enumerates all supported counter types, defining a particular time fetch strategy for
     * each of them.
     */
    public enum Type
    {
        /**
         * The elapsed time experienced by a user waiting for a task to complete.
         */
        WALL_CLOCK_TIME("Wall clock time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getWallClockTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total time spent using a CPU for the current thread.
         */
        CPU_TIME("CPU time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getCpuTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The total CPU time that the current thread has executed in user mode (i.e., the time
         * spent running current thread's code).
         */
        USER_TIME("User time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getUserTimeNanos(), NANOSECONDS);
            }
        },

        /**
         * The time spent by the OS kernel to execute system level operations on behalf of the
         * application, such as context switching, resource allocation, etc.
         */
        SYSTEM_TIME("System time")
        {
            @Override
            public long getTime(TimeUnit targetTimeUnit)
            {
                return targetTimeUnit.convert(SystemUtils.getSystemTimeNanos(), NANOSECONDS);
            }
        };

        private final String name;

        private Type(String name)
        {
            this.name = name;
        }

        /**
         * Returns the textual representation associated with this type.
         */
        @Override
        public String toString()
        {
            return name;
        }

        /**
         * Executes a particular time fetching mode that varies for each counter type.
         *
         * @param timeUnit the time unit in which the time will be returned
         * @return the amount of time at the current instant, in the specified time unit.
         */
        public abstract long getTime(TimeUnit timeUnit);
    }

    private final Type type;
    private final TimeUnit timeUnit;

    private ConversionMode conversionMode;

    private long unitsBefore = 0;
    private long unitsAfter = 0;

    private boolean unitsAfterSet = false;

    /**
     * Builds a Counter with a given type and default time unit.
     *
     * @param type the type to set; cannot be null
     * @throws NullPointerException if the specified type is null
     */
    public Counter(Type type)
    {
        this(type, null);
    }

    /**
     * Builds a Counter with the given type and time unit.
     *
     * @param type     the type to set; cannot be null
     * @param timeUnit the time unit to set
     * @throws NullPointerException if the specified type is null
     */
    public Counter(Type type, TimeUnit timeUnit)
    {
        this(type, timeUnit, null);
    }

    /**
     * Builds a Counter with the given type, time unit, and conversion mode.
     *
     * @param type           the type to set; cannot be null
     * @param timeUnit       the time unit to set
     * @param conversionMode the {@link ConversionMode} to be applied
     * @throws NullPointerException if the specified type is null
     * @since 2.0.0
     */
    public Counter(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        this.type = Objects.requireNonNull(type, "the type must not be null");
        this.timeUnit = defaultIfNull(timeUnit, getConfiguration().getTimeUnit());
        this.conversionMode = defaultIfNull(conversionMode, getConfiguration().getConversionMode());
    }

    /**
     * Returns a default value if the object passed is {@code null}.
     *
     * @param <T>          the type of the object
     * @param object       the {@code Object} to test, may be {@code null}
     * @param defaultValue the default value to return, may be {@code null}
     * @return {@code object} if it is not {@code null}, defaultValue otherwise
     */
    private static <T> T defaultIfNull(T object, T defaultValue)
    {
        return object != null ? object : defaultValue;
    }

    /**
     * Returns the value of the {@code unitsBefore} field.
     *
     * @return the value of the {@code unitsBefore} field
     */
    public long getUnitsBefore()
    {
        return unitsBefore;
    }

    /**
     * Populates the {@code unitsBefore} field with an arbitrary value.
     *
     * @param unitsBefore the units to be set
     */
    public void setUnitsBefore(long unitsBefore)
    {
        this.unitsBefore = unitsBefore;
    }

    /**
     * Returns the value of the {@code unitsAfter} field.
     *
     * @return the value of the {@code unitsAfter} field
     */
    public long getUnitsAfter()
    {
        return unitsAfter;
    }

    /**
     * Populates the {@code unitsAfter} field with an arbitrary value.
     *
     * @param unitsAfter the units to be set
     */
    public void setUnitsAfter(long unitsAfter)
    {
        this.unitsAfter = unitsAfter;
        unitsAfterSet = true;
    }

    /**
     * Returns the {@link Type} associated with this counter.
     *
     * @return the {@link Type} associated with this counter
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns the time unit associated with this counter.
     *
     * @return the time unit associated with this counter
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Returns the {@link ConversionMode} associated with this counter.
     *
     * @return the {@link ConversionMode} associated with this counter
     * @since 2.0.0
     */
    public ConversionMode getConversionMode()
    {
        return conversionMode;
    }

    /**
     * Populates the {@code unitsBefore} field with the value retrieved by the time source
     * defined by this counter's type.
     */
    void setUnitsBefore()
    {
        setUnitsBefore(type.getTime(timeUnit));
    }

    /**
     * Populates the {@code unitsAfter} field with the value retrieved by the time source
     * defined by this counter's type.
     */
    void setUnitsAfter()
    {
        setUnitsAfter(type.getTime(timeUnit));
    }

    /**
     * Returns the elapsed time, in the internal time unit.
     *
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's time source, if {@code unitsAfter} is not set
     */
    long elapsedTimeInternal()
    {
        long tempUnitsAfter = unitsAfterSet ? unitsAfter : type.getTime(timeUnit);
        return tempUnitsAfter >= unitsBefore ? tempUnitsAfter - unitsBefore : -1;
    }

    /**
     * Returns the elapsed time.
     *
     * @return the difference between {@code unitsBefore} and {@code unitsAfter}, if both
     *         units are set; or the difference between {@code unitsBefore} and the current
     *         value retrieved by the counter's time source, if {@code unitsAfter} is not set.
     */
    public Duration elapsedTime()
    {
        return Duration.of(elapsedTimeInternal(), timeUnit);
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
    public double elapsedTime(TimeUnit timeUnit)
    {
        return elapsedTime(timeUnit, this.conversionMode);
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
    public double elapsedTime(TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return conversionMode.convert(elapsedTimeInternal(), this.timeUnit, timeUnit);
    }

    /**
     * Returns a string representation of this object.
     *
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format(STRING_FORMAT, type, timeUnit, unitsBefore, unitsAfter);
    }

}
