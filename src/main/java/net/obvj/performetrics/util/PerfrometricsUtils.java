package net.obvj.performetrics.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class PerfrometricsUtils {

	private PerfrometricsUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns the total CPU time for the current thread in nanoseconds.
	 * 
	 * @return the total CPU time for the current thread if CPU time measurement is
	 *         enabled; 0 otherwise.
	 **/
	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
	}

	/**
	 * Returns the CPU time that the current thread has executed in user mode in
	 * nanoseconds.
	 * 
	 * @return the user-level CPU time for the current thread if CPU time
	 *         measurement is enabled; 0 otherwise.
	 **/
	public static long getUserTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
	}

	/**
	 * Returns the difference between CPU time and user time for the current thread
	 * in nanoseconds.
	 * 
	 * @return the system time for the current thread if CPU time measurement is
	 *         enabled; 0 otherwise.
	 */
	public static long getSystemTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported()
				? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime())
				: 0L;
	}
}
