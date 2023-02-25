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

import static java.util.Arrays.*;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.obvj.performetrics.Counter.Type;

/**
 * Unit tests for the {@link PrintStyleBuilder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
class PrintStyleBuilderTest
{

    private static final String ROW_FORMAT1 = "rowFormat1";
    private static final String LINE1 = "line1";
    private static final String LINE2 = "line2";

    @Test
    void generateLine_lengthZero_empty()
    {
        assertThat(PrintStyleBuilder.generateLine('-', 0), is(""));
    }

    @Test
    void generateLine_lengthOne_singleCharacterString()
    {
        assertThat(PrintStyleBuilder.generateLine('-', 1), is(equalTo("-")));
    }

    @Test
    void generateLine_lengthFive_repeatedFiveTimes()
    {
        assertThat(PrintStyleBuilder.generateLine('>', 5), is(equalTo(">>>>>")));
    }

    @Test
    void build_nullObjects_defaultValues()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.isPrintHeader(), is(false));
        assertThat(printStyle.isPrintSectionSummary(), is(false));
        assertThat(printStyle.isPrintLegend(), is(false));
        assertThat(printStyle.getSimpleLine(), is(nullValue()));
        assertThat(printStyle.getAlternativeLine(), is(nullValue()));
        assertThat(printStyle.getExcludedTypes(), equalTo(emptySet()));
        assertThat(printStyle.getCustomCounterNames(), equalTo(emptyMap()));
    }

    @Test
    void build_emptyRowFormat_emptyRowFormat()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withRowFormat("").build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
    }

    @Test
    void build_withoutHeader_false()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withoutHeader().build();
        assertThat(printStyle.isPrintHeader(), is(false));
    }

    @Test
    void build_withRowFormatAndHeader_applyRowFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withRowFormat(ROW_FORMAT1).withHeader()
                .build();
        assertThat(printStyle.getRowFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    void build_withNoRowFormatAndWithHeader_applyDefaultFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withHeader().build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    void build_withRowFormatAndHeaderAndHeaderFormat_applySpecificFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withHeader(ROW_FORMAT1).build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    void build_withSimpleLine_lineFormatAppliesToSimpleAndAlternativeLines()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withSimpleLine(LINE1).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo(LINE1)));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE1)));
    }

    @Test
    void build_withSimpleLineAndAlternativeLine_lineFormatsApplied()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withSimpleLine(LINE1)
                .withAlternativeLine(LINE2).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo(LINE1)));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE2)));
    }

    @Test
    void build_withoutSimpleLineAndWithAlternativeLine_alternativeLineAppliedButNoSimpleLine()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withAlternativeLine(LINE1).build();
        assertThat(printStyle.getSimpleLine(), is(nullValue()));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE1)));
    }

    @Test
    void build_withLineCharacterAndLength_formattedLines()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withSimpleLine('0', 3)
                .withAlternativeLine('X', 5).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo("000")));
        assertThat(printStyle.getAlternativeLine(), is(equalTo("XXXXX")));
    }

    @Test
    void build_withLegends_true()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withLegends().build();
        assertThat(printStyle.isPrintLegend(), is(true));
    }

    @Test
    void build_withoutLegends_false()
    {
        PrintStyle printStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withoutLegends().build();
        assertThat(printStyle.isPrintLegend(), is(false));
    }

    @Test
    void constructor_nullBasePrintStyle_failure()
    {
        assertThrows(NullPointerException.class, () -> new PrintStyleBuilder((PrintStyle) null));
    }

    @Test
    void build_validBasePrintStyle_copiesValuesFromBasePrintStyle()
    {
        PrintStyle baseStyle = PrintStyle.DETAILED_TABLE_FULL;
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).build();

        assertThat(newStyle.getAlternativeLine(), is(equalTo(baseStyle.getAlternativeLine())));
        assertThat(newStyle.getDurationFormat(), is(equalTo(baseStyle.getDurationFormat())));
        assertThat(newStyle.getHeaderFormat(), is(equalTo(baseStyle.getHeaderFormat())));
        assertThat(newStyle.isPrintHeader(), is(equalTo(baseStyle.isPrintHeader())));
        assertThat(newStyle.isPrintLegend(), is(equalTo(baseStyle.isPrintLegend())));
        assertThat(newStyle.isPrintSectionSummary(), is(equalTo(baseStyle.isPrintSectionSummary())));
        assertThat(newStyle.getRowFormat(), is(equalTo(baseStyle.getRowFormat())));
        assertThat(newStyle.getSectionHeaderFormat(), is(equalTo(baseStyle.getSectionHeaderFormat())));
        assertThat(newStyle.getSectionSummaryRowFormat(), is(equalTo(baseStyle.getSectionSummaryRowFormat())));
        assertThat(newStyle.getSimpleLine(), is(equalTo(baseStyle.getSimpleLine())));
        assertThat(newStyle.getExcludedTypes(), is(equalTo(baseStyle.getExcludedTypes())));
        assertThat(newStyle.getCustomCounterNames(), is(equalTo(baseStyle.getCustomCounterNames())));
    }

    @Test
    void build_validBasePrintStyleAlt_copiesValuesFromBasePrintStyle()
    {
        PrintStyle baseStyle = PrintStyle.LINUX;
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).build();

        assertThat(newStyle.getAlternativeLine(), is(equalTo(baseStyle.getAlternativeLine())));
        assertThat(newStyle.getDurationFormat(), is(equalTo(baseStyle.getDurationFormat())));
        assertThat(newStyle.getHeaderFormat(), is(equalTo(baseStyle.getHeaderFormat())));
        assertThat(newStyle.isPrintHeader(), is(equalTo(baseStyle.isPrintHeader())));
        assertThat(newStyle.isPrintLegend(), is(equalTo(baseStyle.isPrintLegend())));
        assertThat(newStyle.isPrintSectionSummary(), is(equalTo(baseStyle.isPrintSectionSummary())));
        assertThat(newStyle.getRowFormat(), is(equalTo(baseStyle.getRowFormat())));
        assertThat(newStyle.getSectionHeaderFormat(), is(equalTo(baseStyle.getSectionHeaderFormat())));
        assertThat(newStyle.getSectionSummaryRowFormat(), is(equalTo(baseStyle.getSectionSummaryRowFormat())));
        assertThat(newStyle.getSimpleLine(), is(equalTo(baseStyle.getSimpleLine())));
        assertThat(newStyle.getExcludedTypes(), is(equalTo(baseStyle.getExcludedTypes())));
        assertThat(newStyle.getCustomCounterNames(), is(equalTo(baseStyle.getCustomCounterNames())));
    }

    @Test
    void withoutTrailer_basePrintStyleWithTrailer_false()
    {
        PrintStyle baseStyle = new PrintStyleBuilder(PrintFormat.SUMMARIZED).withTrailer("trailer1").build();
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).withoutTrailer().build();
        assertFalse(newStyle.isPrintTrailer());
        assertNull(newStyle.getTrailerFormat());
    }

    @Test
    void withoutSectionTrailer_basePrintStyleWithSectionTrailer_false()
    {
        PrintStyle baseStyle = new PrintStyleBuilder(PrintFormat.DETAILED).withSectionTrailer("trailer1").build();
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).withoutSectionTrailer().build();
        assertFalse(newStyle.isPrintSectionTrailer());
        assertNull(newStyle.getSectionTrailerFormat());
    }

    @Test
    void withoutHeader_basePrintStyleWithHeader_false()
    {
 		PrintStyle baseStyle = new PrintStyleBuilder(PrintFormat.DETAILED).withRowFormat("rowFormat1").withHeader("header1").build();
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).withoutHeader().build();
        assertFalse(newStyle.isPrintHeader());
        assertNull(newStyle.getHeaderFormat());
    }

    @Test
    void withAllTypes_basePrintStyleWithTypesExcluded_noExclusions()
    {
        PrintStyle baseStyle = new PrintStyleBuilder(PrintFormat.DETAILED).withoutTypes(Type.SYSTEM_TIME, Type.USER_TIME).build();
        assertThat(baseStyle.getExcludedTypes().containsAll(asList(Type.SYSTEM_TIME, Type.USER_TIME)), is(true));
        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).withAllTypes().build();
        assertThat(newStyle.getExcludedTypes(), equalTo(emptySet()));
    }

    @Test
    void resetCustomCounterNames_basePrintStyleWithCustomNames_emptyMap()
    {
        PrintStyle baseStyle = new PrintStyleBuilder(PrintFormat.DETAILED)
                .withCustomCounterName(Type.SYSTEM_TIME, "custom1")
                .withCustomCounterName(Type.USER_TIME, "custom2")
                .build();
        assertThat(baseStyle.getCustomCounterNames().get(Type.SYSTEM_TIME), equalTo("custom1"));
        assertThat(baseStyle.getCustomCounterNames().get(Type.USER_TIME), equalTo("custom2"));

        PrintStyle newStyle = new PrintStyleBuilder(baseStyle).resetCustomCounterNames().build();
        assertThat(newStyle.getCustomCounterNames(), equalTo(emptyMap()));
    }

}
