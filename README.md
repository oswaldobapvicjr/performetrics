![Performetrics logo](resources/performetrics_logo.svg)

[![Build Status](https://travis-ci.org/oswaldobapvicjr/performetrics.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/performetrics)
[![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/performetrics/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/performetrics?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/performetrics/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/performetrics)
[![Javadoc](https://javadoc.io/badge2/net.obvj/performetrics/javadoc.svg)](https://javadoc.io/doc/net.obvj/performetrics)

A simple performance data generator for Java applications

---

## Project overview

Most Java developers use the functions `System.currentTimeMillis()` or `System.currentTimeNanos()` inside their code to measure the elapsed time to perform some key operations. This is not a problem if you want to benchmark an application running in a dedicated system. However, the result is strongly affected by other activities in the system, such as background processes and I/O (e.g. disk and network activities).

As from Java 1.5, it is possible to get additional metrics that may help you benchmark a task with some most accurate units:

- **Wall clock time:** the elapsed time experienced by a user waiting for a task to complete (not necessarily a bad metric if you are interested in measuring a real user experience)

- **CPU time:** the total time spent using a CPU for the current thread

- **User time:** the total CPU time that the current thread has executed in user mode (i.e., the time spent running current thread's own code)

- **System time:** the time spent by the OS kernel to execute all the basic/system level operations on behalf of your application (such as context switching, resource allocation, etc.)

**Performetrics** provides convenient objects for time evaluation with support to all of the abovementioned counters.

---

## How to include it

To include **Performetrics** in your Maven project, add it as a dependency on your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>performetrics</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## How to use it

### Example 1: Using the Stopwatch class

1. Once you have included **Performetrics** into your Java project, add the following import to your class:

    ```java
    import net.obvj.performetrics.Stopwatch;
    ```

2. Create a default stopwatch and start it:

    ```java
    Stopwatch sw = new Stopwatch();
    sw.start();
    ```

    > **Note:** A single call to the factory method `Stopwatch.createStarted()` may create a started stopwatch for convenience.

3. Execute the the code to be profiled and then stop the counters (optional): 

    ```java
    sw.stop();
    ```

4. Get the elapsed time for a particular counter (e.g. CPU time):

    ```java
    Counter cpuTime = sw.getCounter(Counter.Type.CPU_TIME);
    long elapsedTimeMillis = cpuTime.elapsedTime(TimeUnit.MILLISECONDS);
    ```

5. Print statistics to the console:

    ```java
    sw.printStatistics(System.out);
    ```

    > **Sample output:**
    >
    > ````
    >  +-----------------+----------------------+--------------+
    >  | Counter         |         Elapsed time | Time unit    |
    >  +-----------------+----------------------+--------------+
    >  | Wall clock time |             85605718 | nanoseconds  |
    >  | CPU time        |             78000500 | nanoseconds  |
    >  | User time       |             62400400 | nanoseconds  |
    >  | System time     |             15600100 | nanoseconds  |
    >  +-----------------+----------------------+--------------+
    > ````

### Example 2: Using a MonitoredRunnable or MonitoredCallable

In this example, we are using the `MonitoredRunnable` class to measure the CPU time of a given Runnable, in nanoseconds:

1. Create a `MonitoredRunnable` with the Runnable to be monitored attached:

    ```java
    Runnable myRunnable; //target runnable initialization omitted
    MonitoredRunnable monitoredRunnable = new MonitoredRunnable(myRunnable, Type.CPU_TIME);
    ```

    > **Note:** If no specific counter type is passed, all available counters will be maintained.

2. Run it:

    ```java
    monitoredRunnable.run();
    ```

3. Get the elapsed time:

    ```java
    long elapsedTimeNanos = monitoredRunnable.elapsedTime(TimeUnit.NANOSECONDS);
    ```
