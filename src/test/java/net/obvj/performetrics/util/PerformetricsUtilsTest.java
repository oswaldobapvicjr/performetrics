package net.obvj.performetrics.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

/**
 * Test methods for the {@link PerformetricsUtils} class
 *
 * @author oswaldo.bapvic.jr
 */
public class PerformetricsUtilsTest
{
    /**
     * Tests that no instances of this utility class are created
     *
     * @throws ReflectiveOperationException in case of error getting constructor metadata or
     *                                      instantiating the private constructor
     */
    @Test(expected = InvocationTargetException.class)
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        Constructor<PerformetricsUtils> constructor = PerformetricsUtils.class.getDeclaredConstructor();
        assertThat("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        constructor.newInstance();
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
