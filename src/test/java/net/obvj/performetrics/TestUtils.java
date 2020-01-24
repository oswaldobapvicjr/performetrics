package net.obvj.performetrics;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Common utilities for working with unit tests.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class TestUtils
{
    /**
     * Tests that no instance of a given class is created.
     *
     * @param targetClass the class to be tested
     * @throws AssertionError               if the target class is instantiated
     * @throws ReflectiveOperationException in case of errors getting class metadata
     */
    public static void assertNoInstancesAllowed(Class<?> targetClass) throws ReflectiveOperationException
    {
        try
        {
            Constructor<?> constructor = targetClass.getDeclaredConstructor();
            assertTrue("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
            throw new AssertionError("Class was instantiated");
        }
        catch (InvocationTargetException ite)
        {
            // Success: the class cannot be instantiated
        }
    }

}
