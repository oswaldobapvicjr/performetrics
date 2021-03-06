package net.obvj.performetrics.util.print;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Unit tests for the {@link PrintUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
class PrintUtilsTest
{
    private static final Counter C1 = newCounter(WALL_CLOCK_TIME, MILLISECONDS, 5000, 6000);
    private static final Counter C2 = newCounter(CPU_TIME, NANOSECONDS, 700000000000l, 900000000000l);
    private static final Map<Type, List<Counter>> ALL_COUNTERS = new EnumMap<>(Type.class);

    static
    {
        ALL_COUNTERS.put(WALL_CLOCK_TIME, singletonList(C1));
        ALL_COUNTERS.put(CPU_TIME, singletonList(C2));
    }

    Stopwatch stopwatch = mock(Stopwatch.class);
    PrintStyle printStyle = mock(PrintStyle.class);

    @BeforeEach
    void setup()
    {
        when(stopwatch.getAllCountersByType()).thenReturn(ALL_COUNTERS);
    }

    private static Counter newCounter(Type type, TimeUnit timeUnit, long unitsBefore, long unitsAfter)
    {
        Counter counter = new Counter(type, timeUnit);
        counter.setUnitsBefore(unitsBefore);
        counter.setUnitsAfter(unitsAfter);
        return counter;
    }

    /**
     * Tests that no instances of this utility class are created.
     */
    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(PrintUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    /**
     * Test stopwatch summary printing onto a PrintStream.
     */
    @Test
    void printSummary_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_TABLE_FULL);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printSummary(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    /**
     * Test stopwatch details printing onto a PrintStream.
     */
    @Test
    void printDetails_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.DETAILED.format(stopwatch, PrintStyle.DETAILED_TABLE_FULL);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printDetails(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    private void prepareTestPrintStyle(PrintFormat printFormat)
    {
        when(printStyle.getPrintFormat()).thenReturn(printFormat);
        when(printStyle.getDurationFormat()).thenReturn(DurationFormat.FULL);
        when(printStyle.getRowFormat()).thenReturn("%s");
    }

    @Test
    void printSummary_withStopwatchAndPrintStreamAndPrintStyle_printsToTheStream()
            throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_CSV);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printSummary(stopwatch, printStream, PrintStyle.SUMMARIZED_CSV);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    @Test
    void printDetails_withStopwatchAndPrintStreamAndPrintStyle_printsToTheStream()
            throws UnsupportedEncodingException
    {
        prepareTestPrintStyle(PrintFormat.DETAILED);
        String expectedString = PrintFormat.DETAILED.format(stopwatch, printStyle);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printDetails(stopwatch, printStream, printStyle);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    @Test
    void printSummary_withStopwatchAndPrintStreamAndNullPrintStyle_printsUsingDefaultStyle()
            throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_TABLE_FULL);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printSummary(stopwatch, ps, null);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    @Test
    void printDetails_withStopwatchAndPrintStreamAndNullPrintStyle_printsUsingDefaultStyle()
            throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.DETAILED.format(stopwatch, PrintStyle.DETAILED_TABLE_FULL);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printDetails(stopwatch, ps, null);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

}
