# performetrics

[![Build Status](https://travis-ci.org/oswaldobapvicjr/performetrics.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/performetrics)

A simple performance data generator for Java applications

---

## Project overview

Most Java developers use the functions `System.currentTimeMillis()` or `System.currentTimeNanos()` inside their code to measure the elapsed time to perform some key operations. This is not a problem if you want to benchmark an application running in a dedicated system. However, the result is strongly affected by other activities in the system, such as background processes and I/O (e.g. disk and network activities). 

As from Java 1.5, it is possible to get additional metrics that may help you benchmark a task with some most accurate units, such as CPU time and user time.

This project provides useful methods for monitoring the performance of Java code in the unit type of your choice:

- **Wall clock time:** the elapsed time experienced by a user waiting for a task to complete (not necessarily a bad metric if you are interested in measuring a real user experience)

- **CPU time:** the total time spent using a CPU for the current thread

- **User time:** the total CPU time that the current thread has executed in user mode (i.e., the time spent running current thread's own code)

- **System time:** the time spent by the OS kernel to execute all the basic/system level operations on behalf of your application (such as context switching, resource allocation, etc.)

---

## How to use it

### Example 1: Using the `Stopwatch` class

The `Stopwatch` is a convenient object for timings with support to all of the abovementioned counters.

1. Add `performetrics` to the class path and import the `Stopwatch` to you Java class:

    ```java
    import net.obvj.performetrics.Stopwatch;
    ```

2. At any point of your code, create a new `Stopwatch` object and start it:

    ```java
    Stopwatch sw = new Stopwatch();
    sw.start();
    ```

    > **Note:** Alternatively, the factory method `Stopwatch.createStarted()` can be used to create a started stopwatch for convenience.

3. Execute the part of the code you want to profile and then stop the watch using the `stop()` method. 

4. Get the elapsed time for a particular counter (e.g. CPU time):

    ```java
    Counter cpuTime = sw.getCounter(Counter.Type.CPU_TIME);
    System.out.println("CPU time: " + cpuTime.elapsedTime());
    System.out.println("Time unit: " + cpuTime.getTimeUnit());
    ```

    > Because **Performetrics** uses the `TimeUnit` class from `java.util.concurrent`, you may convert all results to the time unit of your preference without effort. For example, to convert the output to milliseconds, just call: `cpuTime.getTimeUnit().toMillis(cpuTime.elapsedTime())`.

5. Try different counters to evaluate their results.

6. Print statistics to the console **(optional)**:

    ```java
    sw.printStatistics(System.out);
    ```

    > **Sample output:**
    >
    > ````
    > +-----------------+----------------------+--------------+
    > | Counter         |         Elapsed time | Time unit    |
    > +-----------------+----------------------+--------------+
    > | WALL_CLOCK_TIME |             85605718 | NANOSECONDS  |
    > | CPU_TIME        |             78000500 | NANOSECONDS  |
    > | USER_TIME       |             62400400 | NANOSECONDS  |
    > | SYSTEM_TIME     |             15600100 | NANOSECONDS  |
    > +-----------------+----------------------+--------------+
    > ````
