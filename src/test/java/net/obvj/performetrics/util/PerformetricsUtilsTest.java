package net.obvj.performetrics.util;

import static org.junit.Assert.*;

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
     * @throws Exception in case of error getting constructor metadata or instantiating the
     * private constructor via Reflection
     */
    @Test(expected = InvocationTargetException.class)
    public void constructor_throwsException() throws Exception
    {
        try
        {
            Constructor<PerformetricsUtils> constructor = PerformetricsUtils.class.getDeclaredConstructor();
            assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));

            constructor.setAccessible(true);
            constructor.newInstance();
        }
        catch (InvocationTargetException ite)
        {
            Throwable cause = ite.getCause();
            assertEquals(IllegalStateException.class, cause.getClass());
            assertEquals("Utility class", cause.getMessage());
            throw ite;
        }
    }
}
