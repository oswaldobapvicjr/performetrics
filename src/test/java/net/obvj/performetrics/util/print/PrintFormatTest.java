package net.obvj.performetrics.util.print;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.performetrics.Counter.Type.CPU_TIME;
import static net.obvj.performetrics.Counter.Type.WALL_CLOCK_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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
public class PrintFormatTest
{
    static final PrintStyle SUMMARIZED_TEST_STYLE = new PrintStyleBuilder()
            .withoutHeader()
            .withRowFormat("%s %s")
            .withDurationFormat(DurationFormat.FULL).build();

    static final PrintStyle DETAILED_TEST_STYLE_WITHOUT_TOTALS = new PrintStyleBuilder()
            .withoutHeader()
            .withRowFormat("%s %s %s")
            .withSectionHeaderFormat("%s")
            .withoutSectionSummary()
            .withDurationFormat(DurationFormat.FULL).build();

    static final PrintStyle DETAILED_TEST_STYLE_WITH_TOTALS = new PrintStyleBuilder()
            .withoutHeader()
            .withRowFormat("%s %s %s")
            .withSectionHeaderFormat("%s")
            .withSectionSummary("%s")
            .withDurationFormat(DurationFormat.FULL).build();

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

    Counter s1Counter1 = mock(Counter.class);
    Counter s1Counter2 = mock(Counter.class);
    Counter s2Counter1 = mock(Counter.class);
    Counter s2Counter2 = mock(Counter.class);

    Stopwatch stopwatch = mock(Stopwatch.class);

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
        when(counter.elapsedTime()).thenReturn(elapsedTime);
    }

    private void setupStopwatch()
    {
        when(stopwatch.getTypes()).thenReturn(Arrays.asList(WALL_CLOCK_TIME, CPU_TIME));
        Map<Type, List<Counter>> map = new EnumMap<>(Type.class);
        map.put(WALL_CLOCK_TIME, Arrays.asList(s1Counter1, s2Counter1));
        map.put(CPU_TIME, Arrays.asList(s1Counter2, s2Counter2));
        when(stopwatch.getAllCountersByType()).thenReturn(map);
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
    public void detailed_withoutSectionTotals_printsElapsedTimesAndAccumulatedValues()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE_WITHOUT_TOTALS);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME.toString())));
        assertThat(lines[1], is(equalTo(1 + " " + STR_DURATION_TS1_C1 + " " + STR_DURATION_TS1_C1)));
        assertThat(lines[2], is(equalTo(2 + " " + STR_DURATION_TS2_C1 + " " + STR_DURATION_SUM_C1)));

        assertThat(lines[3], is(equalTo(CPU_TIME.toString())));
        assertThat(lines[4], is(equalTo(1 + " " + STR_DURATION_TS1_C2 + " " + STR_DURATION_TS1_C2)));
        assertThat(lines[5], is(equalTo(2 + " " + STR_DURATION_TS2_C2 + " " + STR_DURATION_SUM_C2)));
    }

    @Test
    public void detailed_withSectionTotals_printsElapsedTimesAndAccumulatedValuesAndTotals()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE_WITH_TOTALS);
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
        StringBuilder stringBuilder = new StringBuilder();
        PrintFormat.appendLine(stringBuilder, null);
        PrintFormat.appendLine(stringBuilder, null, "");
        PrintFormat.appendLine(stringBuilder, "");
        PrintFormat.appendLine(stringBuilder, "", "");
        assertThat(stringBuilder.length(), is(equalTo(0)));
    }

    @Test
    public void appendLine_validFormat_appendsResultAndLineSepator()
    {
        StringBuilder sb = new StringBuilder();
        PrintFormat.appendLine(sb, "test=%s", "test");
        assertThat(sb.toString(), is(equalTo("test=test" + PrintFormat.LINE_SEPARATOR)));
    }

}
