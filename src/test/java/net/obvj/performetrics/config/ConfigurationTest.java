package net.obvj.performetrics.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.util.print.PrintFormat;
import net.obvj.performetrics.util.print.PrintStyle;

/**
 * Unit tests for the {@link Configuration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class ConfigurationTest
{
    Configuration configuration = new Configuration();

    @Test
    void constructor_default_defaultValues()
    {
        assertThat(configuration.getTimeUnit(), is(equalTo(Configuration.INITIAL_TIME_UNIT)));
        assertThat(configuration.getConversionMode(), is(equalTo(Configuration.INITIAL_CONVERSION_MODE)));
        assertThat(configuration.getScale(), is(equalTo(Configuration.INITIAL_SCALE)));
        assertThat(configuration.getPrintStyleForSummary(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_SUMMARY)));
        assertThat(configuration.getPrintStyleForDetails(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_DETAILS)));
    }

    @Test
    void setTimeUnit_validTimeUnit_suceeds()
    {
        configuration.setTimeUnit(TimeUnit.SECONDS);
        assertThat(configuration.getTimeUnit(), is(equalTo(TimeUnit.SECONDS)));
    }

    @Test
    void setConversionMode_validMode_suceeds()
    {
        configuration.setConversionMode(ConversionMode.FAST);
        assertThat(configuration.getConversionMode(), is(equalTo(ConversionMode.FAST)));
    }

    @Test
    void setScale_validNumber_suceeds()
    {
        configuration.setScale(16);
        assertThat(configuration.getScale(), is(equalTo(16)));
    }

    @Test
    void setScale_negativeNumber_fails()
    {
        assertThrows(IllegalArgumentException.class, () -> configuration.setScale(-1));
    }

    @Test
    void setScale_higherThanMaximum_fails()
    {
        assertThrows(IllegalArgumentException.class, () -> configuration.setScale(17));
    }

    @Test
    void setSummarizedPrintStyle_null_fails()
    {
        assertThrows(NullPointerException.class, () -> configuration.setPrintStyleForSummary(null));
    }

    @Test
    void setSummarizedPrintStyle_notNull_succeeds()
    {
        PrintStyle style = PrintStyle.builder(PrintFormat.SUMMARIZED).build();
        configuration.setPrintStyleForSummary(style);
        assertThat(configuration.getPrintStyleForSummary(), is(equalTo(style)));
    }

    @Test
    void setDetailedPrintStyle_null_fails()
    {
        assertThrows(NullPointerException.class, () -> configuration.setPrintStyleForDetails(null));
    }

    @Test
    void setDetailedPrintStyle_notNull_succeeds()
    {
        PrintStyle style = PrintStyle.builder(PrintFormat.DETAILED).build();
        configuration.setPrintStyleForDetails(style);
        assertThat(configuration.getPrintStyleForDetails(), is(equalTo(style)));
    }

}
