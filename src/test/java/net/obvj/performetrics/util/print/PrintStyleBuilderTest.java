package net.obvj.performetrics.util.print;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Unit tests for the {@link PrintStyleBuilder}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.2.1
 */
public class PrintStyleBuilderTest
{

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

}
