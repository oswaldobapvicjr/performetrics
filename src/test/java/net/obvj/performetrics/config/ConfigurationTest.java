package net.obvj.performetrics.config;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.obvj.performetrics.ConversionMode;
import net.obvj.performetrics.util.print.PrintStyle;
import net.obvj.performetrics.util.print.PrintStyleBuilder;

/**
 * Unit tests for the {@link Configuration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class ConfigurationTest
{
    Configuration configuration = new Configuration();

    @Test
    public void constructor_default_defaultValues()
    {
        assertThat(configuration.getTimeUnit(), is(equalTo(Configuration.INITIAL_TIME_UNIT)));
        assertThat(configuration.getConversionMode(), is(equalTo(Configuration.INITIAL_CONVERSION_MODE)));
        assertThat(configuration.getScale(), is(equalTo(Configuration.INITIAL_SCALE)));
        assertThat(configuration.getPrintStyleForSummary(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_SUMMARY)));
        assertThat(configuration.getPrintStyleForDetails(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_DETAILS)));
    }

    @Test
    public void setTimeUnit_validTimeUnit_suceeds()
    {
        configuration.setTimeUnit(TimeUnit.SECONDS);
        assertThat(configuration.getTimeUnit(), is(equalTo(TimeUnit.SECONDS)));
    }

    @Test
    public void setConversionMode_validMode_suceeds()
    {
        configuration.setConversionMode(ConversionMode.FAST);
        assertThat(configuration.getConversionMode(), is(equalTo(ConversionMode.FAST)));
    }

    @Test
    public void setScale_validNumber_suceeds()
    {
        configuration.setScale(16);
        assertThat(configuration.getScale(), is(equalTo(16)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setScale_negativeNumber_fails()
    {
        configuration.setScale(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setScale_higherThanMaximum_fails()
    {
        configuration.setScale(17);
    }

    @Test(expected = NullPointerException.class)
    public void setSummarizedPrintStyle_null_fails()
    {
        configuration.setPrintStyleForSummary(null);
    }

    @Test
    public void setSummarizedPrintStyle_notNull_succeeds()
    {
        PrintStyle style = new PrintStyleBuilder().build();
        configuration.setPrintStyleForSummary(style);
        assertThat(configuration.getPrintStyleForSummary(), is(equalTo(style)));
    }

    @Test(expected = NullPointerException.class)
    public void setDetailedPrintStyle_null_fails()
    {
        configuration.setPrintStyleForDetails(null);
    }

    @Test
    public void setDetailedPrintStyle_notNull_succeeds()
    {
        PrintStyle style = new PrintStyleBuilder().build();
        configuration.setPrintStyleForDetails(style);
        assertThat(configuration.getPrintStyleForDetails(), is(equalTo(style)));
    }

}
