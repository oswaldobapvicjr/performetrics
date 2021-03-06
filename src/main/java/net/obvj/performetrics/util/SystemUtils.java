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
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : -1L;
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
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : -1L;
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
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime())
                : -1L;
    }
}
