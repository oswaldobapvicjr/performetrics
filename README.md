![Performetrics logo](resources/performetrics_logo.svg)

[![Build Status](https://travis-ci.org/oswaldobapvicjr/performetrics.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/performetrics)
[![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/performetrics/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/performetrics?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/performetrics/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/performetrics)
[![Javadoc](https://javadoc.io/badge2/net.obvj/performetrics/javadoc.svg)](https://javadoc.io/doc/net.obvj/performetrics)

A simple performance data generator for Java applications

---

## Project overview

Most Java developers use the functions `System.currentTimeMillis()` or `System.currentTimeNanos()` inside their code to measure the elapsed time to run some key operations. This is not a problem if you want to benchmark an application running in a dedicated system. However, the result is strongly affected by other activities in the system, such as background processes and I/O (e.g. disk and network activities).

As from Java 1.5, it is possible to get additional metrics that may help you benchmark a task with some most accurate units:

- **Wall clock time:** the elapsed time experienced by a user waiting for a task to complete (not necessarily a bad metric if you are interested in measuring a real user experience)

- **CPU time:** the total time spent using a CPU for the current thread

- **User time:** the total CPU time that the current thread has executed in user mode (i.e., the time spent running current thread's  code)

- **System time:** the time spent by the OS kernel to execute all the basic/system-level operations on behalf of your application (such as context switching, resource allocation, etc.)

**Performetrics** provides convenient objects for time evaluation with support to all of the abovementioned counters.

---

## How to include it

If you are using Maven, add **Performetrics** as a dependency on your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>performetrics</artifactId>
    <version>2.0.1</version>
</dependency>
```

---

## How to use it

### Example 1: Using the Stopwatch class

1. Once available in your Java classpath, add the following import to your class:

    ```java
    import net.obvj.performetrics.Stopwatch;
    ```

2. Create a default stopwatch and start it:

    ```java
    Stopwatch sw = new Stopwatch();
    sw.start();
    ```

    > **Note:** A single call to the factory method `Stopwatch.createStarted()` may create a started stopwatch for convenience.

3. Execute the code to be profiled and then stop the counters (optional): 

    ```java
    sw.stop();
    ```

4. Get the elapsed time for a particular counter (e.g. CPU time):

    ```java
    long cpuTimeMillis = sw.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.MILLISECONDS);
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

---
    
## Configuration

**Performetrics** does not only collect useful metrics. A comprehensive set of features was carefully designed to optimize data collection and present the results in different styles with a minimum of code.

### Conversion Modes

Performetrics provides two different conversion modes that can be applied depending on the user's requirements.

* **Fast conversion**: uses Java-standard classes to convert durations to different time units. Although conversions in this mode are extremely fast, those from finer to coarser granularities truncate, so lose precision. For example, converting 999 milliseconds to seconds results in 0 (worst case).

  To set this mode, call `Performetrics.setDefaultConversionMode(ConversionMode.FAST)`.  

* **Double-precision (default)**: implements a more robust conversion logic that avoids truncation from finer to coarser granularities. For example, converting 999 milliseconds to seconds results in 0.999

  A initial precision of 9 decimal places is set by default. This property can be changed calling `Performetrics.setScale(int)`.

---

## Architecture

The following picture represents the main classes and their relationships. Click on the image to see a detailed diagram.

[![High-level classes overview](resources/High-level%20overview%20-%20v2.0-A.svg)](resources/Detailed%20class%20diagram%20-%20v2.1-A.svg)
