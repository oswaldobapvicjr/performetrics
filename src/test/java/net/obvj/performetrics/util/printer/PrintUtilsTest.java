package net.obvj.performetrics.util.printer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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
    private static final String SECONDS = "seconds";
    private static final String MILLISECONDS = "milliseconds";
    private static final String NANOSECONDS = "nanoseconds";
    private static final String TABLE_COLUMN_SEPARATOR = "\\|";

    /**
     * Tests that no instances of this utility class are created
     *
     * @throws ReflectiveOperationException in case of error getting constructor metadata or
     *                                      instantiating the private constructor
     */
    @Test(expected = InvocationTargetException.class)
    public void constructor_throwsException() throws ReflectiveOperationException
    {
        Constructor<PrintUtils> constructor = PrintUtils.class.getDeclaredConstructor();
        assertThat("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        constructor.newInstance();
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
    public void toRowFormat_withValidCounterAndIntegerResult_printsTypeAndElapsedTimeAndTimeUnit()
    {
        Counter c = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);

        String resultRow = PrintUtils.toRowFormat(c);
        String[] columns = resultRow.split(TABLE_COLUMN_SEPARATOR);

        assertThat(columns[1].trim(), is(Type.WALL_CLOCK_TIME.toString()));
        assertThat(columns[2].trim(), is("1000"));
        assertThat(columns[3].trim(), is(MILLISECONDS));
    }

    /**
     * Tests that the row is composed by (1) the counter type, (2) the elapsed time
     * (decimal-formatted) and (3) the time unit
     */
    @Test
    public void toRowFormat_withValidCounterAndCoarserTimeUnit_printsElapsedTimeFormatted()
    {
        Counter c = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 5789);

        String resultRow = PrintUtils.toRowFormat(c, TimeUnit.SECONDS);
        String[] columns = resultRow.split(TABLE_COLUMN_SEPARATOR);

        assertThat(columns[1].trim(), is(Type.WALL_CLOCK_TIME.toString()));
        assertThat(columns[2].trim(), is("0,789"));
        assertThat(columns[3].trim(), is(SECONDS));
    }

    /**
     * Tests that each row is composed by (1) the counter type, (2) the elapsed time and (3)
     * the time unit
     */
    @Test
    public void toTableFormat_withTwoCounters_printsTypeAndElapsedTimeAndTimeUnit()
    {
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);

        String result = PrintUtils.toTableFormat(Arrays.asList(c1, c2));
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        // Rows from 1 to 3 should contain the table header
        String[] header = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertThat(header[1].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_COUNTER));
        assertThat(header[2].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_ELAPSED_TIME));
        assertThat(header[3].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_TIME_UNIT));

        // Remaining rows should contain counters and elapsed times
        String[] columnsRow1 = rows[4].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[5].split(TABLE_COLUMN_SEPARATOR);

        assertThat(columnsRow1[1].trim(), is(Type.WALL_CLOCK_TIME.toString()));
        assertThat(columnsRow1[2].trim(), is("1000"));
        assertThat(columnsRow1[3].trim(), is(MILLISECONDS));

        assertThat(columnsRow2[1].trim(), is(Type.CPU_TIME.toString()));
        assertThat(columnsRow2[2].trim(), is("200000000000"));
        // assertThat(columnsRow2[2].trim(), is("2.0E11"));
        assertThat(columnsRow2[3].trim(), is(NANOSECONDS));
    }

    /**
     * Tests that each row is composed by (1) the counter type, (2) the elapsed time and (3)
     * the time unit; and the elapsed time is converted to the custom time unit (milliseconds)
     */
    @Test
    public void toTableFormat_withTwoCountersAndTimeUnit_printsTypeAndElapsedTimeAndTimeUnitConverted()
    {
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);

        String result = PrintUtils.toTableFormat(Arrays.asList(c1, c2), TimeUnit.MILLISECONDS);
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        // Rows from 1 to 3 should contain the table header
        String[] header = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertThat(header[1].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_COUNTER));
        assertThat(header[2].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_ELAPSED_TIME));
        assertThat(header[3].trim(), is(PrintUtils.COUNTERS_TABLE_COLUMN_TIME_UNIT));

        // Remaining rows should contain counters and elapsed times
        String[] columnsRow1 = rows[4].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[5].split(TABLE_COLUMN_SEPARATOR);

        assertThat(columnsRow1[1].trim(), is(Type.WALL_CLOCK_TIME.toString()));
        assertThat(columnsRow1[2].trim(), is("1000"));
        assertThat(columnsRow1[3].trim(), is(MILLISECONDS));

        assertThat(columnsRow2[1].trim(), is(Type.CPU_TIME.toString()));
        assertThat(columnsRow2[2].trim(), is("200000"));
        assertThat(columnsRow2[3].trim(), is(MILLISECONDS));
    }

    /**
     * Test stopwatch printing onto a PrintStream.
     */
    @Test
    public void printStopwatch_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
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

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test stopwatch printing onto a PrintStream with a custom time unit
     */
    @Test
    public void printStopwatch_withStopwatchAndPrintStreamAndTimeUnit_printsTableToTheStreamInTheGivenTimeUnit()
            throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        String expectedString = PrintUtils.toTableFormat(Arrays.asList(c1, c2), TimeUnit.MILLISECONDS);

        // Prepare stopwatch
        Stopwatch stopwatch = Mockito.mock(Stopwatch.class);
        Mockito.when(stopwatch.getAllCounters()).thenReturn(Arrays.asList(c1, c2));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printStopwatch(stopwatch, ps, TimeUnit.MILLISECONDS);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream.
     */
    @Test
    public void printCounters_withCollectionAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        String expectedString = PrintUtils.toTableFormat(counters);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printCounters(counters, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream with a custom time unit
     */
    @Test
    public void printCounters_withCollectionsAndPrintStreamAndTimeUnit_printsTableToTheStreamInTheGivenTimeUnit()
            throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        String expectedString = PrintUtils.toTableFormat(counters, TimeUnit.MILLISECONDS);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printCounters(counters, ps, TimeUnit.MILLISECONDS);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

}
