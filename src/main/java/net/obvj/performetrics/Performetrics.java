package net.obvj.performetrics;

import java.util.concurrent.TimeUnit;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredOperation;
import net.obvj.performetrics.monitors.MonitoredRunnable;

/**
 * A Facade class meant to provide a simple interface for common parameters setup and
 * other operations.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class Performetrics
{
    /**
     * This is a utility class, not meant to be instantiated.
     */
    private Performetrics()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Sets a time unit to be maintained by default if no specific time unit is specified.
     *
     * @param timeUnit the {@link TimeUnit} to set
     */
    public static void setDefaultTimeUnit(TimeUnit timeUnit)
    {
        ConfigurationHolder.getConfiguration().setTimeUnit(timeUnit);
    }

    /**
     * Sets a conversion mode to be applied by supported operations if no specific mode is
     * set.
     *
     * @param conversionMode the {@link ConversionMode} to set
     */
    public static void setDefaultConversionMode(ConversionMode conversionMode)
    {
        ConfigurationHolder.getConfiguration().setConversionMode(conversionMode);
    }

    /**
     * Sets a maximum number of decimal places to be applied if double-precision conversion
     * mode is set.
     *
     * @param scale a number between 0 and 16 to be set
     * @throws IllegalArgumentException if a number outside the allowed range is received
     */
    public static void setScale(int scale)
    {
        ConfigurationHolder.getConfiguration().setScale(scale);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for all available counter types.
     * <p>
     * For example:
     *
     * <pre>
     * {@code MonitoredOperation operation = Performetrics.monitorOperation(() -> myObj.doStuff());}
     * {@code System.out.println(operation.elapsedTime(Type.WALL_CLOCK_TIME));}
     * </pre>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @return the resulting {@link MonitoredOperation}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredOperation monitorOperation(Runnable runnable)
    {
        return monitorOperation(runnable, new Type[0]);
    }

    /**
     * Runs the specified {@link Runnable}, which can also be a lambda expression, and
     * collects metrics for the specified counter type(s).
     * <p>
     * For example:
     *
     * <pre>
     * {@code MonitoredOperation operation = Performetrics
     *        .monitorOperation(() -> myObj.doStuff(), Type.CPU_TIME);}
     * {@code System.out.println(operation.elapsedTime(Type.CPU_TIME));}
     * </pre>
     *
     * @param runnable the {@link Runnable} to be run and monitored
     * @param types    the counter types to be measured in the operation
     * @return the resulting {@link MonitoredOperation}, which can be used to retrieve the
     *         collected results.
     * @since 2.2.0
     */
    public static MonitoredOperation monitorOperation(Runnable runnable, Type... types)
    {
        MonitoredRunnable monitoredRunnable = new MonitoredRunnable(runnable, types);
        monitoredRunnable.run();
        return monitoredRunnable;
    }
}
