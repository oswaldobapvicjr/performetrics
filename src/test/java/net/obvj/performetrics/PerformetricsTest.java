package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static net.obvj.performetrics.ConversionMode.DOUBLE_PRECISION;
import static net.obvj.performetrics.ConversionMode.FAST;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

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
@RunWith(MockitoJUnitRunner.class)
public class PerformetricsTest
{
    // Default values
    private static final TimeUnit INITIAL_TIME_UNIT = ConfigurationHolder.getConfiguration().getTimeUnit();
    private static final ConversionMode INITIAL_CONVERSION_MODE = ConfigurationHolder.getConfiguration().getConversionMode();
    private static final int INITIAL_SCALE = ConfigurationHolder.getConfiguration().getScale();

    @Mock
    private Runnable runnable;

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
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredOperation operation = Performetrics.monitorOperation(runnable);
            then(runnable).should().run();
            assertThat(operation.getAllCountersByType().keySet().size(), is(equalTo(Type.values().length)));

            systemUtils.verify(times(2), SystemUtils::getWallClockTimeNanos);
            systemUtils.verify(times(2), SystemUtils::getCpuTimeNanos);
            systemUtils.verify(times(2), SystemUtils::getUserTimeNanos);
            systemUtils.verify(times(2), SystemUtils::getSystemTimeNanos);
        }
    }

    @Test
    public void monitorOperation_twoSpecificCounters()
    {
        try (MockedStatic<SystemUtils> systemUtils = mockStatic(SystemUtils.class))
        {
            MonitoredOperation operation = Performetrics.monitorOperation(runnable, WALL_CLOCK_TIME, CPU_TIME);

            then(runnable).should().run();
            assertThat(operation.getAllCountersByType().keySet().size(), is(equalTo(2)));

            systemUtils.verify(times(2), SystemUtils::getWallClockTimeNanos);
            systemUtils.verify(times(2), SystemUtils::getCpuTimeNanos);
            systemUtils.verify(never(), SystemUtils::getUserTimeNanos);
            systemUtils.verify(never(), SystemUtils::getSystemTimeNanos);
        }
    }

}
