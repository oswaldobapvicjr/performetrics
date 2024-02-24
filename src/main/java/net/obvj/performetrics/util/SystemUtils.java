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

package net.obvj.performetrics.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * A utility class for retrieving system data.
 *
 * @author oswaldo.bapvic.jr
 */
public class SystemUtils
{

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    /**
     * This is a utility class, not meant to be instantiated.
     */
    private SystemUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return the difference, measured in milliseconds, between the current time and
     *         midnight, January 1, 1970 UTC.
     */
    public static long getWallClockTimeMillis()
    {
        return System.currentTimeMillis();
    }

    /**
     * Returns the current value of the current Java Virtual Machine's high-resolution time
     * source in nanoseconds.
     * <p>
     * This is a convenience method and is equivalent to calling:
     * <blockquote>
     * {@code System.nanoTime();}
     * </blockquote>
     * <p>
     * <b>Note:</b> This method provides nanosecond precision, but not necessarily nanosecond
     * resolution.
     *
     * @return the difference, measured in nanoseconds between current time and some arbitrary
     *         origin time for the current JVM, that can be used for measuring elapsed times.
     */
    public static long getWallClockTimeNanos()
    {
        return System.nanoTime();
    }

    /**
     * Returns the total time spent using a CPU for the current thread in nanoseconds.
     *
     * @return the total CPU time for the current thread if CPU time measurement is enabled;
     *         -1 otherwise.
     **/
    public static long getCpuTimeNanos()
    {
        return THREAD_MX_BEAN.isCurrentThreadCpuTimeSupported()
                ? THREAD_MX_BEAN.getCurrentThreadCpuTime()
                : -1L;
    }

    /**
     * Returns the CPU time that the current thread has executed in user mode in nanoseconds
     * (i.e., the time spent running current thread's own code).
     *
     * @return the user-level CPU time for the current thread if CPU time measurement is
     *         enabled; -1 otherwise.
     **/
    public static long getUserTimeNanos()
    {
        return THREAD_MX_BEAN.isCurrentThreadCpuTimeSupported()
                ? THREAD_MX_BEAN.getCurrentThreadUserTime()
                : -1L;
    }

    /**
     * Returns system time (the time spent running OS kernel code on behalf of your
     * application) in nanoseconds, by calculating the difference between CPU time and user
     * time for the current thread.
     *
     * @return the system time for the current thread if CPU time measurement is enabled; -1
     *         otherwise.
     */
    public static long getSystemTimeNanos()
    {
        return THREAD_MX_BEAN.isCurrentThreadCpuTimeSupported()
                ? (THREAD_MX_BEAN.getCurrentThreadCpuTime()
                        - THREAD_MX_BEAN.getCurrentThreadUserTime())
                : -1L;
    }
}
