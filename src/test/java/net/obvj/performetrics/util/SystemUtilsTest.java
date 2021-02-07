package net.obvj.performetrics.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Test methods for the {@link SystemUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class SystemUtilsTest
{
    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(SystemUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    public void getWallClockTimeMillis_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeMillis() > 0, is(true));
    }

    @Test
    public void getWallClockTimeNanos_positiveAmount()
    {
        assertThat(SystemUtils.getWallClockTimeNanos() > 0, is(true));
    }
}
