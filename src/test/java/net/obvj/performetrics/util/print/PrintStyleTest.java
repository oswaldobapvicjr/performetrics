package net.obvj.performetrics.util.print;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import net.obvj.performetrics.util.print.PrintStyle;

/**
 * Unit tests for the {@link PrintStyle}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public class PrintStyleTest
{

    @Test
    public void generateLine_lengthZero_empty()
    {
        assertThat(PrintStyle.generateLine('-', 0), is(emptyString()));
    }

    @Test
    public void generateLine_lengthOne_singleCharacterString()
    {
        assertThat(PrintStyle.generateLine('-', 1), is(equalTo("-")));
    }

    @Test
    public void generateLine_lengthFive_repeatedFiveTimes()
    {
        assertThat(PrintStyle.generateLine('>', 5), is(equalTo(">>>>>")));
    }

}
