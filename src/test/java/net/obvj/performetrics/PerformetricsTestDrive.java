package net.obvj.performetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.monitors.MonitoredCallable;
import net.obvj.performetrics.monitors.MonitoredRunnable;

public class PerformetricsTestDrive
{
    public static void main(String[] args) throws Exception
    {
        Locale.setDefault(new Locale("en", "US"));

        testStopwatch1();
        System.out.println("\n\n=======================================\n");
        System.out.println("Now in fast mode...\n");
        Performetrics.setDefaultConversionMode(ConversionMode.FAST);
        testStopwatch1();

        System.out.println("\n\n=======================================\n");
        System.out.println("Now with a custom scale...\n");
        Performetrics.setDefaultConversionMode(ConversionMode.DOUBLE_PRECISION);
        Performetrics.setScale(2);

        testCallableWithLambda();

        Runnable r = null;
        MonitoredRunnable mr = new MonitoredRunnable(r);
        mr.run();
    }

    private static void testStopwatch1() throws InterruptedException, IOException
    {
        Stopwatch sw = Stopwatch.createStarted();

        // Enforcing some wall-clock time...
        Thread.sleep(2000);

        // Enforcing some user time...
        UUID.randomUUID();

        // Enforcing some system time...
        Files.list(Paths.get("/")).sorted().collect(Collectors.toList());

        sw.stop();

        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME));
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.NANOSECONDS));
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS));
        System.out.println(sw.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.SECONDS));
        sw.printStatistics(System.out);
        sw.printStatistics(System.out, TimeUnit.MILLISECONDS);
        sw.printStatistics(System.out, TimeUnit.SECONDS);
    }

    private static void testCallableWithLambda() throws Exception
    {
        MonitoredCallable<String> operation = new MonitoredCallable<>(() ->
        {
            List<Path> paths = Files.list(Paths.get(System.getenv("TEMP"))).sorted()
                    .collect(Collectors.toList());
            return "File count = " + paths.size();
        });

        System.out.println(operation.call());

        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME));
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.NANOSECONDS));
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS));
        System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME, TimeUnit.SECONDS));
        operation.printStatistics(System.out);
        operation.printStatistics(System.out, TimeUnit.MILLISECONDS);
        operation.printStatistics(System.out, TimeUnit.SECONDS);
    }
}
