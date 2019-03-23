package net.obvj.performetrics.util.printer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;

/**
 * Unit tests for the {@link PrintUtils} class
 *
 * @author oswaldo.bapvic.jr
 */
public class PrintUtilsTest
{

    private static final String TABLE_COLUMN_SEPARATOR = "\\|";

    /**
     * Tests that no instances of this utility class are created
     *
     * @throws Exception in case of error getting constructor metadata or instantiating the
     * private constructor via Reflection
     */
    @Test(expected = InvocationTargetException.class)
    public void testNoInstancesAllowed() throws Exception
    {
        try
        {
            Constructor<PrintUtils> constructor = PrintUtils.class.getDeclaredConstructor();
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

    private Counter newCounter(Type type, TimeUnit timeUnit, long unitsBefore, long unitsAfter)
    {
        Counter counter = new Counter(type, timeUnit);
        counter.setUnitsBefore(unitsBefore);
        counter.setUnitsAfter(unitsAfter);
        return counter;
    }

    /**
     * Tests that the row is composed by (1) the counter type, (2) the elapsed time and (3)
     * the time unit
     */
    @Test
    public void testCounterToRowFormat()
    {
        Counter c = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);

        String resultRow = PrintUtils.toRowFormat(c);
        String[] columns = resultRow.split(TABLE_COLUMN_SEPARATOR);

        assertEquals("WALL_CLOCK_TIME", columns[1].trim());
        assertEquals("1000", columns[2].trim());
        assertEquals("MILLISECONDS", columns[3].trim());
    }

    /**
     * Tests that the row is composed by (1) the counter type, (2) the elapsed time and (3)
     * the time unit
     */
    @Test
    public void testCountersListToRowFormat()
    {
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);

        String result = PrintUtils.toRowFormat(Arrays.asList(c1, c2));
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        String[] columnsRow1 = rows[1].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertEquals("WALL_CLOCK_TIME", columnsRow1[1].trim());
        assertEquals("1000", columnsRow1[2].trim());
        assertEquals("MILLISECONDS", columnsRow1[3].trim());

        assertEquals("CPU_TIME", columnsRow2[1].trim());
        assertEquals("200000000000", columnsRow2[2].trim());
        assertEquals("NANOSECONDS", columnsRow2[3].trim());
    }

}
