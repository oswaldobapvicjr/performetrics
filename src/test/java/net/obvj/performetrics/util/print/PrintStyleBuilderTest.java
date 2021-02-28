package net.obvj.performetrics.util.print;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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

        assertThat(newStyle.getAlternativeLine(), is(equalTo(newStyle.getAlternativeLine())));
        assertThat(newStyle.getDurationFormat(), is(equalTo(newStyle.getDurationFormat())));
        assertThat(newStyle.getHeaderFormat(), is(equalTo(newStyle.getHeaderFormat())));
        assertThat(newStyle.isPrintHeader(), is(equalTo(newStyle.isPrintHeader())));
        assertThat(newStyle.isPrintLegend(), is(equalTo(newStyle.isPrintLegend())));
        assertThat(newStyle.isPrintSectionSummary(), is(equalTo(newStyle.isPrintSectionSummary())));
        assertThat(newStyle.getRowFormat(), is(equalTo(newStyle.getRowFormat())));
        assertThat(newStyle.getSectionHeaderFormat(), is(equalTo(newStyle.getSectionHeaderFormat())));
        assertThat(newStyle.getSectionSummaryRowFormat(), is(equalTo(newStyle.getSectionSummaryRowFormat())));
        assertThat(newStyle.getSimpleLine(), is(equalTo(newStyle.getSimpleLine())));
    }

}
