![Performetrics logo](resources/performetrics_logo.svg)

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/oswaldobapvicjr/performetrics/Java%20CI%20with%20Maven)](https://github.com/oswaldobapvicjr/performetrics/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/codecov/c/github/oswaldobapvicjr/performetrics)](https://codecov.io/gh/oswaldobapvicjr/performetrics)
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
    <version>2.2.3</version>
</dependency>
```

If you use other dependency managers (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/performetrics).

---

## How to use it

### Example 1: Using the Stopwatch class

1. Create a stopwatch and start it:

    ```java
    Stopwatch stopwatch = new Stopwatch();
    stopwatch.start();
    ```

    > **Note:** A single call `Stopwatch.createStarted()` may provide a started stopwatch for convenience.

2. Execute the code to be profiled and then stop the timing session:

    ```java
    stopwatch.stop();
    ```

3. Get the elapsed time for a particular counter (e.g. CPU time), in a specific time unit:

    ```java
    double cpuTimeNanos = stopwatch.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.NANOSECONDS);
    ```

    > **Note:** Check the different `elapsedTime` options available to find the most suitable for you.

4. Print the summary to the system console:

    ```java
    stopwatch.printSummary(System.out);
    ```

    > **Sample output:**
    >
    > ````
    >  ====================================
    >  Counter                 Elapsed time
    >  ------------------------------------
    >  Wall clock time    0:00:01.062960500
    >  CPU time           0:00:00.109375000
    >  User time          0:00:00.046875000
    >  System time        0:00:00.062500000
    >  ====================================
    > ````

    > **Hint:** Call `stopwatch.printSummary(new PrintStream("stopwatch.csv"), PrintStyle.SUMMARIZED_CSV)` to generate a file with stopwatch data in CSV format.

5. Call `start` again to add a new timing session to the existing stopwatch.

6. Print stopwatch details:

    ```java
    stopwatch.printDetails(System.out);
    ```

    > **Sample output:**
    >
    > ````
    >  ===============================================
    >      #         Elapsed time     Elapsed time (+)
    >  ===============================================
    >  Wall clock time
    >  -----------------------------------------------
    >      1    0:00:01.062960500    0:00:01.062960500
    >      2    0:00:00.935263400    0:00:01.998223900
    >  -----------------------------------------------
    >  TOTAL                         0:00:01.998223900
    >  ===============================================
    >  CPU time
    >  -----------------------------------------------
    >      1    0:00:00.109375000    0:00:00.109375000
    >      2    0:00:00.140625000    0:00:00.250000000
    >  -----------------------------------------------
    >  TOTAL                         0:00:00.250000000
    >  ===============================================
    >  User time
    >  -----------------------------------------------
    >      1    0:00:00.046875000    0:00:00.046875000
    >      2    0:00:00.062500000    0:00:00.109375000
    >  -----------------------------------------------
    >  TOTAL                         0:00:00.109375000
    >  ===============================================
    > ````

---

### Example 2: Using a Monitored Operation

In this example, we are using `Performetrics.monitorOperation(...)` to run a procedure represented by a lambda expression and print the elapsed **wall-clock** and **CPU time** at the system console.

1. Create a monitored operation, with the the procedure to be executed attached:

    ```java
    MonitoredOperation operation = Performetrics.monitorOperation(() -> myObject.doStuff());
    ```

2. Print the elapsed time for each counter:

    ```java
    System.out.println(operation.elapsedTime(Counter.Type.WALL_CLOCK_TIME));
    System.out.println(operation.elapsedTime(Counter.Type.CPU_TIME));
    ```
    > **Sample output:**
    >
    > ````
    > 0.250739 second(s)
    > 0.000001 second(s)
    > ````

---

### Example 3: Working with Durations

The objects from the package `net.obvj.performetrics.util` contain useful features for parsing, conversion, formatting, and working with time durations. See some examples below:

1. Create a collection of time durations (or obtain some using `Stopwatch.elapsedTime()`):

    ```java
    Duration duration1 = Duration.of(1, TimeUnit.SECONDS);
    Duration duration2 = Duration.of(500, TimeUnit.MILLISECONDS);
    List<Duration> durations = Arrays.asList(duration1, duration2);
    ```

2. Discover the average duration:

    ```java
    Duration average = DurationUtils.average(durations);
    System.out.println(average); //result: 0.75 second(s)
    ```

2. Discover the lowest and highest durations and convert them to seconds:

    ```java
    double min = DurationUtils.min(durations).toSeconds(); //result: 0.5
    double max = DurationUtils.max(durations).toSeconds(); //result: 1.0
    ```

---
    
## Configuration

**Performetrics** does not only collect useful metrics. A comprehensive set of features was carefully designed to optimize data collection and present the results in different styles requiring a minimum of code.

### Conversion Modes

Performetrics provides two different conversion modes that can be applied depending on the user's requirements.

* **Fast conversion**: uses Java-standard classes to convert durations to different time units. Although conversions in this mode are extremely fast, those from finer to coarser granularities truncate, so lose precision. For example, converting 999 milliseconds to seconds results in 0 (worst case).

  To set this mode, call `Performetrics.setDefaultConversionMode(ConversionMode.FAST)`.  

* **Double-precision (default)**: implements a more robust conversion logic that avoids truncation from finer to coarser granularities. For example, converting 999 milliseconds to seconds results in 0.999

  A initial precision of 9 decimal places is set by default. This property can be changed calling `Performetrics.setScale(int)`.

> **Note:** Check the Javadoc to find out how to specify a different conversion mode for a single operation.

---

## Architecture

The following picture represents the main classes and their relationships. Click on the image to see a detailed diagram.

[![High-level classes overview](resources/High-level%20overview%20-%20v2.2-A.svg)](resources/Detailed%20class%20diagram%20-%20v2.2-B.svg)
