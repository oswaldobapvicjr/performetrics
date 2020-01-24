package net.obvj.performetrics.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import net.obvj.performetrics.TestUtils;

/**
 * Test methods for the {@link PerformetricsUtils} class
 *
 * @author oswaldo.bapvic.jr
 */
public class PerformetricsUtilsTest
{
    /**
     * Tests that no instances of this utility class are created.
     *
     * @throws ReflectiveOperationException in case of error getting class metadata
     */
    @Test
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        TestUtils.assertNoInstancesAllowed(PerformetricsUtils.class);
    }

    @Test
    public void getWallClockTimeMillis_positiveAmount()
    {
        assertThat(PerformetricsUtils.getWallClockTimeMillis(), is(greaterThan(0L)));
    }

    @Test
    public void getWallClockTimeNanos_positiveAmount()
    {
        assertThat(PerformetricsUtils.getWallClockTimeNanos(), is(greaterThan(0L)));
    }
}
