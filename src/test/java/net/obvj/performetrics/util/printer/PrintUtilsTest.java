package net.obvj.performetrics.util.printer;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
     * Test stopwatch printing onto a PrintStream.
     */
    @Test
    public void printStopwatch_withStopwatchAndPrintStream_printsTableToTheStream() throws UnsupportedEncodingException
    {
        // Prepare data
        Counter c1 = newCounter(Type.WALL_CLOCK_TIME, TimeUnit.MILLISECONDS, 5000, 6000);
        Counter c2 = newCounter(Type.CPU_TIME, TimeUnit.NANOSECONDS, 700000000000l, 900000000000l);

        // Prepare the stopwatch
        Stopwatch stopwatch = Mockito.mock(Stopwatch.class);
        Mockito.when(stopwatch.getCounters()).thenReturn(Arrays.asList(c1, c2));

        String expectedString = StopwatchFormatter.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_HORIZONTAL_LINES);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintUtils.print(stopwatch, ps);
        String printedString = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(printedString, is(equalTo(expectedString)));
    }

}
