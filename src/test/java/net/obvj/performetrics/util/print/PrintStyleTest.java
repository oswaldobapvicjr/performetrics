package net.obvj.performetrics.util.print;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit tests for the {@link PrintStyle} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.2
 */
public class PrintStyleTest
{

    @Test
    public void builder_noArguments_defaultBuilder()
    {
        PrintStyleBuilder builder = PrintStyle.builder(PrintFormat.SUMMARIZED);
        assertThat(builder, is(not(nullValue())));
    }

    @Test
    public void builder_validPrintStyle_presetBuilder()
    {
        PrintStyle sourcePrintStyle = PrintStyle.SUMMARIZED_CSV_NO_HEADER;
        PrintStyleBuilder builder = PrintStyle.builder(sourcePrintStyle);
        assertThat(builder.getRowFormat(), is(equalTo(sourcePrintStyle.getRowFormat())));
        assertThat(builder.isPrintHeader(), is(equalTo(sourcePrintStyle.isPrintHeader())));
    }

}
