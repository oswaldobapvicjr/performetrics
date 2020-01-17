package net.obvj.performetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.runnable.MonitoredRunnable;

public class PerformetricsTestDrive
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        testStopwatch1();
        System.out.println("-----");
        testRunnableWithLambda();
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

        System.out.println(sw.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.NANOSECONDS));
        System.out.println(sw.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.MILLISECONDS));
        System.out.println(sw.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.SECONDS));
        sw.printStatistics(System.out);
        sw.printStatistics(System.out, TimeUnit.MILLISECONDS);
        sw.printStatistics(System.out, TimeUnit.SECONDS);
    }

    private static void testRunnableWithLambda() throws InterruptedException
    {
        MonitoredRunnable operation = new MonitoredRunnable(() -> {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {}
        }, Counter.Type.WALL_CLOCK_TIME);

        operation.run();

        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.NANOSECONDS));
        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.MILLISECONDS));
        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.SECONDS));
        operation.printStatistics(System.out);
        operation.printStatistics(System.out, TimeUnit.MILLISECONDS);
        operation.printStatistics(System.out, TimeUnit.SECONDS);
    }
}
