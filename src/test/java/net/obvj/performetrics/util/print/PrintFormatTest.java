package net.obvj.performetrics.util.print;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Unit tests for the {@link PrintFormat}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
@RunWith(MockitoJUnitRunner.class)
public class PrintFormatTest
{
    static final PrintStyle SUMMARIZED_TEST_STYLE = new PrintStyle(DurationFormat.FULL, null, "%s %s", null, null, null);
    static final PrintStyle DETAILED_TEST_STYLE = new PrintStyle(DurationFormat.FULL, null, "%s %s %s", "%s", null, null);

    static final Duration DURATION_TS1_C1 = Duration.of(1000, MILLISECONDS);
    static final Duration DURATION_TS1_C2 = Duration.of(  50, MILLISECONDS);
    static final Duration DURATION_TS2_C1 = Duration.of( 750, MILLISECONDS);
    static final Duration DURATION_TS2_C2 = Duration.of( 500, MILLISECONDS);
    static final Duration DURATION_SUM_C1 = Duration.of(1750, MILLISECONDS);
    static final Duration DURATION_SUM_C2 = Duration.of( 550, MILLISECONDS);

    static final String STR_DURATION_TS1_C1 = DURATION_TS1_C1.toString(DurationFormat.FULL, false);
    static final String STR_DURATION_TS1_C2 = DURATION_TS1_C2.toString(DurationFormat.FULL, false);
    static final String STR_DURATION_TS2_C1 = DURATION_TS2_C1.toString(DurationFormat.FULL, false);
    static final String STR_DURATION_TS2_C2 = DURATION_TS2_C2.toString(DurationFormat.FULL, false);
    static final String STR_DURATION_SUM_C1 = DURATION_SUM_C1.toString(DurationFormat.FULL, false);
    static final String STR_DURATION_SUM_C2 = DURATION_SUM_C2.toString(DurationFormat.FULL, false);

    @Mock Counter s1Counter1;
    @Mock Counter s1Counter2;
    @Mock Counter s2Counter1;
    @Mock Counter s2Counter2;

    @Mock Stopwatch stopwatch;

    @Mock StringBuilder stringBuilder;

    @Before
    public void setupMocks()
    {
        setupCounters();
        setupStopwatch();
    }

    private void setupCounters()
    {
        setupCounter(s1Counter1, DURATION_TS1_C1, WALL_CLOCK_TIME);
        setupCounter(s1Counter2, DURATION_TS1_C2, CPU_TIME);
        setupCounter(s2Counter1, DURATION_TS2_C1, WALL_CLOCK_TIME);
        setupCounter(s2Counter2, DURATION_TS2_C2, CPU_TIME);
    }

    private void setupCounter(Counter counter, Duration elapsedTime, Type type)
    {
        when(counter.getType()).thenReturn(type);
        when(counter.elapsedTime()).thenReturn(elapsedTime);
    }

    private void setupStopwatch()
    {
        when(stopwatch.getTypes()).thenReturn(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME));
        when(stopwatch.getCounters()).thenReturn(Arrays.asList(s1Counter1, s1Counter2, s2Counter1, s2Counter2));
        when(stopwatch.elapsedTime(WALL_CLOCK_TIME)).thenReturn(DURATION_SUM_C1);
        when(stopwatch.elapsedTime(CPU_TIME)).thenReturn(DURATION_SUM_C2);
    }

    @Test
    public void summarized_printsTypesAndTotalElapsedTimes()
    {
        String result = PrintFormat.SUMMARIZED.format(stopwatch, SUMMARIZED_TEST_STYLE);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME + " " + STR_DURATION_SUM_C1)));
        assertThat(lines[1], is(equalTo(CPU_TIME + " " + STR_DURATION_SUM_C2)));
    }

    @Test
    public void detailed_printsElapsedTimesAndAccumulatedValues()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME.toString())));
        assertThat(lines[1], is(equalTo(1 + " " + STR_DURATION_TS1_C1 + " " + STR_DURATION_TS1_C1)));
        assertThat(lines[2], is(equalTo(2 + " " + STR_DURATION_TS2_C1 + " " + STR_DURATION_SUM_C1)));
        assertThat(lines[3], is(equalTo(STR_DURATION_SUM_C1)));

        assertThat(lines[4], is(equalTo(CPU_TIME.toString())));
        assertThat(lines[5], is(equalTo(1 + " " + STR_DURATION_TS1_C2 + " " + STR_DURATION_TS1_C2)));
        assertThat(lines[6], is(equalTo(2 + " " + STR_DURATION_TS2_C2 + " " + STR_DURATION_SUM_C2)));
        assertThat(lines[7], is(equalTo(STR_DURATION_SUM_C2)));
    }

    @Test
    public void appendLine_nullOrEmptyFormat_doNothing()
    {
        PrintFormat.appendLine(stringBuilder, null);
        PrintFormat.appendLine(stringBuilder, null, "");
        PrintFormat.appendLine(stringBuilder, "");
        PrintFormat.appendLine(stringBuilder, "", "");
        verifyNoInteractions(stringBuilder);
    }

    @Test
    public void appendLine_validFormat_appendsResultAndLineSepator()
    {
        StringBuilder sb = new StringBuilder();
        PrintFormat.appendLine(sb, "test=%s", "test");
        assertThat(sb.toString(), is(equalTo("test=test" + PrintFormat.LINE_SEPARATOR)));
    }

}
