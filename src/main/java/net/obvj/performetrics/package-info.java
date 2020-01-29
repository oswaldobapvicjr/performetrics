/**
 * <p>
 * Provides convenient classes for extracting performance metrics of Java code.
 * </p>
 *
 * <p>
 * The top level package contains the <b>Counter</b> class, capable of computing the
 * following metrics:
 * </p>
 *
 * <ul>
 * <li><b>Wall-clock time:</b> the elapsed time experienced by a user waiting for a task
 * to complete</li>
 * <li><b>CPU time:</b> the total time spent using a CPU for the current thread</li>
 * <li><b>User time:</b> the total CPU time that the current thread has executed in user
 * mode, i.e., the time spent running current thread's own code</li>
 * <li><b>System time:</b> the time spent by the OS kernel to execute all the basic/system
 * level operations on behalf of your application, such as context switching, resource
 * allocation, etc.</li>
 * </ul>
 *
 * <p>
 * This package also contains the <b>Stopwatch</b> class that supports all of the
 * abovementioned counters.
 * </p>
 *
 * <p>
 * Global configuration parameters, such as default conversion mode, time unit and
 * precision, can be set up using the <b>Performetrics</b> facade.
 * </p>
 */
package net.obvj.performetrics;
