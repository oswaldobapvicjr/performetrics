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

package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.*;
import static net.obvj.performetrics.Counter.Type.*;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.obvj.performetrics.monitors.MonitoredCallable;
import net.obvj.performetrics.monitors.MonitoredRunnable;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.DurationFormat;
import net.obvj.performetrics.util.DurationUtils;
import net.obvj.performetrics.util.print.PrintStyle;

public class PerformetricsTestDrive
{
    public static void main(String[] args) throws Exception
    {
        Locale.setDefault(new Locale("en", "US"));

        testStopwatch1();
        System.out.println("\n\n****************************************************\n");
        System.out.println("Now in fast mode...\n");
        Performetrics.configuration().setConversionMode(ConversionMode.FAST);
        testStopwatch1();

        System.out.println("\n\n****************************************************\n");
        System.out.println("Now with a custom scale...\n");
        Performetrics.configuration().setConversionMode(ConversionMode.DOUBLE_PRECISION);
        Performetrics.configuration().setScale(2);

        System.out.println("\n\n****************************************************\n");
        testCallableWithLambda();

        System.out.println("\n\n****************************************************\n");
        testOnADifferentThread();

        System.out.println("\n\n****************************************************\n");
        loadTest();
    }

    private static void testStopwatch1() throws InterruptedException, IOException
    {
        Stopwatch sw = Stopwatch.createStarted();
        test(sw);

        for (int i = 0; i < 3; i++)
        {
            System.out.println("\nResuming the same stopwatch...\n");
            sw.start();
            test(sw);
        }
    }

    private static void test(Stopwatch sw) throws InterruptedException, IOException
    {
        // Enforcing some wall-clock time...
        int sleepTimeMillis = new Random(9999).nextInt(3000);
        Thread.sleep(sleepTimeMillis);

        // Enforcing some user time...
        UUID.randomUUID();
        System.out.println(sleepTimeMillis + "! = " + factorial(sleepTimeMillis));

        // Enforcing some system time...
        Files.list(Paths.get("/")).sorted().collect(Collectors.toList());

        sw.stop();

        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME));
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME, NANOSECONDS) + " nanoseconds");
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(NANOSECONDS) + " nanoseconds");
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME, MILLISECONDS) + " milliseconds");
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(MILLISECONDS) + " milliseconds");
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME, SECONDS) + " seconds");
        System.out.println(sw.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(SECONDS) + " seconds");

        System.out.println();
        sw.print(System.out, PrintStyle.LINUX);
        System.out.println();
        sw.printDetails(System.out);

        System.out.println("\nSESSION CHECK:");
        System.out.println(" - Last elapsed time (WALL_CLOCK_TIME): " + sw.lastSession().elapsedTime(WALL_CLOCK_TIME).toString(DurationFormat.LINUX));
        System.out.println(" - Last elapsed time (CPU_TIME): "        + sw.lastSession().elapsedTime(CPU_TIME).toString(DurationFormat.LINUX));
        System.out.println();
    }

    private static void testCallableWithLambda() throws Exception
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(() ->
        {
            List<Path> paths = Files.list(Paths.get(".")).sorted().collect(Collectors.toList());
            return "File count = " + paths.size();
        });

        testMonitoredCallable(operation);

        System.out.println("\n\n-----------------------------------------------------\n");
        System.out.println("Calling the same monitored operation again...\n");

        testMonitoredCallable(operation);
    }

    private static void testMonitoredCallable(MonitoredCallable<String> operation) throws Exception
    {
        System.out.println(operation.call());
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME));
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME, NANOSECONDS) + " nanosecods");
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(NANOSECONDS) + " nanosecods");
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME, MILLISECONDS) + " millisecods");
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(MILLISECONDS) + " millisecods");
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME, SECONDS) + " seconds");
        System.out.println(operation.elapsedTime(WALL_CLOCK_TIME).toTimeUnit(SECONDS) + " seconds");

        System.out.println();
        operation.print(System.out, PrintStyle.SUMMARIZED_XML);
        System.out.println("*****");
        operation.print(System.out, PrintStyle.DETAILED_XML);

        operation.printDetails(new PrintStream("stopwatch.csv"), PrintStyle.DETAILED_CSV);
        System.out.println();

        operation.printDetails(System.out);
    }

    public static BigInteger factorial(long x)
    {
        if (x == 0)
        {
            return BigInteger.ONE;
        }

        return BigInteger.valueOf(x).multiply(factorial(x - 1));
    }

    private static void testOnADifferentThread() throws InterruptedException
    {
        System.out.println("[main] Starting test on a different thread");
        Thread t1 = new Thread(() ->
        {
            Performetrics.monitorOperation(() ->
            {
                String logFormat = "[%s] %s";
                String threadName = Thread.currentThread().getName();
                System.out.println(String.format(logFormat, threadName, "Running..."));

                int size = 5_000_000;
                IntStream.range(0, size)
                        .mapToObj(i -> UUID.randomUUID())
                        .map(UUID::toString)
                        .sorted()
                        .collect(Collectors.toSet());

                System.out.println(String.format(logFormat, threadName, size + " UUIDs generated and sorted"));
                System.out.println();
            }).printSummary(System.out, PrintStyle.SUMMARIZED_YAML);
        });

        t1.start();
        System.out.println("[main] New thread started");
        t1.join();
    }

    private static void loadTest()
    {
        System.out.println("[main] Starting load test...");

        int repeatTimes = 1_000_000;
        Map<Double, AtomicInteger> amounts = new HashMap<>();
        List<Duration> durations = new ArrayList<>(repeatTimes);

        for (int i = 0; i < repeatTimes; i++)
        {
            MonitoredRunnable runnable = Performetrics.monitorOperation(() -> factorial(100L),
                    WALL_CLOCK_TIME, USER_TIME, SYSTEM_TIME);
            runnable.elapsedTime(WALL_CLOCK_TIME);
            runnable.elapsedTime(USER_TIME);
            Duration st = runnable.elapsedTime(SYSTEM_TIME);

            durations.add(st);
            amounts.computeIfAbsent(st.toSeconds(), k -> new AtomicInteger(0)).incrementAndGet();
        }

        System.out.println("[main] Load test finished");

        System.out.print("[main] Computing average of elapsed SYSTEM_TIME... ");
        System.out.println(DurationUtils.average(durations));

        System.out.println(amounts);
    }

}
