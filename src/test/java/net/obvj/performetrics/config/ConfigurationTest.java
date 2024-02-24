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

package net.obvj.performetrics.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThat(configuration.getConversionMode(), is(equalTo(Configuration.INITIAL_CONVERSION_MODE)));
        assertThat(configuration.getScale(), is(equalTo(Configuration.INITIAL_SCALE)));
        assertThat(configuration.getPrintStyleForSummary(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_SUMMARY)));
        assertThat(configuration.getPrintStyleForDetails(), is(equalTo(Configuration.INITIAL_PRINT_STYLE_FOR_DETAILS)));
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
