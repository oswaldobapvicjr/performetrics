package net.obvj.performetrics.util.printer;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;

/**
 * Unit tests for the {@link PrintUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class PrintUtilsTest
{
    private static final String SECONDS = "seconds";
    private static final String MILLISECONDS = "milliseconds";
    private static final String NANOSECONDS = "nanoseconds";
    private static final String TABLE_COLUMN_SEPARATOR = "\\|";

    static
    {
        // Default locale for unit tests using DecimalFormat
        Locale.setDefault(new Locale("en", "US"));
    }

    /**
     * Tests that no instances of this utility class are created.
     */
    @Test
    public void constructor_instantiationNotAllowed()
    {
        assertThat(PrintUtils.class, instantiationNotAllowed());
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

        assertThat(columns[1].trim(), is(equalTo(Type.WALL_CLOCK_TIME.toString())));
        assertThat(columns[2].trim(), is(equalTo("1000")));
        assertThat(columns[3].trim(), is(equalTo(MILLISECONDS)));
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

        assertThat(columns[1].trim(), is(equalTo(Type.WALL_CLOCK_TIME.toString())));
        assertThat(columns[2].trim(), is(equalTo("0.789"))); // en_US Locale dependent test
        assertThat(columns[3].trim(), is(equalTo(SECONDS)));
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

        String result = PrintUtils.toTableFormat(PrintUtils.groupCountersByType(Arrays.asList(c1, c2)));
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        // Rows from 1 to 3 should contain the table header
        String[] header = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertThat(header[1].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_COUNTER)));
        assertThat(header[2].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_ELAPSED_TIME)));
        assertThat(header[3].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_TIME_UNIT)));

        // Remaining rows should contain counters and elapsed times
        String[] columnsRow1 = rows[4].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[5].split(TABLE_COLUMN_SEPARATOR);

        assertThat(columnsRow1[1].trim(), is(equalTo(Type.WALL_CLOCK_TIME.toString())));
        assertThat(columnsRow1[2].trim(), is(equalTo("1000")));
        assertThat(columnsRow1[3].trim(), is(equalTo(MILLISECONDS)));

        assertThat(columnsRow2[1].trim(), is(equalTo(Type.CPU_TIME.toString())));
        assertThat(columnsRow2[2].trim(), is(equalTo("200000000000")));
        assertThat(columnsRow2[3].trim(), is(equalTo(NANOSECONDS)));
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

        String result = PrintUtils.toTableFormat(PrintUtils.groupCountersByType(Arrays.asList(c1, c2)),
                TimeUnit.MILLISECONDS);
        String[] rows = result.split(PrintUtils.LINE_SEPARATOR);

        // Rows from 1 to 3 should contain the table header
        String[] header = rows[2].split(TABLE_COLUMN_SEPARATOR);

        assertThat(header[1].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_COUNTER)));
        assertThat(header[2].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_ELAPSED_TIME)));
        assertThat(header[3].trim(), is(equalTo(PrintUtils.COUNTERS_TABLE_COLUMN_TIME_UNIT)));

        // Remaining rows should contain counters and elapsed times
        String[] columnsRow1 = rows[4].split(TABLE_COLUMN_SEPARATOR);
        String[] columnsRow2 = rows[5].split(TABLE_COLUMN_SEPARATOR);

        assertThat(columnsRow1[1].trim(), is(equalTo(Type.WALL_CLOCK_TIME.toString())));
        assertThat(columnsRow1[2].trim(), is(equalTo("1000")));
        assertThat(columnsRow1[3].trim(), is(equalTo(MILLISECONDS)));

        assertThat(columnsRow2[1].trim(), is(equalTo(Type.CPU_TIME.toString())));
        assertThat(columnsRow2[2].trim(), is(equalTo("200000")));
        assertThat(columnsRow2[3].trim(), is(equalTo(MILLISECONDS)));
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
        String expectedString = PrintUtils.toTableFormat(PrintUtils.groupCountersByType(Arrays.asList(c1, c2)));

        // Prepare the stopwatch
        Stopwatch stopwatch = Mockito.mock(Stopwatch.class);
        Mockito.when(stopwatch.getCounters()).thenReturn(Arrays.asList(c1, c2));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
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
        String expectedString = PrintUtils
                .toTableFormat(PrintUtils.groupCountersByType(Arrays.asList(c1, c2)), TimeUnit.MILLISECONDS);

        // Prepare stopwatch
        Stopwatch stopwatch = Mockito.mock(Stopwatch.class);
        Mockito.when(stopwatch.getCounters()).thenReturn(Arrays.asList(c1, c2));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(stopwatch, ps, TimeUnit.MILLISECONDS);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream.
     */
    @Test
    public void printCounters_withListAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        Map<Type, List<Counter>> countersByType = PrintUtils.groupCountersByType(counters);
        String expectedString = PrintUtils.toTableFormat(countersByType);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(counters, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream with a custom time unit
     */
    @Test
    public void printCounters_withListAndPrintStreamAndTimeUnit_printsTableToTheStreamInTheGivenTimeUnit()
            throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        Map<Type, List<Counter>> countersByType = PrintUtils.groupCountersByType(counters);
        String expectedString = PrintUtils.toTableFormat(countersByType, TimeUnit.MILLISECONDS);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(counters, ps, TimeUnit.MILLISECONDS);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream.
     */
    @Test
    public void printCounters_withMapAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        Map<Type, List<Counter>> countersByType = PrintUtils.groupCountersByType(counters);
        String expectedString = PrintUtils.toTableFormat(countersByType);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(countersByType, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    /**
     * Test counters printing onto a PrintStream with a custom time unit
     */
    @Test
    public void printCounters_withMapAndPrintStreamAndTimeUnit_printsTableToTheStreamInTheGivenTimeUnit()
            throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
        List<Counter> counters = Arrays.asList(c1, c2);
        Map<Type, List<Counter>> countersByType = PrintUtils.groupCountersByType(counters);
        String expectedString = PrintUtils.toTableFormat(countersByType, TimeUnit.MILLISECONDS);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(countersByType, ps, TimeUnit.MILLISECONDS);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(expectedString));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTotalRowFormat_emptyList_illegalArgumentException()
    {
        PrintUtils.toTotalRowFormat(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTotalRowFormat_emptyListAndValidTimeUnit_illegalArgumentException()
    {
        PrintUtils.toTotalRowFormat(Collections.emptyList(), TimeUnit.SECONDS);
    }
}
