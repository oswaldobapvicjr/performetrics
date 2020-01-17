package net.obvj.performetrics.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit tests for the {@link ConversionStrategy}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ConversionStrategyTest
{

    @Test
    public void convert_fast999millisecondsToSeconds()
    {
        assertThat(ConversionStrategy.FAST.convert(999, MILLISECONDS, SECONDS), is(0.0));
    }

    @Test
    public void convert_doublePrecision999millisecondsToSeconds()
    {
        assertThat(ConversionStrategy.DOUBLE_PRECISION.convert(999, MILLISECONDS, SECONDS), is(0.999));
    }

}
