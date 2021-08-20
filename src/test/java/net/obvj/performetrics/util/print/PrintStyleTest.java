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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PrintStyle} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.2
 */
class PrintStyleTest
{

    @Test
    void builder_noArguments_defaultBuilder()
    {
        PrintStyleBuilder builder = PrintStyle.builder(PrintFormat.SUMMARIZED);
        assertThat(builder, is(not(nullValue())));
    }

    @Test
    void builder_validPrintStyle_presetBuilder()
    {
        PrintStyle sourcePrintStyle = PrintStyle.SUMMARIZED_CSV_NO_HEADER;
        PrintStyleBuilder builder = PrintStyle.builder(sourcePrintStyle);
        assertThat(builder.getRowFormat(), is(equalTo(sourcePrintStyle.getRowFormat())));
        assertThat(builder.isPrintHeader(), is(equalTo(sourcePrintStyle.isPrintHeader())));
    }

}
