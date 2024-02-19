/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.performetrics.util.print;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.obvj.performetrics.Counter.Type.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.TimingSessionContainer;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.DurationFormat;

/**
 * Unit tests for the {@link PrintFormat}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
class PrintFormatTest
{
    static final PrintStyle SUMMARIZED_TEST_STYLE = PrintStyle.builder(PrintFormat.SUMMARIZED)
            .withoutHeader()
            .withRowFormat("%s %s")
            .withDurationFormat(DurationFormat.FULL).build();

    static final PrintStyle SUMMARIZED_TEST_STYLE_WITH_WALL_CLOCK_EXCLUDED = PrintStyle.builder(SUMMARIZED_TEST_STYLE)
            .withoutTypes(Type.WALL_CLOCK_TIME)
            .build();

    static final PrintStyle SUMMARIZED_TEST_STYLE_CUSTOM_NAME = PrintStyle.builder(SUMMARIZED_TEST_STYLE)
            .withCustomCounterName(CPU_TIME, "custom1")
            .build();

    static final PrintStyle DETAILED_TEST_STYLE_WITHOUT_TOTALS = PrintStyle.builder(PrintFormat.DETAILED)
            .withoutHeader()
            .withRowFormat("%s %s %s")
            .withSectionHeader("%s")
            .withoutSectionSummary()
            .withDurationFormat(DurationFormat.FULL).build();

    static final PrintStyle DETAILED_TEST_STYLE_WITH_TOTALS = PrintStyle.builder(PrintFormat.DETAILED)
            .withoutHeader()
            .withRowFormat("%s %s %s")
            .withSectionHeader("%s")
            .withSectionSummary("%s")
            .withDurationFormat(DurationFormat.FULL).build();

    static final PrintStyle DETAILED_TEST_STYLE_WITH_TOTALS_AND_TWO_TYPES_EXCLUDED_AND_CUSTOM_NAMES = PrintStyle
            .builder(DETAILED_TEST_STYLE_WITH_TOTALS)
            .withoutTypes(CPU_TIME)
            .withCustomCounterName(WALL_CLOCK_TIME, "real")
            .build();

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

    TimingSessionContainer stopwatch = mock(Stopwatch.class);

    @BeforeEach
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
    void summarized_printsTypesAndTotalElapsedTimes()
    {
        String result = PrintFormat.SUMMARIZED.format(stopwatch, SUMMARIZED_TEST_STYLE);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines.length, is(equalTo(2)));
        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME + " " + STR_DURATION_SUM_C1)));
        assertThat(lines[1], is(equalTo(CPU_TIME + " " + STR_DURATION_SUM_C2)));
    }

    @Test
    void summarizedWithOneExclusion_printsTypesAndElapsedTimesOfNonExcluded()
    {
        String result = PrintFormat.SUMMARIZED.format(stopwatch, SUMMARIZED_TEST_STYLE_WITH_WALL_CLOCK_EXCLUDED);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines.length, is(equalTo(1)));
        assertThat(lines[0], is(equalTo(CPU_TIME + " " + STR_DURATION_SUM_C2)));
    }

    @Test
    void summarizedWithOneCustomName_printsCounterNamesAccordingly()
    {
        String result = PrintFormat.SUMMARIZED.format(stopwatch, SUMMARIZED_TEST_STYLE_CUSTOM_NAME);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines.length, is(equalTo(2)));
        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME + " " + STR_DURATION_SUM_C1))); // default, not specified
        assertThat(lines[1], is(equalTo("custom1 " + STR_DURATION_SUM_C2))); // custom name
    }

    @Test
    void detailed_withoutSectionTotals_printsElapsedTimesAndAccumulatedValues()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE_WITHOUT_TOTALS);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME.toString())));
        assertThat(lines[1], is(equalTo(0 + " " + STR_DURATION_TS1_C1 + " " + STR_DURATION_TS1_C1)));
        assertThat(lines[2], is(equalTo(1 + " " + STR_DURATION_TS2_C1 + " " + STR_DURATION_SUM_C1)));

        assertThat(lines[3], is(equalTo(CPU_TIME.toString())));
        assertThat(lines[4], is(equalTo(0 + " " + STR_DURATION_TS1_C2 + " " + STR_DURATION_TS1_C2)));
        assertThat(lines[5], is(equalTo(1 + " " + STR_DURATION_TS2_C2 + " " + STR_DURATION_SUM_C2)));
    }

    @Test
    void detailed_withSectionTotals_printsElapsedTimesAndAccumulatedValuesAndTotals()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE_WITH_TOTALS);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo(WALL_CLOCK_TIME.toString())));
        assertThat(lines[1], is(equalTo(0 + " " + STR_DURATION_TS1_C1 + " " + STR_DURATION_TS1_C1)));
        assertThat(lines[2], is(equalTo(1 + " " + STR_DURATION_TS2_C1 + " " + STR_DURATION_SUM_C1)));
        assertThat(lines[3], is(equalTo(STR_DURATION_SUM_C1)));

        assertThat(lines[4], is(equalTo(CPU_TIME.toString())));
        assertThat(lines[5], is(equalTo(0 + " " + STR_DURATION_TS1_C2 + " " + STR_DURATION_TS1_C2)));
        assertThat(lines[6], is(equalTo(1 + " " + STR_DURATION_TS2_C2 + " " + STR_DURATION_SUM_C2)));
        assertThat(lines[7], is(equalTo(STR_DURATION_SUM_C2)));
        assertThat(lines.length, equalTo(8));
    }

    @Test
    void detailed_withTwoExcludedTypesAndCustomNames_printsAccordingly()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, DETAILED_TEST_STYLE_WITH_TOTALS_AND_TWO_TYPES_EXCLUDED_AND_CUSTOM_NAMES);
        String[] lines = result.split(PrintFormat.LINE_SEPARATOR);

        assertThat(lines[0], is(equalTo("real")));
        assertThat(lines[1], is(equalTo(0 + " " + STR_DURATION_TS1_C1 + " " + STR_DURATION_TS1_C1)));
        assertThat(lines[2], is(equalTo(1 + " " + STR_DURATION_TS2_C1 + " " + STR_DURATION_SUM_C1)));
        assertThat(lines[3], is(equalTo(STR_DURATION_SUM_C1)));
        assertThat(lines.length, equalTo(4));
    }

    @Test
    void appendLine_nullOrEmptyFormat_doNothing()
    {
        StringBuilder stringBuilder = new StringBuilder();
        PrintFormat.appendLine(stringBuilder, null);
        PrintFormat.appendLine(stringBuilder, null, "");
        PrintFormat.appendLine(stringBuilder, "");
        PrintFormat.appendLine(stringBuilder, "", "");
        assertThat(stringBuilder.length(), is(equalTo(0)));
    }

    @Test
    void appendLine_validFormat_appendsResultAndLineSepator()
    {
        StringBuilder sb = new StringBuilder();
        PrintFormat.appendLine(sb, "test=%s", "test");
        assertThat(sb.toString(), is(equalTo("test=test" + PrintFormat.LINE_SEPARATOR)));
    }

    @Test
    void checkCompatibility_summarized_incompatiblePrintStyle_exception()
    {
        assertThrows(IllegalArgumentException.class,
                () -> PrintFormat.SUMMARIZED.checkCompatibility(DETAILED_TEST_STYLE_WITH_TOTALS));
    }

    @Test
    void checkCompatibility_detailed_incompatiblePrintStyle_exception()
    {
        assertThrows(IllegalArgumentException.class,
                () -> PrintFormat.DETAILED.checkCompatibility(SUMMARIZED_TEST_STYLE));
    }

    private void assertEachLine(String expected, String actual)
    {
        String[] expectedLines = expected.split("\n");
        String[] actualLine = actual.split("\n");
        for (int i = 0; i < expectedLines.length; i++)
        {
            assertThat(actualLine[i].trim(), equalTo(expectedLines[i].trim()));
        }
    }

    @Test
    void format_summarizedXml_properFormatting()
    {
        String expected = "<counters>\n"
                        + "  <counter type=\"Wall clock time\">"+ STR_DURATION_SUM_C1 +"</counter>\n"
                        + "  <counter type=\"CPU time\">" + STR_DURATION_SUM_C2 + "</counter>\n"
                        + "</counters>";

        String result = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_XML);
        assertEachLine(expected, result);
    }

    @Test
    void format_detailedXml_properFormatting()
    {
        String expected = "<counters>\n"
                        + "  <counter type=\"Wall clock time\">\n"
                        + "    <session index=\"0\">" + STR_DURATION_TS1_C1 + "</session>\n"
                        + "    <session index=\"1\">" + STR_DURATION_TS2_C1 + "</session>\n"
                        + "    <total>"+ STR_DURATION_SUM_C1 +"</total>\n"
                        + "  </counter>\n"
                        + "  <counter type=\"CPU time\">\n"
                        + "    <session index=\"0\">" + STR_DURATION_TS1_C2 + "</session>\n"
                        + "    <session index=\"1\">" + STR_DURATION_TS2_C2 + "</session>\n"
                        + "    <total>" + STR_DURATION_SUM_C2 + "</total>\n"
                        + "  </counter>\n"
                        + "</counters>";

        String result = PrintFormat.DETAILED.format(stopwatch, PrintStyle.DETAILED_XML);
        assertEachLine(expected, result);
    }

    /*
     * Classes for tests using the PrintStyle.SUMMARIZED_YAML
     */
    static class CountersSummaryYaml
    {
        public List<CounterSummaryYaml> counters;
    }

    static class CounterSummaryYaml
    {
        public String type;
        public String value;
    }

    @Test
    void format_summarizedYaml_properFormatting()
    {
        String result = PrintFormat.SUMMARIZED.format(stopwatch, PrintStyle.SUMMARIZED_YAML);
        CountersSummaryYaml yaml = new Yaml().loadAs(result, CountersSummaryYaml.class);
        assertThat(yaml.counters.get(0).type, equalTo(WALL_CLOCK_TIME.toString()));
        assertThat(yaml.counters.get(0).value, equalTo(STR_DURATION_SUM_C1));
        assertThat(yaml.counters.get(1).type, equalTo(CPU_TIME.toString()));
        assertThat(yaml.counters.get(1).value, equalTo(STR_DURATION_SUM_C2));
    }

    /*
     * Classes for tests using the PrintStyle.DETAILED_YAML
     */
    static class CountersDetailsYaml
    {
        public List<CounterDetailsYaml> counters;
    }

    static class CounterDetailsYaml
    {
        public String type;
        public List<String> sessions;
        public String total;
    }

    @Test
    void format_detailedYaml_properFormatting()
    {
        String result = PrintFormat.DETAILED.format(stopwatch, PrintStyle.DETAILED_YAML);
        CountersDetailsYaml yaml = new Yaml().loadAs(result, CountersDetailsYaml.class);
        assertThat(yaml.counters.get(0).type, equalTo(WALL_CLOCK_TIME.toString()));
        assertThat(yaml.counters.get(0).sessions.get(0), equalTo(STR_DURATION_TS1_C1));
        assertThat(yaml.counters.get(0).sessions.get(1), equalTo(STR_DURATION_TS2_C1));
        assertThat(yaml.counters.get(0).total, equalTo(STR_DURATION_SUM_C1));
        assertThat(yaml.counters.get(1).type, equalTo(CPU_TIME.toString()));
        assertThat(yaml.counters.get(1).sessions.get(0), equalTo(STR_DURATION_TS1_C2));
        assertThat(yaml.counters.get(1).sessions.get(1), equalTo(STR_DURATION_TS2_C2));
        assertThat(yaml.counters.get(1).total, equalTo(STR_DURATION_SUM_C2));
    }

}
