package net.obvj.performetrics.util.printer;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;

/**
 * Unit tests for the {@link PrintUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintUtilsTest
{
    private static final Counter C1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
    private static final Counter C2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);
    private static final List<Counter> ALL_COUNTERS = Arrays.asList(C1, C2);

    @Mock Stopwatch stopwatch;

    @Before
    public void setup()
    {
        when(stopwatch.getCounters()).thenReturn(ALL_COUNTERS);
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
    public void constructor_instantiationNotAllowed()
    {
        assertThat(PrintUtils.class, instantiationNotAllowed());
    }

    /**
     * Test stopwatch summary printing onto a PrintStream.
     */
    @Test
    public void printSummary_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_HORIZONTAL_LINES);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintUtils.printSummary(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

    /**
     * Test stopwatch details printing onto a PrintStream.
     */
    @Test
    public void printDetails_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        String expectedString = PrintFormat.DETAILED.format(stopwatch, PrintStyle.DETAILED_HORIZONTAL_LINES);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintUtils.printDetails(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

}
