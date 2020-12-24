package net.obvj.performetrics.util.print;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

/**
 * Unit tests for the {@link PrintStyleBuilder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public class PrintStyleBuilderTest
{

    private static final String ROW_FORMAT1 = "rowFormat1";
    private static final String LINE1 = "line1";
    private static final String LINE2 = "line2";

    @Test
    public void generateLine_lengthZero_empty()
    {
        assertThat(PrintStyleBuilder.generateLine('-', 0), is(emptyString()));
    }

    @Test
    public void generateLine_lengthOne_singleCharacterString()
    {
        assertThat(PrintStyleBuilder.generateLine('-', 1), is(equalTo("-")));
    }

    @Test
    public void generateLine_lengthFive_repeatedFiveTimes()
    {
        assertThat(PrintStyleBuilder.generateLine('>', 5), is(equalTo(">>>>>")));
    }

    @Test
    public void build_nullObjects_defaultValues()
    {
        PrintStyle printStyle = new PrintStyleBuilder().build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.isPrintHeader(), is(false));
        assertThat(printStyle.isPrintSectionSummary(), is(false));
        assertThat(printStyle.isPrintLegend(), is(false));
        assertThat(printStyle.getSimpleLine(), is(nullValue()));
        assertThat(printStyle.getAlternativeLine(), is(nullValue()));
    }

    @Test
    public void build_emptyRowFormat_emptyRowFormat()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withRowFormat("").build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
    }

    @Test
    public void build_withoutHeader_false()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withoutHeader().build();
        assertThat(printStyle.isPrintHeader(), is(false));
    }

    @Test
    public void build_withRowFormatAndHeader_applyRowFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withRowFormat(ROW_FORMAT1).withHeader().build();
        assertThat(printStyle.getRowFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    public void build_withNoRowFormatAndWithHeader_applyDefaultFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withHeader().build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    public void build_withRowFormatAndHeaderAndHeaderFormat_applySpecificFormatForHeader()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withHeader(ROW_FORMAT1).build();
        assertThat(printStyle.getRowFormat(), is(equalTo(PrintStyleBuilder.DEFAULT_FORMAT)));
        assertThat(printStyle.getHeaderFormat(), is(equalTo(ROW_FORMAT1)));
        assertThat(printStyle.isPrintHeader(), is(true));
    }

    @Test
    public void build_withSimpleLine_lineFormatAppliesToSimpleAndAlternativeLines()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withSimpleLine(LINE1).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo(LINE1)));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE1)));
    }

    @Test
    public void build_withSimpleLineAndAlternativeLine_lineFormatsApplied()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withSimpleLine(LINE1).withAlternativeLine(LINE2).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo(LINE1)));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE2)));
    }

    @Test
    public void build_withoutSimpleLineAndWithAlternativeLine_alternativeLineAppliedButNoSimpleLine()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withAlternativeLine(LINE1).build();
        assertThat(printStyle.getSimpleLine(), is(nullValue()));
        assertThat(printStyle.getAlternativeLine(), is(equalTo(LINE1)));
    }

    @Test
    public void build_withLineCharacterAndLength_formattedLines()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withSimpleLine('0', 3).withAlternativeLine('X', 5).build();
        assertThat(printStyle.getSimpleLine(), is(equalTo("000")));
        assertThat(printStyle.getAlternativeLine(), is(equalTo("XXXXX")));
    }

    @Test
    public void build_withLegends_true()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withLegends().build();
        assertThat(printStyle.isPrintLegend(), is(true));
    }

    @Test
    public void build_withoutLegends_false()
    {
        PrintStyle printStyle = new PrintStyleBuilder().withoutLegends().build();
        assertThat(printStyle.isPrintLegend(), is(false));
    }

}
