/*
 * Copyright 2023 obvj.net
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

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.util.Duration;

/**
 * <p>
 * A "wrapper" class that allows retrieving values from an existing {@link TimingSession}
 * but prevents users from modifying it.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 * @since 2.4.0
 * @see TimingSession
 */
public final class UnmodifiableTimingSession extends TimingSession
{
    private final TimingSession timingSession;

    /**
     * Creates an unmodifiable object for a preset {@link TimingSession}.
     *
     * @param timingSession the {@link TimingSession} to be wrapped; not null
     * @throws NullPointerException if the {@link TimingSession} to be wrapped is null
     */
    public UnmodifiableTimingSession(TimingSession timingSession)
    {
        super(requireNonNull(timingSession, "the TimingSession to be wrapped must not be null").getTypes());
        this.timingSession = timingSession;
    }

    private static UnsupportedOperationException unsupportedOperation(String methodName)
    {
        return new UnsupportedOperationException(String
                .format("%s operation received on an unmodifiable TimingSession", methodName));
    }

    @Override
    public void reset()
    {
        throw unsupportedOperation("reset");
    }

    @Override
    public void start()
    {
        throw unsupportedOperation("start");
    }

    @Override
    public void stop()
    {
        throw unsupportedOperation("stop");
    }

    @Override
    public boolean isStarted()
    {
        return timingSession.isStarted();
    }

    @Override
    public Duration elapsedTime(Type type)
    {
        return timingSession.elapsedTime(type);
    }

    @Override
    public double elapsedTime(Type type, TimeUnit timeUnit)
    {
        return timingSession.elapsedTime(type, timeUnit);
    }

    @Override
    public double elapsedTime(Type type, TimeUnit timeUnit, ConversionMode conversionMode)
    {
        return timingSession.elapsedTime(type, timeUnit, conversionMode);
    }

    @Override
    Collection<Counter> getCounters()
    {
        return timingSession.getCounters().stream()
                .map(UnmodifiableCounter::new)
                .collect(toList());
    }

    @Override
    Counter getCounter(Type type)
    {
        return new UnmodifiableCounter(timingSession.getCounter(type));
    }

}
