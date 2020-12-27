package net.obvj.performetrics.util.print;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

import org.junit.Before;
import org.junit.Test;

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
    private static final Counter C1 = newCounter(WALL_CLOCK_TIME, MILLISECONDS, 5000, 6000);
    private static final Counter C2 = newCounter(CPU_TIME, NANOSECONDS, 700000000000l, 900000000000l);
    private static final Map<Type, List<Counter>> ALL_COUNTERS = new EnumMap<>(Type.class);

    static
    {
        ALL_COUNTERS.put(WALL_CLOCK_TIME, singletonList(C1));
        ALL_COUNTERS.put(CPU_TIME, singletonList(C2));
    }

    Stopwatch stopwatch = mock(Stopwatch.class);

    @Before
    public void setup()
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
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
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
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.printDetails(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

}
