package net.obvj.performetrics.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Test methods for the {@link SystemUtils} class
 *
 * @author oswaldo.bapvic.jr
 */
public class SystemUtilsTest
{
    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(SystemUtils.class, instantiationNotAllowed());
    }

    @Test
    public void getWallClockTimeMillis_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeMillis(), is(greaterThan(0L)));
    }

    @Test
    public void getWallClockTimeNanos_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeNanos(), is(greaterThan(0L)));
    }
}
