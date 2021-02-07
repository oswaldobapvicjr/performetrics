package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;
import org.mockito.MockedStatic;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredOperation;
import net.obvj.performetrics.util.SystemUtils;
import net.obvj.performetrics.util.print.PrintStyle;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class PerformetricsTest
{
    // Default values
    private static final TimeUnit INITIAL_TIME_UNIT = ConfigurationHolder.getConfiguration().getTimeUnit();
    private static final ConversionMode INITIAL_CONVERSION_MODE = ConfigurationHolder.getConfiguration().getConversionMode();
    private static final int INITIAL_SCALE = ConfigurationHolder.getConfiguration().getScale();
    private static final PrintStyle INITIAL_PRINT_STYLE_FOR_SUMMARY = ConfigurationHolder.getConfiguration().getPrintStyleForSummary();
    private static final PrintStyle INITIAL_PRINT_STYLE_FOR_DETAILS = ConfigurationHolder.getConfiguration().getPrintStyleForDetails();

    private boolean runFlag = false;

    // Since JDK 17, Mockito cannot mock java.util.Runnable
    private Runnable runnable = () ->
    {
        runFlag = true;
    };

    @After
    public void resetFlag()
    {
        runFlag = false;
    }

    private void checkAllDefaultValues()
    {
        checkDefaultTimeUnit();
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    private void checkDefaultTimeUnit()
    {
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(equalTo(INITIAL_TIME_UNIT)));
    }

    private void checkDefaultConversionMode()
    {
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(INITIAL_CONVERSION_MODE)));
    }

    private void checkDefaulScale()
    {
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(equalTo(INITIAL_SCALE)));
    }

    private void checkDefaulPrintStyleForSummary()
    {
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForSummary(),
                is(equalTo(INITIAL_PRINT_STYLE_FOR_SUMMARY)));
    }

    private void checkDefaulPrintStyleForDetails()
    {
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForDetails(),
                is(equalTo(INITIAL_PRINT_STYLE_FOR_DETAILS)));
    }

    @After
    public void reset()
    {
        ConfigurationHolder.reset();
    }

    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(Performetrics.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    public void setDefaultConversionMode_fast_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(FAST);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(FAST)));
        checkDefaultTimeUnit();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    public void setDefaultTimeUnit_seconds_updatesConfiguration()
    {
        Performetrics.setDefaultTimeUnit(MILLISECONDS);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(equalTo(MILLISECONDS)));
        checkDefaultConversionMode();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();

    }

    @Test
    public void setDefaultConversionMode_doublePrecision_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(DOUBLE_PRECISION);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(DOUBLE_PRECISION)));
        checkDefaultTimeUnit();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();

    }

    @Test
    public void setScale_valid_updatesConfiguration()
    {
        Performetrics.setScale(16);
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(equalTo(16)));
        checkDefaultTimeUnit();
        checkDefaultConversionMode();
        checkDefaulPrintStyleForSummary();
        checkDefaulPrintStyleForDetails();

    }

    @Test
    public void setScale_invalid_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setScale(-1);
        }
        catch (IllegalArgumentException e)
        {
            checkAllDefaultValues();
        }
    }

    @Test
    public void monitorOperation_noSpecificCounter()
    {
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredOperation operation = Performetrics.monitorOperation(runnable);
            assertThat(runFlag, is(equalTo(true)));
            assertThat(operation.getAllCountersByType().keySet().size(), is(equalTo(Type.values().length)));

            systemUtils.verify(SystemUtils::getWallClockTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getCpuTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getUserTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getSystemTimeNanos, times(2));
        }
    }

    @Test
    public void monitorOperation_twoSpecificCounters()
    {
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredOperation operation = Performetrics.monitorOperation(runnable, WALL_CLOCK_TIME, CPU_TIME);

            assertThat(runFlag, is(equalTo(true)));
            assertThat(operation.getAllCountersByType().keySet().size(), is(equalTo(2)));

            systemUtils.verify(SystemUtils::getWallClockTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getCpuTimeNanos, times(2));
            systemUtils.verify(SystemUtils::getUserTimeNanos, never());
            systemUtils.verify(SystemUtils::getSystemTimeNanos, never());
        }
    }

    @Test
    public void setDefaultPrintStyleForSummary_valid_updatesConfiguration()
    {
        PrintStyle ps = mock(PrintStyle.class);
        Performetrics.setDefaultPrintStyleForSummary(ps);
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForSummary(), is(equalTo(ps)));
        checkDefaultConversionMode();
        checkDefaultTimeUnit();
        checkDefaulScale();
        checkDefaulPrintStyleForDetails();
    }

    @Test
    public void setDefaultPrintStyleForSummary_null_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setDefaultPrintStyleForSummary(null);
        }
        catch (NullPointerException e)
        {
            checkAllDefaultValues();
        }
    }

    @Test
    public void setDefaultPrintStyleForDetails_valid_updatesConfiguration()
    {
        PrintStyle ps = mock(PrintStyle.class);
        Performetrics.setDefaultPrintStyleForDetails(ps);
        assertThat(ConfigurationHolder.getConfiguration().getPrintStyleForDetails(), is(equalTo(ps)));
        checkDefaultConversionMode();
        checkDefaultTimeUnit();
        checkDefaulScale();
        checkDefaulPrintStyleForSummary();
    }

    @Test
    public void setDefaultPrintStyleForDetails_null_doesNotUpdateConfiguration()
    {
        try
        {
            Performetrics.setDefaultPrintStyleForDetails(null);
        }
        catch (NullPointerException e)
        {
            checkAllDefaultValues();
        }
    }

}
