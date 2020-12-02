package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.config.ConfigurationHolder;
import net.obvj.performetrics.monitors.MonitoredOperation;
import net.obvj.performetrics.util.SystemUtils;

/**
 * Unit tests for the {@link Performetrics} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SystemUtils.class)
public class PerformetricsTest
{
    // Default values
    private static final TimeUnit INITIAL_TIME_UNIT = ConfigurationHolder.getConfiguration().getTimeUnit();
    private static final ConversionMode INITIAL_CONVERSION_MODE = ConfigurationHolder.getConfiguration().getConversionMode();
    private static final int INITIAL_SCALE = ConfigurationHolder.getConfiguration().getScale();

    @Mock
    private Runnable runnable;

    @Before
    public void setup()
    {
        mockStatic(SystemUtils.class);
    }

    private void checkAllDefaultValues()
    {
        checkDefaultTimeUnit();
        checkDefaultConversionMode();
        checkDefaulScale();
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

    @After
    public void reset()
    {
        ConfigurationHolder.reset();
    }

    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(Performetrics.class, instantiationNotAllowed());
    }

    @Test
    public void setDefaultConversionMode_fast_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(FAST);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(FAST)));
        checkDefaultTimeUnit();
        checkDefaulScale();
    }

    @Test
    public void setDefaultTimeUnit_seconds_updatesConfiguration()
    {
        Performetrics.setDefaultTimeUnit(MILLISECONDS);
        assertThat(ConfigurationHolder.getConfiguration().getTimeUnit(), is(equalTo(MILLISECONDS)));
        checkDefaultConversionMode();
        checkDefaulScale();
    }

    @Test
    public void setDefaultConversionMode_doublePrecision_updatesConfiguration()
    {
        Performetrics.setDefaultConversionMode(DOUBLE_PRECISION);
        assertThat(ConfigurationHolder.getConfiguration().getConversionMode(), is(equalTo(DOUBLE_PRECISION)));
        checkDefaultTimeUnit();
        checkDefaulScale();
    }

    @Test
    public void setScale_valid_updatesConfiguration()
    {
        Performetrics.setScale(16);
        assertThat(ConfigurationHolder.getConfiguration().getScale(), is(equalTo(16)));
        checkDefaultTimeUnit();
        checkDefaultConversionMode();
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
        MonitoredOperation operation = Performetrics.monitorOperation(runnable);

        then(runnable).should().run();
        assertThat(operation.getCounters().size(), is(equalTo(Type.values().length)));

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getWallClockTimeNanos();

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getCpuTimeNanos();

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getUserTimeNanos();

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getSystemTimeNanos();
    }

    @Test
    public void monitorOperation_twoSpecificCounters()
    {
        MonitoredOperation operation = Performetrics.monitorOperation(runnable, Type.WALL_CLOCK_TIME, Type.CPU_TIME);

        then(runnable).should().run();
        assertThat(operation.getCounters().size(), is(equalTo(2)));

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getWallClockTimeNanos();

        verifyStatic(SystemUtils.class, times(2));
        SystemUtils.getCpuTimeNanos();

        verifyStatic(SystemUtils.class, never());
        SystemUtils.getUserTimeNanos();

        verifyStatic(SystemUtils.class, never());
        SystemUtils.getSystemTimeNanos();
    }

}
