package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.runnable.MultiCounterRunnableOperation;

public class PerformetricsTestDrive
{
    public static void main(String[] args) throws InterruptedException
    {
        testStopwatch1();
        System.out.println("-----");
        testRunnableWithLambda();
    }

    private static void testStopwatch1() throws InterruptedException
    {
        Stopwatch sw = Stopwatch.createStarted(Type.WALL_CLOCK_TIME);
        Thread.sleep(2000);
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
        MultiCounterRunnableOperation operation = new MultiCounterRunnableOperation(() -> {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {}
        });
        
        operation.run();
        
        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.NANOSECONDS));
        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.MILLISECONDS));
        System.out.println(operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime(TimeUnit.SECONDS));
        operation.printStatistics(System.out);
        operation.printStatistics(System.out, TimeUnit.MILLISECONDS);
        operation.printStatistics(System.out, TimeUnit.SECONDS);
    }
}
