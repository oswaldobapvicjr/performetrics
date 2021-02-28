package net.obvj.performetrics;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ConversionMode}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
class ConversionModeTest
{

    @Test
    void convert_fast999millisecondsToSeconds()
    {
        assertThat(ConversionMode.FAST.convert(999, MILLISECONDS, SECONDS), is(0.0));
    }

    @Test
    void convert_doublePrecision999millisecondsToSeconds()
    {
        assertThat(ConversionMode.DOUBLE_PRECISION.convert(999, MILLISECONDS, SECONDS), is(0.999));
    }

}
