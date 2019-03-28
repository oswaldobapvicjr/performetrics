# performetrics

[![Build Status](https://travis-ci.org/oswaldobapvicjr/performetrics.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/performetrics)
[![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/performetrics/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/performetrics?branch=master)

A simple performance data generator for Java applications

---

## Project overview

Most Java developers use the functions `System.currentTimeMillis()` or `System.currentTimeNanos()` inside their code to measure the elapsed time to perform some key operations. This is not a problem if you want to benchmark an application running in a dedicated system. However, the result is strongly affected by other activities in the system, such as background processes and I/O (e.g. disk and network activities). 

As from Java 1.5, it is possible to get additional metrics that may help you benchmark a task with some most accurate units:

- **Wall clock time:** the elapsed time experienced by a user waiting for a task to complete (not necessarily a bad metric if you are interested in measuring a real user experience)

- **CPU time:** the total time spent using a CPU for the current thread

- **User time:** the total CPU time that the current thread has executed in user mode (i.e., the time spent running current thread's own code)

- **System time:** the time spent by the OS kernel to execute all the basic/system level operations on behalf of your application (such as context switching, resource allocation, etc.)

**Performetrics** provides convenient objects for time evaluations with support to all of the abovementioned counters.

---

## How to use it

### Example 1: Using the Stopwatch class

1. Add **Performetrics** to your project and the following import to your class:

    ```java
    import net.obvj.performetrics.Stopwatch;
    ```

2. Create a stopwatch and start it:

    ```java
    Stopwatch sw = new Stopwatch();
    sw.start();
    ```

    > **Note:** A stopwatch with all available counters will be created by default. To specify which counters shall be maintained, use the overloaded constructor `Stopwatch(Type... types)`. 

    > **Hint:** Alternatively, the factory methods `Stopwatch.createStarted()` and `Stopwatch.createStarted(Type... types)` may create a started stopwatch for convenience.

3. Execute the the code to be profiled and then stop the watch: 

    ```java
    sw.stop();
    ```

4. Get the elapsed time for a particular counter (e.g. CPU time):

    ```java
    Counter cpuTime = sw.getCounter(Counter.Type.CPU_TIME);
    long elapsedTime = cpuTime.elapsedTime();
    TimeUnit timeUnit = cpuTime.getTimeUnit();
    ```

    > **Note:** Because **Performetrics** uses the `TimeUnit` class from `java.util.concurrent`, all results can be converted to different time units without effort. To convert the output to milliseconds, for example, just call: `timeUnit.toMillis(cpuTime.elapsedTime())`.

5. Try different counters to evaluate their results.

### Example 2: Printing statistics from the Stopwatch

1. Execute steps from 1 to 3 at Example 1.

2. Print statistics to the console with the following statement:

    ```java
    sw.printStatistics(System.out);
    ```

    > **Sample output:**
    >
    > ````
    >  +-----------------+----------------------+--------------+
    >  | Counter         |         Elapsed time | Time unit    |
    >  +-----------------+----------------------+--------------+
    >  | WALL_CLOCK_TIME |             85605718 | NANOSECONDS  |
    >  | CPU_TIME        |             78000500 | NANOSECONDS  |
    >  | USER_TIME       |             62400400 | NANOSECONDS  |
    >  | SYSTEM_TIME     |             15600100 | NANOSECONDS  |
    >  +-----------------+----------------------+--------------+
    > ````

### Example 3: Using one of the Runnable or Callable operations

Check out some convenient classes inside the packages `net.obvj.performetrics.callable` and `net.obvj.performetrics.runnable`. In this example, we are using the `CpuTimeRunnableOperation` to measure the CPU time of a given Runnable, in nanoseconds:

1. Create a `CpuTimeRunnableOperation` with the Runnable to be monitored attached:

    ```java
    Runnable myRunnable; //target runnable initialization omitted
    CpuTimeRunnableOperation timedRunnable = new CpuTimeRunnableOperation(myRunnable);
    ```

2. Run it:

    ```java
    timedRunnable.run();
    ```

3. Get the elapsed time:

    ```java
    Counter cpuTime = timedRunnable.getCounter();
    long elapsedTime = cpuTime.elapsedTime();
    TimeUnit timeUnit = cpuTime.getTimeUnit();
    ```
