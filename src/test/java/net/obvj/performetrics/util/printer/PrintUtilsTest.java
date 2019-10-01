package net.obvj.performetrics.util.printer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;

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

        String result = PrintUtils.toTableFormat(Arrays.asList(c1, c2));
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        // Rows from 1 to 3 should contain the table header
        String[] header = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertEquals(PrintUtils.COUNTERS_TABLE_COLUMN_COUNTER, header[1].trim());
        assertEquals(PrintUtils.COUNTERS_TABLE_COLUMN_ELAPSED_TIME, header[2].trim());
        assertEquals(PrintUtils.COUNTERS_TABLE_COLUMN_TIME_UNIT, header[3].trim());

        // Remaining rows should contain counters and elapsed times
        String[] columnsRow1 = rows[4].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[5].split(TABLE_COLUMN_SEPARATOR);

        assertEquals("WALL_CLOCK_TIME", columnsRow1[1].trim());
        assertEquals("1000", columnsRow1[2].trim());
        assertEquals("MILLISECONDS", columnsRow1[3].trim());

        assertEquals("CPU_TIME", columnsRow2[1].trim());
        assertEquals("200000000000", columnsRow2[2].trim());
        assertEquals("NANOSECONDS", columnsRow2[3].trim());
    }
    
    /**
     * Test stopwatch printing onto a PrintStream.
     */
    @Test
    public void testPrintStopwatch() throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        String expectedString = PrintUtils.toTableFormat(Arrays.asList(c1, c2));

        // Prepare stopwatch
        Stopwatch stopwatch = Mockito.mock(Stopwatch.class);
        Mockito.when(stopwatch.getAllCounters()).thenReturn(Arrays.asList(c1, c2));
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printStopwatch(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        
        assertEquals(printedString, expectedString);
    }

}
