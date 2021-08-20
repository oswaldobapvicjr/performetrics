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

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.monitors.MonitoredCallable;
import net.obvj.performetrics.util.print.PrintStyle;

public class PerformetricsTestDrive
{
    public static void main(String[] args) throws Exception
    {
        Locale.setDefault(new Locale("en", "US"));

        testStopwatch1();
        System.out.println("\n\n****************************************************\n");
        System.out.println("Now in fast mode...\n");
        Performetrics.setDefaultConversionMode(ConversionMode.FAST);
        testStopwatch1();

        System.out.println("\n\n****************************************************\n");
        System.out.println("Now with a custom scale...\n");
        Performetrics.setDefaultConversionMode(ConversionMode.DOUBLE_PRECISION);
        Performetrics.setScale(2);

        testCallableWithLambda();
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

        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME));
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.NANOSECONDS) + " nanosecods");
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.NANOSECONDS) + " nanosecods");
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS) + " millisecods");
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.MILLISECONDS) + " millisecods");
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.SECONDS) + " seconds");
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.SECONDS) + " seconds");
        sw.printDetails(System.out);
    }

    private static void testCallableWithLambda() throws Exception
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(() ->
        {
            List<Path> paths = Files.list(Paths.get(System.getenv("TEMP"))).sorted().collect(Collectors.toList());
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

        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME));
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.NANOSECONDS) + " nanosecods");
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.NANOSECONDS) + " nanosecods");
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS) + " millisecods");
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.MILLISECONDS) + " millisecods");
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.SECONDS) + " seconds");
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME).toTimeUnit(TimeUnit.SECONDS) + " seconds");

        operation.printSummary(System.out);
        System.out.println();

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

}
